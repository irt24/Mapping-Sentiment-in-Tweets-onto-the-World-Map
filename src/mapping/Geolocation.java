package mapping;

/**
 * This class represents the geolocation of a tweet
 */
public class Geolocation {

    private String json;
    private double latitude;
    private double longitude;
    
    public Geolocation(String json) {
        // Format of json:
        // GeoLocation{latitude=..., longitude=...}
        this.json = json;
        
        String aux = json;
        int pos1 = aux.toString().indexOf('=');
        int pos2 = aux.toString().indexOf(',');
        latitude = Double.parseDouble(aux.substring(pos1 + 1, pos2));
        
        aux = aux.substring(pos2);
        pos1 = aux.indexOf('=');
        pos2 = aux.indexOf('}');
        longitude = Double.parseDouble(aux.substring(pos1 + 1, pos2));
    }
    
    public String getJson() {
        return json;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
}
