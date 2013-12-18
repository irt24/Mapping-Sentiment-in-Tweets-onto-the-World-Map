package supervised.classification;

import auxiliaries.DBUtils;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

/**
 * This class represents a centralised dictionary for the supervised system,
 * taking into consideration all the terms that come up in the data corpus.
 * Terms refer to:
 * 1. unigrams
 * 2. bigrams
 */
public class CentralisedDictionary {
    
    // All words in the data corpus that are not stop words
    private TreeSet<String> allWords = new TreeSet<>();
    
    // A hash map with key = word and value = rank of word in the dictionary
    private HashMap<String, Integer> wordRanks = new HashMap<>();
    
    private List<String> getWordsFromTweet(String text) {
        return Arrays.asList(text.split(","));
        /*
        // Add bigrams
        String[] items = text.split(",");
        ArrayList<String> list = new ArrayList<>();
        String prev_item = null;
        for (String item : items) {
            if (prev_item != null) list.add(prev_item + "_" + item);
            prev_item = item;
        }
        return list;
        */
    }

    private void rankWordsFromTweets(String column, String table) throws Exception {
        String selectString = "SELECT " + column + " FROM " + table;
        DBUtils db = new DBUtils();
        ResultSet resultSet = db.select(selectString);
        while (resultSet.next()) {
            String text = resultSet.getString(column);
            allWords.addAll(getWordsFromTweet(text));
        } 
        int count = 1;
        for (String word : allWords) {
            if (word.contains("!")) word = word.replace("!","");
            wordRanks.put(word, count++);
        }
    }
 
    public CentralisedDictionary(String column, String table) throws Exception{
        System.out.println("Creating the dictionary...");
        rankWordsFromTweets(column, table);
        System.out.println("Dictionary created.");
    }
    
    public HashMap<String, Integer> getRankHashMap() {
        return wordRanks;
    }
    
    public int getNumberOfEntries() {
        return wordRanks.size();
    }
}