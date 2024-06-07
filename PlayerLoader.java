
package game;

/**
 *
 * @author dexter
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

/**
 * Class responsible for loading players with wallet information from the database.
 */
public class PlayerLoader
{
    /**
     * Loads players with wallet information from the database.
     * 
     * @return A list of players with wallet information.
     */
    public List<PlayerWithWallet> loadPlayersWithWallet() 
    {
        List<PlayerWithWallet> players = new ArrayList<>();
        String sql = "SELECT * FROM Player";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
             // Iterate through the result set and create PlayerWithWallet objects
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                BigDecimal wallet = rs.getBigDecimal("wallet");
                PlayerWithWallet player = new PlayerWithWallet(id, name, wallet.intValue());
                players.add(player);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }
}

