
package game;

/**
 *
 * @author milas
 */

public class Game {
    public static void main(String[] args) {
        // Launch the GUI
        javax.swing.SwingUtilities.invokeLater(() -> {
            PokerGameGUI gui = new PokerGameGUI();
            gui.setVisible(true);
        });
    }
}


