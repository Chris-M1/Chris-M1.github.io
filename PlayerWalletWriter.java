
package game;


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