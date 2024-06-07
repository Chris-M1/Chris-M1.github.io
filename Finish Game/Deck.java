
package game;

/**
 *
 * @author chris
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a standard deck of playing cards.
 */
public class Deck 
{
    // List to store the cards in the deck
    private List<String> cards;

     /**
     * Constructs a new deck of cards with all 52 standard cards.
     */ /**
     * Constructs a new deck of cards with all 52 standard cards.
     */
    public Deck() 
    {
        // Initialize the list to store the cards
        cards = new ArrayList<>();
        
        // Define the suits and ranks of the cards
        String[] suits = {"H", "D", "C", "S"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A"};
        
        // Create each card by combining a rank and a suit
        for (String suit : suits) 
        {
            for (String rank : ranks) 
            {
                cards.add(rank + " of " + suit);
            }
        }
    }
    
     /**
     * Shuffles the cards in the deck.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }
    
     /**
     * Deals a single card from the top of the deck.
     * 
     * @return the dealt card, or null if the deck is empty
     */
    public String dealCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }
    
     /**
     * Deals a specified number of cards from the top of the deck.
     * 
     * @param count the number of cards to deal
     * @return a list of dealt cards
     */
    public List<String> dealCards(int count) 
    {
        List<String> dealtCards = new ArrayList<>();
        for (int i = 0; i < count; i++)
        {
           // Deal a single card and add it to the list of dealt cards 
            dealtCards.add(dealCard());
        }
        return dealtCards;
    }

     /**
     * Gets the current cards in the deck.
     * 
     * @return a list of cards in the deck
     */
    public List<String> getCards()
    {
        return cards;
    }
}
