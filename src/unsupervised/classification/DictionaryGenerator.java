package unsupervised.classification;

import auxiliaries.FileIO;
import auxiliaries.SimplePair;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * This class uses pre-defined dictionaries to construct merged dictionaries.
 * <br> There are five pre-defined dictionaries:
 * <ol>
 *  <li> Manual dictionary
 *  <li> NRC Emotion Dictionary
 *  <li> General Inquirer Dictionary
 *  <li> SentiWordNet Dictionary
 *  <li> ANEW dataset (this is already in the proper format, so no need to generate it)
 * </ol>
 */

public class DictionaryGenerator {
    
    private static TreeSet<SimplePair<String, Integer>> dictionary;
    private static final String filePath = "text_files\\dictionaries\\";
    private static int nDictionaries = 0;
    
    public static void createDictionary1() throws Exception {
        // Clear datastructure
        dictionary = new TreeSet<>();
        
       // Manual dictionary automatically included
       mergeDictionary(0); 
        
       BufferedReader in = new FileIO().getBufferedReader(filePath + "fullDictionary1.txt");
       String line;
       int positiveScore;
       int negativeScore = 0;
       boolean first = true;
       while ((line = in.readLine()) != null) {
           // In fullDictionary.txt, "positive" and "negative" are binary properties of each word.
           // The structure of a line is: word \t property \t binary_value.
           String[] items = line.split("\\t");
           String word = items[0];
           if (items[1].equals("negative")) {
               negativeScore = Integer.parseInt(items[2]);
               continue;
           } 
           if (items[1].equals("positive")) {
               positiveScore = Integer.parseInt(items[2]);
               int finalScore = positiveScore - negativeScore;
               dictionary.add(new SimplePair(word, finalScore));
           }
       }
       writeToFile("dictionary1");
    }
    
    public static void createDictionary2() throws Exception {
       // Clear datastructure
       dictionary = new TreeSet<>();
        
       // Manual dictionary automatically included
       mergeDictionary(0);  
        
       BufferedReader in = new FileIO().getBufferedReader(filePath + "fullDictionary2.txt");
       String line;
       int score;
       boolean first = true;
       while ((line = in.readLine()) != null) {
           // In fullDictionary2.txt, polarity is marked by either "Negativ" or "Positiv"
           // on the third column of text.
           score = 0;
           String[] items = line.split("\\t");
           
           // Words are written in upper case
           String word = items[0].toLowerCase();
           
           // In case of words containing '#', take the word before the symbol.
           // Adding the words to a TreeSet ensures that only the first meaning of a word is considered.
           if (word.contains("#")) {
               int pos = word.indexOf("#");
               word = word.substring(0, pos);
           }
           
           // We only work with letters from the alphabet, so concatenating the components is the best we can do.
           if (word.contains("-")) {
               word = word.replaceAll("-","");
           }
           
           if (items[2].equals("Positiv")) {
               score = +1;
           }
           if (items[3].equals("Negativ")) {
               score = -1;
           } 
           dictionary.add(new SimplePair(word, score));
           }
       writeToFile("dictionary2");
    }
    
    public static void createDictionary3() throws Exception{
        // Clear datastructure
        dictionary = new TreeSet<>();
        
        // Manual dictionary automatically included
        // 4 is just a code to let the merger know that dictionary3 is the one being merged with
        mergeDictionary(4); 
        
        BufferedReader in = new FileIO().getBufferedReader(filePath + "fullDictionary3.txt");
        String line;
        while ((line = in.readLine()) != null) {
            /*  In the SentiWord dictionary, the useful data is stored in the following columns:
            *   column with index 2: positive score (0 <= score <= 1)
            *   column with index 3: negative score (0 <= score <= 1)
            *   column with index 4: word#definition_of_word
            */
            
            // We only work with letters from the alphabet, 
            // so the best we can do is concatenate words separated by other characters
            line = line.replaceAll("_","");
            line = line.replaceAll("-","");
            line = line.replaceAll("'","");
            
            String[] items = line.split("\\t");
            String[] words = items[4].split("#");
            // Don't take into consideration the last element of words, that is the definition
            for (int i = 0; i < words.length - 1; i++) {
                if (!words[i].matches(".*[0-9]+.*")) {
                    double posScore = Double.parseDouble(items[2]);
                    double negScore = Double.parseDouble(items[3]);
                    double objScore = 1 - posScore - negScore;
                    String label = posScore + "\t" + negScore + "\t" + objScore;
                    dictionary.add(new SimplePair(words[i], label));
                }
            }
        }
        writeToFile("dictionary3");
    } 
    
    private static void createEmoticonDictionary() throws Exception {
        ArrayList<String> array = new ArrayList<>();
        HashMap<String, String> hm = new HashMap<>();
        Comparator myComparator = new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                if (s1.length() < s2.length()) return -1;
                if (s1.length() == s2.length()) return s1.compareTo(s2);
                return 1;
            }
        };
        BufferedReader in = new FileIO().getBufferedReader(filePath + "emoticonsDictionary");
        BufferedWriter out = new FileIO().getBufferedWriter(filePath + "emoticons");
        String line;
        while ((line = in.readLine()) != null) {
            String[] items = line.split("\\t");
            array.add(items[0]);
            try {
                hm.put(items[0], items[1]);
            } catch (ArrayIndexOutOfBoundsException e) {
                return;
            }
        }
        Collections.sort(array, myComparator);
        for (int i = array.size() - 1; i >=0; i--) {
            out.write(array.get(i) + "\t" + hm.get(array.get(i)));
            out.write(System.getProperty("line.separator"));
        }
        out.close();
    }

    private static void mergeDictionary(int index) throws Exception {
        int i = index;
        if (index == 4) i = 0;
        BufferedReader in = new FileIO().getBufferedReader(filePath + "dictionary" + i);
        String line;
        while ((line = in.readLine()) != null) {
            String[] items = line.split("\\t");
            if (index == 4) {
                double posScore = Double.parseDouble(items[2]);
                double negScore = Double.parseDouble(items[3]);
                double objScore = Double.parseDouble(items[4]);
                String label = posScore + "\t" + negScore + "\t" + objScore;
                dictionary.add(new SimplePair(items[0], label));
            } else {
                double score = Double.parseDouble(items[1]);
                dictionary.add(new SimplePair(items[0], score));
            }
        }
    }
    
    private static void writeToFile(String fileName) throws Exception {
        BufferedWriter out = new FileIO().getBufferedWriter(filePath + fileName);
        for (SimplePair pair : dictionary) {
            out.write(pair.getLeft() + "\t" + pair.getRight());
            if (pair != dictionary.last()) {
                out.write(System.getProperty("line.separator"));
            }
        }
        out.close();
    }
    
    public static void combineDictionaries(int[] dictionaries) throws Exception{
        // Clear datastructure
        dictionary = new TreeSet<>();
        
        nDictionaries = dictionaries.length;
        if (nDictionaries <= 0) {
            System.out.println("You must specify at least one dictionary!");
        }
        if (nDictionaries > 4) {
            System.out.println("There is a maximum number of 4 dictionaries!");
        }
        for (int i = 0; i < nDictionaries; i++) {
            if ((dictionaries[i] < 0)||(dictionaries[i] > 3)) {
                System.out.println("Dictionary " + dictionaries[i] + "not available");
            }
        }
        for (int i = 0; i < nDictionaries; i++) {
            mergeDictionary(dictionaries[i]);
        }
        StringBuilder name = new StringBuilder("dictionary");
        for (int d : dictionaries)
            name.append(d);
        writeToFile(name.toString());
    }
    
    public static void main(String[] args) throws Exception {
        // These methods only need to be called once, because they produce files
        // that contain the corresponding dictionaries
        createDictionary1();
        createDictionary2();
        createDictionary3();
        createEmoticonDictionary();
        int[] c1 = {1, 2};
        combineDictionaries(c1);
        int[] c2 = {2, 1};
        combineDictionaries(c2);
    }
}
