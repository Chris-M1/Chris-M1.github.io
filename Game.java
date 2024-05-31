
package game;

/**
 *
 * @author milas
 */


import java.util.Scanner;

public class Game {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("TEXAS HOLD'EM POKER!\n");

        // Initialize the game
        GameLogic game = new GameLogic(scanner);
        game.start();
    }
}


