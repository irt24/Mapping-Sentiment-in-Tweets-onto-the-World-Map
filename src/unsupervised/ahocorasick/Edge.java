package unsupervised.ahocorasick;

import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.lang.builder.HashCodeBuilder;

/*
 *  An edge in a trie si labeled by a character.
 *  Any two edges out of a node have different labels
 */
public class Edge implements Comparable<Edge>, Serializable{
    private State state1;
    private State state2;
    private char label;
    
    public Edge(State state1, State state2, char label) {
        this.state1 = state1;
        this.state2 = state2;
        this.label = label;
    }
    
    @Override
    public int compareTo(Edge anotherEdge) {
        int state1Eq = state1.compareTo(anotherEdge.getState1());
        int state2Eq = state2.compareTo(anotherEdge.getState2());
        if (state1Eq > 0) {
            return 1;
        }
        if (state1Eq < 0) {
            return -1;
        }
        // state1Eq == 0
        if (state2Eq > 0) {
            return 1;
        }
        if (state2Eq < 0) {
            return -1;
        }
        // state2Eq == 0
        if (label > anotherEdge.getLabel()) {
            return 1;
        }
        if (label < anotherEdge.getLabel()) {
            return -1;
        }
        // label == anotherEdge.getLabel();
        return 0;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final Edge other = (Edge) obj;
        if (!Objects.equals(this.state1, other.state1)) {
            return false;
        }
        if (!Objects.equals(this.state2, other.state2)) {
            return false;
        }
        if (this.label != other.label) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31)
                .append(state1.getLabel())
                .append(state2.getLabel())
                .append(label)
                .toHashCode();
    }
    
    public State getState1() {
        return state1;
    }
    
    public State getState2() {
        return state2;
    }
    
    public char getLabel() {
        return label;
    }
    
    public void printEdge() {
        System.out.print("Edge: (");
        state1.printState();
        System.out.print(", ");
        state2.printState();
        System.out.print(", " + label + ")");
    }
    
    public void printlnEdge() {
        printEdge();
        System.out.println();
    }
}
