package unsupervised.classification;

import auxiliaries.DBUtils;
import auxiliaries.SimpleDictionary;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

/**
 * The purpose of this class is to find terms that are widely used on Twitter,
 * but not covered in SentiWordNet
 */
public class SlangFinder {
    
    private static HashMap<String, Integer> slangs = new HashMap<>();
    private static TreeSet<String> knownWords;

    public static void main(String[] args) throws Exception {
        
        // Put all words not recognised by SentiWordNet into a hash map
        SimpleDictionary dictionary = new SimpleDictionary("text_files\\dictionaries\\dictionary123");
        knownWords = dictionary.getWords();
        DBUtils db = new DBUtils();
        String selectStr = "SELECT FORMATTED_TEXT FROM SUPERVISED";
        ResultSet rs = db.select(selectStr);
        while (rs.next()) {
            String text = rs.getString("FORMATTED_TEXT");
            String[] terms = text.split(",");
            for (String term : terms) {
                if (!knownWords.contains(term))
                if (slangs.containsKey(term)) {
                    int freq = slangs.get(term);
                    slangs.remove(term);
                    slangs.put(term, ++freq);
                } else {
                    slangs.put(term, 1);
                }
            }
        }
        
        // Iterate over the hash map and print those slangs that appear three or more times 
        Iterator it = slangs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            if ((int)pairs.getValue() >= 3) 
                System.out.println(pairs.getKey() + " " + pairs.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }
}
