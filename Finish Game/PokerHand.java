
package game;

/**
 *
 * @author dexter
 */
import java.util.*;
import java.util.stream.Collectors;

public class PokerHand {
    
    public enum HandRank {
    HIGH_CARD,
    ONE_PAIR,
    TWO_PAIR,
    THREE_OF_A_KIND,
    STRAIGHT,
    FLUSH,
    FULL_HOUSE,
    FOUR_OF_A_KIND,
    STRAIGHT_FLUSH,
    ROYAL_FLUSH
    }
    
    private HandRank rank;
    private List<String> cards; 
    
    
    /**
    * Constructs a PokerHand object with the specified hand rank and cards.
    *
    * @param rank  The rank of the hand.
    * @param cards The list of cards comprising the hand.
    */
    public PokerHand(HandRank rank, List<String> cards)
    {
        this.rank = rank;
        this.cards = cards;
    }
    
    
    /**
    * Retrieves the rank of the poker hand.
    *
    * @return The rank of the hand.
    */
    public HandRank getRank()
    {
        return rank;
    }
    
    /**
    * Retrieves the list of cards in the poker hand.
    *
    * @return The list of cards.
    */
    public List<String> getCards() 
    {
        return cards;
    }
    
    /**
    * Retrieves the integer values of the cards in the poker hand.
    *
    * @return The list of integer values representing the cards.
    */
    public List<Integer> getCardValues()
    {
        return cards.stream()
                .map(card -> PokerHandEvaluator.getCardValue(card))
                .collect(Collectors.toList());
    }
    
    /**
    * Retrieves a map containing the counts of each card value that appears more than once in the poker hand.
    *
    * @return A map where the keys represent card values and the values represent the count of occurrences.
    */
    public Map<Integer, Long> getMultiples() 
    {
        Map<Integer, Long> frequencyMap = this.cards.stream()
                .collect(Collectors.groupingBy(PokerHandEvaluator::getCardValue, Collectors.counting()));
        return frequencyMap.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    /**
    * Retrieves a list of card values representing the ranks of cards that appear more than once in the poker hand.
    * The list is sorted in descending order of rank.
    *
    * @return A list of card values representing the ranks of cards that appear more than once.
    */
     public List<Integer> getMultipleRanks()
     {
        return getMultiples().entrySet().stream()
                .map(Map.Entry::getKey)
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());
    }
     
     /**
     * Retrieves a list of card values representing the kickers (cards that are not part of multiples)
     * in the poker hand. The list is sorted in descending order of card value.
     *
     * @return A list of card values representing the kickers in the poker hand.
     */
     public List<Integer> gKickers() {
        Map<Integer, Long> frequencyMap = this.cards.stream()
                .collect(Collectors.groupingBy(PokerHandEvaluator::getCardValue, Collectors.counting()));

        // Extracting kickers - cards that are not part of multiples
        List<Integer> kickers = frequencyMap.entrySet().stream()
                .filter(entry -> entry.getValue() == 1)
                .map(Map.Entry::getKey)
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());

        return kickers;
    }
     
     @Override
public String toString() {
    // Assuming cards is a List<String> where each string represents a card
    if (cards != null) {
        return String.join(", ", cards);
    } else {
        return "No cards";
    }
}   
}

