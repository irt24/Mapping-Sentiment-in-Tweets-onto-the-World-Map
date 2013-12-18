package romanian;

import auxiliaries.FileIO;
import auxiliaries.PTweet;
import auxiliaries.Pair;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

/**
 * Represents a Romanian Tweet, after preprocessing
 */
public class RomPTweet extends PTweet{

    public TreeSet<String> bagOfWords = new TreeSet<>();
    
    private final String path = "text_files\\romanian\\lemmatization\\";
    private final String inputFile = "input.txt";
    private FileIO io = new FileIO();
    
    /*
     * Creates a file containing the tokens in the Tweet, which is later
     * fed into the lemmatizer.
     */
    private void createTokenFile() throws IOException{
        BufferedWriter out = io.getBufferedWriter(path + inputFile);
        Properties props = new Properties();
        props.put("annotators","tokenize, ssplit");
        StanfordCoreNLP snlp = new StanfordCoreNLP(props);
        Annotation document = new Annotation(formattedText);
        snlp.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        if (sentences == null) System.out.println("No sentences");
        for (CoreMap sentence : sentences) {
           List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
           for (CoreLabel token : tokens) {
               out.write(token.originalText());
               out.write(System.getProperty("line.separator"));
           }   
        }
        out.close();
    }
    
    private void printErrors(InputStream errors) throws IOException{
       BufferedReader in = new BufferedReader(new InputStreamReader(errors));
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        } 
    }
    
    private void moveFile(String task) throws Exception{
        Process proc = Runtime.getRuntime().exec(
                "mv .\\"                        // move
                + task +"Tagged.txt .\\"        // file just created
                + path + task + "Tagged.txt"    // to the right location
                );
        printErrors(proc.getErrorStream());
    }
    
    private void runTagger(String task, String input) throws Exception{
        Process proc1 = Runtime.getRuntime().exec(
                "java -cp " 
                + path + "BTagger.jar bTagger/BTagger -p " 
                + task + " "
                + input + " "                   // input text file
                + path + task + ".fea "         // POS feature weight file
                + path + task + ".scr"          // PSO feature script file
                ); 
        printErrors(proc1.getErrorStream());
        proc1.waitFor();
        moveFile(task);
    }
    
    private void decodeTags() throws Exception{
        Process proc = Runtime.getRuntime().exec(
                "java  -cp BTagger.jar LCS_WDiff2L "
                + path + "lemTagged.txt "           // input file from the lemmatizer     
                + path + "finalTagged.txt "         // final output file    
                + "1 3"                             // column indices (word and lemma tag)
                );
        printErrors(proc.getErrorStream());
    }
    
    private String clean(String word) {
        String newWord = word.replaceAll("ã¢", "â").replaceAll("äƒ", "ă");
        return newWord;
    }
    
    private void createBag() throws Exception {
        BufferedReader in = io.getBufferedReader(path + "finalTagged.txt");
        String line;
        while ((line = in.readLine()) != null) {
            if (!line.contains("\t")) continue;
            String[] items = line.split("\\t");
            // the lemma is on the thrid column
            // manually replace incorrect characters
            bagOfWords.add(clean(items[2]));
        }
    }
    
    private void lemmatize() {
        try {
            createTokenFile();
        } catch(Exception e) {
            System.out.println("Problem with tokenisation");
        }
        // Run POS Tagger
        try {
            runTagger("pos", path + inputFile);
        } catch(Exception e) {
            System.out.println("Problem with pos");
        }
        // Run Lemmatizer
        try {
            runTagger("lem", path + "posTagged.txt");
        } catch(Exception e) {
            System.out.println("Problem with lem");
        }
        // Decode lemma-tags
        try {
            decodeTags();
        } catch(Exception e) {
            System.out.println("Problem with decoding");
        }
        // Create bag of words
        try {
            createBag();
        } catch(Exception e) {
            System.out.println("Problems creating the bag");
            e.printStackTrace();
        }
    }
    
    public RomPTweet(String text) {
        super(text);
        lemmatize();
    }
    
    public TreeSet<String> getBagOfWords() {
        return bagOfWords;
    }
}
