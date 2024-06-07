
package game;

/**
 *
 * @author dexter
 */
public class Game 
{
    public static void main(String[] args) 
    {
        // Launch the GUI
        javax.swing.SwingUtilities.invokeLater(() -> 
        {
            // Create a new instance of PokerGameGUI
            PokerGameGUI gui = new PokerGameGUI();
            // Set the GUI visible
            gui.setVisible(true);
        });
    }
}


