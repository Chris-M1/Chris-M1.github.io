
package game;

/**
 *
 * @author milas
 */
import java.util.*;

public class PokerHandEvaluator {

    // Method to evaluate the best hand
    public static PokerHand evaluateBestHand(List<String> holeCards, List<String> communityCards) {
        List<String> allCards = new ArrayList<>(holeCards);
        allCards.addAll(communityCards);

        // Sort cards by rank for easier evaluation
        Collections.sort(allCards, new Comparator<String>() {
            @Override
            public int compare(String card1, String card2) {
                return parseRank(card1) - parseSuit(card1);
            }
        });

        if (isRoyalFlush(allCards)) return new PokerHand(PokerHand.HandRank.ROYAL_FLUSH, allCards);
        if (isStraightFlush(allCards)) return new PokerHand(PokerHand.HandRank.STRAIGHT_FLUSH, allCards);
        if (isFourOfAKind(allCards)) return new PokerHand(PokerHand.HandRank.FOUR_OF_A_KIND, allCards);
        if (isFullHouse(allCards)) return new PokerHand(PokerHand.HandRank.FULL_HOUSE, allCards);
        if (isFlush(allCards)) return new PokerHand(PokerHand.HandRank.FLUSH, allCards);
        if (isStraight(allCards)) return new PokerHand(PokerHand.HandRank.STRAIGHT, allCards);
        if (isThreeOfAKind(allCards)) return new PokerHand(PokerHand.HandRank.THREE_OF_A_KIND, allCards);
        if (isTwoPair(allCards)) return new PokerHand(PokerHand.HandRank.TWO_PAIR, allCards);
        if (isOnePair(allCards)) return new PokerHand(PokerHand.HandRank.ONE_PAIR, allCards);
        if (isHighCard(allCards)) return new PokerHand(PokerHand.HandRank.HIGH_CARD, allCards);

        // Assuming methods like isRoyalFlush, isStraightFlush, etc., are implemented to check each hand type
        // This approach checks for the best hand down from the top rank to the lowest

        return new PokerHand(PokerHand.HandRank.HIGH_CARD, Arrays.asList(findHighCard(allCards)));
    }

    // Map rank character to its value
    public static int parseRank(String card) {
    char rankChar = card.charAt(0); // Get the first character of the card string
    int rankValue;

    if (rankChar >= '2' && rankChar <= '9') {
        rankValue = rankChar - '0'; // Convert character to integer value
    } else {
        // Handle special rank characters (T, J, Q, K, A)
        switch (rankChar) {
            case 'T':
                rankValue = 10;
                break;
            case 'J':
                rankValue = 11;
                break;
            case 'Q':
                rankValue = 12;
                break;
            case 'K':
                rankValue = 13;
                break;
            case 'A':
                rankValue = 14;
                break;
            default:
                throw new IllegalArgumentException("Invalid rank character: " + rankChar);
        }
    }

    return rankValue;
}
    
    public static char parseSuit(String card) {
    return card.charAt(1); // Get the second character of the card string
}

    // Example helper method to find the highest card (simplified)
    private static String findHighCard(List<String> cards) {
        // The highest card is the last card in the sorted list
        return cards.get(cards.size() - 1);
    }

    // Placeholder for actual logic to check for a Royal Flush
    private static boolean isRoyalFlush(List<String> cards) {
        // Implement logic to check for a Royal Flush
        return false;
    }

    // Placeholder for actual logic to check for a Straight Flush
    private static boolean isStraightFlush(List<String> cards) {
        // Implement logic to check for a Straight Flush
        return false;
    }

    // Add methods for checking other hand types...
}


