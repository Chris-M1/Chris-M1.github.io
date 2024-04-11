package game;

import java.util.List;
import java.util.ArrayList;

public class PWallet extends Player {
    private int wallet;

    public PWallet(String name, int initialWallet) {
        super(name);  // Call the constructor of the superclass, Player
        this.wallet = initialWallet;
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
        } else {
            System.out.println("Insufficient funds to deduct from " + getName());
        }
    }

    public int getWallet() {
        return wallet;
    }
    
    @Override
    public String toString() {
        return super.toString() + " | Wallet: $" + wallet;
    }
}