package datacollection.searchAPI;

import auxiliaries.MyConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.Twitter;

/*
 * This is the application that fetches Tweets using the Twitter Search API, from all locations over the globe.
 * For simple fetches that do not require geographical precision, use SimpleMain.java
 * Runtime arguments:
 * 1. A string representing the query string, with words separated by "_"
 * 2. The number of pages of Tweets / location
 * 3. The number of Tweets / page
 */
public class ComplexDownloader {

    private static Connection conn;
    private static Twitter twitter;
    private static TweetsDownload td;
    private static String queryString;
    private static int pages;
    private static int tweetsPerPage;
    
    public static void twitterQueries(ResultSet resultSet, int page) throws Exception {
       while (resultSet.next()) {
            Query query = new Query();
            
            // set parameter q
            query.setQuery(queryString);
            
            // set parameter geocode
            Double lat = Double.valueOf(resultSet.getString("LATITUDE"));
            Double lon = Double.valueOf(resultSet.getString("LONGITUDE"));
            Double radius = Double.valueOf(resultSet.getString("RADIUS"));
            query.setGeoCode(new GeoLocation(lat, lon), radius, Query.KILOMETERS);
            
            // set parameter lang
            query.setLang("en");
            
            // set result_type parameter (possible values: "mixed, recent, popular")
            //query.setResultType("recent");
            
            // set count parameter
            query.setRpp(tweetsPerPage);
            
            // set index of page
            query.setPage(page);
            
            // set until parameter (Returns tweets generated before the given date)
            //query.setUntil("2012-11-21");
            
            // set since_id parameter (Returns tweets generated after the given date)
            //query.setSince("2011-11-11");
            
            // set since_id (Returns results with a bigger ID)
            //query.setSinceId(12345);
            
            // set max_id (Returns results with a smaller ID)
            //query.setMaxId(54321);  
            
            td = new TweetsDownload(conn, twitter, query, resultSet.getString("PLACE"));
        } 
    }
    
    public static void makeQueries(int page) throws Exception {
        String selectString = "SELECT PLACE, LATITUDE, LONGITUDE, RADIUS FROM PLACES WHERE TYPE='country'";
        // TYPE='state' AND REP = true";
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(selectString);
        twitterQueries(resultSet, page);
    }

    public static void main(String[] args) throws Exception {
        conn = new MyConnection().getConnection();
        twitter = new AuthTwitter().getTwitter();
        // need HTML encoding of space
        queryString = args[0].replaceAll("_","%20");
        pages = Integer.parseInt(args[1]);
        tweetsPerPage = Integer.parseInt(args[2]);
        for (int page = 1; page <=pages; page++) {
            makeQueries(page);
        }
    }
}
