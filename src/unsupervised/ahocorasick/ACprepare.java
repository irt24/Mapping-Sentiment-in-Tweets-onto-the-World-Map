package unsupervised.ahocorasick;

import auxiliaries.SimpleDictionary;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This class is meant to prepare the automaton that Aho-Corasick will use for all the Tweets.
 * Its purpose is efficiency (so that the automaton doesn't need to be loaded each time Aho-Corasick is called)
 */
public class ACprepare {
    
    SimpleDictionary dictionary;
    ACautomaton fsm;
    
    private ACautomaton constructAutomaton() {
        fsm = null;
        try {
           FileInputStream fIn = new FileInputStream(dictionary.getFileName() + ".data");
           ObjectInputStream objIn = new ObjectInputStream(fIn);
           fsm = (ACautomaton) objIn.readObject();
           System.out.println("Automaton is already serialised.");
        } catch(IOException e) {
           try {
               fsm = new ACautomaton(dictionary);
               FileOutputStream fOut = new FileOutputStream(dictionary.getFileName() + ".data");
               ObjectOutputStream objOut = new ObjectOutputStream(fOut);
               objOut.writeObject(fsm);
               System.out.println("Automaton was serialised.");
           } catch (IOException ex) {
               System.out.println("The automaton cannot be serialised");
           }
        } catch (ClassNotFoundException e) {
           System.out.println("Cannot find automaton class");
        }
        return fsm;
    }
    
    public ACprepare(SimpleDictionary dictionary) {
        this.dictionary = dictionary;
        fsm = constructAutomaton();
    }
    
    public ACautomaton getAutomaton() {
        return fsm;
    }
}
