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
 
    private int smallBlind;
    private int bigBlind;
    private int potAmount = 0;
    private int highestBet = 0;
    private List<String> communityCards = new ArrayList<>();
    private Deck deck = new Deck();
    private PlayerDAO playerDAO;
    private PlayerLoader playerLoader;
    private List<PlayerWithWallet> players;
    private BettingRoundGUI bettingRoundGUI;
    private int currentPlayerIndex = 1;
    private int roundNumber = 0;
    private Blind blinds;
    private int dPos = -1;
    
    public GameLogic() {
        DatabaseUtil.initializeDatabase(); // Initialize the database
        this.playerDAO = new PlayerDAO();
        this.playerLoader = new PlayerLoader();
        this.players = new ArrayList<>();
        this.deck = new Deck();
        this.blinds = new Blind(0, 0); 
        this.communityCards = new ArrayList<>();
        addShutdownHook();
    }
    
    public Blind getBlinds() {
    return this.blinds;
    }

   private void updateGUI() {
    if (bettingRoundGUI != null) {
        bettingRoundGUI.updatePlayerDisplays(players);
    }
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
        BlindSetup();
        // Betting rounds
        preFBetting();
        postFBetting();
        pTBetting();
        pRBetting();

        showdown();        // Showdown to find the winner
        determineWinner(); // Determine and handle winner

        System.out.println("Game ended. Thank you for playing!");
    }
    
    public void BlindSetup() {
    BlindsSetupGUI blindsGUI = new BlindsSetupGUI();
    if (blindsGUI.getSmallBlind() > 0 && blindsGUI.getBigBlind() > 0) {
        this.blinds = new Blind(blindsGUI.getSmallBlind(), blindsGUI.getBigBlind());
        bettingRoundGUI = new BettingRoundGUI(this);
        bettingRoundGUI.setVisible(true);
        
        
        startBettingRound(); // Start the first betting round
    } else {
        System.out.println("Game not started. Blinds not set.");
    }
}

    public void initializeGame() {
        players = loadPlayerWallets();
        deck.shuffle();
        dealCardsToPlayers();
        dealCommunityCards();
        deductBlinds();
        System.out.println("Loaded players: ");
        for (PlayerWithWallet player : players) {
            System.out.println("Name: " + player.getName() + ", Wallet: " + player.getWallet() + ", Cards: " + player.getCards());
        }
        updateGUI();
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
        PlayerWithWallet newPlayer = new PlayerWithWallet(playerName, initialWallet);

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
    
    public PlayerWithWallet getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
    
    public List<PlayerWithWallet> getPlayers() {
        return players;
    }
    
    private List<PlayerWithWallet> loadPlayerWallets() {
        return playerLoader.loadPlayersWithWallet();
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
                    return new PlayerWithWallet(id, name, wallet);
                }
            }
        }
        throw new SQLException("Player not found");
    }



    private void deductBlinds() {
        dPos = (dPos + 1) % players.size(); // Update dealer position
        int sBP = (dPos + 1) % players.size(); // Small blind position
        int bBP = (dPos + 2) % players.size(); // Big blind position
        
        PlayerWithWallet sBPl = players.get(sBP); // Small blind player
        PlayerWithWallet bBPl = players.get(bBP); // Big blind player

        // Deduct small blind
        int aSB = sBPl.getWallet() >= smallBlind ? smallBlind : sBPl.getWallet(); // Actual small blind
        sBPl.subtractFromWallet(aSB);
        potAmount += aSB;
        System.out.println(sBPl.getName() + " posts small blind of $" + aSB);

        // Deduct big blind
        int aBB = bBPl.getWallet() >= bigBlind ? bigBlind : bBPl.getWallet(); // Actual big blind
        bBPl.subtractFromWallet(aBB);
        potAmount += aBB;
        System.out.println(bBPl.getName() + " posts big blind of $" + aBB);

        // Update the GUI to reflect the new wallet balances
        updateGUI();
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
        BettingRoundGUI.updateCurrentPlayerDisplay(currentPlayer.getName(), currentPlayer.getCards(), communityCards, currentPlayer.getWallet());
    }

    public String getCurrentPlayerCards() {
        PlayerWithWallet currentPlayer = players.get(currentPlayerIndex);
        return "Cards: " + currentPlayer.getCards();
    }
    
    public void fold() {
        PlayerWithWallet currentPlayer = players.get(currentPlayerIndex);
        currentPlayer.fold();
        System.out.println(currentPlayer.getName() + " folds.");
        moveToNextPlayer();
    }

    public void callCheck() {
        PlayerWithWallet currentPlayer = players.get(currentPlayerIndex);
        int callAmt = highestBet - currentPlayer.getCurrentBet();
        if (currentPlayer.getWallet() >= callAmt) {
            currentPlayer.subtractFromWallet(callAmt);
            potAmount += callAmt;
            currentPlayer.setCurrentBet(highestBet);
            System.out.println(currentPlayer.getName() + " calls and adds $" + callAmt + " to the pot.");
            moveToNextPlayer();
        } else {
            System.out.println("Not enough funds to call. Consider going all-in or folding.");
            bettingRoundGUI.showError("Not enough funds to call. Consider going all-in or folding.");
        }
    }

    public void raise(int amount) {
        PlayerWithWallet currentPlayer = players.get(currentPlayerIndex);
        int raiseAmt = amount - currentPlayer.getCurrentBet();
        if (currentPlayer.getWallet() >= raiseAmt) {
            currentPlayer.subtractFromWallet(raiseAmt);
            potAmount += raiseAmt;
            currentPlayer.setCurrentBet(amount);
            highestBet = amount;
            System.out.println(currentPlayer.getName() + " raises to $" + amount);
            moveToNextPlayer();
        } else {
            System.out.println("Not enough funds to raise. Consider going all-in or folding.");
            bettingRoundGUI.showError("Not enough funds to raise. Consider going all-in or folding.");
        }
    }

    public void allIn() {
        PlayerWithWallet currentPlayer = players.get(currentPlayerIndex);
        int allInAmt = currentPlayer.getWallet();
        currentPlayer.subtractFromWallet(allInAmt);
        potAmount += allInAmt;
        currentPlayer.setCurrentBet(currentPlayer.getCurrentBet() + allInAmt);
        System.out.println(currentPlayer.getName() + " goes all-in with $" + allInAmt);
        if (currentPlayer.getCurrentBet() > highestBet) {
            highestBet = currentPlayer.getCurrentBet();
        }
        currentPlayer.allIn();
        moveToNextPlayer();
    }

     public void moveToNextPlayer() {
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (players.get(currentPlayerIndex).hasFolded());

        if (currentPlayerIndex == 0) {
            // All players have completed their turn, proceed to the next round
            progressToNextRound();
        } else {
            showCurrentPlayerTurn();
        }

        checkForEndOfRound();
    }
     
     private void checkForEndOfRound() {
        if (allPlayersCannotAct()) {
            System.out.println("All players have folded or are all-in. Moving to the next phase.");
            concludeRound();
        } else {
            long activePlrs = players.stream().filter(p -> !p.hasFolded()).count();
            System.out.println("Active players remaining: " + activePlrs);
            if (activePlrs <= 1) {
                concludeRound();
            }
        }
    }
     
      private boolean allPlayersCannotAct() {
        return players.stream().allMatch(p -> p.hasFolded() || p.isAllIn());
    }

    private void concludeRound() {
    for (PlayerWithWallet player : players) {
        if (!player.hasFolded()) {
            String winnerMessage = String.format("%s wins by default with $%d", player.getName(), potAmount);
            System.out.println(winnerMessage);
            player.addToWallet(potAmount);
            bettingRoundGUI.displayWinner(winnerMessage);
            break; // Since the winner is found, no need to continue the loop
        }
    }
}
    
    private void progressToNextRound() {
        roundNumber++;
        switch (roundNumber) {
            case 1:
                // Post-Flop round
                checkForEndOfRound();
                displayTableInfo();
                // No additional cards, just proceed to the next round of betting
                break;
            case 2:
                // Turn round
                checkForEndOfRound();
                displayTableInfo();
                communityCards.addAll(deck.dealCards(1)); // Deal the Turn card
                break;
            case 3:
                // River round
                checkForEndOfRound();
                displayTableInfo();
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
            winner.addToWallet(potAmount);
            winnerMessage.append(winner.getName())
                .append(" with ")
                .append(bestHand.getRank())
                .append(", wins $")
                .append(potAmount)
                .append("\n");
        }

        // Send winner information to the GUI to be displayed
        if (bettingRoundGUI != null) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                bettingRoundGUI.dispose();
                bettingRoundGUI.displayWinner(winnerMessage.toString());
            });
        } else {
            System.err.println("GUI not initialized.");
        }
    }
}