package auxiliaries;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/*
 * This class fetches tweets from the database and puts them into a file.
 * This file will be imported by the online database, for annotation.
 * Runtime arguments:
 * 1. Topic string, with words being separated by "_"
 * 2. Name of the table in the online database where the data is moved
 */
public class DumpDB2File {
    
    private static Connection conn;
    private static String path = "text_files\\auxiliaries\\dump.txt";
    
    public static void main(String[] args) throws Exception{
        /*
        if (args.length < 2) {
            System.out.println("No arguments provided");
            return;
        }*/
        
        // Fetch from database
        conn = new MyConnection().getConnection();
        //String queryString = args[0].replaceAll("_", " ");
        String selectString = "SELECT GEOLOCATION FROM TWITTER_DATA";
        //String selectString = "SELECT ID, TEXT, QUERY FROM TWEETS WHERE QUERY = '" +
        //        queryString + "'";
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(selectString);
        
        // Write to file  
        FileWriter fw = new FileWriter(path);
        
        // First, insert the SQL code:
        //String sql = "INSERT INTO " + args[1] + "(ID, TEXT, TOPIC) VALUES ";
        String sql = "INSERT INTO OBAMA(GEOLOCATION) VALUES ";
        fw.write(sql);
        
        int count = 0;
        StringBuilder item;
        // This boolean is meant to control when the commma is added.
        boolean first = true;
        
        while(resultSet.next()) {
            item = new StringBuilder();
            if (!first) {
                item.append(",");
            }
            first = false;
            item.append("('");
            /*
            item.append(resultSet.getString("ID").replaceAll("'", "''"));
            item.append("', '");
            item.append(resultSet.getString("TEXT").replaceAll("'", "''").replaceAll("\"","''"));
            item.append("', '");
            String query = resultSet.getString("QUERY").replaceAll("'", "''"); 
            item.append(query.replaceAll("%20"," "));
            item.append("')");
            */
            item.append(resultSet.getString("GEOLOCATION").replaceAll("'", "''"));
            item.append("')");
            count ++;
            fw.write(item.toString());
            fw.write(System.getProperty("line.separator"));
        }
        fw.close();
        
        // Print the number of items fetched from the database
        System.out.println("Count: " + count);
    }
}
