package game;

import java.util.List;

public class PlayerWithWallet extends Player {
    private int walletAmount;

    public PlayerWithWallet(String name, int initialBalance, int walletAmount) {
        super(name, initialBalance);
        this.walletAmount = walletAmount;
    }

    public int getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(int walletAmount) {
        this.walletAmount = walletAmount;
    }
}



