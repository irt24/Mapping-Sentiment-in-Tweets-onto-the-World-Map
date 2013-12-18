package romanian;

import auxiliaries.FileIO;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.TreeSet;

/**
 * Creates a Romanian opinion dictionary
 */
public class CreateDictionary {

    private static final String path = "text_files\\romanian\\dictionaries";
    private static final String[] sources = {"anger.txt", "disgust.txt", "fear.txt", "joy.txt", "sadness.txt"};
    private static final double[] scores = {-1.0, -1.0, -1.0, 1.0, -1.0};
    private static TreeSet<String> wordSet = new TreeSet<>();
    private static FileIO io;
    private static BufferedWriter out;
    
    private static void add2dictionary(int i) throws Exception {
        BufferedReader in = io.getBufferedReader(path + sources[i]);
        String line;
        while ((line = in.readLine()) != null) {
            if (!line.contains("\t")) continue;
            String[] items = line.split("\\t");
            String romanian = items[4];
            String[] words = romanian.split("\\s");
            for (String word : words) {
                wordSet.add(word + "\t" + scores[i]);
            }
        }
    }
    
    public static void main(String[] args) throws Exception{
        
        io = new FileIO();
        out = io.getBufferedWriter(path + "dictionary");

        for (int i = 0; i < sources.length; i++) {
            add2dictionary(i);
        }
        for (String word : wordSet) {
            out.write(word);
            out.write(System.getProperty("line.separator"));
        }    
        out.close();
    }
}
