package game;

import java.util.List;
import java.util.Collections;

public class HandComparison {

    public static int compareHands(PokerHand hand1, PokerHand hand2) {
        int rankComparison = hand1.getRank().compareTo(hand2.getRank());
        if (rankComparison != 0) {
            return rankComparison;
        }

        return compareSameRankHands(hand1, hand2);
    }

    private static int compareSameRankHands(PokerHand hand1, PokerHand hand2) {
        switch (hand1.getRank()) {
            case ROYAL_FLUSH:
                return 0; // Royal Flushes are equal
            case STRAIGHT_FLUSH:
            case FLUSH:
            case STRAIGHT:
            case HIGH_CARD:
                return compareHighCards(hand1.getCardValues(), hand2.getCardValues());
            default:
                return compareMultiples(hand1, hand2);
        }
    }

    public static int compareMultiples(PokerHand hand1, PokerHand hand2) {
        List<Integer> multiples1 = hand1.getMultipleRanks();
        List<Integer> multiples2 = hand2.getMultipleRanks();
        Collections.sort(multiples1, Collections.reverseOrder());
        Collections.sort(multiples2, Collections.reverseOrder());

        for (int i = 0; i < Math.min(multiples1.size(), multiples2.size()); i++) {
            int comparison = multiples1.get(i).compareTo(multiples2.get(i));
            if (comparison != 0) {
                return comparison;
            }
        }
        return 0;
    }

    public static int compareHighCards(List<Integer> cards1, List<Integer> cards2) {
        for (int i = 0; i < Math.min(cards1.size(), cards2.size()); i++) {
            int comparison = cards1.get(i).compareTo(cards2.get(i));
            if (comparison != 0) {
                return comparison;
            }
        }
        return 0;
    }
}



