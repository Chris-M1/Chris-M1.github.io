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
    private List<String> cards; 

    public PokerHand(HandRank rank, List<String> cards) {
        this.rank = rank;
        this.cards = cards;
    }
    
    public HandRank getRank() {
        return rank;
    }

    public List<String> getCards() {
        return cards;
    }
    
    public List<Integer> getCardValues() {
        return cards.stream()
                .map(card -> PokerHandEvaluator.getCardValue(card))
                .collect(Collectors.toList());
    }

    public Map<Integer, Long> getMultiples() {
        Map<Integer, Long> frequencyMap = this.cards.stream()
                .collect(Collectors.groupingBy(PokerHandEvaluator::getCardValue, Collectors.counting()));
        return frequencyMap.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
     public List<Integer> getMultipleRanks() {
        return getMultiples().entrySet().stream()
                .map(Map.Entry::getKey)
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());
    }
     
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

