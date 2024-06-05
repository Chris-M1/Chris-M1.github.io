/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PokerGameGUI extends JFrame {
    private GameLogic gameLogic;
    private JTextArea displayArea;
    private JTextField nameField;
    private JTextField walletField;
    private JButton addButton;
    private JButton startButton;

    public PokerGameGUI() {
        gameLogic = new GameLogic();

        setTitle("Poker Game");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Player Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Initial Wallet:"));
        walletField = new JTextField();
        inputPanel.add(walletField);
        addButton = new JButton("Add Player");
        inputPanel.add(addButton);
        startButton = new JButton("Start Game");
        inputPanel.add(startButton);
        add(inputPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPlayer();
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
    }

    private void addPlayer() {
        String playerName = nameField.getText();
        String walletText = walletField.getText();
        try {
            int initialWallet = Integer.parseInt(walletText);
            gameLogic.addNewPlayer(playerName, initialWallet, false);
            displayArea.append("Player " + playerName + " with wallet " + initialWallet + " added.\n");
            nameField.setText("");
            walletField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid wallet amount.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startGame() {
        gameLogic.initializeGame();
        displayArea.append("Game started with players:\n");
        for (PlayerWithWallet player : gameLogic.getPlayers()) {
            displayArea.append("Name: " + player.getName() + ", Wallet: " + player.getWallet() + "\n");
        }
        gameLogic.startBettingRound(); // Start the betting round
    }
}
