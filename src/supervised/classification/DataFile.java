package supervised.classification;

import auxiliaries.DBUtils;
import auxiliaries.FileIO;
import java.io.BufferedWriter;
import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents an input file for the Support Vector Machine
 */
public class DataFile extends File{
    
    // The name of the data file
    private String fileName;
    private String table;
    private String textCol = "FORMATTED_TEXT";
    
    // Dictionary containing all the words in the corpus
    private CentralisedDictionary dictionary;
    
    // Hash map with key = line index in data file, and value = tweet id
    private HashMap<Integer, String> line2id = new HashMap<>();
    
    private void putDataIntoFile() throws Exception{
        FileIO io = new FileIO();
        BufferedWriter out = io.getBufferedWriter(fileName);
        
        DBUtils db = new DBUtils();
        String selectionString = "SELECT ID, "+textCol+" FROM " + table;
        ResultSet resultSet = db.select(selectionString);
        
        int count = 0;
        dictionary = new CentralisedDictionary(textCol, table);
        while (resultSet.next()) {
            count++;
            
            // Get tweet data
            String id = resultSet.getString("ID");
            String text = resultSet.getString(textCol);
            
            // Get list of rank of words that occur in the text
            // Properties of this list: already sorted, no duplicates.
            HashMap<Integer, Integer> ranksAndScores = WordRankMap.getRanksAndScores(dictionary, text);
            List<Integer> ranks = new ArrayList<>();
            ranks.addAll(ranksAndScores.keySet());
            Collections.sort(ranks);
            
            // Prevent blank rows and store (tweetID, line) correspondence
            if ((count != 1)&&(!ranks.isEmpty())) {
                out.write(System.getProperty("line.separator"));
                line2id.put(count, id);
            }
            if ((count == 1)&&(!ranks.isEmpty()))
                line2id.put(count, id);
                    
            for (Integer rank : ranks) {
                out.write(rank + ":" + ranksAndScores.get(rank) + "\t");
            }
        }
        out.close();
    }
    
    public DataFile(String fileName, String tableName) {
        super(fileName);
        this.table = tableName;
        this.fileName = fileName;
        try {
            putDataIntoFile();
        } catch(Exception e) {
            System.out.println("Warning: the input file for the Support Vector Machine is empty");
            e.printStackTrace();
        }
    }
    
    public HashMap<Integer, String> getLineToIdMap() {
        return line2id;
    }
}
