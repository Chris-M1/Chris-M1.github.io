package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;

public class PokerGameGUI extends JFrame {
    private GameLogic gameLogic;
    private JTextArea gameInfoTextArea;
    private JButton startButton;

    public PokerGameGUI(GameLogic gameLogic) {
        super("Poker Game");
        this.gameLogic = gameLogic;

        initComponents();
        addListeners();

        setPreferredSize(new Dimension(720, 720));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon background = new ImageIcon("C:\\Users\\milas\\OneDrive\\Desktop\\poker BG.png");
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };
        add(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel welcomeLabel = new JLabel("WELCOME TO");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 35));
        welcomeLabel.setForeground(Color.YELLOW);
        mainPanel.add(welcomeLabel, gbc);

        JLabel pokerLabel = new JLabel("TEXAS HOLD'EM POKER");
        pokerLabel.setFont(new Font("Arial", Font.BOLD, 30));
        pokerLabel.setForeground(Color.YELLOW);
        gbc.gridy = 1;
        mainPanel.add(pokerLabel, gbc);

        startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 25));
        startButton.setPreferredSize(new Dimension(200, 50));
        startButton.setForeground(Color.RED);
        startButton.setBackground(new Color(255, 215, 0)); // Gold color background
        gbc.gridy = 2;
        mainPanel.add(startButton, gbc);

        gameInfoTextArea = new JTextArea();
        gameInfoTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(gameInfoTextArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        mainPanel.add(scrollPane, gbc);
    }

    private void addListeners() {
        startButton.addActionListener(e -> startGame());
    }

    private void startGame() {
        // Create a new panel for player input
        JPanel playerInputPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon background = new ImageIcon("C:\\Users\\milas\\OneDrive\\Desktop\\poker BG.png");
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };

        // Create components for player input
        JLabel label = new JLabel("How many Players:");
        label.setFont(new Font("Arial", Font.BOLD, 35));
        label.setForeground(Color.YELLOW);

        Font font = new Font("Arial", Font.PLAIN, 18);
        JComboBox<Integer> playerCountDropdown = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        playerCountDropdown.setPreferredSize(new Dimension(100, 40));
        playerCountDropdown.setFont(font);

        // Enter small blind
        JLabel smallBlindLabel = new JLabel("Small Blind:");
        smallBlindLabel.setFont(new Font("Arial", Font.BOLD, 35));
        smallBlindLabel.setForeground(Color.YELLOW);

        JTextField smallBlindField = new JTextField();
        smallBlindField.setPreferredSize(new Dimension(200, 40));
        smallBlindField.setFont(font);

        JButton continueButton = new JButton("Continue");
        continueButton.setFont(new Font("Arial", Font.BOLD, 25));
        continueButton.setPreferredSize(new Dimension(200, 50));

        // Add components to the player input panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);
        playerInputPanel.add(label, gbc);

        gbc.gridy++;
        playerInputPanel.add(playerCountDropdown, gbc);

        gbc.gridy++;
        playerInputPanel.add(smallBlindLabel, gbc);

        gbc.gridy++;
        playerInputPanel.add(smallBlindField, gbc);

        gbc.gridy++;
        playerInputPanel.add(continueButton, gbc);

        // Remove the existing content from the main panel
        getContentPane().removeAll();

        // Add the player input panel to the main panel
        add(playerInputPanel);

        // Repaint the window to reflect the changes
        revalidate();
        repaint();

        // Add action listener to the continue button
        continueButton.addActionListener(e -> {
            int playerCount = (int) playerCountDropdown.getSelectedItem();
            int smallBlind = Integer.parseInt(smallBlindField.getText());
            goToPlayerDetailsScreen(playerCount, smallBlind);
        });
    }

    private void goToPlayerDetailsScreen(int playerCount, int smallBlind) {
        JPanel playerDetailsPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon background = new ImageIcon("C:\\Users\\milas\\OneDrive\\Desktop\\poker BG.png");
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel[] playerLabels = new JLabel[playerCount];
        JTextField[] playerTextFields = new JTextField[playerCount];
        JLabel[] walletLabels = new JLabel[playerCount];
        JTextField[] walletTextFields = new JTextField[playerCount];

        for (int i = 0; i < playerCount; i++) {
            playerLabels[i] = new JLabel("Player " + (i + 1) + " Name:");
            playerLabels[i].setFont(new Font("Arial", Font.BOLD, 25));
            playerLabels[i].setForeground(Color.YELLOW);

            playerTextFields[i] = new JTextField();
            playerTextFields[i].setPreferredSize(new Dimension(200, 40));
            playerTextFields[i].setFont(new Font("Arial", Font.PLAIN, 18));

            walletLabels[i] = new JLabel("Wallet:");
            walletLabels[i].setFont(new Font("Arial", Font.BOLD, 25));
            walletLabels[i].setForeground(Color.YELLOW);

            walletTextFields[i] = new JTextField();
            walletTextFields[i].setPreferredSize(new Dimension(200, 40));
            walletTextFields[i].setFont(new Font("Arial", Font.PLAIN, 18));

            gbc.gridx = 0;
            gbc.gridy = i * 2;
            playerDetailsPanel.add(playerLabels[i], gbc);

            gbc.gridx = 1;
            playerDetailsPanel.add(playerTextFields[i], gbc);

            gbc.gridx = 0;
            gbc.gridy = i * 2 + 1;
            playerDetailsPanel.add(walletLabels[i], gbc);

            gbc.gridx = 1;
            playerDetailsPanel.add(walletTextFields[i], gbc);
        }

        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.BOLD, 25));
        submitButton.setPreferredSize(new Dimension(200, 50));
        gbc.gridx = 0;
        gbc.gridy = playerCount * 2;
        gbc.gridwidth = 2;
        playerDetailsPanel.add(submitButton, gbc);

        getContentPane().removeAll();
        add(playerDetailsPanel);
        revalidate();
        repaint();

        submitButton.addActionListener(e -> {
            String[] playerNames = new String[playerCount];
            int[] wallets = new int[playerCount];
            for (int i = 0; i < playerCount; i++) {
                playerNames[i] = playerTextFields[i].getText();
                wallets[i] = Integer.parseInt(walletTextFields[i].getText());
            }
            gameLogic.addPlayers(playerNames, wallets);
            goToGameScreen(playerCount, smallBlind, playerNames, wallets);
        });
    }

    private void goToGameScreen(int playerCount, int smallBlind, String[] playerNames, int[] wallets) {
        JPanel gamePanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon background = new ImageIcon("C:\\Users\\milas\\OneDrive\\Desktop\\poker BG.png");
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // Dealer's square
        JLabel dealerLabel = new JLabel();
        dealerLabel.setPreferredSize(new Dimension(100, 100));
        dealerLabel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gamePanel.add(dealerLabel, gbc);

        // River card squares
        for (int i = 1; i <= 5; i++) {
            JLabel riverCardLabel = new JLabel();
            riverCardLabel.setPreferredSize(new Dimension(80, 120));
            riverCardLabel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
            gbc.gridx = i;
            gamePanel.add(riverCardLabel, gbc);
        }

        // Player details
        for (int i = 0; i < playerCount; i++) {
            JLabel playerNameLabel = new JLabel(playerNames[i]);
            playerNameLabel.setFont(new Font("Arial", Font.BOLD, 20));
            playerNameLabel.setForeground(Color.YELLOW);

            JLabel walletLabel = new JLabel("Wallet: " + wallets[i]);
            walletLabel.setFont(new Font("Arial", Font.BOLD, 20));
            walletLabel.setForeground(Color.YELLOW);

            JPanel playerPanel = new JPanel();
            playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
            playerPanel.setOpaque(false);
            playerPanel.add(playerNameLabel);
            playerPanel.add(walletLabel);

            JPanel cardPanel = new JPanel();
            cardPanel.setOpaque(false);
            cardPanel.setLayout(new FlowLayout());
            for (int j = 0; j < 2; j++) {
                JLabel cardLabel = new JLabel();
                cardLabel.setPreferredSize(new Dimension(80, 120));
                cardLabel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                cardPanel.add(cardLabel);
            }

            gbc.gridx = 0;
            gbc.gridy = i + 1;
            gbc.gridwidth = 1;
            gamePanel.add(playerPanel, gbc);

            gbc.gridx = 1;
            gbc.gridwidth = 5;
            gamePanel.add(cardPanel, gbc);
        }

        getContentPane().removeAll();
        add(gamePanel);
        revalidate();
        repaint();
    }

    private void updateGameInfo(String info) {
        gameInfoTextArea.append(info);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Scanner scanner = new Scanner(System.in);
            GameLogic gameLogic = new GameLogic(scanner);
            new PokerGameGUI(gameLogic);
        });
    }

    public void startGameWithPlayers(int playerCount, int smallBlind) {
        gameLogic.startWithPlayers(playerCount, smallBlind);
        updateGameInfo("Game started with " + playerCount + " players. Small Blind: " + smallBlind + "\n");
    }
}




