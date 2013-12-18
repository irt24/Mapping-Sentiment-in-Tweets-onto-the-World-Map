package mapping;

import auxiliaries.FileIO;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * This class converts a CSV file to GeoJSON format.
 * Call method execute to generate GeoJson file.
 * Format of TSV file:
 * TweetID <tab> TweetText <tab> TweetPolarity <tab> Latitude <tab> Longitude
 */
public class GeoJSON {
    
    private static BufferedReader in;
    private static BufferedWriter out;
    private static final String newLine = System.getProperty("line.separator");
    private static final String path = "text_files\\mapping\\";
    
    public static void printFilePreamble() throws IOException{
        out.write("{" + newLine);
        out.write("\t" + "\"type\":\"FeatureCollection\"," + newLine);
        out.write("\t" + "\"features\":[" + newLine);
    }
    
    public static void printFilePostamble() throws IOException{
        out.write(newLine);
        out.write("\t" + "]" + newLine);
        out.write("}");
    }
    
    public static void printTweet(String[] items, boolean first) throws IOException {
        // if line doesn't have all arguments, ignore it
       if (items.length < 5) {
            System.out.println("Skipped one tweet" + items[0]);
            return;
        }
        if (!first) out.write("," + newLine);
        out.write("\t\t" + "{" + newLine);
        out.write("\t\t\t" + "\"type\":\"Feature\"," + newLine);
        // items[0] = tweetId;
        out.write("\t\t\t" + "\"id\":\"" + items[0] + "\"," + newLine);
        out.write("\t\t\t" + "\"properties\":{" + newLine);
        // items[1] = tweetText;
        items[1] = items[1].replaceAll("\\\\","");
        items[1] = items[1].replaceAll("\"","");
        out.write("\t\t\t\t" + "\"text\":\"" + items[1] + "\"," + newLine);
        // items[2] = polarity;
        out.write("\t\t\t\t" + "\"polarity\":\"" + items[2] + "\"" + newLine);
        out.write("\t\t\t" + "}," + newLine);
        // items[3], items[4] = geographical coodrinates
        out.write("\t\t\t" + "\"geometry\":{\"type\":\"Point\",\"coordinates\":[" +
                items[4] + ", " + items[3] + "]}" + newLine);
        out.write("\t\t" + "}");
    }
    
    public static void TSVToGeoJSON() throws Exception{
        in = new FileIO().getBufferedReader(path + "tsv");
        out = new FileIO().getBufferedWriter(path + "tweets.json");
        printFilePreamble();
        String line;
        boolean first = true;
        while ((line = in.readLine()) != null) {
            printTweet(line.split("\\t"), first);
            first = false;
        }
        printFilePostamble();
        out.close();
    }
}