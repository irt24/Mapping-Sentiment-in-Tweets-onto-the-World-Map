package romanian;

import auxiliaries.DBUtils;
import auxiliaries.Dictionary;
import auxiliaries.SimpleDictionary;
import java.sql.ResultSet;
import java.util.TreeSet;

/**
 * Tests the tokenization process for Romanian Tweets
 */
public class Test {
    
    private static void getWords(String tweet) {
        RomPTweet pt = new RomPTweet(tweet);
        TreeSet<String> bagOfWords = pt.getBagOfWords();
        Dictionary dictionary = new SimpleDictionary("text_files\\romanian\\dictionaries\\dictionary");
        TreeSet<String> dWords = dictionary.getWords();
        System.out.println("Formatted text: " + pt.getFormattedText());
        System.out.println("Words in the tweet: ");
        for (String word : bagOfWords) {
            System.out.println(word);
        }
        System.out.println("Words recognized by the dictionary: ");
        for (String word : bagOfWords) {
            if (dWords.contains(word))
                System.out.println(word);
        }  
    }
    
    public static void main(String[] args) throws Exception{
        DBUtils db = new DBUtils();
        String select = "SELECT TEXT FROM SUPERVISED";
        ResultSet rs = db.select(select);
        while (rs.next()) {
            String tweet = rs.getString("TEXT");
            getWords(tweet);
        }
    }
}
