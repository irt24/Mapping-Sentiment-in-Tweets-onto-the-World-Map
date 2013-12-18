package supervised.classification;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Gives a word-rank map
 */
public class WordRankMap {
    
    final static int elongatedBoost = 10;

    public static HashMap<Integer,Integer> getRanksAndScores(CentralisedDictionary dictionary, String text) {
        List<String> list = Arrays.asList(text.split(","));
        HashMap<String,Integer> allWordsAndRanks = dictionary.getRankHashMap();
        HashMap<Integer,Integer> ranksAndScores = new HashMap<>();
        //String prev_word = null;
        for (String word : list) {
            //if (word.equals("justin") || word.equals("bieber")) continue;
            /*
             // Add bigrams
             if (prev_word != null) ranks.add(allWordsAndRanks.get(prev_word + "_" + word));
             prev_word = word;
             */
            int score = 1;
            if (word.contains("!")) {
                word = word.replace("!","");
                score *= elongatedBoost;
            }
            ranksAndScores.put(allWordsAndRanks.get(word),score);
        }
        return ranksAndScores;
    }
}
