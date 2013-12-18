package datacollection.searchAPI;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class AuthTwitter{
    
    private Twitter twitter = new TwitterFactory().getInstance();
    private String consumerKey = "S5A8B7h3nNzZJMw9eKw7iQ";
    private String consumerSecret = "rbkL8LAw78hmcD1KSScS4szMczD9tIGCLLpGDO9N8";
    private String token = "912605168-Khgh84JtER5SBaAX848rIAW3LzL8eIbjOpSxO0qS";
    private String tokenSecret = "VY5t4SdrDs6uKfqv9oOfAJTfzusMIM5sJBQr4eixVvw";
    
    public Twitter getTwitter() {
        AccessToken accessToken = new AccessToken(token, tokenSecret);
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        twitter.setOAuthAccessToken(accessToken);
        return twitter;
    }    
}
