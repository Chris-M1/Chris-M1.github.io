
package game;

/**
 *
 * @author milas
 */

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String name;
    private int balance;
    private final List<String> cards = new ArrayList<>();
    private boolean folded = false;

    public Player(String name, int initialBalance) {
        this.name = name;
        this.balance = initialBalance;
    }

    public void receiveCard(String card) {
        cards.add(card);
    }

    public String getName() {
        return name;
    }

    public List<String> getCards() {
        return cards;
    }

    public int getBalance() {
        return balance;
    }

    public boolean hasFolded() {
        return folded;
    }

    public void fold() {
        folded = true;
    }

    @Override
    public String toString() {
        return name + ": ($" + balance + ") " + cards;
    }
}





