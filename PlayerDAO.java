/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

public class PlayerDAO {
    public void addPlayer(PlayerWithWallet player) {
        String sql = "INSERT INTO Player (name, wallet) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, player.getName());
            stmt.setInt(2, player.getWallet());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Player getPlayer(int id) throws SQLException {
        String sql = "SELECT * FROM Player WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Player(rs.getInt("id"), rs.getString("name"), rs.getInt("wallet"));
                }
            }
        }
        return null;
    }
    
    public void updateWallet(int playerId, int newWalletAmount) {
        String sql = "UPDATE Player SET wallet = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, newWalletAmount);
            stmt.setInt(2, playerId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deletePlayer(String name) {
        String sql = "DELETE FROM Player WHERE name = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, name);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<PlayerWithWallet> loadPlayersWithWallet() {
        List<PlayerWithWallet> players = new ArrayList<>();
        String sql = "SELECT * FROM Player";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

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

