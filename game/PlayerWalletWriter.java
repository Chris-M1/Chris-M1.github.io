/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
/**
 *
 * @author chris
 */
class PlayerWalletWriter {
    private static final String resourcesDirPath = "./resources"; // Define the resources directory path
    private static final String filePath = resourcesDirPath + "/Players.txt"; // Define the file path

    public static void writeToFile(List<Player> players) {
        
        // Check if the player list is empty
        if (players.isEmpty()) {
            System.out.println("No player information provided. File will not be created.");
            return; // Exit the method if there are no players
        }
        
        File resourcesDir = new File(resourcesDirPath);
        if (!resourcesDir.exists()) {
            boolean dirCreated = resourcesDir.mkdir(); // Create the resources directory if it doesn't exist
            if (!dirCreated) {
                System.out.println("Failed to create the resources directory.");
                return; // Exit if unable to create the directory
            }
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Player player : players) {
                // Check if player name is null or balance is negative
                if (player.getName() == null || player.getBalance() < 0) {
                    System.out.println("Invalid player information: " + player);
                    continue; // Skip writing this player's information
                }
                
                writer.write(player.getName() + "," + player.getBalance());
                writer.newLine();
            }
            System.out.println("Player wallet information has been written to: " + filePath);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }
    public static String getFilePath() {
        return filePath; // Make sure filePath is accessible
    }
}
