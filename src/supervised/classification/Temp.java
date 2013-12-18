package supervised.classification;

import auxiliaries.DBUtils;
import java.sql.ResultSet;

/**
 * Purpose of this class
 */
public class Temp {
    
    private static void evaluatePolarities() throws Exception {
        DBUtils db = new DBUtils();
      
        String[] polarities = {"positive","neutral","negative"};
        for (String polarity: polarities) {
            String selection = "SELECT COUNT(*) AS N FROM SUPERVISED WHERE LABEL = '" + polarity + "'";
            ResultSet resultSet = db.select(selection);
            int total = 0;
            if (resultSet.next())
                total = resultSet.getInt("N");
            selection = "SELECT COUNT(*) AS N FROM SUPERVISED WHERE POLARITY = '" + polarity + "'";
            resultSet = db.select(selection);
            int classified = 0;
            if (resultSet.next())
                classified = resultSet.getInt("N");
            selection = "SELECT COUNT(*) AS N FROM SUPERVISED WHERE LABEL = '"
                    + polarity + "' AND POLARITY = " + "'" + polarity + "'";
            resultSet = db.select(selection);
            int correct = 0;
            if (resultSet.next())
                correct = resultSet.getInt("N");
            double precision = 100*correct/classified;
            double recall = 100*correct/total;
            double fmeasure = 2*precision*recall/(precision + recall);
            System.out.println(polarity + " " + precision + " " + recall + " " + fmeasure);
        }
        
    }
    public static void main(String[] args) throws Exception{
        //SupervisedClassifier.perfromClassification("SUPERVISED");
        evaluatePolarities();
    }
}
