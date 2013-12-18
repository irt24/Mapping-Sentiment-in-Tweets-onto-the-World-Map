package datacollection.searchAPI;

import auxiliaries.DBUtils;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;

/**
 * Download neutral Tweets from newspaper accounts
 * Runtime arguments:
 * 1. The username of the newspaper to be followed;
 * 2. The number of pages of Tweets (preferably below 10);
 */
public class TimelineDownloader {
    
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Insufficient arguments. Aborting");
            return;
        }
        
        //Connection conn = new MyConnection().getConnection();
        Twitter twitter = new AuthTwitter().getTwitter();
        
        String user = args[0];
        int nPages = Integer.parseInt(args[1]);
        Paging paging = new Paging(1, nPages);
        /*
        ResponseList<Status> userTimeline = twitter.getUserTimeline(user, paging);
        DBUtils db = new DBUtils();
        int count = 0;
        for (Status status : userTimeline) {
            // Insert the status into the database (two tables for safety)
            // The topic is the user (the newspaper)
            String text = status.getText().replaceAll("'","''");
            String update1 = "INSERT INTO SUPERVISED (ID, TEXT, TOPIC) VALUES ('" +
                    status.getId() + "', '" + text + "', '" + user + "')";
            //String update2 = "INSERT INTO TWEETS (ID, TEXT, QUERY) VALUES ('" +
            //        status.getId() + "', '" + text + "', '" + user + "')";
            db.update(update1);
            //db.update(update2);
            count++;
            System.out.println(count + ". " + text);
        }
        */
    }
}