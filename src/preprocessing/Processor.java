package preprocessing;

import auxiliaries.DBUtils;
import auxiliaries.Pair;
import auxiliaries.Tweet;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.TreeSet;

/*
 * This class updates the database by formatting the Tweets.
 */
public class Processor {
    
    private static ArrayList<Tweet> tweets = new ArrayList<>();
    private static DBUtils db;
    private static String table = "SUPERVISED";
    
    private static void updateDB(String id, String formatted, String column) throws Exception{
        formatted = formatted.replaceAll("'","''");
        String updateString = "UPDATE " + table + " SET " + column + " = ? WHERE ID = ?";
        PreparedStatement ps = db.getPreparedStatement(updateString);
        ps.setString(1, formatted);
        ps.setString(2, id);
        ps.execute();        
    }
    
    private static void processBunch(String column, String whereClause) throws Exception {
        // Fetch Tweets from the database
        db = new DBUtils();       
        String selectString = "SELECT ID, TEXT FROM " + table + whereClause;
        ResultSet resultSet = db.select(selectString);
        while (resultSet.next()) {
            tweets.add(new Tweet(resultSet.getString("ID"), resultSet.getString("TEXT")));
        }

        ArrayList<Pair> emoticons = new EmoticonDictionary().getEmoticons();
        // Process the text of each Tweet
        for (Tweet tweet : tweets) {
            boolean removeEmoticons = whereClause.contains("emoticon");
            ProcessedTweet pt = null;
            if (column.equals("U_FORMATTED")) {
                TreeSet<String> stopWords = new StopWordsList().getWords();
                pt = new UnsupervisedProcessedTweet(tweet.getId(), tweet.getOriginalText(),
                        emoticons, removeEmoticons, stopWords); 
            }
            if (column.equals("S_FORMATTED")) {
                pt = new SupervisedProcessedTweet(tweet.getId(), tweet.getOriginalText(),
                        emoticons, removeEmoticons);
            }
            updateDB(pt.getId(), pt.getFormattedText(), column);
        }  
    }

    public static void main(String[] args) throws Exception{
        //mute the Stanford library
        PrintStream dummy = null;
        try {
            dummy = new PrintStream("text_files\\dummy");
        } catch (FileNotFoundException e) {}
        System.setOut(dummy);
        System.setErr(dummy);

        //process unsupervised
        processBunch("U_FORMATTED", " WHERE TOPIC = 'emoticon' ");
        processBunch("U_FORMATTED", " WHERE TOPIC <> 'emoticon' ");
        
        //process supervised
        processBunch("S_FORMATTED", " WHERE TOPIC = 'emoticon' ");
        processBunch("S_FORMATTED", " WHERE TOPIC <> 'emoticon' ");
    }
}