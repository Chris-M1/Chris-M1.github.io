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
    private List<String> cards = new ArrayList<>();
    private boolean folded = false;

    public Player(String name, int initialBalance) {
        this.name = name;
        this.balance = initialBalance;
    }

    public void setCards(List<String> cards) {
        this.cards = new ArrayList<>(cards);
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

    public void deductBalance(int amount) {
        balance -= amount;
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
