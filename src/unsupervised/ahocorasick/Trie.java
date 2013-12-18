package unsupervised.ahocorasick;

import java.io.Serializable;
import java.util.HashMap;
import java.util.TreeSet;

/*
 * A keyword tree (or trie) for a dictionary D is a rooted tree K such that:
 * 1. each edge of K is labeled by a character;
 * 2. any two edges out of a node have different labels;
 * Define the label of a node v as the concatenation of edge labels on the path 
 * from the root to v, and denote it by L(v).
 * 3. for each word in the dictionary, there's a node v with L(v) = word;
 * 4. the label L(v) of any leaf v equals some word in the dictionary.
 */
public class Trie implements Serializable{
    private State root;
    private TreeSet<State> states = new TreeSet<>();
    private HashMap<State, TreeSet<Edge>> setsOfEdges = new HashMap<>();
    
    public Trie() {
        root = new State("",0);
        addState(root);
    }
    
    public boolean addEdge(Edge e) {
        if (e == null) return false;
        State state1 = e.getState1();
        State state2 = e.getState2();
        if (!states.contains(state1)) {
            return false;
        }
        if (!states.contains(state2)) {
            return false;
        }
        if(setsOfEdges.get(state1).add(e)) return true;
        else return false;
    }
    
    public final boolean addState(State s) {
        if (s == null) return false;
        if (states.add(s)) {
            // Instantiate an edge list for this state;
            setsOfEdges.put(s, new TreeSet<Edge>());
            return true;
        }
        return false;
    }
    
    public State getRoot() {
        return root;
    }
    
    public HashMap<State, TreeSet<Edge>> getSetsOfEdges() {
        return setsOfEdges;
    }
    
    public TreeSet<State> getStates() {
        return states;
    }
    
    public void printTrie() {
        System.out.println("The states and the corresponding edges in the trie are: ");
        for (State s : states) {
            s.printlnState();
            TreeSet<Edge> edges = setsOfEdges.get(s);
            if (!(edges == null)) {
                for (Edge e: edges) {
                    e.printlnEdge();
                }
            }
        }
    }
    
    public State getState2(State state1, char label) {
        TreeSet<Edge> edges = setsOfEdges.get(state1);
        for (Edge edge : edges) {
            if (edge.getLabel() == label) {
                return edge.getState2();
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        Trie trie = (Trie) o;
        if (this.root.equals(trie.root) && (this.states.equals(trie.states) && (this.setsOfEdges.equals(trie.setsOfEdges))))
            return true;
        return false;
    }

 }
