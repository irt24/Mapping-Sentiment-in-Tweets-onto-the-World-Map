package betterMapping;

import auxiliaries.DBUtils;
import auxiliaries.SimpleDictionary;
import java.sql.ResultSet;
import java.util.TreeSet;

/**
 * Marks English Tweets
 */
public class EnglishSelector {
    
    private static final String table = "OBAMA";
    
    public static void main(String[] args) throws Exception{
        SimpleDictionary dictionary = new SimpleDictionary("text_files\\dictionaries\\dictionary3");
        TreeSet<String> knownWords = dictionary.getWords();
        
        DBUtils db = new DBUtils();
        String select = "SELECT ID, FORMATTED_TEXT FROM " + table;
        ResultSet rs = db.select(select);
        while (rs.next()) {
            String id = rs.getString("ID");
            String text = rs.getString("FORMATTED_TEXT");
            String[] words = text.split(",");
            int english = 0;
            for (String word:words) {
                if (knownWords.contains(word)) english++;
                if (english >= 2) break;
            }
            String update = "UPDATE " + table + " SET ENGLISH = " + english + 
                    " WHERE ID = '" + id + "'";
            db.update(update);
        }
        
    }
}
