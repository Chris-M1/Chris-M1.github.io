
package game;

/**
 *
 * @author chris
 */
import java.util.*;
import java.util.stream.Collectors;

public class PokerHandEvaluator
{

    /**
     * Evaluates the best poker hand from the given hole cards and community cards.
     * 
     * @param holeCards the player's hole cards
     * @param communityCards the community cards on the table
     * @return the best hand formed by the combination of hole and community cards
     */
    public static PokerHand evaluateBestHand(List<String> holeCards, List<String> communityCards) 
    {
        // Combine hole and community cards into one list
        List<String> allCards = new ArrayList<>(holeCards);
        allCards.addAll(communityCards);

        // Sort cards by rank for easier evaluation
        Collections.sort(allCards, new Comparator<String>() 
        {
            @Override
            public int compare(String card1, String card2)
            {
                return getCardValue(card1) - getCardSuit(card1);
            }
        });
        
        // Check for each possible poker hand rank in decreasing order of strength
        if (isRoyalFlush(allCards)) {
            return new PokerHand(PokerHand.HandRank.ROYAL_FLUSH, allCards);
        }
        if (isStraightFlush(allCards)) {
            return new PokerHand(PokerHand.HandRank.STRAIGHT_FLUSH, allCards);
        }
        if (isFourOfAKind(allCards)) {
            return new PokerHand(PokerHand.HandRank.FOUR_OF_A_KIND, allCards);
        }
        if (isFullHouse(allCards)) {
            return new PokerHand(PokerHand.HandRank.FULL_HOUSE, allCards);
        }
        if (isFlush(allCards)) {
            return new PokerHand(PokerHand.HandRank.FLUSH, allCards);
        }
        if (isStraight(allCards)) {
            return new PokerHand(PokerHand.HandRank.STRAIGHT, allCards);
        }
        if (isThreeOfAKind(allCards)) {
            return new PokerHand(PokerHand.HandRank.THREE_OF_A_KIND, allCards);
        }
        if (isTwoPair(allCards)) {
            return new PokerHand(PokerHand.HandRank.TWO_PAIR, allCards);
        }
        if (isOnePair(allCards)) {
            return new PokerHand(PokerHand.HandRank.ONE_PAIR, allCards);
        }
        return new PokerHand(PokerHand.HandRank.HIGH_CARD, Arrays.asList(findHighCard(allCards)));
    }
    
     /**
     * Converts a card rank character to its corresponding numeric value.
     * 
     * For example, '2' maps to 2, '3' maps to 3, 'T' maps to 10, 'J' maps to 11,
     * 'Q' maps to 12, 'K' maps to 13, and 'A' maps to 14.
     * 
     * @param rank The character representing the rank of the card.
     * @return The numeric value of the rank.
     */
    private static int mapRankToValue(char rank) 
    {
        return "23456789TJQKA".indexOf(rank) + 2;
    }

    // Extracts the numerical value of a card's rank.
    public static int getCardValue(String card) 
    {
        return mapRankToValue(card.charAt(0));
    }

    // Extracts the suit of a card.
    public static char getCardSuit(String card) 
    {
        return card.charAt(1);
    }

    private static String findHighCard(List<String> cards) 
    {
        // The highest card is the last card in the sorted list
        return cards.get(cards.size() - 1);
    }
    
     /**
     * Checks if the given list of cards forms a Royal Flush.
     * 
     * A Royal Flush consists of five cards of the same suit in sequence,
     * from 10 through Ace (10, J, Q, K, A).
     * 
     * @param cards The list of cards to check.
     * @return true if a Royal Flush is found, false otherwise.
     */
    private static boolean isRoyalFlush(List<String> cards) 
    {
        // First, check if the cards form a flush
        if (!isFlush(cards))
        {
            return false;
        }
        // Define the required ranks for a Royal Flush (in descending order)
        String[] requiredRanks = {"A", "K", "Q", "J", "T"};
        
        // Compare the ranks of the first five cards with the required ranks
        for (int i = 0; i < requiredRanks.length; i++)
        {
            // If any card doesn't have the required rank at the corresponding position, it's not a Royal Flush 
            if (!cards.get(i).startsWith(requiredRanks[i]))
            {
                // If all conditions are met, it's a Royal Flush// If all conditions are met, it's a Royal Flush
                return false;
            }
        }
        return true;
    }
   
     /**
     * Checks if the given list of cards forms a Straight Flush.
     * 
     * A Straight Flush consists of five cards of the same suit in sequence.
     * 
     * @param cards The list of cards to check.
     * @return true if a Straight Flush is found, false otherwise.
     */
    private static boolean isStraightFlush(List<String> cards) 
    {
        // Check if the cards form both a flush and a straight
        return isFlush(cards) && isStraight(cards);
    }
    
     /**
     * Checks if the given list of cards forms a Flush.
     * 
     * A Flush consists of five cards of the same suit.
     * 
     * @param cards The list of cards to check.
     * @return true if a Flush is found, false otherwise.
     */
    private static boolean isFlush(List<String> cards)
    {
        // Get the suit of the first card
        char suit = getCardSuit(cards.get(0));
        // Check if all cards have the same suit as the first card
        return cards.stream().allMatch(card -> getCardSuit(card) == suit);
    }
    
     /**
     * Checks if the given list of cards forms a Straight.
     * 
     * A Straight consists of five cards in sequence.
     * 
     * @param cards The list of cards to check.
     * @return true if a Straight is found, false otherwise.
     */ /**
     * Checks if the given list of cards forms a Straight.
     * 
     * A Straight consists of five cards in sequence.
     * 
     * @param cards The list of cards to check.
     * @return true if a Straight is found, false otherwise.
     */
    private static boolean isStraight(List<String> cards)
    {
        // Initialize the value of the previous card to one less than the value of the first card
        int previousValue = getCardValue(cards.get(0)) - 1; 
        
        // Iterate through the cards to check if they form a sequence
        for (String card : cards)
        {
            // If the value of the current card is not one more than the value of the previous card, it's not a straight
            int value = getCardValue(card);
            
            if (value != previousValue + 1) 
            {
                return false;
            }
            previousValue = value;
        }
        // If all cards form a sequence, it's a straight
        return true;
    }
    
    /**
    * Generates a frequency map for the given list of cards.
    * 
    * This method counts the occurrences of each card value in the list.
    * 
    * @param cards the list of cards to analyze
    * @return a map where keys are card values and values are their frequencies
    */
    public static Map<Integer, Long> getFrequencyMap(List<String> cards) 
    {
        // Group the cards by their numeric value and count the occurrences
        return cards.stream()
                .collect(Collectors.groupingBy(
                        PokerHandEvaluator::getCardValue,// Extracts the numeric value of the card
                        Collectors.counting()));// Counts the occurrences of each value
    }
    
    
    /**
    * Checks if the given list of cards contains a four of a kind.
    * 
    * @param cards the list of cards to check
    * @return true if a four of a kind is found, false otherwise
    */
    private static boolean isFourOfAKind(List<String> cards) 
    {
        // Get the frequency of each rank in the cards
        Map<Integer, Long> frequencyMap = getFrequencyMap(cards);
        // Check if any rank appears four times (four of a kind)
        return frequencyMap.values().stream().anyMatch(count -> count == 4);
    }
    
     /**
     * Checks if the given list of cards contains a full house.
     * 
     * @param cards the list of cards to check
     * @return true if a full house is found, false otherwise
     */
    private static boolean isFullHouse(List<String> cards)
    {
        // Get the frequency of each rank in the cards
        Map<Integer, Long> frequencyMap = getFrequencyMap(cards);
        // Check if there is a three of a kind and a pair
        boolean hasThreeOfAKind = frequencyMap.values().contains(3L);
        boolean hasPair = frequencyMap.values().contains(2L);
        return hasThreeOfAKind && hasPair;
    }
    
    /**
     * Checks if the given list of cards contains a three of a kind.
     * 
     * @param cards the list of cards to check
     * @return true if a three of a kind is found, false otherwise
     */
    private static boolean isThreeOfAKind(List<String> cards) 
    {
        // Get the frequency of each rank in the cards
        Map<Integer, Long> frequencyMap = getFrequencyMap(cards);
        // Check if any rank appears three times (three of a kind)
        return frequencyMap.values().stream().anyMatch(count -> count == 3);
    }
    
    /**
     * Checks if the given list of cards contains two pairs.
     * 
     * @param cards the list of cards to check
     * @return true if two pairs are found, false otherwise
     */
    private static boolean isTwoPair(List<String> cards) 
    {
        // Get the frequency of each rank in the cards
        Map<Integer, Long> frequencyMap = getFrequencyMap(cards);
        // Count the number of pairs (ranks that appear twice)
        long pairsCount = frequencyMap.values().stream().filter(count -> count == 2).count();
        // Check if there are exactly two pairs
        return pairsCount == 2;
    }
    
     /**
     * Checks if the given list of cards contains a single pair.
     * 
     * @param cards the list of cards to check
     * @return true if a single pair is found, false otherwise
     */
    private static boolean isOnePair(List<String> cards) 
    {
        // Get the frequency of each rank in the cards
        Map<Integer, Long> frequencyMap = getFrequencyMap(cards);
        // Check if any rank appears twice (one pair)
        return frequencyMap.values().stream().anyMatch(count -> count == 2);
    }
}
