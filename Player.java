
package game;

/**
 *
 * @author dexter
 */
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class Player {

    private int id;
    private String name;
    private int wallet;
    public List<String> cards = new ArrayList<>();
    private boolean folded = false;
    private boolean allIn = false;
    
    /**
    * Constructs a player with the given name and wallet balance.
    * 
    * @param name   The name of the player.
    * @param wallet The wallet balance of the player.
    */
    public Player(String name, int wallet) 
    {
        this.name = name;
        this.wallet = wallet;
    }
    
    /**
    * Constructs a player with the given ID, name, and wallet balance.
    * 
    * @param id     The ID of the player.
    * @param name   The name of the player.
    * @param wallet The wallet balance of the player.
    */
    public Player(int id, String name, int wallet) 
    {
        this.id = id;
        this.name = name;
        this.wallet = wallet;
    }
    
    /**
    * Adds a card to the player's hand.
    * 
    * @param card The card to be added to the player's hand.
    */
    public void receiveCard(String card)
    {
        cards.add(card);
    }

    /**
    * Retrieves a copy of the player's cards.
    * 
    * @return A list containing the player's cards.
    */
    public List<String> getCards()
    {
        return new ArrayList<>(cards);
    }
    
    /**
    * Allows the player to unfold, indicating they are no longer folded.
    */
    public void unfold() 
    {
        this.folded = false;
    }

    /**
    * Sets the player's cards to the provided list of cards.
    * 
    * @param newCards The new list of cards for the player.
    */
    public void setCards(List<String> newCards) 
    {
        this.cards = new ArrayList<>(newCards);
    }
    
    /**
    * Retrieves the ID of the player.
    * 
    * @return The ID of the player.
    */
    public int getId()
    {
        return id;
    }

    /**
    * Retrieves the name of the player.
    * 
    * @return The name of the player.
    */
    public String getName()
    {
        return name;
    }
    
    /**
    * Retrieves the wallet balance of the player.
    * 
    * @return The wallet balance of the player.
    */
    public int getWallet()
    {
        return wallet;
    }
    
    /**
    * Sets the player's status to folded.
    */
    public void fold() 
    {
        folded = true;
    }
    
    /**
    * Checks if the player has folded.
    * 
    * @return True if the player has folded, false otherwise.
    */
    public boolean hasFolded()
    {
        return folded;
    }
    
    /**
    * Checks if the player is all-in.
    * 
    * @return True if the player is all-in, false otherwise.
    */
    public boolean isAllIn()
    {
        return allIn;
    }
    
    /**
    * Sets the player's status to all-in.
    */
    public void allIn()
    {
        allIn = true;
    }
    
    /**
    * Returns a string representation of the player.
    * 
    * @return A string containing the player's name and cards.
    */
    @Override
    public String toString()
    {
        return getName() + " | Cards: " + cards.toString(); // Customize as needed
    }
}
