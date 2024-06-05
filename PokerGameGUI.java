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
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Load the background image
        ImageIcon icon = new ImageIcon("C:\\Users\\milas\\OneDrive\\Desktop\\P24_20118392_21150503 (2)\\P24_20118392_21150503\\Game\\Game\\poker BG.png");
        Image backgroundImage = icon.getImage();

        // Set the content pane to the BackgroundPanel
        setContentPane(new BackgroundPanel(backgroundImage));
        setLayout(new BorderLayout());

        // Add title label
        JLabel titleLabel = new JLabel("TEXAS HOLD' EM POKER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 35));
        titleLabel.setForeground(Color.white);
        add(titleLabel, BorderLayout.NORTH);

        // Create a panel for the display area with a set size
        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setPreferredSize(new Dimension(200, 200));
        displayPanel.setOpaque(false); // Make the panel transparent

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        displayPanel.add(scrollPane, BorderLayout.CENTER);

        // Create a center panel to hold the display panel
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);
        centerPanel.add(displayPanel);

        add(centerPanel, BorderLayout.CENTER); // Add the center panel to the center

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.setOpaque(false);

        // Create labels with custom foreground color
        JLabel playerNameLabel = new JLabel("Player Name:");
        playerNameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        playerNameLabel.setForeground(Color.WHITE); // Change text color to white
        inputPanel.add(playerNameLabel);

        nameField = new JTextField();
        inputPanel.add(nameField);

        JLabel walletLabel = new JLabel("Initial Wallet:");
        walletLabel.setFont(new Font("Arial", Font.BOLD, 20));
        walletLabel.setForeground(Color.white); // Change text color to white
        inputPanel.add(walletLabel);

        walletField = new JTextField();
        inputPanel.add(walletField);

        addButton = new JButton("Add Player");
        addButton.setForeground(Color.BLACK); // Change text color to white
        //addButton.setBackground(Color.BLACK); // Change button background color
        inputPanel.add(addButton);

        startButton = new JButton("Start Game");
        startButton.setForeground(Color.BLACK); // Change text color to white
        //startButton.setBackground(Color.BLACK); // Change button background color
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                PokerGameGUI gui = new PokerGameGUI();
                gui.setVisible(true);
            }
        });
    }

    private static class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(Image backgroundImage) {
            this.backgroundImage = backgroundImage;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}




