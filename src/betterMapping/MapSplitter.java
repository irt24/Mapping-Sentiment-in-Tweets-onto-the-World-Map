package betterMapping;

import java.util.List;

public class MapSplitter {
    
    private double squareEdge;
    private int nHorizontal;
    private int nVertical;
    
    public MapSplitter(double squareEdge) {
        this.squareEdge = squareEdge;
        nHorizontal = (int)(Map.length / squareEdge);
        nVertical = (int)(Map.height / squareEdge);
    }
    
    public Square getSquare(double longitude, double latitude) {
        // Work with positive values:
        longitude += 180;
        latitude += 90;
        
        int horizontalIndex = (int) (longitude/squareEdge);
        int verticalIndex = (int) (latitude/squareEdge);
        
        Square square = new Square(horizontalIndex, verticalIndex ,squareEdge);
        return square;
    }
    
    public double[][] getGrid(List<Tweet> tweets) {
        double[][] nTweets = new double[nVertical][nHorizontal];
        double[][] grid = new double[nVertical][nHorizontal];
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = 0;
                nTweets[i][j] = 0;
            }
        for (Tweet tweet : tweets) {
            Square square = getSquare(tweet.longitude, tweet.latitude);
            int x = nVertical-1-square.verticalIndex();
            int y = square.horizontalIndex();
            
            switch (tweet.polarity){
                case "positive": {grid[x][y]++; nTweets[x][y]++;} break;
                case "negative": {grid[x][y]--; nTweets[x][y]++;} break;
            }
        }
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid[0].length; j++) {
                if (nTweets[i][j] != 0) {
                    grid[i][j] /= nTweets[i][j];
                }
            }
        return grid;
    }
    
    public double getSquareEdge() {
        return squareEdge;
    }
    public int getNHorizontal() {
        return nHorizontal;
    }
    public int getNVertical() {
        return nVertical;
    }
}
