
package game;

/**
 *
 * @author chris
 */
import java.util.List;
import java.util.Map;
import java.util.Collections;

/**
 * HandComparison provides functionality to compare two poker hands.
 */
public class HandComparison 
{

    public static int compareHands(PokerHand hand1, PokerHand hand2) {
        // First, compare the hand ranks
        int rankComparison = hand1.getRank().compareTo(hand2.getRank());
        if (rankComparison != 0) {
            return rankComparison;
        }

        // If ranks are the same, further comparisons are necessary
        return compareSameRankHands(hand1, hand2);
    }

    private static int compareSameRankHands(PokerHand handA, PokerHand handB) {
        switch (handA.getRank()) {
            case ROYAL_FLUSH:
                // Royal Flushes are equal; there's no higher card to compare
                return 0;
            case STRAIGHT_FLUSH:
            case FLUSH:
            case STRAIGHT:
            case HIGH_CARD:
                // Compare the highest card down to the lowest until a difference is found
                return compareHighCards(handA.getCardValues(), handB.getCardValues());
            case FOUR_OF_A_KIND:
            case FULL_HOUSE:
            case THREE_OF_A_KIND:
            case TWO_PAIR:
            case ONE_PAIR:
                // Compare the values of the multiples, then compare kickers if necessary
               int multiplesComparison = compareMultiples(handA.getMultipleRanks(), handB.getMultipleRanks());
                if (multiplesComparison != 0) {
                    return multiplesComparison;
                }
                // If multiples (e.g., pairs) are the same, compare kickers
                return cKickers(handA.gKickers(), handB.gKickers());
            default:
                throw new IllegalArgumentException("Unknown hand rank: " + handA.getRank());
        }
    }
    
    // Compare the kickers
    private static int cKickers(List<Integer> kickers1, List<Integer> kickers2) {
        return compareHighCards(kickers1, kickers2);
    }

    // Compare the values of the multiples
    public static int compareMultiples(List<Integer> firstMultiples, List<Integer> secondMultiples) {
        Collections.sort(firstMultiples, Collections.reverseOrder());
        Collections.sort(secondMultiples, Collections.reverseOrder());

        int loopLimit = Math.min(firstMultiples.size(), secondMultiples.size());
        for (int index = 0; index < loopLimit; index++) {
            int result = firstMultiples.get(index).compareTo(secondMultiples.get(index));
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }
    
    /**
    * Compares two lists of kickers to determine the winner.
    * 
    * @param firstKickers  The kickers of the first hand.
    * @param secondKickers The kickers of the second hand.
    * @return A value indicating the comparison result.
    */
    private static int compareKickers(List<Integer> firstKickers, List<Integer> secondKickers)
    {
        // Delegate the comparison to the method for comparing high cards
        return compareHighCards(firstKickers, secondKickers);
    }
    
    /**
    * Compares two lists of high cards to determine the winner.
    * 
    * @param firstCards The high cards of the first hand.
    * @param secondCards The high cards of the second hand.
    * @return A value indicating the comparison result.
    */
    public static int compareHighCards(List<Integer> firstCards, List<Integer> secondCards) 
    {
        // Determine the limit for iteration based on the size of the smaller list
        int limit = firstCards.size() < secondCards.size() ? firstCards.size() : secondCards.size();
        // Iterate through the lists and compare corresponding cards
        for (int i = 0; i < limit; i++) 
        {
            int comparisonResult = firstCards.get(i).compareTo(secondCards.get(i));
            // If the comparison result is not zero, return it
            if (comparisonResult != 0)
            {
                return comparisonResult;
            }
        }
        // If all cards are equal, return 0
        return 0;
    }

}


