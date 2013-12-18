package auxiliaries;

/**
 * Defines a correspondance between numerical values and polarity
 */
public class Polarity {
    // type = 1 for interval type
    // type = 2 for fixed value type
    // type = 3 for SentiWordNet
    private int type;
    
    // for type 1
    private Pair<Double, Double> positiveBounds;
    private Pair<Double, Double> neutralBounds;
    private Pair<Double, Double> negativeBounds;
    
    // for type 2
    private int neutral;
    
    // for type 3
    private double posScore;
    private double negScore;
    private double objScore;
    
    public Polarity (Pair positive, Pair neutral, Pair negative) {
        this.positiveBounds = positive;
        this.neutralBounds = neutral;
        this.negativeBounds = negative;
        type = 1;
    }
    
    public Polarity (int neutral) {
        this.neutral = neutral;
        type = 2;
    }
    
    public Polarity (double posScore, double negScore, double objScore) {
        this.posScore = posScore;
        this.negScore = negScore;
        this.objScore = objScore;
        type = 3;
    }
    
    public String getPolarityLabel(double value) {
        if (type == 1) {
            if ((positiveBounds.getLeft() <= value)&&(value <= positiveBounds.getRight()))
                return "positive";
            if ((neutralBounds.getLeft() <= value)&&(value <= neutralBounds.getRight()))
                return "neutral";
            if ((negativeBounds.getLeft() <= value)&&(value <= negativeBounds.getRight()))
                return "negative";
            return "undecidable";
        } 
        if (type == 2) {
            if (value > neutral) return "positive";
            if (value < neutral) return "negative";
            return "neutral";
        }
        // type = 3
        if ((posScore > negScore)&&(posScore > objScore)) return "positive";
        if ((negScore > posScore)&&(negScore > objScore)) return "negative";
        if ((objScore > posScore)&&(objScore > negScore)) return "neutral";
        // In case of equality, give the following priorities: neutral, positive, negative
        if ((objScore == posScore) || (objScore == negScore)) return "neutral";
        // if (posScore == negScore)
        return "positive";
    }
}
