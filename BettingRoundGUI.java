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

    public BettingRoundGUI(GameLogic gameLogic, PokerGameGUI pokerGameGUI) {
        this.gameLogic = gameLogic;
        this.pokerGameGUI = pokerGameGUI;

        setTitle("Poker Betting Round");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        displayArea = new JTextPane();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        JPanel cardsPanel = new JPanel(new GridLayout(2, 1));
        playerCardsArea = createCenteredTextPane("Your Cards");
        communityCardsArea = createCenteredTextPane("Community Cards");

        cardsPanel.add(playerCardsArea);
        cardsPanel.add(communityCardsArea);
        add(cardsPanel, BorderLayout.NORTH);

        JPanel betPanel = new JPanel();
        betField = new JTextField(10);
        betField.setHorizontalAlignment(JTextField.CENTER);

        foldButton = new JButton("Fold");
        callCheckButton = new JButton("Call/Check");
        raiseButton = new JButton("Raise");
        allInButton = new JButton("All-In");
        resetButton = new JButton("Reset");

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

        betPanel.add(new JLabel("Bet Amount:"));
        betPanel.add(betField);
        betPanel.add(foldButton);
        betPanel.add(callCheckButton);
        betPanel.add(raiseButton);
        betPanel.add(allInButton);
        betPanel.add(resetButton);
        add(betPanel, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(600, 600));
        pack();
        setLocationRelativeTo(null);

        gameLogic.showCurrentPlayerTurn();
        updateCurrentPlayer();
    }

    private JTextPane createCenteredTextPane(String title) {
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBorder(BorderFactory.createTitledBorder(title));
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        return textPane;
    }

    public void resetGame() {
        gameLogic.resetGame();
        pokerGameGUI.refreshPlayerList();
        dispose();
    }

    public void scheduleReset() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            resetGame();
        });
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void updateCurrentPlayer() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            PlayerWithWallet currentPlayer = gameLogic.getCurrentPlayer();
            gameLogic.displayTableInfo();
            appendMessage(currentPlayer.getName() + "'s turn. Cards: " + String.join(", ", currentPlayer.getCards()) + "\n");
            appendMessage("Wallet: $" + currentPlayer.getWallet() + "\n" + "Current bet: " + currentPlayer.getCurrentBet() + "\n" + "Bet To Match: " + gameLogic.getHighestBet());
        });

    }

    public void resetTableView() {
        displayArea.setText("");
    }

    public void updateBlindsDisplay() {
        blindsLabel.setText("Small Blind: " + gameLogic.getBlinds().getSmallBlind()
                + ", Big Blind: " + gameLogic.getBlinds().getBigBlind());
    }

    private void placeRaise() {
        try {
            int betAmount = Integer.parseInt(betField.getText());
            gameLogic.raise(betAmount);
            betField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid bet amount.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void updateCurrentPlayerDisplay(String playerName, List<String> playerCards, List<String> communityCards, int wallet) {
        playerCardsArea.setText(String.join(", ", playerCards));
        communityCardsArea.setText(String.join(", ", communityCards));
    }

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

    public void showWinner(String winnerName) {
        JOptionPane.showMessageDialog(this, "The winner is " + winnerName, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        appendMessage("The winner is " + winnerName + "\n");
    }

    public void displayWinner(String winnerMessage) {
        JOptionPane.showMessageDialog(this, winnerMessage, "Winner", JOptionPane.INFORMATION_MESSAGE);
    }

    public void appendMessage(String message) {
        StyledDocument doc = displayArea.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        try {
            doc.insertString(doc.getLength(), message + "\n", center);
        } catch (BadLocationException e) {
        }
    }
}
