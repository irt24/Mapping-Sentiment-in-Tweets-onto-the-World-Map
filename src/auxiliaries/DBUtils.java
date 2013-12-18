package auxiliaries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Useful operations on the database
 */
public class DBUtils {
    
    private Connection conn;
    
    public DBUtils() throws SQLException {
        conn = new MyConnection().getConnection();
    }

    public ResultSet select(String selectionString) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(selectionString);
        return resultSet;
    }
    
    public void update(String updateString) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(updateString);
    }
    
    public PreparedStatement getPreparedStatement(String stmt) throws SQLException {
        return conn.prepareStatement(stmt);
    }
}
