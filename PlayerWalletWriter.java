
package game;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.List;

class PlayerWalletWriter {
    public static void writeToFile(List<Player> players) {
        String resourcesDirPath = "resources";
        String filePath = resourcesDirPath + File.separator + "playerWallets.txt";
        
        // Check if the player list is empty
        if (players.isEmpty()) {
            System.out.println("No player information provided. File will not be created.");
            return; // Exit the method if there are no players
        }
        
        File resourcesDir = new File(resourcesDirPath);
        if (!resourcesDir.exists()) {
            resourcesDir.mkdir(); // Create the resources directory if it doesn't exist
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Player player : players) {
                // Check if player name or balance is null
                if (player.getName() == null || player.getBalance() < 0) {
                    System.out.println("Invalid player information: " + player);
                    continue; // Skip writing this player's information
                }
                
                writer.write(player.getName() + "," + player.getBalance());
                writer.newLine();
            }
            System.out.println("Player wallet information has been written to: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

