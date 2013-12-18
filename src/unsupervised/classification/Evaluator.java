package unsupervised.classification;

import auxiliaries.DBUtils;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class evaluates the performance of the unsupervised system by counting 
 * the number of correctly labeled Tweets.
 */
public class Evaluator {
    
    private int nTweets = 0;        // total number of Tweets considered
    private float posPrecision;
    private float objPrecision;
    private float negPrecision;
    private float posRecall;
    private float objRecall;
    private float negRecall;
    private float coverage;
    private DBUtils db;
    
    public Evaluator(String whereClause) {
        try {
            db = new DBUtils();
            computePerformance(whereClause, "positive");
            computePerformance(whereClause, "neutral");
            computePerformance(whereClause, "negative");
            computeCoverage(whereClause);
        } catch (SQLException e) {
            System.out.println("Cannot access the tweets table for evaluation");
        }
    }
    
    private int howMany(String whereClause) throws SQLException{
        String select = "SELECT COUNT(*) AS N FROM SUPERVISED " + whereClause;
        ResultSet resultSet = db.select(select);
        if (resultSet.next()) return resultSet.getInt("N");
        return 0;
    }
  
    private void computePerformance(String whereClause, String orientation) throws SQLException {
        // How many tweets have this true orientation?
        int nTrue = howMany(whereClause + " AND LABEL = '" + orientation + "'");
        nTweets += nTrue;
        // How many tweets have this orientation assigned by the classifier?
        int nClassified = howMany(whereClause + " AND POLARITY = '" + orientation + "'");
        // How many tweets have this orientation both true and assigned by the classifier?
        int nCorrect = howMany(whereClause + " AND LABEL = '"
                + orientation + "' AND POLARITY = '" + orientation + "'");
        float precision;
        if (nClassified != 0) precision = 100f * nCorrect / nClassified;
        else precision = -1;
        float recall;
        if (nTrue != 0) recall = 100f * nCorrect / nTrue;
        else recall = -1;
        switch (orientation) {
            case "positive":{
                posPrecision = precision;
                posRecall = recall;
            } break;
            case "neutral":{
                objPrecision = precision;
                objRecall = recall;
            } break;
            case "negative":{
                negPrecision = precision;
                negRecall = recall;
            } break;
        }  
    }
    
    private void computeCoverage(String whereClause) throws SQLException {
        // How many tweets have more than 0 recognised words?
        int nCovered = howMany(whereClause + "AND DICTIONARY_WORDS <> 0");
        coverage = 100 * nCovered / nTweets;
    }
    
    public float getAccuracy() {
        String select1 = "SELECT COUNT(*) AS N FROM SUPERVISED WHERE LABEL = POLARITY";
        String select2 = "SELECT COUNT(*) AS N FROM SUPERVISED";
        int nCorrect = 0;
        int total = 0;
        try {
            DBUtils db = new DBUtils();
            ResultSet resultSet = db.select(select1);
            if (resultSet.next()) nCorrect = resultSet.getInt("N");
            resultSet = db.select(select2);
            if (resultSet.next()) total = resultSet.getInt("N");
        } catch (SQLException e) {
            System.out.println("Cannot access the database to calculate precision");
        }
        return 100 * nCorrect / total;
    }
    
    public float getAveragePrecision() {
        return (posPrecision + objPrecision + negPrecision)/3;
    }
    
    public float getAverageRecall() {
        return (posRecall + objRecall + negRecall)/3;
    }
    
    public float getFMeasure() {
        float p = getAveragePrecision();
        float r = getAverageRecall();
        return 2*p*r/(p+r);
    }
    
    public float getPosPrecision() {
        return posPrecision;
    }
    public float getObjPrecision() {
        return objPrecision;
    }
    public float getNegPrecision() {
        return negPrecision;
    }
    public float getCoverage() {
        return coverage;
    }  
    public float getPosRecall() {
        return posRecall;
    }
    public float getObjRecall() {
        return objRecall;
    }
    public float getNegRecall() {
        return negRecall;
    }
}
