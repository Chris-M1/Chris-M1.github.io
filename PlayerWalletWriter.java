
package game;

/**
 *
 * @author chris
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class PlayerWalletWriter 
{
    // Define the resources directory path
    private static final String resourcesDirPath = "./resources"; 
    // Define the file path
    private static final String filePath = resourcesDirPath + "/Players.txt"; 
    
    
    /**
    * Writes player information to a file.
    * 
    * @param players The list of players, which can include subclasses of Player.
    */
    public static void writeToFile(List<? extends Player> players)
    { 
        // Accept any subclass of Player
        // Check if the player list is empty
        if (players.isEmpty()) 
        {
            System.out.println("No player information provided. File will not be created.");
            return; // Exit the method if there are no players
        }
        
        // Create a File object representing the resources directory
        File resourcesDir = new File(resourcesDirPath);
        if (!resourcesDir.exists()) 
        {
            // Create the resources directory if it doesn't exist
            boolean dirCreated = resourcesDir.mkdir(); 
            
            // Check if the directory creation failed
            if (!dirCreated) 
            {
                System.out.println("Failed to create the resources directory.");
                return; // Exit if unable to create the directory
            }
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) 
        {
            for (Player player : players) 
            {
                // Use instanceof to determine the specific subclass and handle accordingly
                if (player instanceof PlayerWithWallet) 
                {
                    PlayerWithWallet pWallet = (PlayerWithWallet) player;
                    // Check if player name is null or wallet balance is negative
                    if (pWallet.getName() == null || pWallet.getWallet() < 0) 
                    {
                        System.out.println("Invalid player information: " + pWallet);
                        continue; // Skip writing this player's information
                    }
                    
                    writer.write(pWallet.getName() + "," + pWallet.getWallet());
                    writer.newLine();
                } else {
                    // Fallback for players without wallets, if such exist
                    if (player.getName() == null) {
                        System.out.println("Invalid player information: " + player);
                        continue;
                    }
                    
                    writer.write(player.getName() + ",0"); // Assume zero balance for players without wallet
                    writer.newLine();
                }
            }
            System.out.println("Player wallet information has been written to: " + filePath);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }
    
    /**
    * Gets the file path where player wallet information is stored.
    * 
    * @return The file path.
    */
    public static String getFilePath() {
        return filePath; // Ensure filePath is accessible
    }
}
