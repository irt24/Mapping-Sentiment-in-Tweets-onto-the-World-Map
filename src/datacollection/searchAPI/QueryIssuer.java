package datacollection.searchAPI;

import javax.management.Query;

/**
 * Issues Queries
 */
public class QueryIssuer {
    Query query = new Query();
    
    private void getNextGeoLocation() {
        // TO DO: use list of country codes to get location from PLACES table
    }
    
    private void getQueryParams() {
        // TO DO: get topic
    }
    
    public Query getQuery() {
        return query;
    }
    
}
