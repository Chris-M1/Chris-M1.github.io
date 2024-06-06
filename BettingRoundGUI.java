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
    private JButton foldButton;
    private JButton callCheckButton;
    private JButton raiseButton;
    private JButton allInButton;
    

    public BettingRoundGUI(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
        initializeComponents();

        setTitle("Poker Betting Round");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        //add(new JScrollPane(displayArea), BorderLayout.CENTER);

        JPanel cardsPanel = new JPanel(new GridLayout(2, 1));
        playerCardsArea = new JTextArea();
        playerCardsArea.setEditable(false);
        playerCardsArea.setBorder(BorderFactory.createTitledBorder("Your Cards"));
        //cardsPanel.add(playerCardsArea);

        communityCardsArea = new JTextArea();
        communityCardsArea.setEditable(false);
        communityCardsArea.setBorder(BorderFactory.createTitledBorder("Community Cards"));
        cardsPanel.add(communityCardsArea);

        //add(cardsPanel, BorderLayout.NORTH);
        
        JPanel betPanel = new JPanel();
        betField = new JTextField(10);
        
        foldButton = new JButton("Fold");
        callCheckButton = new JButton("Call/Check");
        raiseButton = new JButton("Raise");
        allInButton = new JButton("All-In");

        foldButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameLogic.fold();
                updateCurrentPlayer();
            }
        });

        callCheckButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameLogic.callCheck();
                updateCurrentPlayer();
            }
        });

        raiseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeRaise();
                updateCurrentPlayer();
            }
        });

        allInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameLogic.allIn();
                updateCurrentPlayer();
            }
        });

        betPanel.add(new JLabel("Bet Amount:"));
        betPanel.add(betField);
        betPanel.add(foldButton);
        betPanel.add(callCheckButton);
        betPanel.add(raiseButton);
        betPanel.add(allInButton);
        add(betPanel, BorderLayout.SOUTH);

        pack();

        gameLogic.showCurrentPlayerTurn();
    }
    
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void updateCurrentPlayer() {
        PlayerWithWallet currentPlayer = gameLogic.getCurrentPlayer();
        displayArea.setText("It's " + currentPlayer.getName() + "'s turn. Cards: " + String.join(", ", currentPlayer.getCards()));
        walletLabel.setText("Wallet: $" + currentPlayer.getWallet());
    }
    
    private void initializeComponents() {
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);
        
        blindsLabel = new JLabel();
        updateBlindsDisplay();
        add(blindsLabel, BorderLayout.EAST);
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
    
    public void displayWinner(String winnerMessage) {
        JOptionPane.showMessageDialog(this, winnerMessage, "Winner", JOptionPane.INFORMATION_MESSAGE);
    }
}






