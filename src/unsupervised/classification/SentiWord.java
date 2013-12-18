package unsupervised.classification;

/**
 * This class represents a word, with three scores: positive, negative, and objective.
 */
public class SentiWord implements Comparable<SentiWord>{
    private String word;
    private double posScore;
    private double negScore;
    private double objScore;
    
    private boolean isValidScore(double score) {
        if ((score  >= 0) && (score <= 1)) return true;
        return false;
    }
    
    public SentiWord(String word, double posScore, double negScore, double objScore) {
        // boost polarised scores
        int boost_pos = 10;
        int boost_neg = 10;
        
        this.word = word;
        // Check whether values are between 0 or 1. If not, default score to 0.
        if (isValidScore(posScore)) this.posScore = boost_pos * posScore;
        else this.posScore = 0.0;
        if (isValidScore(negScore)) this.negScore = boost_neg * negScore;
        else this.negScore = 0.0;
        if (isValidScore(objScore)) this.objScore = objScore;
        else this.objScore = 0.0;
    }
    
    public String getWord() {
        return word;
    }
    
    public Double getPosScore() {
        return posScore;
    }
    
    public Double getNegScore() {
        return negScore;
    }
    
    public Double getObjScore() {
        return objScore;
    }

    @Override
    public int compareTo(SentiWord o) {
        return this.word.compareTo(o.word);
    }
}
