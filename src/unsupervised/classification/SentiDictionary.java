package unsupervised.classification;

import auxiliaries.Dictionary;
import auxiliaries.FileIO;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * This class processes the SentiWordNet dictionary and brings it to the following form:
 * word <tab> positive score <tab> negative score <tab> objective score
 * Note that for each word, positive score + negative score + objective score
 */
public class SentiDictionary extends Dictionary {
    
    // This datastructure is redundant in the sense that it stores the word twice
    private static HashMap<String,SentiWord> entries = new HashMap<>();
    
    public SentiDictionary() {
        fileName = "text_files\\dictionaries\\dictionary3";
        
        try {
        FileIO io = new FileIO();
        BufferedReader in = io.getBufferedReader(fileName);
        
        String line;
        while ((line = in.readLine()) != null) {
            String[] items = line.split("\\t");
            String word = items[0];
            double posScore = Double.parseDouble(items[1]);
            double negScore = Double.parseDouble(items[2]);
            double objScore = Double.parseDouble(items[3]);
            entries.put(word, new SentiWord(word, posScore, negScore, objScore));
            words.add(word);
        }
        
        } catch (IOException e) {
            System.out.println("SentiWordDicitionary file cannot be found.");
        }
    }
    
    public HashMap<String, SentiWord> getEntries() {
        return entries;
    }
}
