
package game;

/**
 *
 * @author chris
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerDAO 
{
    /**
    * Adds a player to the database.
    * 
    * @param player The player to add.
    */
    public void addPlayer(PlayerWithWallet player) 
    {
        String sql = "INSERT INTO Player (name, wallet) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, player.getName());
            stmt.setInt(2, player.getWallet());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
    * Retrieves a player from the database by ID.
    * 
    * @param id The ID of the player to retrieve.
    * @return The player retrieved from the database, or null if not found.
    * @throws SQLException If an SQL exception occurs.
    */
    public Player getPlayer(int id) throws SQLException {
        String sql = "SELECT * FROM Player WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Player(rs.getInt("id"), rs.getString("name"), rs.getInt("wallet"));
                }
            }
        }
        return null;
    }
    
    /**
    * Updates the wallet amount of a player in the database.
    * @param playerId The ID of the player whose wallet is to be updated.
    * @param newWalletAmount The new wallet amount to be set.
    */
    public void updateWallet(int playerId, int newWalletAmount) {
        String sql = "UPDATE Player SET wallet = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newWalletAmount);
            stmt.setInt(2, playerId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
    * Deletes a player from the database by name.
    * 
    * @param name The name of the player to be deleted.
    */
    public void deletePlayer(String name) {
        String sql = "DELETE FROM Player WHERE name = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
    * Loads players with their wallets from the database.
    * 
    * @return A list of players with their wallets.
    */
    public List<PlayerWithWallet> loadPlayersWithWallet() {
        List<PlayerWithWallet> players = new ArrayList<>();
        String sql = "SELECT * FROM Player";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int wallet = rs.getInt("wallet");
                players.add(new PlayerWithWallet(id, name, wallet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }
}
