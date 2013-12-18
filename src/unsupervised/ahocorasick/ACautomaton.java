package unsupervised.ahocorasick;

import auxiliaries.SimpleDictionary;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;

/*
 * This class constructs the automaton and serialises it for future use.
 */

public class ACautomaton implements Serializable{
    private TreeSet<String> wordsSet;
    private Trie trie;
    private HashMap<State, State> fail = new HashMap<>();
    private HashMap<State, TreeSet<String>> patterns = new HashMap<>();
    
    public TreeSet<String> getDictionary() {
        return wordsSet;
    }
    
    public Trie getTrie() {
        return trie;
    }
    
    public void add2trie(String word, int wordIndex) {
        // Check the sanity of the word
        if (word == null) return;
        String pattern= "^[a-zA-Z]*$";
        if(!word.matches(pattern)) return;
        // Iterate over word and add states and edges for all symbols.
        State state1 = trie.getRoot();
        for (int i=0; i<word.length(); i++) {
            // c represents the label of an outgoing edge from state1
            char c = word.charAt(i);
            
            // state2 is the child of state1
            int index = 0;
            if (i == word.length() - 1) {
                index = wordIndex;
            }
            State state2 = new State(state1.getLabel() + c, index);
            trie.addState(state2);
            
            // Note that the states incident to the edge will not have an up-to-date wordIndex
            Edge edge = new Edge(state1, state2, c);
            trie.addEdge(edge);
            
            state1 = state2;
        }
    }
    
    private void performDF(State state, TreeSet<String> statePatterns) {
        if (state.getWordIndex() != 0){
            statePatterns.add(state.getLabel());
        }
        patterns.put(state, statePatterns);
        TreeSet<Edge> edges = trie.getSetsOfEdges().get(state);
        TreeSet<State> adjStates = new TreeSet<>();
        for (Edge edge : edges) {
            adjStates.add(edge.getState2());
        }
        for (State adjState : adjStates) {
            TreeSet<String> newStatePatterns = new TreeSet<>();
            newStatePatterns.addAll(statePatterns);
            performDF(adjState, newStatePatterns);
        }
    }
    
    private void initialisePatterns() {
        // perform DF on the trie
        State currentState = trie.getRoot();
        performDF(currentState, new TreeSet<String>());
    }
    
    /*
     * This function gives the state entered from current state by matching the target character a
     * - if edge(currentState, v) is labeled by a, then goTo(currentState, a) = v;
     * - goTo(root, a) = root for each a that does not label an edge out of the root;
     * (so the automaton stays at the initial state while scanning non-matching characters)
     * - otherwise, goTo(currentState, a) = null.
     * (The goTo function represented by green arcs on Wikipedia)
     */
    public State goTo (State currentState, char edgeLabel) {
        // Check whether the current state exists
        TreeSet<State> states = trie.getStates();
        if (!states.contains(currentState)) {
            return null;
        }
        // Check whether there is an outgoing edge from the current state labeled by edgeLabel
        State nextState = trie.getState2(currentState, edgeLabel);
        if ((nextState == null)&&(currentState.equals(trie.getRoot()))) {
            nextState = trie.getRoot();
        } 
        return nextState;
    }
    
    /*
     * The failure function for currentState != root gives the state entered at a mismatch.
     * failure(currentState) is the node labeled by the longest proper suffix w of the label
     * of currentState such that w is the prefix of some pattern.
     * (The failure function is represented by blue arcs on Wikipedia)
     */
    public State failure(State currentState) {
        return fail.get(currentState);
    }
    
    /*
     * The out function gives the set of patterns recognised when entering state currentState
     */
    public TreeSet<String> out(State currentState) {
        return patterns.get(currentState);
    }
    
    /*
     * The fail hash is compute by traversing the nodes in breadth-first order.
     * Hence, nodes closer to the root have already been processed.
     * Consider nodes r and u = goTo(r, c), that is, r is the parent of u and 
     * label(u) = label(r) + c.
     * fail(u) should be the deepest node labeled by a proper suffix of label(u).
     */
    private void constructHashes() {
        Queue q = new LinkedList();
        // Assume all the words consist of a-z characters
        for (char c = 'a'; c <= 'z'; c++) {
            State nextState = goTo(trie.getRoot(), c);
            if (nextState != trie.getRoot()) {
                fail.put(nextState, trie.getRoot());
                q.add(nextState);
                if (nextState.getWordIndex() != 0) {
                    patterns.get(nextState).add(nextState.getLabel());
                }
            }
        }
        while (!q.isEmpty()) {
            State r = (State)q.remove();
            for (char c = 'a'; c <= 'z'; c++) {
                State u = goTo(r, c);
                if (u != null) {
                    q.add(u);
                    State v = failure(r);
                    // The execution of the following while locates the deepest node v such that
                    // label(v) is a proper suffix of label(r) and goTo(v, c) is defined.
                    // Notice that v and toTo(v, c) may both be the root.
                    while (goTo(v, c) == null) {
                        v = failure(v);
                    }
                    fail.put(u, goTo(v, c));
                    
                    // Recognised patterns of f(u) are also recognised patterns of u.
                    patterns.get(u).addAll(patterns.get(failure(u)));
                }
            }  
        }
    }
    
    public ACautomaton(SimpleDictionary dictionary) {
        wordsSet = dictionary.getWords();
        
        // Construct the trie (Corresponds to phase 1 of AC construction)
        trie = new Trie();
        int wordIndex = 0;
        for(String word : wordsSet) {
            wordIndex++;
            add2trie(word, wordIndex);
        }
        
        // Phase 2 of AC construction
        initialisePatterns();
        constructHashes();
    }
}
