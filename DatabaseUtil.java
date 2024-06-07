
package game;

/**
 *
 * @author chris
 */
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String DB_URL = "jdbc:derby:gameDB;create=true";
    private static Connection connection;

    // Static block to load the JDBC driver for Apache Derby database
    static 
    {
        try
        {
            // Attempt to load the EmbeddedDriver class from the Apache Derby library
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {
            // Print a stack trace if the driver class is not found
            e.printStackTrace();
        }
    }
    
    /**
    * Retrieves a connection to the database.
    * 
    * If no connection exists or the existing connection is closed, a new connection is established.
    * 
    * @return A connection to the database.
    * @throws SQLException If there's an issue accessing the database.
    */
    public static Connection getConnection() throws SQLException 
    {
        // If there's no active connection or it's closed, establish a new one
        if (connection == null || connection.isClosed())
        {
            // Create a connection to the database using the predefined URL
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }
    
    /**
    * Initializes the database by creating the necessary table(s) if they don't exist.
    * 
    * This method creates a "Player" table with columns for player ID, name, and wallet.
    */
    public static void initializeDatabase() 
    {
        // SQL statement to create the "Player" table
        String createTableSQL = "CREATE TABLE Player (" +
                "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
                "name VARCHAR(100)," +
                "wallet DECIMAL(10, 2)" +
                ")";
        try (Connection conn = getConnection(); 
             Statement stmt = conn.createStatement()) 
        {
           // Execute the SQL statement to create the table 
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            // Ignore the exception if the table already exists
            if (!e.getSQLState().equals("X0Y32")) 
            {
                e.printStackTrace();
            }
        }
    }
}


