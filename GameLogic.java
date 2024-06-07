
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
            // Set blinds, deduct blinds from players, deal cards, and start the betting round
            this.blinds = new Blind(blindsGUI.getSmallBlind(), blindsGUI.getBigBlind());
            deductBlinds(blinds.getSmallBlind(), blinds.getBigBlind());
            
            bettingRoundGUI = new BettingRoundGUI(this, pokerGameGUI);
            bettingRoundGUI.setVisible(true);
        } else {
            // Display a message if blinds are not set
            System.out.println("Game not started. Blinds not set.");
        }
    }
    
    /**
    * Initializes the game by shuffling the deck, printing player details, and updating the GUI.
    */
    public void initializeGame()
    {
        // Shuffle the deck
        deck.shuffle();
        // Print player details
        System.out.println("Loaded players: \n");
        for (PlayerWithWallet player : players) {
            System.out.println("Name: " + player.getName() + ", Wallet: " + player.getWallet() + ", Cards: " + player.getCards());
        }
        // Update the GUI with current player information
        updateGUI();
    }
    
    /**
    * Deals two cards to each player from the deck.
    */
    public void dealCardsToPlayers()
    {
        // Iterate through all players
        for (PlayerWithWallet player : players)
        {
            // Deal two cards to the player from the deck
            List<String> playerCards = deck.dealCards(2);
            player.setCards(playerCards);
        }
    }

    /**
     * Deals three community cards (Flop) from the deck.
     */
    public void dealCommunityCards()
    {
        // Deal the first three community cards (Flop) and add them to the community cards list
        communityCards.addAll(deck.dealCards(3));
    }

    /**
     * Gets a string representation of the community cards.
     * @return A string representing the community cards.
     */
    public String getCommunityCards() 
    {
        // Return a string representation of the community cards
        return "Community Cards: " + String.join(", ", communityCards);
    }

    /**
     * Retrieves all players from the database 
     * @return A list of all players with wallets.
     */
    public List<PlayerWithWallet> getAllPlayers() 
    {
        // Load all players with wallets from the database
        return new PlayerLoader().loadPlayersWithWallet();
    }

    /**
     * Retrieves a player by name from the list of all players. 
     * @param name The name of the player to retrieve.
     * @return The player with the specified name, or null if not found.
     */
    public PlayerWithWallet getPlayerByName(String name) 
    {
        // Find and return the player with the specified name
        return getAllPlayers().stream()
                .filter(player -> player.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Deletes a player from the database by name.
     * @param name The name of the player to delete.
     */
    public void deletePlayer(String name) 
    {
        // Delete the player from the database
        new PlayerDAO().deletePlayer(name);
    }

    /**
     * Adds a new player with the specified name and initial wallet balance.
     * @param playerName The name of the new player.
     * @param initialWallet The initial wallet balance of the new player.
     */
    public void addNewPlayer(String playerName, int initialWallet)
    {
        // Create a new player instance
        PlayerWithWallet newPlayer = new PlayerWithWallet(playerName, initialWallet);
        try 
        {
            // Add the new player to the database and to the list of players
            playerDAO.addPlayer(newPlayer);
            PlayerWithWallet playerWithWallet = loadSinglePlayer(playerName);
            players.add(playerWithWallet);
        } catch (SQLException e) 
        {
            // Handle SQL exception if needed
        }
    }

    /**
     * Updates the wallet balance of a player with the specified name.
     * 
     * @param name The name of the player whose wallet balance to update.
     * @param newWallet The new wallet balance to set for the player.
     */
    public void updatePlayerWallet(String name, int newWallet)
    {
        // Find the player by name and update their wallet balance in the database
        PlayerWithWallet player = getPlayerByName(name);
        if (player != null) 
        {
            player.setWallet(newWallet);
            player.updatePlayerWalletDB();
        }
    }

    /**
     * Sets the list of players to the specified list.
     * @param players The list of players to set.
     */
    public void setPlayers(List<PlayerWithWallet> players)
    {
        this.players = players;
    }

    /**
     * Gets the list of players. 
     * @return The list of players.
     */
    public List<PlayerWithWallet> getPlayers()
    {
        return this.players;
    }

    /**
     * Gets the player with the specified ID.
     * @param id The ID of the player to retrieve.
     * @return The player with the specified ID, or null if not found.
     */
    public Player getPlayer(int id) 
    {
        try
        {
            return playerDAO.getPlayer(id);
        } catch (SQLException e) 
        {
            // Handle SQL exception if needed
        }
        return null;
    }

    /**
     * Gets the current player.
     * @return The current player.
     */
    public PlayerWithWallet getCurrentPlayer()
    {
        return players.get(currentPlayerIndex);
    }

  
    /**
    * Loads a single player from the database by their name.
    * @param playerName The name of the player to load.
    * @return The loaded player with wallet details.
    * @throws SQLException If an SQL exception occurs during database access.
    */
    private PlayerWithWallet loadSinglePlayer(String playerName) throws SQLException
    {
        // SQL query to retrieve player information by name
        String sql = "SELECT * FROM Player WHERE name = ?";
        
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            // Set the player name parameter in the prepared statement
            stmt.setString(1, playerName);
            try (ResultSet rs = stmt.executeQuery()) 
            {
                // If player exists in the database, retrieve their details and create a PlayerWithWallet object
                if (rs.next()) 
                {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int wallet = rs.getInt("wallet");
                    return new PlayerWithWallet(id, name, wallet);
                }
            }
        }
        // Throw an SQL exception if player not found
        throw new SQLException("Player not found");
    }

    /**
    * Deducts small blind and big blind amounts from players.
    * @param sBlind The amount of the small blind.
    * @param bBlind The amount of the big blind.
    */
   private void deductBlinds(int sBlind, int bBlind) 
   {
       // Update dealer position
       dPos = (dPos + 1) % players.size(); 

       // Calculate small blind position and big blind position
       int sBP = (dPos + 1) % players.size(); 
       int bBP = (dPos + 2) % players.size(); 

       // Get small blind player and big blind player
       PlayerWithWallet smallBlindPlayer = players.get(sBP); 
       PlayerWithWallet bigBlindPlayer = players.get(bBP); 

       // Deduct small blind
       int aSB = Math.min(smallBlindPlayer.getWallet(), sBlind); 
       smallBlindPlayer.deductFromWallet(aSB);
       potAmount += aSB;
       System.out.println(smallBlindPlayer.getName() + " posts small blind of $" + aSB);

       // Deduct big blind
       int aBB = Math.min(bigBlindPlayer.getWallet(), bBlind); 
       bigBlindPlayer.deductFromWallet(aBB);
       potAmount += aBB;
       System.out.println(bigBlindPlayer.getName() + " posts big blind of $" + aBB);

       // Set current bet for small blind player and big blind player
       smallBlindPlayer.setCurrentBet(aSB);
       bigBlindPlayer.setCurrentBet(aSB);
       highestBet = aBB;

       // Update the GUI to reflect the new wallet balances
       updateGUI();

       // Skip players if more than two players are present
       if (players.size() > 2) 
       {
           currentPlayerIndex += 2;
       }
   }


    /**
     * Shows the current player's turn and updates the GUI.
     */
    public void showCurrentPlayerTurn()
    {
        PlayerWithWallet currentPlayer = players.get(currentPlayerIndex);
        System.out.println("It's " + currentPlayer.getName() + "'s turn.");
        // Update the GUI with the current player's information
        BettingRoundGUI.updateCurrentPlayerDisplay(currentPlayer.getName(), currentPlayer.getCards(), communityCards, currentPlayer.getWallet());
    }

    /**
     * Retrieves the current player's cards. 
     * @return A string representation of the current player's cards.
     */
    public String getCurrentPlayerCards() 
    {
        PlayerWithWallet currentPlayer = players.get(currentPlayerIndex);
        return "Cards: " + currentPlayer.getCards();
    }

    /**
     * Handles the fold action for the current player.
     */
    public void fold() 
    {
        PlayerWithWallet currentPlayer = players.get(currentPlayerIndex);
        currentPlayer.fold();
        System.out.println(currentPlayer.getName() + " folds.");
        moveToNextPlayer();
    }

    /**
     * Handles the call/check action for the current player.
     */
    public void callCheck()
    {
        PlayerWithWallet currentPlayer = players.get(currentPlayerIndex);
        int callAmt = highestBet - currentPlayer.getCurrentBet();
        if (currentPlayer.getWallet() >= callAmt) {
            currentPlayer.deductFromWallet(callAmt);
            potAmount += callAmt;
            currentPlayer.setCurrentBet(highestBet);
            System.out.println(currentPlayer.getName() + " calls and adds $" + callAmt + " to the pot.");
            moveToNextPlayer();
        } else
        {
            System.out.println("Not enough funds to call. Consider going all-in or folding.");
            bettingRoundGUI.showError("Not enough funds to call. Consider going all-in or folding.");
        }
    }

    /**
     * Handles the raise action for the current player.
     * @param amount The amount to raise.
     */
    public void raise(int amount)
    {
        PlayerWithWallet currentPlayer = players.get(currentPlayerIndex);
        int raiseAmt = amount - currentPlayer.getCurrentBet();
        if (currentPlayer.getWallet() >= raiseAmt) {
            currentPlayer.deductFromWallet(raiseAmt);
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
    /**
     * Handles the all-in action for the current player.
     */
    public void allIn() 
    {
        PlayerWithWallet currentPlayer = players.get(currentPlayerIndex);
        int allInAmt = currentPlayer.getWallet();
        currentPlayer.deductFromWallet(allInAmt);
        potAmount += allInAmt;
        currentPlayer.setCurrentBet(currentPlayer.getCurrentBet() + allInAmt);
        System.out.println(currentPlayer.getName() + " goes all-in with $" + allInAmt);
        if (currentPlayer.getCurrentBet() > highestBet) 
        {
            highestBet = currentPlayer.getCurrentBet();
        }
        currentPlayer.allIn();
        moveToNextPlayer();
    }

    /**
     * Moves to the next active player in the round.
     * If all players have completed their turn, proceeds to the next round.
     */
    public void moveToNextPlayer()
    {
        // Move to the next player who has not folde
        do 
        {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (players.get(currentPlayerIndex).hasFolded());
        
        // If all players have completed their turn, proceed to the next round
        if (currentPlayerIndex == 0)
        {
            progressToNextRound();
        } else 
        {
            // Show the turn of the next active player
            showCurrentPlayerTurn();
        }
        // Check if the current round has ended
        checkForEndOfRound();
    }
    
    /**
    * Checks if the current round has ended.
    * If all players cannot act (folded or all-in), concludes the round.
    * If there's only one active player remaining, concludes the round.
    */
    private void checkForEndOfRound() 
    {
        // Check if all players have folded or are all-in
        if (allPlayersCannotAct()) 
        {
            System.out.println("All players have folded or are all-in. Moving to the next phase.");
            concludeRound();
        } else 
        {
             // Count the number of active players remaining
            long activePlrs = players.stream().filter(p -> !p.hasFolded()).count();
            System.out.println("Active players remaining: " + activePlrs);
            // If there's only one active player remaining, conclude the round
            if (activePlrs <= 1)
            {
                concludeRound();
            }
        }
    }

    /**
    * Checks if all players cannot act in the current round.
    * @return true if all players have folded or are all-in, false otherwise.
    */
   private boolean allPlayersCannotAct()
   {
       // Check if all players have folded or are all-in
       return players.stream().allMatch(p -> p.hasFolded() || p.isAllIn());
   }

    /**
    * Ends the current round of the game.
    * Determines the winner if only one player is left.
    * Updates player wallets and prepares for the next round.
    */
    public void concludeRound() 
    {
        for (PlayerWithWallet player : players)
        {
            // Determine the winner if only one player remains active
            if (!player.hasFolded())
            {
                String winnerMessage = String.format("%s wins by default with $%d", player.getName(), potAmount);
                // Add pot amount to the winner's wallet
                System.out.println(winnerMessage);
                player.addToWallet(potAmount);
                player.updatePlayerWalletDB();
                 // Display winner message
                bettingRoundGUI.displayWinner(winnerMessage);
                break; // Since the winner is found, no need to continue the loop
            }
        }
        // Update player wallets and reset the game
        updateAllPlayerWalletsInDB();
        resetGame();
        // Close the betting round GUI
        bettingRoundGUI.dispose();
    }
    
    /**
    * Moves to the next round of the game.
    * Deals additional community cards and determines the winner if all rounds are completed.
    */
    private void progressToNextRound() 
    {
        roundNumber++;
        switch (roundNumber) 
        {
            case 1 -> {
                // Post-Flop round
                dealCardsToPlayers();
                dealCommunityCards();
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
    
     private String currentRound() {
        switch (roundNumber) {
            case 1:
                return "POST-FLOP ROUND";
            case 2:
                return "TURN ROUND";
            case 3:
                return "RIVER ROUND";

            default:
                return "PRE-FLOP ROUND";
        }
    }


    /**
    * Displays the current state of the table, including player status, wallets, and cards.
    */
    public void displayTableInfo() 
    {
        StringBuilder info = new StringBuilder(currentRound() + "\n\n=!@ Table Information @!=\nPlayers at the table:\n\n");
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
        info.append("\nCommunity Cards: ").append(communityCardsDisplay).append("\n");
        info.append("Current Pot: $").append(potAmount).append("\n");

        if (bettingRoundGUI != null) {
            bettingRoundGUI.appendMessage(info.toString());
        } else {
            System.out.println(info);
        }
    }
    
    /**
    * Displays showdown information, including community cards and player hands.
    */
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
    public void determineWinner() 
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
