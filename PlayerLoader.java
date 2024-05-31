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

public class PlayerLoader {
    private PlayerDAO playerDAO;

    public PlayerLoader() {
        this.playerDAO = new PlayerDAO();
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
                PlayerWithWallet player = new PlayerWithWallet(name, wallet);
                players.add(player);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }
}

