package game;

import java.util.ArrayList;
import java.util.List;

/**
 * Extends the Player class to include wallet functionality specific to games involving betting.
 */
public class PlayerWithWallet extends Player {
    private int wallet;           // Stores the player's current money amount
    private int currentBet;       // Tracks the current bet amount for the player within a round

    /**
     * Constructor for PlayerWithWallet.
     * 
     * @param name The name of the player.
     * @param initialWallet The initial amount of money the player starts with.
     */
    public PlayerWithWallet(String name, int initialWallet) {
        super(name); // Call the constructor of the superclass, Player
        this.wallet = initialWallet;
        this.currentBet = 0;
    }

    /**
     * Sets the player's wallet to the specified amount.
     * 
     * @param amount The amount to set the player's wallet to.
     */
    public void setWallet(int amount) {
        this.wallet = amount;
    }

    /**
     * Adds the specified amount to the player's wallet.
     * 
     * @param amount The amount to add to the wallet.
     */
    public void addToWallet(int amount) {
        this.wallet += amount;
    }

    /**
     * Deducts the specified amount from the player's wallet. It also updates the current bet amount.
     * If the player does not have sufficient funds, prints an error message.
     * 
     * @param amount The amount to deduct from the wallet.
     */
     public void deductFromWallet(int amount) {
        if (amount <= this.wallet) {
            this.wallet -= amount;
            this.currentBet += amount;
        } else {
            System.out.println("Insufficient funds for " + getName());
        }
    }

    public int getWallet() {
        return wallet;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    @Override
    public String toString() {
        return super.toString() + " | Wallet: $" + wallet + " | Current Bet: $" + currentBet;
    }
}
