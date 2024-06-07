
package game;

/**
 *
 * @author dexter
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class GameLogic {

    private int potAmount = 0;
    private int highestBet = 0;
    private List<String> communityCards = new ArrayList<>();
    private Deck deck = new Deck();
    private final PlayerDAO playerDAO;
    private final PlayerLoader playerLoader;
    private List<PlayerWithWallet> players;
    private BettingRoundGUI bettingRoundGUI;
    private PokerGameGUI pokerGameGUI;
    private int currentPlayerIndex = 0;
    private int roundNumber = 0;
    private Blind blinds;
    private int dPos = -2;
    
    /**
    * Constructs a new instance of the GameLogic class.
    */
    public GameLogic() 
    {
        // Initialize the database
        DatabaseUtil.initializeDatabase();
        
        // Initialize player DAO and player loader
        this.playerDAO = new PlayerDAO();
        this.playerLoader = new PlayerLoader();
        
        // Initialize players list, deck, blinds, and community cards
        this.players = new ArrayList<>();
        this.deck = new Deck();
        this.blinds = new Blind(0, 0);
        this.communityCards = new ArrayList<>();
        
        // Add shutdown hook to handle cleanup operations
        addShutdownHook();
    }
    
    /**
    * Sets the PokerGameGUI instance.
    * @param pokerGameGUI The PokerGameGUI instance to set.
    */
    public void setPokerGameGUI(PokerGameGUI pokerGameGUI)
    {
        this.pokerGameGUI = pokerGameGUI;
    }
    
    /**
    * Gets the highest bet.
    * 
    * @return The highest bet.
    */
    public int getHighestBet() 
    {
        return this.highestBet;
    }
    
    /**
    * Gets the blinds.
    * 
    * @return The blinds.
    */
    public Blind getBlinds()
    {
        return this.blinds;
    }
    
    /**
    * Updates the GUI with current player information.
    */
    private void updateGUI() 
    {
        if (bettingRoundGUI != null) 
        {
            bettingRoundGUI.updatePlayerDisplays(players);
        }
    }
    
    /**
    * Adds a shutdown hook to handle database cleanup on program termination.
    */
    private void addShutdownHook()
    {
        // Add a shutdown hook to gracefully shut down the database
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            try 
            {
                DriverManager.getConnection("jdbc:derby:;shutdown=true");
            } catch (SQLException e) {
                // Ignore expected exception on successful database shutdown
                if (!e.getSQLState().equals("XJ015")) {
                    e.printStackTrace(); // Log unexpected exceptions
                }
            }
        }));
    }
    
    /**
    * Resets the game state.
    */
    public void resetGame()
    {
        // Clear player list, reset deck and community cards, and reset game variables
        this.players.clear();
        this.deck = new Deck();
        this.communityCards.clear();
        this.currentPlayerIndex = 0;
        this.roundNumber = 0;
        this.potAmount = 0;
        this.highestBet = 0;
        this.dPos = -2;
    }
    
    /**
    * Sets up blinds and starts the betting round.
    * @param pokerGameGUI The PokerGameGUI instance.
    */
    public void BlindSetup(PokerGameGUI pokerGameGUI)
    {
        // Create a BlindsSetupGUI instance
        BlindsSetupGUI blindsGUI = new BlindsSetupGUI();
        // Check if small blind and big blind are set
        if (blindsGUI.getSmallBlind() > 0 && blindsGUI.getBigBlind() > 0)
        {
            // Check if small blind and big blind are set
            this.blinds = new Blind(blindsGUI.getSmallBlind(), blindsGUI.getBigBlind());
            deductBlinds(blinds.getSmallBlind(), blinds.getBigBlind());
            dealCardsToPlayers();
            dealCommunityCards();
            bettingRoundGUI = new BettingRoundGUI(this, pokerGameGUI);
            bettingRoundGUI.setVisible(true);
        } else {
            
            System.out.println("Game not started. Blinds not set.");
        }
    }

    public void initializeGame() {
        deck.shuffle();
        System.out.println("Loaded players: \n");
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

    public List<PlayerWithWallet> getAllPlayers() {
        // Load all players from the database
        return new PlayerLoader().loadPlayersWithWallet();
    }

    public PlayerWithWallet getPlayerByName(String name) {
        return getAllPlayers().stream()
                .filter(player -> player.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public void deletePlayer(String name) {
        // Delete player from the database
        new PlayerDAO().deletePlayer(name);
    }

    public void addNewPlayer(String playerName, int initialWallet) {
        PlayerWithWallet newPlayer = new PlayerWithWallet(playerName, initialWallet);

        try {
            playerDAO.addPlayer(newPlayer);
            PlayerWithWallet playerWithWallet = loadSinglePlayer(playerName);
            players.add(playerWithWallet);
        } catch (SQLException e) {
        }
    }

    public void updatePlayerWallet(String name, int newWallet) {
        // Update player's wallet in the database
        PlayerWithWallet player = getPlayerByName(name);
        if (player != null) {
            player.setWallet(newWallet);
            player.updatePlayerWalletDB();
        }
    }

    public void setPlayers(List<PlayerWithWallet> players) {
        this.players = players;
    }

    public List<PlayerWithWallet> getPlayers() {
        return players;
    }

    public Player getPlayer(int id) {
        try {
            return playerDAO.getPlayer(id);
        } catch (SQLException e) {
        }
        return null;
    }

    public PlayerWithWallet getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    private List<PlayerWithWallet> loadPlayerWallets() {
        return playerLoader.loadPlayersWithWallet();
    }

    private PlayerWithWallet loadSinglePlayer(String playerName) throws SQLException {
        String sql = "SELECT * FROM Player WHERE name = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    private void deductBlinds(int sBlind, int bBlind) {
        dPos = (dPos + 1) % players.size(); // Update dealer position
        int sBP = (dPos + 1) % players.size(); // Small blind position
        int bBP = (dPos + 2) % players.size(); // Big blind position

        PlayerWithWallet smallBlindPlayer = players.get(sBP); // Small blind player
        PlayerWithWallet bigBlindPlayer = players.get(bBP); // Big blind player

        // Deduct small blind
        int aSB = smallBlindPlayer.getWallet() >= sBlind ? sBlind : smallBlindPlayer.getWallet(); // Actual small blind
        smallBlindPlayer.subtractFromWallet(aSB);
        potAmount += aSB;
        System.out.println(smallBlindPlayer.getName() + " posts small blind of $" + aSB);

        // Deduct big blind
        int aBB = bigBlindPlayer.getWallet() >= bBlind ? bBlind : bigBlindPlayer.getWallet(); // Actual big blind
        bigBlindPlayer.subtractFromWallet(aBB);
        potAmount += aBB;
        System.out.println(bigBlindPlayer.getName() + " posts big blind of $" + aBB);

        smallBlindPlayer.setCurrentBet(aSB);
        bigBlindPlayer.setCurrentBet(aSB);
        highestBet = aBB;
        // Update the GUI to reflect the new wallet balances
        updateGUI();

        if (players.size() > 2) {
            currentPlayerIndex = +2;
        }
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
                player.updatePlayerWalletDB();
                bettingRoundGUI.displayWinner(winnerMessage);
                break; // Since the winner is found, no need to continue the loop
            }
        }
        updateAllPlayerWalletsInDB();
        resetGame();
        bettingRoundGUI.dispose();
    }

    private void progressToNextRound() {
        roundNumber++;
        switch (roundNumber) {
            case 1 -> {
                // Post-Flop round
                checkForEndOfRound();
                displayTableInfo();
                // No additional cards, just proceed to the next round of betting
            }
            case 2 -> {
                // Turn round
                checkForEndOfRound();
                displayTableInfo();
                communityCards.addAll(deck.dealCards(1)); // Deal the Turn card
            }
            case 3 -> {
                // River round
                checkForEndOfRound();
                displayTableInfo();
                communityCards.addAll(deck.dealCards(1)); // Deal the River card
            }
            default -> {
                // All rounds completed
                determineWinner();
//                endGame();
                return;
            }
        }
        currentPlayerIndex = 0;
        showCurrentPlayerTurn();
    }

    public void displayTableInfo() {

        StringBuilder info = new StringBuilder("=!@ Table Information @!=\nPlayers at the table:\n");
        for (PlayerWithWallet player : players) {
            if (player.hasFolded()) {
                info.append(player.getName()).append(": Folded\n");
            } else {
                info.append(player.getName())
                        .append(": Active - Wallet: $")
                        .append(player.getWallet())
                        .append(", Cards: ")
                        .append(String.join(", ", player.getCards()))
                        .append("\n");
            }
        }
        String communityCardsDisplay = String.join(", ", communityCards);
        info.append("Community Cards: ").append(communityCardsDisplay).append("\n");
        info.append("Current Pot: $").append(potAmount).append("\n");

        if (bettingRoundGUI != null) {
            bettingRoundGUI.appendMessage(info.toString());
        } else {
            System.out.println(info);
        }
    }

    public void showdown() {
        StringBuilder showdownInfo = new StringBuilder("\n*** SHOWDOWN ***\nCommunity Cards: ").append(communityCards).append("\n");

        for (Player player : players) {
            showdownInfo.append(player.getName()).append("'s Hand: ").append(player.getCards()).append("\n");
        }

        if (bettingRoundGUI != null) {
            bettingRoundGUI.appendMessage(showdownInfo.toString());
        } else {
            System.out.println(showdownInfo);
        }
    }

   /**
    * Determines the winner(s) of the poker game.
    */
    private void determineWinner() 
    {
       // Filter active players (players who haven't folded)
       List<PlayerWithWallet> activePlayers = players.stream()
               .filter(p -> !p.hasFolded())
               .collect(Collectors.toList());

       // Initialize variables to store the best hand and the winners
       PokerHand bestHand = null;
       List<PlayerWithWallet> winners = new ArrayList<>();

       // Iterate through active players to find the best hand and determine winners
       for (PlayerWithWallet player : activePlayers)
       {
           // Evaluate the best hand for the player
           PokerHand playerBestHand = PokerHandEvaluator.evaluateBestHand(player.getCards(), communityCards);

           // Compare player's best hand with the current best hand
           if (bestHand == null || HandComparison.compareHands(playerBestHand, bestHand) > 0) 
           {
               // If player's hand is better, update the best hand and clear the winners list
               bestHand = playerBestHand;
               winners.clear();
               winners.add(player);
           } else if (HandComparison.compareHands(playerBestHand, bestHand) == 0) {
               // If player's hand ties with the best hand, add the player to the winners list
               winners.add(player);
           }
       }

       // Announce the winners
       announceWinners(winners, bestHand);
    }


    /**
     * Announces the winners of the poker game and updates their wallet balances.
     * 
     * @param winners   The list of winners.
     * @param bestHand  The best hand among the winners.
     */
     public void announceWinners(List<PlayerWithWallet> winners, PokerHand bestHand)
     {
        // Prepare the message to announce the winners
        StringBuilder winnerMessage = new StringBuilder("Winner(s): \n");
        for (PlayerWithWallet winner : winners) 
        {
            // Update each winner's wallet balance and build the winner message
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
            // Update GUI on the event dispatch thread to avoid race conditions
            javax.swing.SwingUtilities.invokeLater(() -> {
                bettingRoundGUI.displayWinner(winnerMessage.toString());
                // Defer the reset operation to avoid race conditions
                bettingRoundGUI.scheduleReset();
            });
        } else 
        {
            // Print an error message if GUI is not initialized
            System.err.println("GUI not initialized.");
        }

        // Update wallet balances of all players in the database
        updateAllPlayerWalletsInDB();
    }

    
    /**
    * Updates the wallet balances of all players in the database.
    */
    private void updateAllPlayerWalletsInDB() 
    {
        // Iterate through all players
        for (PlayerWithWallet player : players)
        {
        // Update the player's wallet balance in the database
        player.updatePlayerWalletDB();
        }
    }
}
