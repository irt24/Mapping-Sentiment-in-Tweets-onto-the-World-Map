package unsupervised.ahocorasick;

import java.util.TreeSet;

/*
 * Given a text and a dictionary, the Aho-Corasick algorithm identifies all the
 * words in the dictionary that occur in the text.
 */
public class AhoCorasick {
    
    private TreeSet<String> knownWords = new TreeSet<>();
    
    /*
     * Check whether the pattern is the suffix or prefix of a word.
     * If not, then it is a valid word.
     */
    private boolean validWord(String text, String pattern) {
        int pos = text.indexOf(pattern);
        char charBefore = ' ';
        char charAfter = ' ';
        try {
            charBefore = text.charAt(pos - 1);
        } catch(IndexOutOfBoundsException e) {
           // Do nothing. The pattern is at the beginning of the text. 
        }
        try {
            charAfter = text.charAt(pos + pattern.length());
        } catch(IndexOutOfBoundsException e) {
            // Do nothing. The patern is at the end of the text.
        }
        if ((charBefore >='a')&&(charBefore <='z')) {
            return false;
        }
        if ((charAfter >='a')&&(charAfter <='z')) {
            return false;
        }
        return true;
    }
    
    public AhoCorasick(ACautomaton fsm, String text) {
        Trie trie = fsm.getTrie();
        State state = trie.getRoot();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            // De-comment this line in case of NullPointerException
            // usually, the problem is symbols in dicitionaries not supported by 
            // Aho-Corasick.
            //state.printlnState();
            while (fsm.goTo(state, c) == null) {
                state = fsm.failure(state); 
            }
            state = fsm.goTo(state, c);
            TreeSet<String> patterns = fsm.out(state);
            if (patterns != null) {
                for (String pattern : patterns) {
                    if (validWord(text, pattern)) {
                        knownWords.add(pattern);
                    }
                }
            }
        }
    }
    
    public TreeSet<String> getKnownWords() { 
        return knownWords;
    }
}
