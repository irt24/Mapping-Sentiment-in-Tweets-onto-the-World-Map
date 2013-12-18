package unsupervised.classification;

import auxiliaries.DBUtils;
import auxiliaries.Dictionary;
import auxiliaries.Polarity;
import auxiliaries.SimpleDictionary;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import unsupervised.ahocorasick.Label;

/*
 * This class fetches Tweets to be labled from the database, labels them using the
 * usupervised system, then updates the database.
 */
public class LabelPredictor {

    // private static ACprepare prepare;
    private String dictionaryFile;
    private Dictionary dictionary;
    private static HashMap<String, Double> wordsAndScores;
    private Polarity polarityScheme;
    private final int negationType = 2;
    /*
     * Accepted values for inversion type:
     * 0 = no inversion
     * 1 = invert whole sentence
     * 2 = invert individual words
     */
    
    // just for testing purposes, not included in the UML diagram
    private static int nWords;
    private StringBuilder words;
     
    public LabelPredictor(String dictionaryFile, Polarity polarityScheme) {
        this.dictionaryFile = dictionaryFile;
        if (dictionaryFile.equals("text_files\\dictionaries\\dictionary3")) {
            dictionary = new SentiDictionary();
        } else {
            dictionary = new SimpleDictionary(dictionaryFile);
            wordsAndScores = ((SimpleDictionary)dictionary).getWordToValueMap();
            this.polarityScheme = polarityScheme;
        }
        //prepare = new ACprepare(dictionary);
    }
    
    public void updateDB(DBUtils db, String id, Label label, 
            int nWords, String words) throws SQLException{
        // limit words to 300 characters
        if (words.length() > 300)
            words = words.substring(0, 300-1);
        
        String updateString = "UPDATE SUPERVISED SET POLARITY = '" + label.getPolarity() +
                "', SCORE = " + label.getScoreString() +
                ", DICTIONARY_WORDS = " + nWords +
                ", WORDS = '" + words +
                "' WHERE ID = '" + id + "'";
        db.update(updateString);
    }
    
    private ArrayList<String> getTokens(String text) {
        //TreeSet<String> knownWords = new AhoCorasick(prepare.getAutomaton(), text).getKnownWords();
        
        // Temporarily replace Aho-Corasick with something simpler
        // Note that this requires the preprocessed text to be a CSV.
        ArrayList<String> knownWords = new ArrayList<>();
        String[] terms = text.split(",");
        for (String term : terms) {
            if (dictionary.getWords().contains(term.replaceAll("!","")))
                knownWords.add(term);
        }
        return knownWords;
    }
    
    private boolean isNegated(int type, String text, String word) {
        if (type == 0) return false;
        if (type == 1)
            if (text.contains("!")) return true;
            else return false;
        if ((type == 2)&&(word.startsWith("!"))) return true;
        // type == 2 and word does not start with !
        return false;
    }
    
    private Label getLabel3(String text) {
        HashMap<String, SentiWord> entries = ((SentiDictionary)dictionary).getEntries();
        TreeSet<String> dictionaryWords = ((SentiDictionary)dictionary).getWords();
        String[] terms = text.split(",");
        double posScore = 0;
        double negScore = 0;
        double objScore = 0;
        int count = 0;
        boolean inversion;
        words = new StringBuilder();
        for (String term : terms) {
            inversion = isNegated(negationType, text, term);
            if (term.startsWith("!")) term = term.substring(1);
            if (dictionaryWords.contains(term)) {
                words.append(term + ",");
                count++;
                SentiWord word = entries.get(term);
                double thisPosScore;
                double thisNegScore;
                double thisObjScore;
                if (inversion) {
                    thisPosScore = word.getNegScore();
                    thisNegScore = word.getPosScore();
                } else {
                    thisPosScore = word.getPosScore();
                    thisNegScore = word.getNegScore();
                }
                thisObjScore = word.getObjScore();
                posScore += thisPosScore;
                negScore += thisNegScore;
                objScore += thisObjScore;
            }
        }
        nWords = count;
        Polarity polarity = new Polarity(posScore, negScore, objScore);
        // Score will be irrelevant
        Label label = new Label(polarity.getPolarityLabel(0.0), 0.0);
        //System.out.println("Final Scores: " + posScore + " " + negScore + " " + objScore);
        //System.out.println("Label: " + polarity.getPolarityLabel(0.0));
        return label;
    }
    
    private Label getLabel(String text) throws Exception{
        // special situation for dictionary 3
        if (dictionaryFile.equals("text_files\\dictionaries\\dictionary3")) return getLabel3(text);
        
        ArrayList<String> knownWords = getTokens(text);

        // for testing purposes only
        nWords = knownWords.size();
        words = new StringBuilder();
        
        double totalScore = 0;
        int count = 0;
        for (String word : knownWords) {
            boolean inversion = isNegated(negationType, text, word);
            word = word.replaceAll("!","");
            double thisScore;
            if (!inversion) thisScore = wordsAndScores.get(word);
            else thisScore = -wordsAndScores.get(word);
               
            totalScore += thisScore;
            count++;
            
            //for testing purposes only
            words.append(word);
            words.append(thisScore);
            words.append(", ");
        }
        if (count != 0) totalScore /= count; 
        String polarity = polarityScheme.getPolarityLabel(totalScore);
        Label label = new Label(polarity, totalScore);
        return label;
    }
    
    public void predictAllLabels() throws Exception {
        // Fetch Tweets to be labeled from the database
        DBUtils db = new DBUtils();
        String selectionString = "SELECT ID, U_FORMATTED FROM SUPERVISED";
        ResultSet rs = db.select(selectionString);
        
        while (rs.next()) {
            String id = rs.getString("ID");
            String text = rs.getString("U_FORMATTED");
            Label label = getLabel(text);
            updateDB(db, id, label, nWords, words.toString());
        }
    }
}
