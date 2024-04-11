/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
public class HandComparison {

    public static int compareHands(PokerHand hand1, PokerHand hand2) {
        // First, compare the hand ranks
        int rankComparison = hand1.getRank().compareTo(hand2.getRank());
        if (rankComparison != 0) {
            return rankComparison;
        }

        // If ranks are the same, further comparisons are necessary
        return compareSameRankHands(hand1, hand2);
    }

    private static int compareSameRankHands(PokerHand hand1, PokerHand hand2) {
        switch (hand1.getRank()) {
            case ROYAL_FLUSH:
                // Royal Flushes are equal; there's no higher card to compare
                return 0;
            case STRAIGHT_FLUSH:
            case FLUSH:
            case STRAIGHT:
            case HIGH_CARD:
                // Compare the highest card down to the lowest until a difference is found
                return compareHighCards(hand1.getCardValues(), hand2.getCardValues());
            case FOUR_OF_A_KIND:
            case FULL_HOUSE:
            case THREE_OF_A_KIND:
            case TWO_PAIR:
            case ONE_PAIR:
                // Compare the values of the multiples, then compare kickers if necessary
                int multiplesComparison = compareMultiples(hand1, hand2);
                if (multiplesComparison != 0) {
                    return multiplesComparison;
                }
                // If multiples (e.g., pairs) are the same, compare kickers
                return compareKickers(hand1.getKickers(), hand2.getKickers());
            default:
                throw new IllegalArgumentException("Unknown hand rank: " + hand1.getRank());
        }
    }

    // Compare the values of the multiples
    public static int compareMultiples(PokerHand hand1, PokerHand hand2) {
        List<Integer> multiples1 = hand1.getMultipleRanks();
        List<Integer> multiples2 = hand2.getMultipleRanks();

        // Ensure that the list of multiples is sorted in descending order
        multiples1.sort(Collections.reverseOrder());
        multiples2.sort(Collections.reverseOrder());

        // Compare each corresponding set of multiples between the two hands
        for (int i = 0; i < Math.min(multiples1.size(), multiples2.size()); i++) {
            if (!multiples1.get(i).equals(multiples2.get(i))) {
                return multiples1.get(i).compareTo(multiples2.get(i));
            }
        }
        // If Multiples are the same, return 0 to compare Kickers
        return 0;
    }

    // Compare the kickers
    private static int compareKickers(List<Integer> kickers1, List<Integer> kickers2) {
        return compareHighCards(kickers1, kickers2);
    }

    public static int compareHighCards(List<Integer> cards1, List<Integer> cards2) {
        for (int i = 0; i < Math.min(cards1.size(), cards2.size()); i++) {
            int comparisonResult = cards1.get(i).compareTo(cards2.get(i));
            if (comparisonResult != 0) {
                return comparisonResult;
            }
        }
        return 0; // The high cards are equal, return for draw.
    }
}


