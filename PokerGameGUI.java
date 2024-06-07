/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

public class PokerGameGUI extends JFrame {

    private GameLogic gameLogic;
    private JTextArea displayArea;
    private JTextField nameField;
    private JTextField walletField;
    private JButton addButton;
    private JButton startButton;
    private JButton alterButton;
    private JButton deleteButton;
    private JList<String> playerList;
    private DefaultListModel<String> listModel;

    public PokerGameGUI() {
        gameLogic = new GameLogic();
        setTitle("Poker Game");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        listModel = new DefaultListModel<>();
        playerList = new JList<>(listModel);
        add(new JScrollPane(playerList), BorderLayout.WEST);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Player Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Wallet:"));
        walletField = new JTextField();
        inputPanel.add(walletField);
        addButton = new JButton("Add Player");
        inputPanel.add(addButton);
        alterButton = new JButton("Alter Player");
        inputPanel.add(alterButton);
        deleteButton = new JButton("Delete Player");
        inputPanel.add(deleteButton);
        startButton = new JButton("Start Game");
        inputPanel.add(startButton);
        add(inputPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPlayer();
            }
        });

        alterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alterPlayer();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deletePlayer();
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        loadPlayers();
        welcomeHelpMessage();
    }
    
    private void welcomeHelpMessage() {
        displayArea.append("  Welcome to our POKER GAME!\n\n"
                + "  You can ADD players and set their initial wallet \n  amounts in the box below and pressing Add.\n\n"
                + "  You can ALTER a players wallet by clicking their names,\n and inputting their new wallet amount in the box below.\n\n"
                + "  You can DELETE a player by selecting their name and clicking the Delete button.\n\n"
                + "  You can SELECT two or more players by holding 'CTRL' AND CLICKING \n  the players you want.\n\n"
                + "  Once ready to play, select two or more players and click 'Start Game' to begin :)\n\n");
    }
    
    public JTextField getNameField() {
        return nameField;
    }

    public JTextField getWalletField() {
        return walletField;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getAlterButton() {
        return alterButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JList<String> getPlayerList() {
        return playerList;
    }

    public DefaultListModel<String> getListModel() {
        return listModel;
    }

    private void loadPlayers() {
        listModel.clear();
        List<PlayerWithWallet> players = gameLogic.getAllPlayers();
        for (PlayerWithWallet player : players) {
            listModel.addElement(player.getName() + " - Wallet: $" + player.getWallet());
        }
    }

    private void addPlayer() {
        String playerName = nameField.getText().trim();
        String walletText = walletField.getText().trim();

        if (playerName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Player name is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (listModel.contains(playerName + " - Wallet: $" + walletText)) {
            JOptionPane.showMessageDialog(this, "Player name already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int initialWallet = Integer.parseInt(walletText);
            gameLogic.addNewPlayer(playerName, initialWallet);
            listModel.addElement(playerName + " - Wallet: $" + initialWallet);
            displayArea.append("Player " + playerName + " with wallet " + initialWallet + " added.\n");
            nameField.setText("");
            walletField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid wallet amount.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void alterPlayer() {
        String selectedValue = playerList.getSelectedValue();
        if (selectedValue == null) {
            JOptionPane.showMessageDialog(this, "No player selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String playerName = selectedValue.split(" - ")[0];
        String newWalletText = walletField.getText().trim();
        try {
            int newWallet = Integer.parseInt(newWalletText);
            gameLogic.updatePlayerWallet(playerName, newWallet);
            int index = playerList.getSelectedIndex();
            listModel.set(index, playerName + " - Wallet: $" + newWallet);
            displayArea.append("Player " + playerName + "'s wallet updated to " + newWallet + ".\n");
            walletField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid wallet amount.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePlayer() {
        String selectedValue = playerList.getSelectedValue();
        if (selectedValue == null) {
            JOptionPane.showMessageDialog(this, "No player selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String playerName = selectedValue.split(" - ")[0];
        gameLogic.deletePlayer(playerName);
        listModel.removeElement(selectedValue);
        displayArea.append("Player " + playerName + " deleted.\n");
    }

    private void startGame() {
        List<String> selectedPlayers = playerList.getSelectedValuesList();
        if (selectedPlayers.size() < 2) {
            JOptionPane.showMessageDialog(this, "Select at least two players to start the game.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<PlayerWithWallet> players = selectedPlayers.stream()
                .map(player -> gameLogic.getPlayerByName(player.split(" - ")[0]))
                .collect(Collectors.toList());
        
        displayArea.setText("");
        welcomeHelpMessage();
        gameLogic.setPlayers(players);
        displayArea.append("  Game started with players:\n\n");
        for (PlayerWithWallet player : gameLogic.getPlayers()) {
            displayArea.append("  Name: " + player.getName() + " | Wallet: " + player.getWallet() + "\n");
        }
        gameLogic.initializeGame();
        gameLogic.BlindSetup(this); // Start the betting round

    }
    
    public void refreshPlayerList() {
        loadPlayers();
    }
}
