package preprocessing;

import auxiliaries.FileIO;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.TreeSet;

/**
 * This represents a collection of stop words
 */
public class StopWordsList {
    
    private final String filePath = "text_files\\dictionaries\\stopWords";
    private TreeSet<String> bagOfWords = new TreeSet<>();

    public StopWordsList() throws IOException{
        BufferedReader in = new FileIO().getBufferedReader(filePath);
        String stopWord;
        while ((stopWord = in.readLine()) != null) {
            bagOfWords.add(stopWord);
        }
    }
    
    public TreeSet<String> getWords() {
        return bagOfWords;
    }
}
