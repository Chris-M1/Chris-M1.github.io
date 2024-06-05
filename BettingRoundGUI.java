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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BettingRoundGUI extends JFrame {
    private static GameLogic gameLogic;
    private static JTextArea displayArea;
    private static JTextArea playerCardsArea;
    private static JTextArea communityCardsArea;
    private JTextField betField;
    private static JLabel walletLabel;
    private static JLabel blindsLabel;
    private JButton betButton;
    private JButton checkButton;
    private JButton callButton;
    private JButton raiseButton;
    private JButton foldButton;
    

    public BettingRoundGUI(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
        initializeComponents();

        setTitle("Poker Betting Round");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        JPanel cardsPanel = new JPanel(new GridLayout(2, 1));
        playerCardsArea = new JTextArea();
        playerCardsArea.setEditable(false);
        playerCardsArea.setBorder(BorderFactory.createTitledBorder("Your Cards"));
        cardsPanel.add(playerCardsArea);

        communityCardsArea = new JTextArea();
        communityCardsArea.setEditable(false);
        communityCardsArea.setBorder(BorderFactory.createTitledBorder("Community Cards"));
        cardsPanel.add(communityCardsArea);

        add(cardsPanel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(4, 1));
        inputPanel.add(new JLabel("Enter your bet amount:"));
        betField = new JTextField();
        inputPanel.add(betField);
        betButton = new JButton("Place Bet");
        inputPanel.add(betButton);
        checkButton = new JButton("Check");
        inputPanel.add(checkButton);
        foldButton = new JButton("Fold");
        inputPanel.add(foldButton);
        add(inputPanel, BorderLayout.SOUTH);

        betButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeBet();
            }
        });

        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                check();
            }
        });

        foldButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fold();
            }
        });

        gameLogic.showCurrentPlayerTurn();
    }
    
    private void initializeComponents() {
        blindsLabel = new JLabel();
        updateBlindsDisplay();
        add(blindsLabel, BorderLayout.NORTH);
        // Additional GUI setupdisplayArea = new JTextArea();
        displayArea = new JTextArea();
        
        walletLabel = new JLabel("Wallet: $0");
        this.add(displayArea, BorderLayout.CENTER);
        this.add(walletLabel, BorderLayout.SOUTH);
        this.pack(); // Layout components
        this.setVisible(true); // Make GUI visible
        
    }

    public void updateBlindsDisplay() {
        blindsLabel.setText("Small Blind: " + gameLogic.getBlinds().getSmallBlind() + 
                            ", Big Blind: " + gameLogic.getBlinds().getBigBlind());
    }

    private void placeBet() {
        String betText = betField.getText();
        try {
            int betAmount = Integer.parseInt(betText);
            gameLogic.placeBet(betAmount);
            displayArea.append("You placed a bet of " + betAmount + ".\n");
            betField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid bet amount.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void check() {
        gameLogic.check();
        displayArea.append("You checked.\n");
    }

    private void fold() {
        gameLogic.fold();
        displayArea.append("You folded.\n");
    }

    public static void updateCurrentPlayerDisplay(String playerName, List<String> playerCards, List<String> communityCards, int wallet) {
        playerCardsArea.setText(String.join(", ", playerCards));
        communityCardsArea.setText(String.join(", ", communityCards));
        walletLabel.setText("Wallet: $" + wallet);
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
        displayArea.append("The winner is " + winnerName + "\n");
    }
    
   public void displayWinners(String message) {
    JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    // Optionally close the window or reset the game
    this.dispose(); // Closes the betting round window
}
}


