/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

/**
 *
 * @author chris
 */
import javax.swing.*;

public class BlindsSetupGUI {

    private int smallBlind;
    private int bigBlind;

    public BlindsSetupGUI() {
        while (true) { // Loop until valid input is provided
            JTextField smallBlindField = new JTextField(5);
            JTextField bigBlindField = new JTextField(5);
            JPanel dialogPanel = new JPanel();

            dialogPanel.add(new JLabel("Small Blind:"));
            dialogPanel.add(smallBlindField);
            dialogPanel.add(Box.createHorizontalStrut(15)); // a spacer
            dialogPanel.add(new JLabel("Big Blind:"));
            dialogPanel.add(bigBlindField);

            int result = JOptionPane.showConfirmDialog(null, dialogPanel,
                    "Please Enter Blinds", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    smallBlind = Integer.parseInt(smallBlindField.getText());
                    bigBlind = Integer.parseInt(bigBlindField.getText());

                    if (bigBlind < smallBlind) {
                        JOptionPane.showMessageDialog(null, "The Big Blind must be greater or equal to the Small Blind. Please enter valid blinds.");
                    } else {
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

    public int getSmallBlind() {
        return smallBlind;
    }

    public int getBigBlind() {
        return bigBlind;
    }
}
