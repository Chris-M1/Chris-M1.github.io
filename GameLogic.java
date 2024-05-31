package game;

/**
 *
 * @author milas
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GameLogic {

    private final Scanner scanner;
    private int smallBlind;
    private int bigBlind;
    private int potAmount = 0;
    private int highestBet;
    private final List<String> communityCards = new ArrayList<>();
    private final Deck deck = new Deck();
    private PlayerDAO playerDAO;
    private PlayerLoader playerLoader;
    private List<PlayerWithWallet> players;
    
    public GameLogic(Scanner scanner) {
        DatabaseUtil.initializeDatabase();
        this.scanner = scanner;
        this.playerDAO = new PlayerDAO();
        this.playerLoader = new PlayerLoader();
        this.players = new ArrayList<>();
        addShutdownHook();
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                DriverManager.getConnection("jdbc:derby:;shutdown=true");
            } catch (SQLException e) {
                if (!e.getSQLState().equals("XJ015")) {
                    e.printStackTrace();
                }
            }
        }));
    }
    
    public void start() {
        initializeGame();  // Setup game or new round
        dealHands();       // Deal cards

        // Betting rounds
        preFBetting();
        postFBetting();
        pTBetting();
        pRBetting();

        showdown();        // Showdown to find the winner
        determineWinner(); // Determine and handle winner

        savePlayerWallets();
        System.out.println("Game ended. Thank you for playing!");
    }

    private void initializeGame() {
        players = loadPlayerWallets();
        
        // Display loaded players
        System.out.println("Loaded players: ");
        for (PlayerWithWallet player : players) {
            System.out.println("Name: " + player.getName() + ", Wallet: " + player.getWallet());
        }

        // Prompt for new players
        promptForNewPlayers();
        
        // Final list of players
        System.out.println("Final list of players: ");
        for (PlayerWithWallet player : players) {
            System.out.println("Name: " + player.getName() + ", Wallet: " + player.getWallet());
        }
    }

    
    private PlayerWithWallet loadSinglePlayer(String playerName) throws SQLException {
        String sql = "SELECT * FROM Player WHERE name = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    BigDecimal wallet = rs.getBigDecimal("wallet");
                    return new PlayerWithWallet(id, name, wallet.intValue());
                }
            }
        }
        throw new SQLException("Player not found");
    }
    
    public void addNewPlayer(Player player) {
        try {
            playerDAO.addPlayer(player);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Player getPlayer(int id) {
        try {
            return playerDAO.getPlayer(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void promptForNewPlayers() {
        Scanner scanner = new Scanner(System.in);
        boolean addingPlayers = true;

        while (addingPlayers) {
            System.out.println("Enter player's name (or type 'done' to finish): ");
            String playerName = scanner.nextLine();
            if (playerName.equalsIgnoreCase("done")) {
                addingPlayers = false;
            } else {
                System.out.println("Enter initial wallet amount for " + playerName + ": ");
                int initialWallet = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                // Create a new Player object
                Player newPlayer = new Player(0, playerName, initialWallet); // Assuming 0 for ID, it should be auto-generated or managed

                // Add the new player to the database
                try {
                    playerDAO.addPlayer(newPlayer);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // Additional logic for adding player to the game...
                System.out.println("Player " + playerName + " added with initial wallet of " + initialWallet);
            }
        }
        scanner.close();
    }

    private int readIntInput(String prompt, int min, int max) { // Inspired by ChatGPT
        int input = 0;
        while (true) {
            System.out.println(prompt);
            if (scanner.hasNextInt()) {
                input = scanner.nextInt();
                scanner.nextLine();

                if (input >= min && input <= max) {
                    break;
                }
                System.out.println("Input must be between " + min + " and " + max + ".");
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Consume the invalid input
            }
            // Consume newline left-over
        }
        return input;
    }

    public void savePlayerWallets() {
        PlayerWalletWriter.writeToFile(players); // Save player information to file
    }

    private List<PlayerWithWallet> loadPlayerWallets() {
        return playerLoader.loadPlayersWithWallet();
    }

    private void setupBlinds() {
        smallBlind = readIntInput("Enter small blind amount:", 1, 5000);
        bigBlind = smallBlind * 2;
        deductBlinds();
    }

    private int dPos = -1;

    private void deductBlinds() {
        dPos = (dPos + 1) % players.size(); // Update dealer position
        int sBP = (dPos + 1) % players.size(); // Small blind position
        int bBP = (dPos + 2) % players.size(); // Big blind position
        
        PlayerWithWallet sBPl = players.get(sBP); // Small blind player
        PlayerWithWallet bBPl = players.get(bBP); // Big blind player

        // Deduct small blind
        int aSB = sBPl.getWallet() >= smallBlind ? smallBlind : sBPl.getWallet(); // Actual small blind
        sBPl.deductFromWallet(aSB);
        potAmount += aSB;
        System.out.println(sBPl.getName() + " posts small blind of $" + aSB);

        // Deduct big blind
        int aBB = bBPl.getWallet() >= bigBlind ? bigBlind : bBPl.getWallet(); // Actual big blind
        bBPl.deductFromWallet(aBB);
        potAmount += aBB;
        System.out.println(bBPl.getName() + " posts big blind of $" + aBB);
    }

    private void dealHands() {
        for (Player player : players) {
            player.receiveCard(deck.draw());
            player.receiveCard(deck.draw());
        }
    }

    private void preFBetting() {
        System.out.println("! Pre-Flop Betting Round !");
        displayTableInfo();
        startBettingRound();
    }

    private void postFBetting() {
        System.out.println("\n! Flop Cards !");
        dealCCards(3); // Deal the flop cards
        displayTableInfo();
        startBettingRound();
    }

    private void pTBetting() {
        System.out.println("\n! Turn Card !");
        dealCCards(1); // Deal the turn card
        displayTableInfo();
        startBettingRound();
    }

    private void pRBetting() {
        System.out.println("\n! River Card !");
        dealCCards(1); // Deal the river card
        displayTableInfo();
        startBettingRound();
    }

    private boolean allPlayersCannotAct() {
        return players.stream().allMatch(p -> p.hasFolded() || p.isAllIn());
    }

    private void startBettingRound() {
        potAmount += smallBlind + bigBlind; // Update pot with blinds
        System.out.println("\n!@ START BETTING @!");

        if (allPlayersCannotAct()) {
            System.out.println("All players have folded or are all-in. Moving to the next phase.");
            return; // Exit the method early if no active players can make decisions
        }

        for (PlayerWithWallet pl : players) {
            if (pl.hasFolded() || pl.isAllIn()) {
                continue; // Skip folded players
            }
            System.out.println("\n" + pl.getName() + "'s turn. Wallet: $" + pl.getWallet());
            System.out.println("Current Bet: $" + pl.getCurrentBet() + ", Highest Bet: $" + highestBet);

            boolean vChoice = false; // validChoice
            while (!vChoice) {
                System.out.println("Options: Fold [f], Call/Check [c], Raise [r], All-In [a]");
                String choice = scanner.nextLine().toLowerCase();

                switch (choice) {
                    case "f":
                        System.out.println(pl.getName() + " folds.");
                        pl.fold();
                        vChoice = true;
                        break;
                    case "c":
                        int callAmt = highestBet - pl.getCurrentBet() + bigBlind; // callAmount
                        if (pl.getWallet() >= callAmt) {
                            pl.deductFromWallet(callAmt);
                            potAmount += callAmt;
                            if (callAmt != 0) {
                                System.out.println(pl.getName() + " calls and adds $" + callAmt + " to the pot.");
                            } else {
                                System.out.println(pl.getName() + " calls.");
                            }
                            vChoice = true;
                        } else {
                            System.out.println("Not enough funds to call. Consider going all-in or folding.");
                        }
                        break;
                    case "r":
                        System.out.println("Enter the total amount you want to bet (must be greater than $" + highestBet + "):");
                        int totalBet = readIntInput("Your bet:", highestBet + 1, pl.getWallet() + pl.getCurrentBet());
                        int raiseAmt = totalBet - pl.getCurrentBet(); // raiseAmount
                        pl.deductFromWallet(raiseAmt);
                        potAmount += raiseAmt;
                        highestBet = totalBet;
                        System.out.println(pl.getName() + " raises to $" + totalBet);
                        vChoice = true;
                        break;
                    case "a":
                        int allInAmt = pl.getWallet(); // allInAmount
                        pl.deductFromWallet(allInAmt);
                        potAmount += allInAmt;
                        System.out.println(pl.getName() + " goes all-in with $" + (allInAmt));
                        if (pl.getCurrentBet() + allInAmt > highestBet) {
                            highestBet = allInAmt;
                        }
                        vChoice = true;
                        pl.allIn();
                        break;
                    default:
                        System.out.println("Invalid choice. Please choose again. OR NOT" + choice);
                        break;
                }
            }
        }
        checkForEndOfRound();
    }

    private void checkForEndOfRound() {
        long activePlrs = players.stream().filter(p -> !p.hasFolded()).count();
        if (activePlrs <= 1) {
            concludeRound();
        }
    }

    private void concludeRound() {
        for (PlayerWithWallet player : players) {
            if (!player.hasFolded()) {
                System.out.printf(player.getName() + " wins by default with $" + potAmount);
                player.addToWallet(potAmount);
                break; // Since the winner is found, no need to continue the loop
            }
        }
    }

    private void dealCCards(int numCards) {
        for (int i = 0; i < numCards; i++) {
            communityCards.add(deck.draw());  // Draw card from deck and add to community cards
        }
    }

    private void displayTableInfo() {
        System.out.println("=!@ Table Information @!=");
        System.out.println("Players at the table:");
        for (Player player : players) {
            if (player.hasFolded()) {
                System.out.println(player.getName() + ": Folded");
            } else {
                System.out.println(player.getName() + ": Active - " + player);
            }
        }
        // Assuming communityCards is a List<String>
        String cardsDisplay = String.join(", ", communityCards); // Joining strings directly
        System.out.println("Community Cards: " + cardsDisplay);
        System.out.println("Current Pot: $" + potAmount);
    }

    private void showdown() {
        System.out.println("\n*** SHOWDOWN ***");
        System.out.println("Community Cards: " + communityCards);

        players.forEach(player -> System.out.println(player.getName() + "'s Hand: " + player.getCards()));
    }

    private void determineWinner() {

        List<PlayerWithWallet> activePlayers = players.stream()
                .filter(p -> !p.hasFolded())
                .collect(Collectors.toList());
        PokerHand bestHand = null;
        List<PlayerWithWallet> winners = new ArrayList<>();

        for (PlayerWithWallet player : activePlayers) {
            PokerHand playerBestHand = PokerHandEvaluator.evaluateBestHand(player.getCards(), communityCards);
            if (bestHand == null || HandComparison.compareHands(playerBestHand, bestHand) > 0) {
                bestHand = playerBestHand;
                winners.clear();
                winners.add(player);
            } else if (HandComparison.compareHands(playerBestHand, bestHand) == 0) {
                winners.add(player);
            }
        }

        announceWinners(winners, bestHand);
        // Calculate winnings per winner
        int totalWinners = winners.size();
        int winningsPerWinner = potAmount / totalWinners;

        // Save winnings to winners' wallets and print out their winnings
        for (PlayerWithWallet winner : winners) {
            winner.addToWallet(winningsPerWinner);
            System.out.println(winner.getName() + " wins $" + winningsPerWinner);
        }
    }

    private void announceWinners(List<PlayerWithWallet> winners, PokerHand bestHand) {
        if (winners.size() > 1) {
            System.out.println("It's a tie!");
        }
        for (PlayerWithWallet winner : winners) {
            System.out.println(winner.getName() + " wins with a " + bestHand.getRank());
        }
    }

}
