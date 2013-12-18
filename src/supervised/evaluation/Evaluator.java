package supervised.evaluation;

import auxiliaries.DBUtils;
import auxiliaries.FileIO;
import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;
import java.io.BufferedReader;
import java.io.File;
import java.sql.ResultSet;

/**
 * Evaluate the supervised system using the 10-fold technique
 */
public class Evaluator {
    
    private static final int bias = 1;
    private static final int nFolds = 10;
    private static final String path = "text_files\\supervised\\rankFolds\\";
    private static double[] measures = new double[9];
    private static double[] averages4Label = new double[3];
    
    private static Model getModel(int index, Parameter parameter) throws Exception{
        File file = new File(path + "foldWithout" + index);
        Problem problem = Problem.readFromFile(file, bias);
        return Linear.train(problem, parameter);
    }
    
    private static void performPrediction(int index, Model model) throws Exception{
        String fileName = path + "fold" + index;
        BufferedReader in = new FileIO().getBufferedReader(fileName);
        String line;
        
        int truePos = 0;    int labelledPos = 0;    int correctPos = 0;
        int trueObj = 0;    int labelledObj = 0;    int correctObj = 0;
        int trueNeg = 0;    int labelledNeg = 0;    int correctNeg = 0;
        
        while ((line = in.readLine()) != null) {
            String[] items = line.split("\\t");
            // Create feature vector.
            // Ignore items[0], that is the label
            Feature[] instance = new Feature[items.length - 1];
            for (int i = 1; i < items.length; i++) {
                int featureIndex = Integer.parseInt(items[i].split(":")[0]);
                FeatureNode featureNode = new FeatureNode(featureIndex, 1);
                instance[i - 1] = featureNode;
            }
            // Make prediction
            double prediction = Linear.predict(model, instance);
            // Compute performance
            int realLabel = Integer.parseInt(items[0]);
            switch (realLabel) {
                case -1: trueNeg++; break;
                case 0:  trueObj++; break;   
                case 1:  truePos++; break;
            }
            switch ((int)prediction) {
                case -1: labelledNeg++; break;
                case 0:  labelledObj++; break;
                case 1:  labelledPos++; break;
            }
            if (realLabel == prediction) {
                switch (realLabel) {
                    case -1: correctNeg++; break;
                    case 0:  correctObj++; break;   
                    case 1:  correctPos++; break;
                }
            }
        }
            double posPrec = 100.0 * correctPos/labelledPos;
            double posRec = 100.0 * correctPos/truePos;
            double posF = 2*posPrec*posRec/(posPrec + posRec);
            double objPrec = 100.0 * correctObj/labelledObj;
            double objRec = 100.0 * correctObj/trueObj;
            double objF = 2*objPrec*objRec/(objPrec + objRec);
            double negPrec = 100.0 * correctNeg/labelledNeg;
            double negRec = 100.0 * correctNeg/trueNeg;
            double negF = 2*negPrec*negRec/(negPrec + negRec);
            measures[0] += posPrec;
            measures[1] += posRec;
            measures[2] += posF;
            measures[3] += objPrec;
            measures[4] += objRec;
            measures[5] += objF;
            measures[6] += negPrec;
            measures[7] += negRec;
            measures[8] += negF;
    }
    
    private static void printPolarities() {
        System.out.println("Positive precision: " + measures[0]/10);
        System.out.println("Positive recall: " + measures[1]/10);
        System.out.println("Positive F-measure: " + measures[2]/10);
        System.out.println("Neutral precision: " + measures[3]/10);
        System.out.println("Neutral recall: " + measures[4]/10);
        System.out.println("Neutral F-measure: " + measures[5]/10);
        System.out.println("Negative precision: " + measures[6]/10);
        System.out.println("Negative recall: " + measures[7]/10);
        System.out.println("Negative F-measure: " + measures[8]/10);
    }
    
    private static void printLabels() {
        System.out.println("Manual: " + averages4Label[0]/10);
        System.out.println("Timeline: " + averages4Label[1]/10);
        System.out.println("Emoticon: " + averages4Label[2]/10);
    }
    
    public static void evaluateLabels(int fold) throws Exception{
        DBUtils db = new DBUtils();
        String[] types = {"Manual", "Timeline", "Emoticon"};
        String[] polarities= {"positive", "negative", "neutral"};
        int averagePerLabelType;
        for (String type:types) {
            averagePerLabelType = 0;
            for (String polarity : polarities) {
                String select = "SELECT COUNT(*) AS N FROM SUPERVISED WHERE FOLD_INDEX = " 
                        + fold + " AND LABEL_TYPE = " + "'" + type + "' AND LABEL = '" + polarity + "'";
                ResultSet rs = db.select(select);
                int total = 0;
                if (rs.next()){
                    total = rs.getInt("N");
                }
                select = "SELECT COUNT(*) AS N FROM SUPERVISED WHERE FOLD_INDEX = " 
                        + fold + " AND LABEL_TYPE = " + "'" + type + "' AND POLARITY = '" + polarity + "'";
                rs = db.select(select);
                int classified = 0;
                if (rs.next()){
                    classified = rs.getInt("N");
                }
                select = "SELECT COUNT(*) AS N FROM SUPERVISED WHERE FOLD_INDEX = " 
                        + fold + " AND LABEL_TYPE = " + "'" + type 
                        + "' AND LABEL = '" + polarity + "' AND POLARITY = '" + polarity + "'";
                rs = db.select(select);
                int correct = 0;
                if (rs.next()){
                    correct = rs.getInt("N");
                }
                if ((classified == 0)||(total == 0)) continue;
                float precision = 100*correct/classified;
                float recall = 100*correct/total;
                float fmeasure = 2*precision*recall/(precision + recall);
                averagePerLabelType += fmeasure;
            }
            if (type.equals("Manual")) averages4Label[0] += averagePerLabelType/3;
            if (type.equals("Timeline")) averages4Label[1] += averagePerLabelType/3;
            if (type.equals("Emoticon")) averages4Label[2] += averagePerLabelType/2;
        }
    }
    
    public static void main(String[] args) throws Exception{
        // Use 10-cross folding
        FoldSplitter.split(10);
        FoldCreator.create(10);
        
        SolverType solver = SolverType.L2R_L2LOSS_SVC_DUAL;
        double C = 0.125;   // cost of constraints violation
        double eps = 0.3;   // stopping criteria
        Parameter parameter = new Parameter(solver, C, eps);
        
        int[] correct = new int[nFolds];
        for (int i = 1; i <= nFolds; i++) {
            Model model = getModel(i, parameter);
            performPrediction(i, model);
            evaluateLabels(i);
        }
        printLabels();
    }
}