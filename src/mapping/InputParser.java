package mapping;

import auxiliaries.DBUtils;
import auxiliaries.FileIO;
import java.io.BufferedReader;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

/**
 * This class retrieves useful information from the csv file of Tweets
 */
public class InputParser {

    private static String fileName = "text_files\\mapping_corpus\\tweets.tsv";
    
    private static List<Tweet> getTweetList() throws Exception{
        ArrayList<Tweet> tweets = new ArrayList<>();
        FileIO io = new FileIO();
        BufferedReader in = io.getBufferedReader(fileName);
        String line;
        while ((line = in.readLine()) != null) {
            String[] items = line.split("\t");
            if (items.length < 3) continue;
            Tweet tweet = new Tweet();
            tweet.id = items[0];
            tweet.text = items[1];
            tweet.geolocation = items[2];
            tweets.add(tweet);
        }
        return tweets;
    }
    
    private static List<Tweet> getTweetsOnTopic(List<Tweet> tweets, String[] topics) {
        List<Tweet> selectedTweets = new ArrayList<>();
        for (Tweet tweet: tweets) {
            for (String topic : topics) {
                if (tweet.text.toLowerCase().contains(topic)) {
                    tweet.topic = topics[0];
                    selectedTweets.add(tweet);
                    break;
                }
            }
        }
        return selectedTweets;
    }
    
    private static void updateDB(List<Tweet> tweets, String table) throws Exception{
        DBUtils db = new DBUtils();
        for (Tweet tweet : tweets) {
            String insertion = "INSERT INTO " + table + " (ID, ORIGINAL_TEXT, GEOLOCATION, TOPIC) VALUES (?,?,?,?)";
            PreparedStatement stmt = db.getPreparedStatement(insertion);
            stmt.setString(1, tweet.id);
            stmt.setString(2, tweet.text);
            stmt.setString(3, tweet.geolocation);
            stmt.setString(4, tweet.topic);
            stmt.execute();
        }
    }
    
    public static void main(String[] args) throws Exception{
        List<Tweet> tweets = getTweetList();
        System.out.println(tweets.size());
        String table = "THE_SUN";

        String[] ids = {"the_guardian","theguardians"};
        List<Tweet> selection = getTweetsOnTopic(tweets, ids);
        System.out.println("Number of selected tweets: " + selection.size());
        //updateDB(selection, table);
    }
}
