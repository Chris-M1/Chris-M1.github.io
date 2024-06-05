package game;

/**
 *
 * @author chris
 */
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class Player {
    private int id;
    private String name;
    private int wallet;
    public List<String> cards = new ArrayList<>();
    private boolean folded = false;
    private boolean allIn = false;

    public Player(String name, int wallet) {
        this.name = name;
        this.wallet = wallet;
    }
   
    
    public Player(int id, String name, int wallet) {
        this.id = id;
        this.name = name;
        this.wallet = wallet;
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

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWallet() {
        return wallet;
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
