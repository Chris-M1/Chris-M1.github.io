/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

/**
 *
 * @author chris
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
    private List<String> cards; // Consider using a more sophisticated representation

    public PokerHand(HandRank rank, List<String> cards) {
        this.rank = rank;
        this.cards = cards;
    }
    
    private static Map<Integer, Long> getFrequencyMap(List<String> cards) {
        return cards.stream()
                .collect(Collectors.groupingBy(
                        PokerHandEvaluator::getCardValue, 
                        Collectors.counting()));
    }

    
    public HandRank getRank() {
        return rank;
    }

    public List<String> getCards() {
        return cards;
    }

    public List<Integer> getCardValues() {
        List<Integer> cardValues = new ArrayList<>();
        for (String card : this.cards) { // Assuming `this.cards` holds your hand's cards as strings
            int value = PokerHandEvaluator.parseRank(card); // Assuming a method to map rank char to its value
            cardValues.add(value);
        }
        Collections.sort(cardValues, Collections.reverseOrder()); // Sort in descending order for convenience
        return cardValues;
    }
    
    public List<String> getKickers() {
        // This method's implementation heavily depends on how you track the composition of your hand.
        // Here's a conceptual outline for a hand with a pair, needing three kickers:
        List<Integer> kickers = new ArrayList<>();
        Map<Integer, Integer> frequencyMap = getFrequencyMap(); // A method to get the frequency of each card value in the hand

        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() == 1) { // Assuming single cards are kickers
                kickers.add(entry.getKey());
            }
     }

        Collections.sort(kickers, Collections.reverseOrder()); // Sort in descending order for convenience
        return kickers.size() > 3 ? kickers.subList(0, 3) : kickers; // Assuming we need the top 3 kickers
}
    public Map<Integer, Integer> getMultiples() {
    // Returns a map with card values as keys and their counts as values, filtered to only include multiples
        Map<Integer, Integer> multiples = new HashMap<>();
        Map<Integer, Integer> frequencyMap = getFrequencyMap(); // A method to get the frequency of each card value in the hand

        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > 1) { // Assuming multiples are cards with a frequency > 1
              multiples.put(entry.getKey(), entry.getValue());
            }
        }

        return multiples;
    }
    // You might need methods to compare cards within the same rank
}

