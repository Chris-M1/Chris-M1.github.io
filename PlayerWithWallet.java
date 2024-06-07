package game;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author chris
 */
public class PlayerWithWallet extends Player {

    private int wallet;// Stores the player's current money amount
    private int currentBet;     // Tracks the current bet amount for the player within a round 
    private PlayerDAO playerDAO;
    private List<String> cards;

    public PlayerWithWallet(int id, String name, int wallet) {
        super(id, name, wallet);
        this.wallet = wallet;
        this.playerDAO = new PlayerDAO();
        this.cards = new ArrayList<>();
    }

    public PlayerWithWallet(String name, int wallet) {
        super(name, wallet);
        this.wallet = wallet;
        this.cards = new ArrayList<>();
    }

    public void deductFromWallet(int amount) {
        if (amount <= this.wallet) {
            this.wallet -= amount;
            this.currentBet += amount;
        } else {
            System.out.println("Insufficient funds for " + getName());
        }
    }

    @Override
    public int getWallet() {
        return wallet;
    }

    public void setWallet(int wallet) {
        this.wallet = wallet;
        playerDAO.updateWallet(this.getId(), wallet); // Update the database
    }

    public int getCurrentBet() {
        return currentBet;
    }

    @Override
    public List<String> getCards() {
        return cards;
    }

    @Override
    public void setCards(List<String> cards) {
        this.cards = cards;
    }

    public void setCurrentBet(int currentBet) {
        this.currentBet = currentBet;
    }

    public void addToWallet(int amount) {
        this.wallet += amount;
    }

    public void updatePlayerWalletDB() {
        playerDAO.updateWallet(this.getId(), this.wallet); // Update the database
    }

    @Override
    public String toString() {
        return "PlayerWithWallet{"
                + "name='" + getName() + '\''
                + ", wallet=" + getWallet()
                + ", currentBet=" + currentBet
                + ", cards=" + cards
                + '}';
    }
}
