package supervised.classification;

import auxiliaries.DBUtils;
import auxiliaries.FileIO;
import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import java.io.BufferedReader;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * This class predicts the label for the tweets in the database.
 */
public class SupervisedClassifier {
    
    private static final String dataFileName = "text_files\\supervised\\data";
    private static final String modelFileName = "text_files\\supervised\\model";
    private static String table;
    private static DBUtils db;
    
    private static String getPolarityLabel(double label) {
        if (label == 1) return "positive";
        if (label == -1) return "negative";
        return "neutral";
    }
    
    private static void updateDB(double label, String id) throws SQLException {
        String update = "UPDATE "+table+" SET SUPERVISED_LABEL = ? WHERE ID = ?";
        PreparedStatement stmt = db.getPreparedStatement(update);
        stmt.setString(1, getPolarityLabel(label));
        stmt.setString(2, id);
        stmt.execute();
    }
    
    private static void predict(Model model, HashMap<Integer, String> map) throws Exception{
        BufferedReader in = new FileIO().getBufferedReader(dataFileName);
        String line;
        int count = 0;
        while ((line = in.readLine()) != null) {
            if (!line.contains(":")) continue;
            //System.out.println(count + 1);
            count++;
            String[] items = line.split("\\t");
            // Create feature vector.
            Feature[] instance = new Feature[items.length];
            for (int i = 0; i < items.length; i++) {
                int featureIndex = Integer.parseInt(items[i].split(":")[0]);
                FeatureNode featureNode = new FeatureNode(featureIndex, 1);
                instance[i] = featureNode;
            }
            // Make prediction
            double label = Linear.predict(model, instance);
            updateDB(label, map.get(count));
        }
    }
    
    public static void perfromClassification(String tableName) throws Exception{
        db = new DBUtils();
        table = tableName;
        System.out.println("Creating the data file...");
        DataFile dataFile = new DataFile(dataFileName, table);
        System.out.println("Data file created");
        HashMap<Integer, String> line2idMap = dataFile.getLineToIdMap();
        File modelFile = new File(modelFileName);
        Model model = Model.load(modelFile);
        predict(model, line2idMap);
    }
}
