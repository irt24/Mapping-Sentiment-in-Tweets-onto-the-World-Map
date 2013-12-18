package unsupervised.ahocorasick;

import java.util.TreeSet;

/*
 * A label represents the prediction of polarity that the system associates with a Tweet.
 */
public class Label {

    private String polarity;
    private TreeSet validPolarities;
    private double score;
    
    public Label(String polarity, double score) {
        validPolarities = new TreeSet();
        validPolarities.add("positive");
        validPolarities.add("negative");
        validPolarities.add("neutral");
        validPolarities.add("undecidable");
        setPolarity(polarity);
        setScore(score);
    }
    
    public String getPolarity() {
        return polarity;
    }
    
    public double getScoreNumber() {
        return score;
    }
    
    public String getScoreString() {
        return Double.toString(score);
    }
    
    public final void setPolarity(String polarity) {
        if (validPolarities.contains(polarity)) {
            this.polarity = polarity;
        }
    }
    
    public final void setScore(double score) {
        this.score = score;
    }
}
