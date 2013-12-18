package preprocessing;

import auxiliaries.Pair;
import java.util.ArrayList;

/**
 * Processed Tweet for the supervised classifier
 */
public class SupervisedProcessedTweet extends ProcessedTweet{
    
    private int nElongatedWords = 0;
    private int nExclamations = 0;
    private int nQuestions = 0;
    
    private String concatWords(ArrayList<String> wordList) {
        StringBuilder sb = new StringBuilder();
        for (String word : wordList) {
            if (word.equals("EX")) {
                nExclamations++;
                // don't append exclamation marks
                continue;
            }
            if (word.equals("QN")) {
                nQuestions++;
                // don't append question marks
                continue;
            }
            if (word.startsWith("E")) {
                nElongatedWords++;
                word = word.substring(1);
            }
            sb.append(word);
            sb.append(",");
        }
        return sb.toString();
    }
    
    public SupervisedProcessedTweet(String id, String originalText, 
            ArrayList<Pair> emoticons, boolean removeEmoticons) {
        super(id, originalText, emoticons, removeEmoticons);
        formattedText = concatWords(this.getWordList());
    }
    
    public int getNElongatedWords() {
        return nElongatedWords;
    }
}
