package preprocessing;

import auxiliaries.FileIO;
import auxiliaries.Pair;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class represents the dicitionary of emoticons
 */
public class EmoticonDictionary {
    
    private final String filePath = "text_files\\dictionaries\\emoticons";
    private ArrayList<Pair> emoticons = new ArrayList<>();

    public EmoticonDictionary() throws IOException {
       FileIO io = new FileIO();
       BufferedReader br = io.getBufferedReader(filePath);
       String line;
       while ((line = br.readLine()) != null) {
           String[] items = line.split("\t");
           Pair pair = new Pair(items[0],items[1]);
           emoticons.add(pair);
       }
   }
    
   public ArrayList<Pair> getEmoticons() {
       return emoticons;
   }
    
}
