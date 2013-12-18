package datacollection.streamingAPI;

import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;

public class AuthTwitterStream {
    
    private TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
    private String consumerKey = "S5A8B7h3nNzZJMw9eKw7iQ";
    private String consumerSecret = "rbkL8LAw78hmcD1KSScS4szMczD9tIGCLLpGDO9N8";
    private String token = "912605168-Khgh84JtER5SBaAX848rIAW3LzL8eIbjOpSxO0qS";
    private String tokenSecret = "VY5t4SdrDs6uKfqv9oOfAJTfzusMIM5sJBQr4eixVvw";
    
    public TwitterStream getTwitterStream() {
        AccessToken accessToken = new AccessToken(token, tokenSecret);
        twitterStream.setOAuthConsumer(consumerKey, consumerSecret);
        twitterStream.setOAuthAccessToken(accessToken);
        return twitterStream;
    }   
}
