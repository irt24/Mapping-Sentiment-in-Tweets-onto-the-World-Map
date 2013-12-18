package unsupervised.ahocorasick;

import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.lang.builder.HashCodeBuilder;

/*
 * A state is a node of the keyword tree. 
 * Define the label of a state v as the concatenation of edge lables on the path
 * from the root to v.
 */
public class State implements Comparable<State>, Serializable{
    private String label;
    private int wordIndex;
    
    public State(String label, int wordIndex) {
        this.label = label;
        if (wordIndex >= 0) {
            this.wordIndex = wordIndex;
        }
        else {
            this.wordIndex = 0;
        }
    }
    
    @Override
    public int compareTo(State otherState) {
        return label.compareTo(otherState.getLabel());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final State other = (State) obj;
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(label).toHashCode();
    }
    
    public String getLabel() {
        return label;
    }
    
    public int getWordIndex() {
        return wordIndex;
    }
    
    public void printState() {
        System.out.print("State \"" + label + wordIndex + "\"");
    }
    
    public void printlnState() {
        printState();
        System.out.println();
    }
}
