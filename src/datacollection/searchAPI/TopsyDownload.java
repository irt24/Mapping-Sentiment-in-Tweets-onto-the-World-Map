package datacollection.searchAPI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.text.Normalizer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class TopsyDownload {
  
    public static void main(String[] args) throws Exception{
        URL topsy = new URL("http://otter.topsy.com/search.json?apikey=200B55F96B1F41089AA5B95A6DE61981&q=US+elections&mintime=1351555200&maxtime=1352160000&allow_lang=en&perpage=100&page=5");
        URLConnection conn = topsy.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        String jsonTxt = null;
        while ((inputLine = in.readLine()) != null) {
            System.out.println(inputLine);
            jsonTxt = inputLine;
        }
        in.close();
        jsonTxt = Normalizer.normalize(jsonTxt,
				Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonTxt);
        JSONObject response = json.getJSONObject("response");
        JSONArray results = response.getJSONArray("list");
        for (int i=0; i<results.size(); i++) {
            JSONObject tweet = results.getJSONObject(i);
            System.out.println(tweet.getString("content"));
            System.out.println();
        }
    }
}

