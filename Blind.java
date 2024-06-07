/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

/**
 *
 * @author chris
 */
class Blind {
    private final int smallBlindAmount;
    private final int bigBlindAmount;

    public Blind(int smallBlindAmount, int bigBlindAmount) {
        this.smallBlindAmount = smallBlindAmount;
        this.bigBlindAmount = bigBlindAmount;
    }

    public int getSmallBlind() {
        return smallBlindAmount;
    }

    public int getBigBlind() {
        return bigBlindAmount;
    }
}
