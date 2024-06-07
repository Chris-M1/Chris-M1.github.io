package game;

/**
 *
 * @author dexter
 */
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

    /**
     * Creates the main user interface for the poker game. When you use this
     * method, it sets up the primary graphical interface for the poker game.
     * This includes setting the window title, size, and close operation,
     * arranging components such as the message display area, player list, input
     * fields for adding, altering, and deleting players, and buttons for
     * adding, altering, deleting players, and starting the game. It also loads
     * previously saved players.
     */
    public PokerGameGUI() {
        // We start by initializing the game logic and setting up the window title, size, and close operation
        gameLogic = new GameLogic();
        setTitle("Poker Game");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // We set up the message display area
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        // We create the player list and add it to the interface
        listModel = new DefaultListModel<>();
        playerList = new JList<>(listModel);
        add(new JScrollPane(playerList), BorderLayout.WEST);

        // Next, we set up the input panel for adding, altering, deleting players, and starting the game
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

        // We define action listeners for each button
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

        // Finally, we load previously saved players
        loadPlayers();
        welcomeHelpMessage();

        setLocationRelativeTo(null);

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

    /**
     * Loads previously saved players into the player list. When you call this
     * method, it clears the current player list and retrieves all players with
     * their respective wallets from the game logic. It then adds each player's
     * name and wallet balance to the player list for display.
     */
    private void loadPlayers() {
        // First, we clear the existing player list
        listModel.clear();
        // Next, we retrieve all players from the game logic
        List<PlayerWithWallet> players = gameLogic.getAllPlayers();
        // We then add each player's name and wallet balance to the player list
        for (PlayerWithWallet player : players) {
            listModel.addElement(player.getName() + " - Wallet: $" + player.getWallet());
        }
    }

    /**
     * Adds a new player to the game. When you use this method, it reads the
     * player name and initial wallet balance from the input fields. If the
     * player name is empty or already exists, it displays an error message. If
     * the wallet amount is invalid, it also shows an error message. Otherwise,
     * it adds the new player to the game, updates the player list, and displays
     * a confirmation message.
     */
    private void addPlayer() {
        // First, we get the player name and wallet balance from the input fields
        String playerName = nameField.getText().trim();
        String walletText = walletField.getText().trim();

        // Then, we check if the player name is empty
        if (playerName.isEmpty()) {
            // If it's empty, we show an error message and return
            JOptionPane.showMessageDialog(this, "Player name is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Next, we check if the player name already exists in the player list
        if (listModel.contains(playerName + " - Wallet: $" + walletText)) {
            // If it already exists, we show an error message and return
            JOptionPane.showMessageDialog(this, "Player name already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Then, we try to parse the wallet amount
        try {
            int initialWallet = Integer.parseInt(walletText);
            // If parsing is successful, we add the new player to the game
            gameLogic.addNewPlayer(playerName, initialWallet);
            // We also update the player list with the new player's name and wallet balance
            listModel.addElement(playerName + " - Wallet: $" + initialWallet);
            // We display a confirmation message in the display area
            displayArea.append("Player " + playerName + " with wallet " + initialWallet + " added.\n");
            // Finally, we clear the input fields
            nameField.setText("");
            walletField.setText("");
        } catch (NumberFormatException e) {
            // If parsing fails, we show an error message for invalid wallet amount
            JOptionPane.showMessageDialog(this, "Invalid wallet amount.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Allows altering the details of a selected player.
     */
    private void alterPlayer() {
        // Get the selected player from the player list
        String selectedValue = playerList.getSelectedValue();
        // Check if a player is selected
        if (selectedValue == null) {
            // Display an error message if no player is selected
            JOptionPane.showMessageDialog(this, "No player selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Extract player name from the selected value and get the new wallet amount from the input field
        String playerName = selectedValue.split(" - ")[0];
        String newWalletText = walletField.getText().trim();
        try {
            // Parse the new wallet amount
            int newWallet = Integer.parseInt(newWalletText);
            // Update the player's wallet in the game logic
            gameLogic.updatePlayerWallet(playerName, newWallet);
            // Update the player's wallet in the player list
            int index = playerList.getSelectedIndex();
            listModel.set(index, playerName + " - Wallet: $" + newWallet);
            // Display a confirmation message
            displayArea.append("Player " + playerName + "'s wallet updated to " + newWallet + ".\n");
            // Clear the wallet input field
            walletField.setText("");
        } catch (NumberFormatException e) {
            // Show an error message if the new wallet amount is invalid
            JOptionPane.showMessageDialog(this, "Invalid wallet amount.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deletes the selected player from the game.
     */
    private void deletePlayer() {
        // Get the selected player from the player list
        String selectedValue = playerList.getSelectedValue();
        // Check if a player is selected
        if (selectedValue == null) {
            // Display an error message if no player is selected
            JOptionPane.showMessageDialog(this, "No player selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String playerName = selectedValue.split(" - ")[0];
        gameLogic.deletePlayer(playerName);
        listModel.removeElement(selectedValue);
        displayArea.append("Player " + playerName + " deleted.\n");
    }

    /**
     * Starts the poker game with the selected players.
     */
    private void startGame() {
        // Get the list of selected players
        List<String> selectedPlayers = playerList.getSelectedValuesList();
        // Check if at least two players are selected
        if (selectedPlayers.size() < 2) {
            // Show an error message if less than two players are selected
            JOptionPane.showMessageDialog(this, "Select at least two players to start the game.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Extract player objects from selected player names
        List<PlayerWithWallet> players = selectedPlayers.stream()
                .map(player -> gameLogic.getPlayerByName(player.split(" - ")[0]))
                .collect(Collectors.toList());

        displayArea.setText("");
        welcomeHelpMessage();
        // Set the players for the game
        gameLogic.setPlayers(players);

        // Display a message indicating the start of the game and the selected players
        displayArea.append("  Game started with players:\n\n");
        for (PlayerWithWallet player : gameLogic.getPlayers()) {

            displayArea.append("  Name: " + player.getName() + " | Wallet: " + player.getWallet() + "\n");
        }

        // Initialize the game
        gameLogic.initializeGame();
        // Start the betting round
        gameLogic.BlindSetup(this);

    }

    /**
     * Refreshes the player list.
     */
    public void refreshPlayerList() {
        loadPlayers();
    }
}
