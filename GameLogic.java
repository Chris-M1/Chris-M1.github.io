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
    
    Scanner scanner = new Scanner(System.in);
    private int smallBlind;
    private int bigBlind;
    private int potAmount = 0;
    private int highestBet;
    private List<String> communityCards = new ArrayList<>();
    private Deck deck = new Deck();
    private PlayerDAO playerDAO;
    private PlayerLoader playerLoader;
    private List<PlayerWithWallet> players;
    private BettingRoundGUI bettingRoundGUI;
    private int currentPlayerIndex = 1;
    private int roundNumber = 0;
    
    public GameLogic() {
        DatabaseUtil.initializeDatabase(); // Initialize the database
        this.playerDAO = new PlayerDAO();
        this.playerLoader = new PlayerLoader();
        this.players = new ArrayList<>();
        this.deck = new Deck();
        this.communityCards = new ArrayList<>();
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
        startBettingRound();
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

    public void initializeGame() {
        players = loadPlayerWallets();
        deck.shuffle();
        dealCardsToPlayers();
        dealCommunityCards();
        System.out.println("Loaded players: ");
        for (PlayerWithWallet player : players) {
            System.out.println("Name: " + player.getName() + ", Wallet: " + player.getWallet() + ", Cards: " + player.getCards());
        }
    }
    
    private void dealCardsToPlayers() {
        for (PlayerWithWallet player : players) {
            List<String> playerCards = deck.dealCards(2);
            player.setCards(playerCards);
        }
    }
    
    private void dealCommunityCards() {
        communityCards.addAll(deck.dealCards(3)); // Deal the first three community cards (Flop)
    }
      
    public String getCommunityCards() {
        return "Community Cards: " + String.join(", ", communityCards);
    }

    public void addNewPlayer(String playerName, int initialWallet, boolean isAI) {
        PlayerWithWallet newPlayer = new PlayerWithWallet(playerName, initialWallet, isAI);

        try {
            playerDAO.addPlayer(newPlayer);
            PlayerWithWallet playerWithWallet = loadSinglePlayer(playerName);
            players.add(playerWithWallet);
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
    
    public List<PlayerWithWallet> getPlayers() {
        return players;
    }
    
    private List<PlayerWithWallet> loadPlayerWallets() {
        return playerLoader.loadPlayersWithWallet();
    }
    
    public void savePlayerWallets() {
        PlayerWalletWriter.writeToFile(players); // Save player information to file
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
                    int wallet = rs.getInt("wallet");
                    boolean isAI = rs.getBoolean("isAI");
                    return new PlayerWithWallet(id, name, wallet, isAI);
                }
            }
        }
        throw new SQLException("Player not found");
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

                // Add the new player to the 
               
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

//    private void dealHands() {
//        for (Player player : players) {
//            player.receiveCard(deck.draw());
//            player.receiveCard(deck.draw());
//        }
//    }

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
    
    public void startBettingRound() {
    javax.swing.SwingUtilities.invokeLater(() -> {
        if (bettingRoundGUI == null) {  // Check if it's not already initialized
            bettingRoundGUI = new BettingRoundGUI(this);  // Initialize at the class level
        }
        bettingRoundGUI.setVisible(true);
    });
}
    
    public void showCurrentPlayerTurn() {
        PlayerWithWallet currentPlayer = players.get(currentPlayerIndex);
        System.out.println("It's " + currentPlayer.getName() + "'s turn.");
        // Update the GUI with the current player's information
        BettingRoundGUI.updateCurrentPlayerDisplay(currentPlayer.getName(), currentPlayer.getCards(), communityCards);
    }
    
    private void handleAITurn(PlayerWithWallet aiPlayer) {
        // Basic AI logic for now: bet a random amount, check, or fold
        int action = (int) (Math.random() * 3);
        switch (action) {
            case 0:
                placeBet((int) (Math.random() * aiPlayer.getWallet()));
                break;
            case 1:
                check();
                break;
            case 2:
                fold();
                break;
        }
    }

    public String getCurrentPlayerCards() {
        PlayerWithWallet currentPlayer = players.get(currentPlayerIndex);
        return "Cards: " + currentPlayer.getCards();
    }

    
    
    public void placeBet(int amount) {
        PlayerWithWallet currentPlayer = players.get(currentPlayerIndex);
        currentPlayer.setCurrentBet(amount);
        currentPlayer.setWallet(currentPlayer.getWallet() - amount);
        System.out.println(currentPlayer.getName() + " placed a bet of " + amount);
        moveToNextPlayer();
    }

    public void check() {
        PlayerWithWallet currentPlayer = players.get(currentPlayerIndex);
        System.out.println(currentPlayer.getName() + " checked.");
        moveToNextPlayer();
    }

    public void fold() {
        PlayerWithWallet currentPlayer = players.get(currentPlayerIndex);
        System.out.println(currentPlayer.getName() + " folded.");
        moveToNextPlayer();
    }

    private void moveToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        if (currentPlayerIndex == 0) {
            // All players have completed their turn, proceed to the next round
            progressToNextRound();
        } else {
            showCurrentPlayerTurn();
        }
    }
    
    private void progressToNextRound() {
        roundNumber++;
        switch (roundNumber) {
            case 1:
                // Post-Flop round
                // No additional cards, just proceed to the next round of betting
                break;
            case 2:
                // Turn round
                communityCards.addAll(deck.dealCards(1)); // Deal the Turn card
                break;
            case 3:
                // River round
                communityCards.addAll(deck.dealCards(1)); // Deal the River card
                break;
            default:
                // All rounds completed
                determineWinner();
//                endGame();
                return;
        }
        currentPlayerIndex = 0;
        showCurrentPlayerTurn();
    }
    
   private void endGame() {
    // Optionally close the betting GUI or reset the game state
    javax.swing.SwingUtilities.invokeLater(() -> bettingRoundGUI.dispose()); // Close the GUI
    System.out.println("Game over. Resetting the game...");
    // Reset logic here if necessary
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
//            communityCards.add(deck.draw());  // Draw card from deck and add to community cards
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
}

public void announceWinners(List<PlayerWithWallet> winners, PokerHand bestHand) {
    StringBuilder winnerMessage = new StringBuilder("Winner(s): \n");
    for (PlayerWithWallet winner : winners) {
        winnerMessage.append(winner.getName()).append(" with ").append(bestHand).append(", wins $").append(winner.getWallet()).append("\n");
    }
    // Send winner information to the GUI to be displayed
    if (bettingRoundGUI != null) {
    javax.swing.SwingUtilities.invokeLater(() -> bettingRoundGUI.dispose());
    javax.swing.SwingUtilities.invokeLater(() -> bettingRoundGUI.displayWinners(winnerMessage.toString()));
} else {
    System.err.println("GUI not initialized.");
}
}
//    private void determineWinner() {
//
//        List<PlayerWithWallet> activePlayers = players.stream()
//                .filter(p -> !p.hasFolded())
//                .collect(Collectors.toList());
//        PokerHand bestHand = null;
//        List<PlayerWithWallet> winners = new ArrayList<>();
//
//        for (PlayerWithWallet player : activePlayers) {
//            PokerHand playerBestHand = PokerHandEvaluator.evaluateBestHand(player.getCards(), communityCards);
//            if (bestHand == null || HandComparison.compareHands(playerBestHand, bestHand) > 0) {
//                bestHand = playerBestHand;
//                winners.clear();
//                winners.add(player);
//            } else if (HandComparison.compareHands(playerBestHand, bestHand) == 0) {
//                winners.add(player);
//            }
//        }
//
//        announceWinners(winners, bestHand);
//        // Calculate winnings per winner
//        int totalWinners = winners.size();
//        int winningsPerWinner = potAmount / totalWinners;
//
//        // Save winnings to winners' wallets and print out their winnings
//        for (PlayerWithWallet winner : winners) {
//            winner.addToWallet(winningsPerWinner);
//            System.out.println(winner.getName() + " wins $" + winningsPerWinner);
//        }
//    }

//    private void announceWinners(List<PlayerWithWallet> winners, PokerHand bestHand) {
//        if (winners.size() > 1) {
//            System.out.println("It's a tie!");
//        }
//        for (PlayerWithWallet winner : winners) {
//            System.out.println(winner.getName() + " wins with a " + bestHand.getRank());
//        }
//    }

}