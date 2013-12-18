package geodistribution;
/*
 * This class detects clusters by looking at duplicate tweets.
 * If two tweets are claimed by Twitter to originate from the same place,
 * those two places are put together into a cluster.
 */

import auxiliaries.FileIO;
import auxiliaries.Pair;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;

public class Overlaps {
    
   private static int nr = 1;
    
   public static void main(String[] args) throws Exception {
       ArrayList<Pair<String, String>> pairs = new ArrayList<>();
       
       BufferedReader br = new FileIO().getBufferedReader("text_files\\countries_and_tweets" + nr + ".txt");
       String pair;
       while ((pair = br.readLine()) != null) {
           String[] items = pair.split("xxx");
           pairs.add(new Pair(items[0], items[1]));
       }
       for (Pair p:pairs) {
           System.out.println(p.getLeft() + " -> " + p.getRight());
       }
       
       // Make clusters of states that overlap
       ArrayList<ArrayList<String>> clusters = new ArrayList<>();
       while (!pairs.isEmpty()) {
           ArrayList<String> cluster = new ArrayList<>();
           Pair current = pairs.get(0);
           cluster.add((String)current.getLeft());
           for (int i=1; i<pairs.size(); i++) {
               if (current.getRight().equals(pairs.get(i).getRight())) {
                   cluster.add(pairs.get(i).getLeft());
                   pairs.remove(i);
                   i--;
               }
           }
           clusters.add(cluster);
           pairs.remove(0);
       }
       System.out.println(clusters.size() + " clusters detected.");
       BufferedWriter out = new FileIO().getBufferedWriter("text_files\\countries_clusters" + nr + ".txt");
       for (ArrayList<String> cluster : clusters) {
           for (String state : cluster) {
               out.write(state);
               out.write(System.getProperty("line.separator"));
           }
           out.write(System.getProperty("line.separator"));
       }
       out.close();
   } 
}
