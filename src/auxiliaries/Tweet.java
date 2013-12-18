package auxiliaries;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Represents a Tweet
 */
public class Tweet {
    
    private String id;
    private String originalText;
    private String topic;
    private String geotag;
    
    public Tweet (String id, String originalText) {
        this.id = id;
        this.originalText = originalText;
    }
    
    public void setTopic(String topic) {
        this.topic = topic;
    }
    
    public void setGeoLocation(String geotag) {
        this.geotag = geotag;
    }
    
    public void storeToDb(String table, String[] cols) throws SQLException{
        DBUtils db = new DBUtils();
        // Assume that the columns follow the order of the private fields.
        StringBuilder insertion = new StringBuilder("INSERT INTO ");
        insertion.append(table);
        insertion.append(" (");
        for (int i = 0; i < cols.length-1; i++) {
            insertion.append(cols[i]);
            insertion.append(", ");
        }
        insertion.append(cols[cols.length-1]);
        insertion.append(") VALUES (");
        for (int i = 0; i < cols.length - 1; i++)
            insertion.append("?,");
        insertion.append("?)");
        PreparedStatement stmt = db.getPreparedStatement(insertion.toString());
        int col = 1;
        if (col <= cols.length) {
            stmt.setString(col, id);
            col++;
        }
        if (col <= cols.length) {
            stmt.setString(col, originalText);
            col++;
        }
        if (col <= cols.length) {
            stmt.setString(col, topic);
            col++;
        }
        if (col <= cols.length) {
            stmt.setString(col, geotag);
            col++;
        }
        stmt.execute();
    }
    
    public String getId() {
        return id;
    }
    
    public String getOriginalText() {
        return originalText;
    }
    
    public String getTopic() {
        return topic;
    }
    
    public String getGeotag() {
        return geotag;
    }
}
