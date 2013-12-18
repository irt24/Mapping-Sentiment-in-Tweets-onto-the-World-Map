package mapping;

import auxiliaries.DBUtils;
import auxiliaries.Pair;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import preprocessing.EmoticonDictionary;
import preprocessing.SupervisedProcessedTweet;

/**
 * Running this class results in a file containing data ready to be plotted.
 */
public class Plotter {
    
    private static String table = "QUEEN";
    
    // Auxiliary tools
    private static DBUtils db;
    
    /*
     * Format the tweets and update the corresponding column in the database
     */
    private static void formatTweets() throws Exception{
        String selectString = "SELECT ID, ORIGINAL_TEXT FROM " + table;
        ResultSet resultSet = db.select(selectString);
        String update = "UPDATE "+table+" SET FORMATTED_TEXT = ? WHERE ID = ?";
        PreparedStatement stmt = db.getPreparedStatement(update);
        ArrayList<Pair> emoticons = new EmoticonDictionary().getEmoticons();
        while (resultSet.next()) {
            String id = resultSet.getString("ID");
            String originalText = resultSet.getString("ORIGINAL_TEXT");
            String text = new SupervisedProcessedTweet(
                    id, originalText, emoticons, false).getFormattedText();
            if (text.length() > 200) text = text.substring(0, 200);
            stmt.setString(1, text);
            stmt.setString(2, id);
            stmt.execute();
        }
        System.out.println("Tweets were formatted");
    }
    
    public static void setCoordinates() throws Exception{
        String selectString = "SELECT ID, GEOLOCATION FROM " + table;
        ResultSet resultSet = db.select(selectString);
        String update = "UPDATE "+table+" SET LATITUDE = ?, LONGITUDE = ? WHERE ID = ?";
        PreparedStatement stmt = db.getPreparedStatement(update);
        while (resultSet.next()) {
            Geolocation location = new Geolocation(resultSet.getString("GEOLOCATION"));
            stmt.setDouble(1, location.getLatitude());
            stmt.setDouble(2, location.getLongitude());
            stmt.setString(3, resultSet.getString("ID"));
            stmt.execute();
        }
        System.out.println("Geographical coordinates were set");
    }
    
    public static void setSupervisedLabel() throws Exception {
        supervised.classification.SupervisedClassifier.perfromClassification(table);
        System.out.println("Supervised label was set");
    }
    
    public static void setUnsupervisedLabel() {
        
    }
    
    // Generate a file that contains data that can be uploaded onto srcf for mapping
    public static void exportTweets() throws Exception {
        TSV.databaseToTSV(table);
        GeoJSON.TSVToGeoJSON();
        System.out.println("GeoJSON file for mapping was exported");
    }

    public static void main(String[] args) throws Exception{
        System.out.println("And the magic begins...");
        
        // Initialise auxiliary tools
        db = new DBUtils();
        
        formatTweets();
        setCoordinates();
        setSupervisedLabel();
        //setUnsupervisedLabel();
        exportTweets();
    }
}
