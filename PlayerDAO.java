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
import java.math.BigDecimal;

public class PlayerDAO {
    public void addPlayer(Player player) throws SQLException {
        String sql = "INSERT INTO Player (name, wallet) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, player.getName());
            stmt.setInt(2, player.getWallet());
            stmt.executeUpdate();
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

    // Additional methods for update and delete operations
}

