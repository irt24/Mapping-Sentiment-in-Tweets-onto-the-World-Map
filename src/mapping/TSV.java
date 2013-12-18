package mapping;

import auxiliaries.DBUtils;
import auxiliaries.FileIO;
import java.io.BufferedWriter;
import java.sql.ResultSet;

/**
 * This class creates a TSV file (Tab Separated Values), with the following structure:
 * TweetID <tab> TweetText <tab> TweetPolarity <tab> Latitude <tab> Longitude
 * Call method databaseToTSV to generate the TSV file.
 */
public class TSV {
    
    private static final String path = "text_files\\mapping\\tsv";
    
    private static String format(String text) {
        String toReturn = text.replaceAll("\n", " ");
        toReturn = toReturn.replaceAll("\t", " ");
        return toReturn;
    }
    
    public static void databaseToTSV(String table) throws Exception{
        DBUtils db = new DBUtils();
        String selectString = "SELECT ID, ORIGINAL_TEXT, SUPERVISED_LABEL, LATITUDE, LONGITUDE FROM " + table;
        ResultSet resultSet = db.select(selectString);
        
        BufferedWriter out = new FileIO().getBufferedWriter(path);
        boolean first = true;
        while (resultSet.next()) {
            if (!first) out.write(System.getProperty("line.separator"));
            else first = false;
            
            String text = format(resultSet.getString("ORIGINAL_text"));
            
            String toWrite = resultSet.getString("ID") + "\t" +
                    text + "\t" +
                    resultSet.getString("SUPERVISED_LABEL") + "\t" +
                    resultSet.getString("LATITUDE") + "\t" +
                    resultSet.getString("LONGITUDE");
            out.write(toWrite);        
        }
        out.close();
    }
}
