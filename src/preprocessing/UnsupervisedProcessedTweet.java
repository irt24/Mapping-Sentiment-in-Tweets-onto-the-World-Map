package preprocessing;

import auxiliaries.Pair;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Processed tweet for the unsupervised classifier.
 * Extra-functionality:
 * - consider negations (put "!" in front of negated words)
 * - remove stop words
 */
public class UnsupervisedProcessedTweet extends ProcessedTweet{

    private TreeSet<String> negations;
    private TreeSet<String> stopWords;
    
    private void createNegations() {
        negations = new TreeSet<>();
        negations.add("t");
        negations.add("no");
        negations.add("not");
        negations.add("never");
    }
    
    private boolean isNegation(String word) {
        if (negations.contains(word)) return true;
        return false;
    }
    
    private boolean isStopWord(String word) {
        if (stopWords.contains(word)) return true;
        return false;
    }
    
    private String concatWords(ArrayList<String> wordList) {
        StringBuilder sb = new StringBuilder();
        boolean prevWasNegation = false;
        for (String word : wordList) {
            // don't care about exclamation or question marks
            if (word.equals("EX") || word.equals("QN")) continue;
            // don't care about elongated words
            if (word.startsWith("E")) word = word.substring(1);
            // don't append stop words
            if (isStopWord(word)) continue;
            if (prevWasNegation) sb.append("!");
            if (isNegation(word)) {
                prevWasNegation = true;
                // don't append negations
                continue;
            } else prevWasNegation = false;
            sb.append(word);
            sb.append(",");
        }
        return sb.toString();
    }
    
    public UnsupervisedProcessedTweet(String id, String originalText, 
            ArrayList<Pair> emoticons, boolean removeEmoticons, 
            TreeSet<String> stopWords) {
        super(id, originalText, emoticons, removeEmoticons);
        this.stopWords = stopWords;
        createNegations();
        formattedText = concatWords(this.getWordList());
    }
}
