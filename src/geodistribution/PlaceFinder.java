package geodistribution;

import auxiliaries.FileIO;
import auxiliaries.MyConnection;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class PlaceFinder {
    
    private static ArrayList<String> countries = new ArrayList<>();
    private static ArrayList<String> states = new ArrayList<>();
    private static Connection conn;
    
    public static List<Object> parseJSON(String result, String type) {
        JSONObject json = (JSONObject) JSONSerializer.toJSON(result);
        JSONObject resultSet = json.getJSONObject("ResultSet");
        if ("0".equals(resultSet.getString("Found"))) {
            return null;
        }
        JSONArray results = resultSet.getJSONArray("Results");
        JSONObject data = results.getJSONObject(0);
        List<Object> values = new LinkedList<>();
        if (type.equals("country")) {
            values.add(data.getString("country"));
            values.add(data.getString("countrycode"));
            // set the type of the place
            values.add("country");
        } else {
            values.add(data.getString("state"));
            values.add(data.getString("statecode"));
            // set the type of the place
            values.add("state");
        }
        values.add(data.getString("latitude"));
        values.add(data.getString("longitude"));
        values.add(data.getString("offsetlat"));
        values.add(data.getString("offsetlon"));
        values.add(data.getString("radius"));
        values.add(data.getString("woeid"));
        values.add(data.getString("woetype"));
        JSONObject bounds = data.getJSONObject("boundingbox");
        values.add(bounds.getString("north"));
        values.add(bounds.getString("south"));
        values.add(bounds.getString("east"));
        values.add(bounds.getString("west"));
        return values;
    }
    
    public static void updateDatabase(List<Object> values) throws SQLException {
        StringBuilder insertString = new StringBuilder("INSERT INTO PLACES VALUES(");
        for (Object v : values) {
            insertString.append("'");
            if (v != null) {
                insertString.append(v.toString().replaceAll("'", "''"));
            }
            insertString.append("',");
        }
        // remove unnecessary comma
        insertString.deleteCharAt(insertString.length() - 1);
        insertString.append(")");
        
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(insertString.toString());
        if (stmt != null) {
            stmt.close();
        }
    }
    
    public static void getCountries() throws FileNotFoundException, IOException {
        BufferedReader br = new FileIO().getBufferedReader("CountryCodes");
        String country;
        while ((country = br.readLine()) != null) {
            String[] data = country.split(",");
            // data[0] = country name, data[1] = country code;
            data[0] = data[0].replaceAll("'", "''");
            data[0] = data[0].replaceAll(" ", "%20");
            countries.add(data[0]);
        }  
    }
    
    public static void getStates()throws FileNotFoundException, IOException {
        BufferedReader br = new FileIO().getBufferedReader("AmericanStates");
        String state;
        while ((state = br.readLine()) != null) {
            state = state.replaceAll("'", "''");
            state = state.replaceAll(" ", "%20");
            states.add(state);
        }  
    }
    
    public static void findCountry(String country) throws MalformedURLException, IOException, SQLException{
        URL url = new URL("http://where.yahooapis.com/geocode?flags=JX&country=" + country);
        URLConnection connection = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        String result = reader.readLine();
        List<Object> values = parseJSON(result, "country");
        // Overwrite country, sometimes commonwealths lose their names
        if (values != null) {
            country = country.replaceAll("%20", " ");
            values.set(0, country);
        }
        if (values != null) {
            updateDatabase(values);
        }
    }
    
    public static void findState(String state) throws MalformedURLException, IOException, SQLException {
        URL url = new URL("http://where.yahooapis.com/geocode?flags=JX&state=" + state);
        // A flag set to "J" returns data in JSON format
        // A flag set to "X" returns a boundingbox field. Also, this requires the "P" flag not to be set.
        URLConnection connection = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        String result = reader.readLine();
        //System.out.println(result);
        List<Object> values = parseJSON(result, "state");
        if (values != null) {
            updateDatabase(values);
        }
    }
    
    public static void main(String[] args) throws FileNotFoundException, 
            IOException, SQLException, MalformedURLException{
       conn = new MyConnection().getConnection();
       
       getCountries();
       for(String country : countries) {
                System.out.println(country);
                findCountry(country);
            }
       
       getStates();
       for (String state: states) {
           System.out.println(state);
           findState(state);
       }
    }
}
