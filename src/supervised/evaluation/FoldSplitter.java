package supervised.evaluation;

import auxiliaries.DBUtils;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class splits the data into 10 folds for the k-fold cross-validation
 * by assigning an index to each Tweet in the database (in the SUPERVISED table).
 */
public class FoldSplitter {
    
    private static int nFolds;
   
    public static void assignFoldIndex(DBUtils db, String label) throws SQLException{ 
        // Make sure that topics are named accordingly before running this
        String selectString = "SELECT ID FROM SUPERVISED WHERE LABEL = '" +
                label + "' AND TOPIC NOT LIKE '%Manual%'";
        ResultSet resultSet = db.select(selectString);
        int count = 0;
        while (resultSet.next()) {
            // Folds are numbered from 1 to 10
            int index = count % nFolds + 1;
            count++;
            String updateString = "UPDATE SUPERVISED SET FOLD_INDEX = " + index + 
                    "WHERE ID = '" + resultSet.getString("ID") + "'";
            db.update(updateString);
        }
    }
    
    public static void assignFoldIndex(DBUtils db) throws SQLException {
        // Make sure that topics are named accordingly before running this
        String selectString = "SELECT ID FROM SUPERVISED WHERE TOPIC LIKE '%Manual'";
        ResultSet resultSet = db.select(selectString);
        int count = 0;
        while (resultSet.next()) {
            int index = count % nFolds + 1;
            count++;
            String updateString = "UPDATE SUPERVISED SET FOLD_INDEX = " + index + 
                    "WHERE ID = '" + resultSet.getString("ID") + "'";
            db.update(updateString);
        }
    }

    public static void split(int folds) throws SQLException{
        nFolds = folds;
        DBUtils db = new DBUtils();
        String[] labels = {"positive", "neutral", "negative"};
        for (String label : labels) {
            assignFoldIndex(db, label);
        }
        assignFoldIndex(db);
    }
}