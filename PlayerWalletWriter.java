/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileWriter;
import java.util.List;

/**
 *
 * @author chris
 */
class PlayerWalletWriter {
    public static void writeToFile(List<Player> players) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("playerWallets.txt"))) {
            for (Player player : players) {
                writer.write(player.getName() + "," + player.getBalance());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
