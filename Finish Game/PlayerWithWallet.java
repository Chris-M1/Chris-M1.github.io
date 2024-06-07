
package game;

/**
 *
 * @author chris
 */
import java.util.ArrayList;
import java.util.List;

public class PlayerWithWallet extends Player
{

    private int wallet;// Stores the player's current money amount
    private int currentBet;     // Tracks the current bet amount for the player within a round 
    private PlayerDAO playerDAO;
    private List<String> cards;

    /**
    * Constructs a new PlayerWithWallet object with the specified ID, name, and wallet balance.
    *
    * @param id the unique identifier for the player
    * @param name the name of the player
    * @param wallet the initial balance in the player's wallet
    */
    public PlayerWithWallet(int id, String name, int wallet)
    {
        super(id, name, wallet);
        this.wallet = wallet;
        this.playerDAO = new PlayerDAO();
        this.cards = new ArrayList<>();
    }
    
    /**
    * Creates a new player with a name and wallet balance.
    *
    * @param name   The player's name.
    * @param wallet The initial amount of money in the player's wallet.
    */
    public PlayerWithWallet(String name, int wallet)
    {
        super(name, wallet);
        this.wallet = wallet;
        this.cards = new ArrayList<>();
    }
    
    /**
    * Deducts the specified amount from the player's wallet, adding it to the current bet.
    * If the player doesn't have enough funds, a message is printed.
    *
    * @param amount The amount to deduct from the wallet.
    */
    public void deductFromWallet(int amount)
    {
        if (amount <= this.wallet)
        {
            this.wallet -= amount;
            this.currentBet += amount;
        } else {
            System.out.println("Insufficient funds for " + getName());
        }
    }
    
     /**
     * Gets the current amount of money in the player's wallet.
     *
     * @return The current wallet balance.
     */
    @Override
    public int getWallet() 
    {
        return wallet;
    }
    
    /**
     * Sets the amount of money in the player's wallet and updates the database.
     *
     * @param wallet The new wallet balance to set.
     */
    public void setWallet(int wallet) 
    {
        this.wallet = wallet;
        playerDAO.updateWallet(this.getId(), wallet); // Update the database
    }
    
     /**
     * Gets the current bet amount of the player.
     *
     * @return The current bet amount.
     */
    public int getCurrentBet()
    {
        return currentBet;
    }
    
     /**
     * Gets the list of cards held by the player.
     *
     * @return The list of cards held by the player.
     */
    @Override
    public List<String> getCards()
    {
        return cards;
    }
    
     /**
     * Sets the list of cards held by the player.
     *
     * @param cards The new list of cards to set.
     */
    @Override
    public void setCards(List<String> cards)
    {
        this.cards = cards;
    }

     /**
     * Sets the current bet amount of the player.
     *
     * @param currentBet The new current bet amount.
     */
    public void setCurrentBet(int currentBet)
    {
        this.currentBet = currentBet;
    }
    
    /**
     * Adds the specified amount to the player's wallet.
     *
     * @param amount The amount to add to the wallet.
     */
    public void addToWallet(int amount) 
    {
        this.wallet += amount;
    }
     
     /**
     * Updates the player's wallet balance in the database.
     */
    public void updatePlayerWalletDB() 
    {
        playerDAO.updateWallet(this.getId(), this.wallet); // Update the database
    }

    @Override
    public String toString() {
        return "PlayerWithWallet{"
                + "name='" + getName() + '\''
                + ", wallet=" + getWallet()
                + ", currentBet=" + currentBet
                + ", cards=" + cards
                + '}';
    }
}
