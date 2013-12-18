package datacollection.streamingAPI;

import auxiliaries.MyConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;

public class Main {
    
    private static String[] topic = new String[1];
    private static int noKeywords;
    
    public static void setTopic() {
        topic[0] = "chocolate"; noKeywords++;
        //topic[1] = ":-)"; noKeywords++;
        //topic[2] = ":-("; noKeywords++;
        //topic[3] = ":("; noKeywords++;
    }
    
    public static String arrayToString(String[] array, int length) {
        StringBuilder toReturn = new StringBuilder();
        for (int i=0; i<length; i++) {
            toReturn.append(array[i]);
            if (i != length - 1) {
                toReturn.append(", ");
            }
        }
        return toReturn.toString();
    }
    
    public static void main(final String[] args) throws Exception {
        setTopic();
        
        final Connection conn = new MyConnection().getConnection();
        System.out.println("Starting to stream...");
        TwitterStream twitterStream = new AuthTwitterStream().getTwitterStream();
        
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
 
                String id = Long.toString(status.getId());
                String text = status.getText().replaceAll("'", "''");
                String topicString = arrayToString(topic, noKeywords);
                GeoLocation geolocation = status.getGeoLocation();
                String geoString = "";
                if (geolocation != null) {
                    geoString = geolocation.toString();
                }
                
                StringBuilder update = new StringBuilder("INSERT INTO STREAMINGTWEETS(ID, TEXT, TOPIC, GEOLOCATION) VALUES ('" + id + "','" + text + "','" + topicString + "','" + geoString + "')");
                try {
                    Statement stmt = conn.createStatement();
                    stmt.execute(update.toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        twitterStream.addListener(listener);

        FilterQuery fq = new FilterQuery();
        fq.track(topic);
        twitterStream.filter(fq);
    }
}
