package datacollection.streamingAPI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MyConnection {
   
   public Connection getConnection(String dbName, String host, String userName, String password) 
              throws SQLException, ClassNotFoundException {
        
        String driver = "com.mysql.jdbc.Driver";
        Class.forName(driver);
        String url = "jdbc:mysql://"+host+":3306/"+dbName;

        Properties connectionProps = new Properties();
        connectionProps.put("user", userName);
        connectionProps.put("password", password);
        Connection conn = DriverManager.getConnection(url, connectionProps);
        System.out.println("Connected to database");
        return conn;
    }
}
