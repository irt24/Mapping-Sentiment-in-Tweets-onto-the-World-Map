package supervised.evaluation;

import auxiliaries.DBUtils;
import auxiliaries.FileIO;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import supervised.classification.CentralisedDictionary;
import supervised.classification.WordRankMap;

/**
 * This class will create the LIBSVM-format files for the folds (foldX, foldWithoutX).
 * So far, the features are binary: 1 if the word occurs in the Tweet and 0 otherwise.
 * 0-valued features need not be written to the data file.
 */
public class FoldCreator {
    
    // Change nFolds to 1 to train the learner with all labelled tweets.
    private static int nFolds;
    private static final String path = "text_files\\supervised\\";
    private static DBUtils db;
    private static FileIO io;
    private static CentralisedDictionary dictionary;

    
    public static String getLabelValue(String label) {
        if (label.equals("positive")) return "1";
        if (label.equals("neutral")) return "0";
        return "-1";
    }

    public static void createFold(int n) throws Exception{
        String rankFileName = path + "rankFolds\\fold" + n;
        BufferedWriter out1 = io.getBufferedWriter(rankFileName);

        // Fetch Tweets from the database
        String selectString = "SELECT S_FORMATTED, ELONGATED, LABEL FROM SUPERVISED WHERE FOLD_INDEX=" + n;
        ResultSet resultSet = db.select(selectString);
        while (resultSet.next()) {
            // Print the label value
            String toWrite = getLabelValue(resultSet.getString("LABEL")) + "\t";
            out1.write(toWrite);
            
            // Get list of ranks. Properties of this list: sorted and without duplicates
            String text = resultSet.getString("S_FORMATTED");
            HashMap<Integer, Integer> ranksAndScores = WordRankMap.getRanksAndScores(dictionary, text);
            List<Integer> ranks = new ArrayList<>();
            ranks.addAll(ranksAndScores.keySet());
            Collections.sort(ranks);
            for (Integer rank : ranks) {
                out1.write(rank + ":" + ranksAndScores.get(rank) +"\t");
            }
            // Extra features;
            /*
            int next = dictionary.getNumberOfEntries() + 1;
            int elongated = resultSet.getInt("ELONGATED");
            if (elongated != 0) out1.write(next + ":" + elongated + "\t");
            */
            out1.write(System.getProperty("line.separator"));
        } 
        out1.close();
    }
    
    public static void mergeFolds(String folder) throws Exception{
        FileIO io = new FileIO();
        BufferedReader[] ins = new BufferedReader[nFolds];
        BufferedWriter[] outs = new BufferedWriter[nFolds];
        for (int i = 0; i < nFolds; i++) {
            ins[i] = io.getBufferedReader(path + folder + "\\fold" + (i+1));
            outs[i] = io.getBufferedWriter(path + folder + "\\foldWithout" + (i+1));
        }
        for (int i = 0; i < nFolds; i++) {
            BufferedReader in = ins[i];
            String line;
            while((line = in.readLine()) != null) {
                for (int j = 0; j < nFolds; j++)
                    if (i != j) 
                        outs[j].write(line + System.getProperty("line.separator"));
            }
        }
        for (int i = 0; i < nFolds; i++)
            outs[i].close();
    }
    
    public static void create(int folds) throws Exception{
        nFolds = folds;
        db = new DBUtils();
        io = new FileIO();
        dictionary = new CentralisedDictionary("S_FORMATTED","SUPERVISED");
        for (int i = 0; i < nFolds; i++) {
            createFold(i+1);
        }
        mergeFolds("rankFolds");
    }
}