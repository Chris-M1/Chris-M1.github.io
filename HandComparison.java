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

/**
 * HandComparison provides functionality to compare two poker hands.
 */
public class HandComparison {

    /**
     * Compares two hands based on their ranks.
     * @param hand1 The first hand to compare.
     * @param hand2 The second hand to compare.
     * @return A positive number if hand1 is better, 0 if they are equal, and a negative number if hand2 is better.
     */
    public int compareHandRanks(PokerHand hand1, PokerHand hand2) {
        return hand1.getRank().compareTo(hand2.getRank());
    }

    /**
     * Compares two lists of card values to determine which one is higher.
     * @param cards1 The first list of card values.
     * @param cards2 The second list of card values.
     * @return A positive number if cards1 is higher, 0 if they are equal, and a negative number if cards2 is higher.
     */
    public int compareHighCards(List<Integer> cards1, List<Integer> cards2) {
        for (int i = 0; i < cards1.size(); i++) {
            int comparisonResult = cards1.get(i).compareTo(cards2.get(i));
            if (comparisonResult != 0) {
                return comparisonResult;
            }
        }
        return 0; // The high cards are equal
    }

    /**
     * Compares the hands considering the multiples (pairs, three-of-a-kinds, etc.) and kickers.
     * @param hand1 The first hand.
     * @param hand2 The second hand.
     * @return A positive number if hand1 is better, 0 if they are equal, and a negative number if hand2 is better.
     */
    public int compareMultiplesAndKickers(PokerHand hand1, PokerHand hand2) {
        Map<Integer, Integer> multiplesHand1 = hand1.getMultiples();
        Map<Integer, Integer> multiplesHand2 = hand2.getMultiples();

        int multipleComparison = compareHighCards(hand1.getMultipleRanks(), hand2.getMultipleRanks());
        if (multipleComparison != 0) return multipleComparison;

        // If the multiples are the same, compare kickers
        return compareHighCards(hand1.getKickers(), hand2.getKickers());
    }
    
    /**
     * Comprehensive comparison method for two PokerHands.
     * @param hand1 The first PokerHand.
     * @param hand2 The second PokerHand.
     * @return An integer indicating the comparison result.
     */
    public int compareHands(PokerHand hand1, PokerHand hand2) {
        int rankComparison = compareHandRanks(hand1, hand2);
        if (rankComparison != 0) return rankComparison;

        // Further comparisons can be added here based on hand type
        return compareMultiplesAndKickers(hand1, hand2);
    }
}

