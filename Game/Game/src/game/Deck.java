
package game;

/**
 *
 * @author milas
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<String> cards = new ArrayList<>();

    public Deck() {
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A"};
        String[] suits = {"C", "D", "H", "S"};

        for (String rank : ranks) {
            for (String suit : suits) {
                cards.add(rank+suit);
            }
        }

        Collections.shuffle(cards);
    }

    public String draw() {
        return cards.remove(0);
    }
}


