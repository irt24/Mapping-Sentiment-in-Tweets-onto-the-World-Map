package auxiliaries;
/*
 * This class supports connection to TwitterDB;
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MyConnection {
   private Connection conn;
   private final String userName = "irt24";
   private final String password = "raluplus";
   private final String url = "jdbc:derby://localhost:1527/TwitterDB";
   
   public Connection getConnection() throws SQLException {
        Properties connectionProps = new Properties();
        connectionProps.put("user", this.userName);
        connectionProps.put("password", this.password);
        conn = DriverManager.getConnection(this.url, connectionProps);
        //System.out.println("Connected to database");
        return conn;
    }
}
