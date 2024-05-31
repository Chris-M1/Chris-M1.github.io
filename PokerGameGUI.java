/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PokerGameGUI extends JFrame {

    private JLabel titleLabel;
    private JButton startButton;
    private Image backgroundImage;

    public PokerGameGUI() {
        // Load the background image
        try {
            backgroundImage = ImageIO.read(new File("background.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set up the frame
        setTitle("Poker Game");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the title label
        titleLabel = new JLabel("TEXAS HOLD'EM POKER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // Create the start button
        startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.PLAIN, 18));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        // Create a panel to hold the label and button
        JPanel mainPanel = new BackgroundPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(Box.createVerticalGlue()); // Add space above the label
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add space between the label and button
        mainPanel.add(startButton);
        mainPanel.add(Box.createVerticalGlue()); // Add space below the button

        // Add the main panel to the frame
        add(mainPanel);

        // Center the main panel
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        // Display the frame
        setVisible(true);
    }

    // Custom JPanel to paint the background image
    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    // Method to start the game
    private void startGame() {
        // Create a new game window
        JFrame gameWindow = new JFrame("Game Window");
        gameWindow.setSize(600, 400);
        gameWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Add a placeholder label
        JLabel gameLabel = new JLabel("Game started! Implement your game here.", SwingConstants.CENTER);
        gameLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Add the label to the game window
        gameWindow.add(gameLabel);

        // Center the label in the game window
        gameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

        // Display the game window
        gameWindow.setVisible(true);
    }

    public static void main(String[] args) {
        // Create and run the GUI on the event dispatching thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PokerGameGUI();
            }
        });
    }
}

