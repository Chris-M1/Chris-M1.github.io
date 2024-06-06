package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<String> cards;

    public Deck() {
        cards = new ArrayList<>();
        String[] suits = {"H", "D", "C", "S"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

        for (String suit : suits) {
            for (String rank : ranks) {
                cards.add(rank + " of " + suit);
            }
        }
    }
    
    public void shuffle() {
        Collections.shuffle(cards);
    }

    public String dealCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }

    public List<String> dealCards(int count) {
        List<String> dealtCards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            dealtCards.add(dealCard());
        }
        return dealtCards;
    }

    public List<String> getCards() {
        return cards;
    }
}
