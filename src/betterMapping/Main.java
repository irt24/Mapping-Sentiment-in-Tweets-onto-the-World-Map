package betterMapping;

import auxiliaries.DBUtils;
import auxiliaries.FileIO;
import java.io.BufferedWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Main {
    
    private static final double squareEdge = 0.2;
    private static final String table = "OBAMA";
    private static final MapSplitter ms = new MapSplitter(squareEdge);

    public static List<Tweet> getListOfTweets(String table) throws Exception {
        ArrayList<Tweet> list = new ArrayList<>();
        DBUtils db = new DBUtils();
        String select = "SELECT * FROM " + table;// + " WHERE ENGLISH = 1";
        ResultSet rs = db.select(select);
        while (rs.next()) {
            Tweet tweet = new Tweet();
            tweet.polarity = rs.getString("SUPERVISED_LABEL");
            tweet.longitude = rs.getDouble("LONGITUDE");
            tweet.latitude = rs.getDouble("LATITUDE");
            list.add(tweet);
        }
        return list;
    }    
    
    public static void printToFile(String fileName, double[][] grid) throws Exception {
        FileIO io = new FileIO();
        BufferedWriter out = io.getBufferedWriter(fileName);
        out.write("longitude,latitude,score,polarity");
        out.write(System.getProperty("line.separator"));
        // For now, represent squares by their center
        System.out.println(grid.length);
        System.out.println(grid[0].length);
        for (int i = grid.length-1; i >= 0; i--)
            for (int j = 0; j < grid[0].length; j++) {
                Square square = new Square(j,i, squareEdge);
                String polarity = null;
                if (grid[i][j] < 0) polarity = "negative";
                if (grid[i][j] > 0) polarity = "positive";
                if (grid[i][j] != 0) {
                    out.write(  square.center().getX()+","+
                                square.center().getY()+","+
                                Math.abs(grid[i][j])+","+
                                polarity);
                    out.write(System.getProperty("line.separator"));   
                }
            }
        out.close();
    }
    
    public static void main(String[] args) throws Exception {
        List<Tweet> tweets = getListOfTweets(table);
        double[][] grid = ms.getGrid(tweets);
        printToFile("text_files\\betterMapping\\test.csv", grid);
    }
}
