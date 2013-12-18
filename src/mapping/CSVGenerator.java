package mapping;

import auxiliaries.DBUtils;
import auxiliaries.FileIO;
import auxiliaries.MyConnection;
import java.io.BufferedWriter;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Purpose of this class
 */
public class CSVGenerator {
    
        private static String path = "text_files\\mapping\\manchester.csv";
    
        public static void main(String[] args) throws Exception {
            FileIO io = new FileIO();
            BufferedWriter out = io.getBufferedWriter(path);
            out.write("latitude,longitude,polarity");
            out.write(System.getProperty("line.separator"));
            
            Connection conn = new MyConnection().getConnection();  
            DBUtils db = new DBUtils();
            String select = "SELECT LATITUDE, LONGITUDE, SUPERVISED_LABEL FROM TWITTER_DATA6";
            ResultSet resultSet = db.select(select);
            while (resultSet.next()) {
                out.write(resultSet.getString("LATITUDE") + 
                        "," + resultSet.getString("LONGITUDE") + 
                        "," + resultSet.getString("SUPERVISED_LABEL"));
                out.write(System.getProperty("line.separator"));
            }
            out.close();
        }
}
