
package game;

/**
 *
 * @author chris
 */
import javax.swing.*;

public class BlindsSetupGUI {

    private int smallBlind;
    private int bigBlind;
    
    /**
    * Constructs a GUI for setting up blinds.
    */
    public BlindsSetupGUI()
    {
        while (true)
        { // Create text fields for entering small blind and big blind amounts
            JTextField smallBlindField = new JTextField(5);
            JTextField bigBlindField = new JTextField(5);
            
            // Create a panel to hold the dialog components
            JPanel dialogPanel = new JPanel();
            
            // Add labels and text fields for small blind and big blind to the dialog panel
            dialogPanel.add(new JLabel("Small Blind:"));
            dialogPanel.add(smallBlindField);
            dialogPanel.add(Box.createHorizontalStrut(15)); // a spacer
            dialogPanel.add(new JLabel("Big Blind:"));
            dialogPanel.add(bigBlindField);
            
            // Display a dialog to prompt the user to enter blinds
            int result = JOptionPane.showConfirmDialog(null, dialogPanel,
                    "Please Enter Blinds", JOptionPane.OK_CANCEL_OPTION);
            
            // If the user clicks OK, parse the entered values and store them as blinds
            if (result == JOptionPane.OK_OPTION) {
                try {
                    smallBlind = Integer.parseInt(smallBlindField.getText());
                    bigBlind = Integer.parseInt(bigBlindField.getText());

                    if (bigBlind < smallBlind) {
                        JOptionPane.showMessageDialog(null, "The Big Blind must be greater or equal to the Small Blind. Please enter valid blinds.");
                    }
                    else if(bigBlind <= 0 || smallBlind <= 0) 
                        JOptionPane.showMessageDialog(null, "The Blinds must be greater than 0. Please enter valid blinds.");
                    else {
                        break; // Exit the loop if the input is valid
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Please enter valid numbers.");
                }
            } else {
                break; // Exit the loop if the user cancels the dialog
            }
        }
    }
    
    /**
    * Gets the amount of the small blind.
    * 
    * @return the amount of the small blind
    */
    public int getSmallBlind()
    {
        return smallBlind;
    }

    /**
    * Gets the amount of the big blind.
    * 
    * @return the amount of the big blind
    */
    public int getBigBlind() 
    {
        return bigBlind;
    }
}
