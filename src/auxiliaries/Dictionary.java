package auxiliaries;

import java.util.TreeSet;

public class Dictionary {
    protected String fileName;
    protected TreeSet<String> words = new TreeSet<>();
    
    public TreeSet<String> getWords() {
        return words;
    }
}
