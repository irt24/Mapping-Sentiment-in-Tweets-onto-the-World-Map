package preprocessing;

import auxiliaries.Pair;
import auxiliaries.Tweet;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pre-process tweet (extended separately by the two classifiers)
 */
public class ProcessedTweet extends Tweet{

    protected String formattedText;    // intermediate format
    private ArrayList<String> wordList = new ArrayList<>();

    public ProcessedTweet(String id, String originalText, ArrayList<Pair> emoticons,
            boolean removeEmoticons) {
        super(id, originalText);
        this.formattedText = originalText;

        // Retrieve useful information from hashtags, before converting the text to lower case.
        manageHashTags();
        //Remove hash tags completely by uncommenting the method.
        //removeHashTags();
        formattedText = formattedText.toLowerCase();

        removeURLs();
        removeUserNames();
        removeSpecialWords();

        // In case of emoticon queries, the emoticons used for queries are removed
        // (and the rest are replaced)
        if (removeEmoticons) {
            removeEmoticons();
        }
        replaceEmoticons(emoticons);

        // Remove special characters only after emoticon manipulation
        removeSpecialCharacters();

        // Tokenize tweet and put tokens in bagOfWords
        try {
            tokenize();
        } catch (IOException e) {
            System.out.println("Usage of Stanford Core NLP library failed");
        }
    }

    /*
     * Usually, hashtags contain multiple concatenated words, with initial letter capitalised.
     * They might provide useful information for the classifiers. 
     */
    private void manageHashTags() {
        String regex = "#[a-zA-Z0-9_]{1,15}";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(formattedText);
        StringBuffer newText = new StringBuffer();
        int offset = 0;
        while (m.find()) {
            String hashTag = m.group();
            offset = m.end();
            StringBuilder replacement = new StringBuilder();
            // start iteration from 1 to ignore '#'
            for (int i = 1; i < hashTag.length(); i++) {
                char c = hashTag.charAt(i);
                if ((c >= 'A') && (c <= 'Z')) {
                    replacement.append((" " + c).toLowerCase());
                } else {
                    replacement.append(c);
                }
            }
            m.appendReplacement(newText, replacement.toString());
        }
        newText.append(formattedText.substring(offset));
        formattedText = newText.toString();
    }

    /*
     * Removes only those emoticons that have been used in queries.
     * These are: :-), :), :d, :-(, :(, =(
     */
    private void removeEmoticons() {
        String emoRegex = "(:-\\))*(:\\))*(:d)*(:-\\()*(:\\()*(=\\()*";
        formattedText = formattedText.replaceAll(emoRegex, "");
    }

    /*
     * Replaces the remaining emoticons with their corresponding meaning in words
     */
    private void replaceEmoticons(ArrayList<Pair> emoticons) {
        StringBuilder sb = new StringBuilder(formattedText);
        boolean emo = false;
        int count = 0;
        for (Pair pair : emoticons) {
            String left = (String) pair.getLeft();
            String right = (String) pair.getRight();
            int pos = sb.indexOf(left);
            while (pos != -1) {
                emo = true;
                //put extra spaces because some of the emoticons are immediately adjacent to words
                sb.replace(pos, pos + left.length(), " " + right + " ");
                pos = sb.indexOf(left);
                count++;
            }
        }
        formattedText = sb.toString();
    }

    private void removeURLs() {
        // Does not handle well the case when the URL is enclosed in brackets:
        // (http://example.com) - the closing bracket is considered to be part of the URL
        String regex = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        formattedText = formattedText.replaceAll(regex, "");
    }

    /*
     private void removeHashTags() {
     String regex = "#[a-zA-Z0-9_]{1,15}";
     formattedText = formattedText.replaceAll(regex, "");
     }
     */
    private void removeUserNames() {
        String regex = "@[a-zA-Z0-9_]{1,15}";
        formattedText = formattedText.replaceAll(regex, "");
    }

    private void removeSpecialWords() {
        String regex = "(\\brt\\b)*(\\bmt\\b)*(\\bht\\b)*";
        formattedText = formattedText.replaceAll(regex, "");
    }

    private void removeSpecialCharacters() {
        // Keep track of exclamation and question marks.
        StringBuilder sb = new StringBuilder();
        String regex = "[a-zA-Z\\s\\t\\n]";
        Pattern pattern = Pattern.compile(regex);
        for (int i = 0; i < formattedText.length(); i++) {
            char c = formattedText.charAt(i);
            Matcher matcher = pattern.matcher(c + "");
            if (matcher.find()) {
                sb.append(c);
            } else {
                if (c == '!') sb.append(" EX");
                if (c == '?') sb.append(" QN");
                sb.append(" ");
            }
        }
        formattedText = sb.toString();
    }

    private String removeRepeatingLetters(String word) {
        String regex = "([a-z])\\1{3,}";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(word);
        StringBuffer newWord = new StringBuffer();
        int count = 1;  // number of X's that replace the sequence of repeating letters
        boolean repeated = false;
        while (m.find()) {
            repeated = true;
            m.appendReplacement(newWord, "X");
        }
        m.appendTail(newWord);
        // now replace "XXX" with the corresponding letter
        int j = 0;  // index in newWord
        for (int i = 0; i < word.length(); i++) {
            char cWord = word.charAt(i);
            char cNewWord = newWord.charAt(j);
            if (cWord != cNewWord) {
                for (int k = j; k < j + count; k++) {
                    newWord.replace(k, k + 1, "" + cWord);
                }
                j += count;
                while ((word.charAt(i) == cWord) && (i < word.length() - 1)) {
                    i++;
                }
            } else {
                j++;
            }
            if (j >= word.length()) {
                break;
            }
        }
        String toReturn = newWord.toString();
        // Mark elongated words
        if (repeated) return "E" + toReturn;
        return toReturn;
    }

    private void tokenize() throws IOException {
        // Create StanfordCoreNLP object
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        StanfordCoreNLP snlp = new StanfordCoreNLP(props);
        // Annotate the text
        Annotation document = new Annotation(formattedText);
        snlp.annotate(document);
        // Use the annotated object
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            // traversing the words in the current sentence
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the lemma of the token
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase();
                wordList.add(removeRepeatingLetters(lemma));
            }
        }
    }
    
    public String getFormattedText() {
        return formattedText;
    }

    public ArrayList<String> getWordList() {
        return wordList;
    }
}
