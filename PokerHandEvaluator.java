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

public class PokerHandEvaluator {

    // Method to evaluate the best hand
    public static PokerHand evaluateBestHand(List<String> holeCards, List<String> communityCards) {
        List<String> allCards = new ArrayList<>(holeCards);
        allCards.addAll(communityCards);

        // Sort cards by rank for easier evaluation
        Collections.sort(allCards, new Comparator<String>() {
            @Override
            public int compare(String card1, String card2) {
                return getCardValue(card1) - getCardSuit(card1);
            }
        });

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

    private static int mapRankToValue(char rank) {
        return "23456789TJQKA".indexOf(rank) + 2;
    }

    // Extracts the numerical value of a card's rank.
    public static int getCardValue(String card) {
        return mapRankToValue(card.charAt(0));
    }

    // Extracts the suit of a card.
    public static char getCardSuit(String card) {
        return card.charAt(1);
    }

    private static String findHighCard(List<String> cards) {
        // The highest card is the last card in the sorted list
        return cards.get(cards.size() - 1);
    }

    private static boolean isRoyalFlush(List<String> cards) {
        if (!isFlush(cards)) {
            return false;
        }
        String[] requiredRanks = {"A", "K", "Q", "J", "T"};
        for (int i = 0; i < requiredRanks.length; i++) {
            if (!cards.get(i).startsWith(requiredRanks[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean isStraightFlush(List<String> cards) {
        return isFlush(cards) && isStraight(cards);
    }

    private static boolean isFlush(List<String> cards) {
        char suit = getCardSuit(cards.get(0));
        return cards.stream().allMatch(card -> getCardSuit(card) == suit);
    }

    private static boolean isStraight(List<String> cards) {
        int previousValue = getCardValue(cards.get(0)) - 1; // Initialize to one less than the first card's value
        for (String card : cards) {
            int value = getCardValue(card);
            if (value != previousValue + 1) {
                return false;
            }
            previousValue = value;
        }
        return true;
    }

    public static Map<Integer, Long> getFrequencyMap(List<String> cards) {
        return cards.stream()
                .collect(Collectors.groupingBy(
                        PokerHandEvaluator::getCardValue,
                        Collectors.counting()));
    }

    private static boolean isFourOfAKind(List<String> cards) {
        Map<Integer, Long> frequencyMap = getFrequencyMap(cards);
        return frequencyMap.values().stream().anyMatch(count -> count == 4);
    }

    private static boolean isFullHouse(List<String> cards) {
        Map<Integer, Long> frequencyMap = getFrequencyMap(cards);
        boolean hasThreeOfAKind = frequencyMap.values().contains(3L);
        boolean hasPair = frequencyMap.values().contains(2L);
        return hasThreeOfAKind && hasPair;
    }

    private static boolean isThreeOfAKind(List<String> cards) {
        Map<Integer, Long> frequencyMap = getFrequencyMap(cards);
        return frequencyMap.values().stream().anyMatch(count -> count == 3);
    }

    private static boolean isTwoPair(List<String> cards) {
        Map<Integer, Long> frequencyMap = getFrequencyMap(cards);
        long pairsCount = frequencyMap.values().stream().filter(count -> count == 2).count();
        return pairsCount == 2;
    }

    private static boolean isOnePair(List<String> cards) {
        Map<Integer, Long> frequencyMap = getFrequencyMap(cards);
        return frequencyMap.values().stream().anyMatch(count -> count == 2);
    }
}
