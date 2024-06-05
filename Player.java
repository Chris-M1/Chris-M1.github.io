package game;

/**
 *
 * @author chris
 */
import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private List<String> cards = new ArrayList<>();
    private boolean folded = false;
    private boolean allIn = false;

    public Player(String name) {
        this.name = name;
    }

    public void receiveCard(String card) {
        cards.add(card);
    }

    public List<String> getCards() {
        return new ArrayList<>(cards);
    }
    
    public void unfold() {
    this.folded = false;
    }

    public void setCards(List<String> newCards) {
        this.cards = new ArrayList<>(newCards);
    }

    public String getName() {
        return name;
    }

    public void fold() {
        folded = true;
    }

    public boolean hasFolded() {
        return folded;
    }
    public boolean isAllIn() {
        return allIn;
    }
    public void allIn(){
        allIn = true;
    }
    @Override
    public String toString() {
        return getName() + " | Cards: " + cards.toString(); // Customize as needed
    }
}
