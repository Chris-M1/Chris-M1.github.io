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
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter valid numbers.");
                return; // Optionally call the constructor again to retry
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
