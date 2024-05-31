package game;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author chris
 */
public class PlayerWithWallet extends Player {
    private int wallet;           // Stores the player's current money amount
    private int currentBet;       // Tracks the current bet amount for the player within a round

    public PlayerWithWallet(int id, String name, int wallet) {
        super(id, name, wallet);
        this.currentBet = 0;
    }

    public void setWallet(int amount) {
        this.wallet = amount;
    }

    public void addToWallet(int amount) {
        this.wallet += amount;
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

    public int getCurrentBet() {
        return currentBet;
    }
    
    public void setCurrentBet(int currentBet) {
        this.currentBet = currentBet;
    }

    @Override
    public String toString() {
        return super.toString() + " | Wallet: $" + wallet + " | Current Bet: $" + currentBet;
    }
}
