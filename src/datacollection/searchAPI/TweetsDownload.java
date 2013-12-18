package datacollection.searchAPI;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import twitter4j.Query;
import twitter4j.Tweet;
import twitter4j.Twitter;

/*
 * This class downloads the tweets and updates the database.
 */
public class TweetsDownload{
    
    private Twitter twitter;
    private Query query;
    
    private void insertRecord(Connection conn, Tweet tweet, String place) throws Exception {
        StringBuilder columns = new StringBuilder();
        List<Object> values = new LinkedList<>();
        values.add(tweet.getAnnotations());             columns.append("(ANNOTATIONS, ");
        values.add(tweet.getCreatedAt());               columns.append("CREATED_AT, ");          
        values.add(tweet.getFromUser());                columns.append("FROM_USER, ");
        values.add(tweet.getFromUserId());              columns.append("FROM_USER_ID, ");
        values.add(tweet.getFromUserName());            columns.append("FROM_USER_NAME, ");
        values.add(tweet.getGeoLocation());             columns.append("GEO_LOCATION, ");
        values.add(tweet.getHashtagEntities());         columns.append("HASHTAG_ENTITIES, ");
        values.add(tweet.getId());                      columns.append("ID, ");
        values.add(tweet.getInReplyToStatusId());       columns.append("IN_REPLY_TO_STATUS_ID, ");
        values.add(tweet.getIsoLanguageCode());         columns.append("ISO_LANGUAGE_CODE, ");
        values.add(tweet.getLocation());                columns.append("LOCATION, ");
        values.add(tweet.getPlace());                   columns.append("PLACE, ");
        String img = tweet.getProfileImageUrl();        
        if (img.length() <= 150) {  
            values.add(img);                            columns.append("PROFILE_IMAGE_URL, ");
        } else {
            values.add(img.substring(150));
        }
        values.add(tweet.getSource());                  columns.append("SOURCE, ");
        values.add(tweet.getText());                    columns.append("TEXT, ");
        values.add(tweet.getToUser());                  columns.append("TO_USER, ");
        values.add(tweet.getToUserId());                columns.append("TO_USER_ID, ");
        values.add(tweet.getToUserName());              columns.append("TO_USER_NAME, ");
        values.add(tweet.getUserMentionEntities());     columns.append("USER_MENTION_ENTITIES, ");
        values.add(query.getQuery());                   columns.append("QUERY, ");
        values.add(place);                              columns.append("COUNTRY)");
        
        // Do not attempt to insert duplicates
        String selectString = "SELECT COUNT(*) FROM TWEETS WHERE ID='" + tweet.getId() + "'";
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(selectString);
        if ((resultSet.next()) && (resultSet.getInt(1) > 0)) {
            //System.out.println("Duplicate detected!" + tweet.getId());
            return;
        }
       
        StringBuilder insertString = new StringBuilder("INSERT INTO TWEETS" + columns + " VALUES (");
        for (Object v : values) {
            insertString.append("'");
            if (v != null) {
                insertString.append(v.toString().replaceAll("'", "''"));
            }
            insertString.append("',");
        }
        // remove unnecessary comma
        insertString.deleteCharAt(insertString.length() - 1);
        insertString.append(")");
        
        stmt = conn.createStatement();
        stmt.executeUpdate(insertString.toString());
        if (stmt != null) {
            stmt.close();
        }
        //System.out.println(tweet.getId() + "toDB: " + insertString);
    }
    
    public TweetsDownload(Connection conn, Twitter twitter, Query query, String place) throws Exception {
        this.twitter = twitter;
        this.query = query;
        List<Tweet> tweets = twitter.search(query).getTweets();
        //System.out.println(place + ":" + tweets.size() + " tweets found.");
        for (Tweet tweet : tweets) {
            //System.out.println(place);
            this.insertRecord(conn, tweet, place);
        }
    }
}
