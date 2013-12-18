package unsupervised.classification;

import auxiliaries.FileIO;
import auxiliaries.Pair;
import auxiliaries.Polarity;
import java.io.BufferedWriter;

/**
 * Performs unsupervised classification and evaluates its performance
 */
public class UnsupervisedClassifier {
    
    private static final String dictionariesPath = "text_files\\dictionaries\\";
    private static final String statisticsPath = "text_files\\statistics\\";
    private static final int nVals = 6;
    
    private static Polarity getPolarityScheme(int index) {
        // For dictionary3, polarity object can only be created when the scores
        // of the whole sentence are known.
        if (index == 3) return null;
        if (index == 4) {
            Pair positiveBounds = new Pair(7.0, 9.0);
            Pair neutralBounds = new Pair(4.0, 7.0);
            Pair negativeBounds = new Pair(1.0, 4.0);
            Polarity polarity = new Polarity(positiveBounds, neutralBounds, negativeBounds);
            return polarity;
        }
        // for dictionaries 1 and 2:
        return new Polarity(0);
    }
    
    private static float[] performanceScores(String whereClause) throws Exception{
        Evaluator eval = new Evaluator(whereClause);
        float[] scores = new float[nVals];
        scores[0] = eval.getPosPrecision();
        scores[1] = eval.getObjPrecision();
        scores[2] = eval.getNegPrecision();
        scores[3] = eval.getPosRecall();
        scores[4] = eval.getObjRecall();
        scores[5] = eval.getNegRecall();
        return scores;
    }
    
    private static void performanceAverages() throws Exception {
        float[] scores = performanceScores(" WHERE TRUE ");
        float avgP = (scores[0] + scores[1] + scores[2])/3;
        float avgR = (scores[3] + scores[4] + scores[5])/3;
        float f = 2*avgP*avgR/(avgP + avgR);
        System.out.println(avgP + "\t" + avgR + "\t" + f);
    }
    
    /*
     * Evaluates how topics influence performance
     */
    private static void evaluateTopics() throws Exception{
        String[] topics = {"Justin Bieber", "Barack Obama", "Hurricane Sandy", "Microsoft Surface"};
        for (String topic : topics) {
            float[] scores = performanceScores(" WHERE TOPIC = '" + topic + "'");
            float fpos = 2*scores[0]*scores[3]/(scores[0] + scores[3]);
            System.out.println(fpos);
            float fobj = 2*scores[1]*scores[4]/(scores[1] + scores[4]);
            System.out.println(fobj);
            float fneg = 2*scores[2]*scores[5]/(scores[2] + scores[5]);
            System.out.println(fneg);
        }
    }
    
    /*
     * Evaluates how different types of labels evaluate performance
     * (e.g. manual or noisy labels)
     */
    private static void evaluateLabels() throws Exception{
        String file = statisticsPath + "label_types.txt";
        BufferedWriter out = new FileIO().getBufferedWriter(file);
        out.write("Label\tPositive\tNeutral\tNegative");
        
        String[] types = {"Manual", "Timeline", "Emoticon"};
        float[] averages = new float[nVals];
        for (String type : types) {
            float[] scores = performanceScores(" WHERE LABEL_TYPE = '" + type + "'");
            out.write(System.getProperty("line.separator"));
            float fpos = 2*scores[0]*scores[3]/(scores[0] + scores[3]);
            float fobj = 2*scores[1]*scores[4]/(scores[1] + scores[4]);
            float fneg = 2*scores[2]*scores[5]/(scores[2] + scores[5]);
            float faverage = 0;
            if (type.equals("Emoticon")) faverage = (fpos + fneg) / 2;
            else faverage = (fpos + fobj + fneg) / 3;
            System.out.println(faverage);
        }
        out.close();
        System.out.println();
    }
    
    /*
     * Evaluate how polarities influence performance
     */
    private static void evaluatePolarities() throws Exception {
        float[] scores = performanceScores(" WHERE TRUE ");
        for (int i = 0; i < 3; i++) {
            float f = 2*scores[i]*scores[i+3]/(scores[i] + scores[i+3]);
            System.out.print(f + "\t");
        }
        System.out.println();
    }
    
    public static void classifyWithDict(int dictionaryIndex) throws Exception{
        // Prepare datastructures needed for label prediction
        String dictionaryName = "dictionary" + dictionaryIndex;
        System.out.println("Predict labels using " + dictionaryName);
        Polarity polarity = getPolarityScheme(dictionaryIndex);
        
        // Perform label prediction
        LabelPredictor labelPrediction = 
                new LabelPredictor(dictionariesPath + dictionaryName, polarity);
        labelPrediction.predictAllLabels();
        
        // Select the property to evaluate
        //evaluatePolarities();
        //evaluateAverages();
        //evaluateLabels();
        //evaluateTopics();
    }
    
    public static void classifyWithAllDicts() throws Exception{
        int[] dictionaries = {1, 2, 3, 4, 12, 21};
        for (int d : dictionaries) {
            classifyWithDict(d);
        }
    }

    public static void main(String[] args) throws Exception{
        //classifyWithAllDicts();
        classifyWithDict(4);
    }
}