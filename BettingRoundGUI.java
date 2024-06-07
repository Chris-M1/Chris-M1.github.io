package game;

/**
 *
 * @author chris
 */
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BettingRoundGUI extends JFrame {

    private static GameLogic gameLogic;
    private PokerGameGUI pokerGameGUI;
    private static JTextPane displayArea;
    private static JTextPane playerCardsArea;
    private static JTextPane communityCardsArea;
    private JTextField betField;
    private static JLabel walletLabel;
    private static JLabel blindsLabel;
    private JButton foldButton;
    private JButton callCheckButton;
    private JButton raiseButton;
    private JButton allInButton;
    private JButton resetButton;

    /**
     * Creates a user interface for the poker betting round. When you call this
     * method, it sets up the graphical interface for the current poker betting
     * round. It arranges various elements like message display, player and
     * community cards, betting options, and buttons for actions like folding,
     * calling/checking, raising, going all-in, and resetting the game. It also
     * configures the window's title, layout, size, and position on the screen.
     *
     * @param gameLogic the brains behind the poker game, controlling the logic
     * @param pokerGameGUI the main interface for the poker game
     */
    public BettingRoundGUI(GameLogic gameLogic, PokerGameGUI pokerGameGUI) {
        // We start by setting up the game logic and main GUI
        this.gameLogic = gameLogic;
        this.pokerGameGUI = pokerGameGUI;

        // We then set the title and how the window behaves when closed
        setTitle("Poker Betting Round\n");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Here, we create a space to display messages
        displayArea = new JTextPane();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        // For showing player and community cards, we prepare a special panel
        JPanel cardsPanel = new JPanel(new GridLayout(2, 1));
        playerCardsArea = createCenteredTextPane("Your Cards");
        communityCardsArea = createCenteredTextPane("Community Cards");
        cardsPanel.add(playerCardsArea);
        cardsPanel.add(communityCardsArea);
        add(cardsPanel, BorderLayout.NORTH);

        // We set up a panel for betting options and action buttons
        JPanel betPanel = new JPanel();
        betField = new JTextField(10);
        betField.setHorizontalAlignment(JTextField.CENTER);
        foldButton = new JButton("Fold");
        callCheckButton = new JButton("Call/Check");
        raiseButton = new JButton("Raise");
        allInButton = new JButton("All-In");
        resetButton = new JButton("Reset");

        // We define what each button does when clicked
        foldButton.addActionListener((ActionEvent e) -> {
            gameLogic.fold();
            resetTableView();
            updateCurrentPlayer();
        });

        callCheckButton.addActionListener((ActionEvent e) -> {
            gameLogic.callCheck();
            resetTableView();
            updateCurrentPlayer();
        });

        raiseButton.addActionListener((ActionEvent e) -> {
            placeRaise();
            resetTableView();
            updateCurrentPlayer();
        });

        allInButton.addActionListener((ActionEvent e) -> {
            gameLogic.allIn();
            resetTableView();
            updateCurrentPlayer();
        });

        resetButton.addActionListener((ActionEvent e) -> {
            dispose();
        });

        // We then add these components to the betting panel
        betPanel.add(new JLabel("Bet Amount:"));
        betPanel.add(betField);
        betPanel.add(foldButton);
        betPanel.add(callCheckButton);
        betPanel.add(raiseButton);
        betPanel.add(allInButton);
        betPanel.add(resetButton);
        add(betPanel, BorderLayout.SOUTH);

        // Finally, we set the window's preferred size, make sure it fits the content, and center it on the screen
        setPreferredSize(new Dimension(600, 600));
        pack();
        setLocationRelativeTo(null);

        // We notify whose turn it is and update player information
        gameLogic.showCurrentPlayerTurn();
        updateCurrentPlayer();
    }

    /**
     * Creates a JTextPane with centered text and a titled border. This method
     * creates a JTextPane with the specified title as its titled border. The
     * text in the JTextPane is set to be centered horizontally. The JTextPane
     * is configured to be non-editable.
     *
     * @param title the title to display in the titled border of the JTextPane
     * @return the JTextPane with centered text and the specified titled border
     */
    private JTextPane createCenteredTextPane(String title) {
        // Create a new JTextPane
        JTextPane textPane = new JTextPane();
        // Set the JTextPane to be non-editable
        textPane.setEditable(false);
        // Create a titled border with the specified title for the JTextPane
        textPane.setBorder(BorderFactory.createTitledBorder(title));
        // Get the styled document of the JTextPane
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        // Create a simple attribute set for center alignment
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        // Apply the center alignment to the entire text in the JTextPane
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        return textPane;
    }

    /**
     * Resets the game state and closes the current window. This method resets
     * the game logic to its initial state and refreshes the player list in the
     * graphical user interface. It then disposes of the current window, closing
     * it.
     */
    public void resetGame() {
        gameLogic.resetGame();
        pokerGameGUI.refreshPlayerList();
        dispose();
    }

    /**
     * Schedules a reset of the game. This method schedules the game reset to
     * occur on the event dispatch thread, ensuring it runs safely within the
     * Swing GUI toolkit. It calls the {@code resetGame} method to reset the
     * game state.
     */
    public void scheduleReset() {
        javax.swing.SwingUtilities.invokeLater(()
                -> {
            resetGame();
        });
    }

    /**
     * Displays an error message dialog. This method shows an error message
     * dialog with the specified message. The dialog has the title "Error" and
     * uses an error icon.
     *
     * @param message the error message to display in the dialog
     */
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Updates the information of the current player in the user interface. This
     * method retrieves the current player's information from the game logic,
     * such as their name, cards, wallet balance, current bet, and the bet
     * amount to match. It then updates the display with this information,
     * indicating the current player's turn.
     */
    private void updateCurrentPlayer() {
        javax.swing.SwingUtilities.invokeLater(()
                -> {
            PlayerWithWallet currentPlayer = gameLogic.getCurrentPlayer();
            gameLogic.displayTableInfo();
            appendMessage(currentPlayer.getName() + "'s turn. Cards: " + String.join(", ", currentPlayer.getCards()) + "\n");
            appendMessage("Wallet: $" + currentPlayer.getWallet() + "\n" + "Current bet: " + currentPlayer.getCurrentBet() + "\n" + "Bet To Match: " + gameLogic.getHighestBet());
        });
    }

    /**
     * Resets the table view by clearing the display area. This method clears
     * any text currently displayed in the display area, effectively resetting
     * the table view.
     */
    public void resetTableView() {
        displayArea.setText("");
    }

    /**
     * Updates the display to show the current small and big blind amounts. This
     * method retrieves the current small and big blind values from the game
     * logic and updates the blinds label to show these values.
     */
    public void updateBlindsDisplay() {
        blindsLabel.setText("Small Blind: " + gameLogic.getBlinds().getSmallBlind()
                + ", Big Blind: " + gameLogic.getBlinds().getBigBlind());
    }

    /**
     * Attempts to place a raise bet based on the input from the bet field. This
     * method reads the bet amount entered by the user, checks if it is greater
     * than the current highest bet, and if so, places the raise. If the entered
     * amount is invalid or not greater than the current highest bet, it shows
     * an error message. After a successful raise, it clears the bet field and
     * updates the current player display.
     */
    private void placeRaise() {
        try {
            int betAmount = Integer.parseInt(betField.getText());
            int highestBet = gameLogic.getHighestBet();

            if (betAmount <= highestBet) {
                JOptionPane.showMessageDialog(this, "Raise amount must be greater than the current highest bet.", "Invalid Raise", JOptionPane.ERROR_MESSAGE);
            } else {
                gameLogic.raise(betAmount);
                betField.setText("");
                updateCurrentPlayer();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid bet amount.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Updates the display areas with the current player's information. This
     * method sets the player's cards and the community cards in their
     * respective display areas. It does not update the wallet information.
     *
     * @param playerName the name of the current player (not used in this
     * method)
     * @param playerCards the list of cards that the current player holds
     * @param communityCards the list of community cards
     * @param wallet the current player's wallet balance (not used in this
     * method)
     */
    public static void updateCurrentPlayerDisplay(String playerName, List<String> playerCards, List<String> communityCards, int wallet) {
        playerCardsArea.setText(String.join(", ", playerCards));
        communityCardsArea.setText(String.join(", ", communityCards));
    }

    /**
     * Updates the display area with the current information of all players.
     * This method takes a list of players, builds a string with each player's
     * name, wallet balance, and cards, and then sets this string as the text in
     * the display area.
     *
     * @param players the list of players to display
     */
    public void updatePlayerDisplays(List<PlayerWithWallet> players) {
        StringBuilder builder = new StringBuilder();
        for (PlayerWithWallet player : players) {
            builder.append("Name: ").append(player.getName())
                    .append(", Wallet: $").append(player.getWallet())
                    .append(", Cards: ").append(String.join(", ", player.getCards()))
                    .append("\n");
        }

        displayArea.setText(builder.toString());
    }

    /**
     * Announces the winner and updates the display area. This method pops up a
     * dialog to announce who won the game. It also adds a message about the
     * winner to the display area.
     *
     * @param winnerName the name of the winner to announce and display
     */
    public void showWinner(String winnerName) {
        JOptionPane.showMessageDialog(this, "The winner is " + winnerName, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        appendMessage("The winner is " + winnerName + "\n");
    }

    /**
     * Displays a message dialog to announce the winner. This method shows a
     * message dialog with the given winner message. The dialog has the title
     * "Winner" and uses an information icon.
     *
     * @param winnerMessage the message to display in the dialog
     */
    public void displayWinner(String winnerMessage) {
        JOptionPane.showMessageDialog(this, winnerMessage, "Winner", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Adds a new message to the display area and centers it.
     *
     * This method takes the given message, centers it, and then adds it to the
     * display area. Each message is followed by a newline character.
     *
     * @param message the message to add to the display area
     */
    public void appendMessage(String message) {
        StyledDocument doc = displayArea.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        try {
            doc.insertString(doc.getLength(), message + "\n", center);
        } catch (BadLocationException e) {
            // Handle the exception (for example, you might log it or print an error message)
        }
    }
}
