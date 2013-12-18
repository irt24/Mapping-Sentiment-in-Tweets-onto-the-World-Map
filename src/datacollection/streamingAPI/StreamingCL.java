package datacollection.streamingAPI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;


public class StreamingCL {
    
    private static String driver = "com.mysql.jdbc.Driver";
    private static String DBname;
    private static String DBhost;
    private static String userName;
    private static String password;
    private static String topic;
    private static String[] trackWords;
    private static Connection conn;
    
    public static String[] stringToArray(String str) {
        String[] array = str.split(",");
        for (String s : array) {
            s = s.replaceAll("_"," ");
        }
        return array;
    }
    
    private static void parseArguments(String[] args) {
        DBname = args[0];
        DBhost = args[1];
        userName = args[2];
        password = args[3];
        topic = args[4];
        trackWords = stringToArray(topic);
    }

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        
        String driver = "com.mysql.jdbc.Driver";
        Class.forName(driver);
        String url = "jdbc:mysql://"+DBhost+":3306/"+DBname;

        Properties connectionProps = new Properties();
        connectionProps.put("user", userName);
        connectionProps.put("password", password);
        Connection conn = DriverManager.getConnection(url, connectionProps);
        System.out.println("Connected to database");
        return conn;
    }

     public static TwitterStream getTwitterStream() {
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        String consumerKey = "S5A8B7h3nNzZJMw9eKw7iQ";
        String consumerSecret = "rbkL8LAw78hmcD1KSScS4szMczD9tIGCLLpGDO9N8";
        String token = "912605168-Khgh84JtER5SBaAX848rIAW3LzL8eIbjOpSxO0qS";
        String tokenSecret = "VY5t4SdrDs6uKfqv9oOfAJTfzusMIM5sJBQr4eixVvw";
        AccessToken accessToken = new AccessToken(token, tokenSecret);
        twitterStream.setOAuthConsumer(consumerKey, consumerSecret);
        twitterStream.setOAuthAccessToken(accessToken);
        return twitterStream;
    } 
     
    public static void insertIntoDB(Status status) {
        String id = Long.toString(status.getId());
        String text = status.getText().replaceAll("'", "''");
        GeoLocation geolocation = status.getGeoLocation();
        String geoString = "";
        if (geolocation != null) {
            geoString = geolocation.toString();
        }
        StringBuilder update = new StringBuilder("INSERT INTO TWEETS(ID, TEXT, TOPIC, GEOLOCATION) VALUES ('" + id + "','" + text + "','" + topic + "','" + geoString + "')");
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(update.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void doStreaming() {
        TwitterStream twitterStream = getTwitterStream();
        
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
                insertIntoDB(status);
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                //System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                //System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                //System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        twitterStream.addListener(listener);
        FilterQuery fq = new FilterQuery();
        fq.track(trackWords);
        twitterStream.filter(fq);
    }
    
    public static void main(String[] args) {
        
        // Parse arguments
        if (args.length < 5) {
            System.out.println("Missing arguments");
            return;
        } else {
           parseArguments(args); 
        }
        
        // Connect to the database
        try {
            conn = getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Streaming
        doStreaming();
    }
}
