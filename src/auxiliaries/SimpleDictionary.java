package auxiliaries;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.TreeSet;

/*
 * This class represents a dictionary that maps words to numerical values.
 * It can be used, for instance, for the following purposes:
 * <ul>
 *  <li> Sentiment dictionary: words are associated with a polarity score; </li>
 *  <li> Frequency dictionary: words are associated with their frequency in the data corpus </li>
 * </ul>
 */

public class SimpleDictionary extends Dictionary {
    
    private HashMap<String, Double> wordsAndScores;
    
    public SimpleDictionary(String file) {
        
        fileName = file;
        // Open dictionary file
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch(FileNotFoundException e) {
            System.out.println("Dictionary not found.");
            return;
        }
        
        // Fill in the fields
        words = new TreeSet<>();
        wordsAndScores = new HashMap<>();
        try {
            String line;
            while ((line = br.readLine()) != null){
                StringTokenizer st = new StringTokenizer(line);
                String letters = st.nextToken();
                String valueString = st.nextToken();
                double value;
                try {
                    value = Double.parseDouble(valueString);
                } catch(NumberFormatException e) {
                    value = 0;
                    // If the value of the word is badly formatted, default to 0
                }
                words.add(letters);
                wordsAndScores.put(letters, value);
            }
        } catch (IOException e) {
            System.out.println("Could not read from the dictionary");
        }
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public HashMap<String, Double> getWordToValueMap() {
        return wordsAndScores;
    }
    
    @Override
    public TreeSet<String> getWords() {
        return words;
    }
}
