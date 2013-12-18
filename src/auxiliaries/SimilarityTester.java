package auxiliaries;

import java.io.BufferedReader;

/**
 * Test similarity of two files
 * Runtime arguments1:
 * 1) path to file1
 * 2) path to file2
 */
public class SimilarityTester {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Insufficient arguments. Aborting");
            return;
        }
        FileIO io = new FileIO();
        BufferedReader r1 = io.getBufferedReader(args[0]);
        BufferedReader r2 = io.getBufferedReader(args[1]);
        String line1;
        String line2;
        while ((line1 = r1.readLine()) != null) {
            line2 = r2.readLine();
            if (!line1.equals(line2)) System.out.println("Distinct lines! " + line1 + ", " + line2);
        }
    }
}
