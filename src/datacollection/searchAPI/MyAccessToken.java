package datacollection.searchAPI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class MyAccessToken {
    
  public static void main(String args[]) throws Exception{
    // The factory instance is re-useable and thread safe.
    Twitter twitter = new TwitterFactory().getInstance();
    twitter.setOAuthConsumer("S5A8B7h3nNzZJMw9eKw7iQ", "rbkL8LAw78hmcD1KSScS4szMczD9tIGCLLpGDO9N8");
    RequestToken requestToken = twitter.getOAuthRequestToken();
    AccessToken accessToken = null;
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    while (null == accessToken) {
      System.out.println("Open the following URL and grant access to your account:");
      System.out.println(requestToken.getAuthorizationURL());
      System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
      String pin = br.readLine();
      try{
         if(pin.length() > 0){
           accessToken = twitter.getOAuthAccessToken(requestToken, pin);
         }else{
           accessToken = twitter.getOAuthAccessToken();
         }
      } catch (TwitterException te) {
        if(401 == te.getStatusCode()){
          System.out.println("Unable to get the access token.");
        }else{
          te.printStackTrace();
        }
      }
    }
    //persist to the accessToken for future reference.
    storeAccessToken((int)twitter.verifyCredentials().getId() , accessToken);
    //Status status = twitter.updateStatus(args[0]);
    //System.out.println("Successfully updated the status to [" + status.getText() + "].");
    System.exit(0);
  }
  private static void storeAccessToken(int useId, AccessToken accessToken){
    System.out.println(accessToken.getToken());
    System.out.println(accessToken.getTokenSecret());
    // Token: 912605168-Khgh84JtER5SBaAX848rIAW3LzL8eIbjOpSxO0qS
    // Token Secret: VY5t4SdrDs6uKfqv9oOfAJTfzusMIM5sJBQr4eixVvw
  }
}
