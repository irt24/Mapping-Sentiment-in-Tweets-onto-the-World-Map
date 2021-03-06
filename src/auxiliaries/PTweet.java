package auxiliaries;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This represents a minimally processed Tweet.
 * Each system will add more desired functionality.
 */
public class PTweet {
    
    protected String originalText;
    protected String formattedText;
    
    /*
     * Extracts possibly useful words in hashtags
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
                if ((c >= 'A')&&(c <= 'Z')) 
                    replacement.append((" " + c).toLowerCase());
                else replacement.append(c);
            }
            m.appendReplacement(newText, replacement.toString());
        }
        newText.append(formattedText.substring(offset));
        formattedText = newText.toString();
    }
    
    private void lowerCase() {
        formattedText = formattedText.toLowerCase();
    }
    
    private void removeURLs() {
       // Does not handle well the case when the URL is enclosed in brackets:
       // (http://example.com) - the closing bracket is considered to be part of the URL
        String regex = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        formattedText = formattedText.replaceAll(regex, "");
    }
     
    private void removeUserNames() {
        String regex = "@[a-zA-Z0-9_]{1,15}";
        formattedText = formattedText.replaceAll(regex, "");
    }     

    private void removeSpecialWords() {
         String regex = "(\\brt\\b)*(\\bmt\\b)*(\\bht\\b)*";
         formattedText = formattedText.replaceAll(regex, "");
     }
    
    private void removeSpecialCharacters() {
        // replace characters with spaces, in case they are separating words
        StringBuilder sb = new StringBuilder();
        String regex = "[a-zA-Z\\s\\t\\năâîşţ]";
        Pattern pattern = Pattern.compile(regex);
        for (int i = 0; i < formattedText.length(); i++) {
            char c = formattedText.charAt(i);
            Matcher matcher = pattern.matcher(c+"");
            if (matcher.find()) sb.append(c);
            else sb.append(" ");
        }
        formattedText = sb.toString();
    }
    
    private void removeRepeatingLetters() {
        String regex = "([a-z])\\1{3,}";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(formattedText);
        StringBuffer newText = new StringBuffer();
        int count = 1;  // number of X's that replace the sequence of repeating letters
        boolean found = false;
        while (m.find()) {
            found = true;
            m.appendReplacement(newText, "X");
        }
        m.appendTail(newText);
        // now replace "XXX" with the corresponding letter
        int j = 0;  // index in newWord
        for (int i = 0; i < formattedText.length(); i++) {
            char cWord = formattedText.charAt(i);
            char cNewWord = newText.charAt(j);
            if (cWord != cNewWord) {
               for (int k = j; k < j+count; k++) newText.replace(k, k+1, ""+cWord);
               j += count;
               while ((formattedText.charAt(i) == cWord) && (i < formattedText.length()-1)) i++;
            } else {
                j++;
            }
            if (j >= formattedText.length()) break;
        }
        formattedText = newText.toString();
    }
    
    public PTweet(String text) {
        originalText = text;
        formattedText = text;
        manageHashTags();
        lowerCase();
        removeURLs();
        removeUserNames();
        removeSpecialWords();
        removeSpecialCharacters();
        removeRepeatingLetters();
    }
    
    public String getOriginalText() {
        return originalText;
    }
    
    public String getFormattedText() {
        return formattedText;
    }
}
