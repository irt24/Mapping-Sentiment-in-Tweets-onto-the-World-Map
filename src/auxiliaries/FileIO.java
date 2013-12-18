package auxiliaries;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class implements several methods for working with files
 */
public class FileIO {
    
    public BufferedReader getBufferedReader(String fileName) throws FileNotFoundException {
       FileInputStream fstream = new FileInputStream(fileName);
       DataInputStream in = new DataInputStream(fstream);
       BufferedReader br = new BufferedReader(new InputStreamReader(in));
       return br;
    }
    
    public BufferedWriter getBufferedWriter(String fileName) throws IOException {
        FileWriter fstream = new FileWriter(fileName);
        return new BufferedWriter(fstream);
    }

}
