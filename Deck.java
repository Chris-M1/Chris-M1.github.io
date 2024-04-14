package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<String> cards = new ArrayList<>();

    public Deck() {
        reset();
    }

    public void reset() {
        cards.clear();
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A"};
        String[] suits = {"C", "D", "H", "S"};

        for (String rank : ranks) {
            for (String suit : suits) {
                cards.add(rank + suit);
            }
        }
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public String draw() {
        if (!cards.isEmpty()) {
            return cards.remove(0);
        } else {
            reset();  // Reset and shuffle the deck if empty, then draw.
            return draw();
        }
    }
}