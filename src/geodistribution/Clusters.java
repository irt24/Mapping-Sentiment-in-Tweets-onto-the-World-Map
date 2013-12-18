package geodistribution;

import auxiliaries.FileIO;
import auxiliaries.MyConnection;
import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.Statement;

/*
 * This class uses the clusters files to update the structure of the database
 * (associate each place -country or state- with a cluster)
 */
public class Clusters {
   public static void main(String[] args) throws Exception{
        Connection conn = new MyConnection().getConnection();
        BufferedReader br = new FileIO().getBufferedReader("text_files\\states_clusters1.txt");
        String place;
        int noClusters = 1;
        boolean rep = true; // representative of the cluster
        while ((place = br.readLine()) != null) {
            System.out.println(place);
            if (place.equals("")) {
                noClusters++;
                rep = true;
            } else {
                if (rep) {
                    String setRep = "UPDATE PLACES SET REP = true WHERE PLACE = '" + place + "'";
                    Statement setStmt = conn.createStatement();
                    setStmt.execute(setRep);
                    rep = false;
                }
                String updateString = "UPDATE PLACES SET CLUSTER=" + noClusters + "WHERE PLACE='" + place + "'";
                Statement stmt = conn.createStatement();
                stmt.execute(updateString);
            }
        }
   }
}
