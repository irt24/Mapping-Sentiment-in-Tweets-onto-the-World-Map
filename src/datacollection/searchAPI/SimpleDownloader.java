package datacollection.searchAPI;

import auxiliaries.MyConnection;
import java.sql.Connection;
import twitter4j.Query;
import twitter4j.Twitter;

/**
 * This class fetches Tweets using the Search API.
 * Runtime arguments:
 * 1. A query string, with words separated by "_";
 * 2. The number of pages of Tweets (preferably below 10);
 * 3. The number of Tweets per page (between 0 and 100);
 */
public class SimpleDownloader {
    
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            return;
        }
        
        Connection conn = new MyConnection().getConnection();
        Twitter twitter = new AuthTwitter().getTwitter();
        
        String queryString = args[0].replaceAll("_","%20");
        int nPages = Integer.parseInt(args[1]);
        int nTweets = Integer.parseInt(args[2]);
        Query query = new Query();
        query.setQuery(queryString);
        query.setLang("en");
        query.setRpp(nTweets);
        for (int i = 1; i <= nPages; i++) {
            query.setPage(i);
            new TweetsDownload(conn, twitter, query, "");
        }
    }

}
