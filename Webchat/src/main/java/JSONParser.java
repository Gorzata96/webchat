import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Gosia on 2017-01-16.
 */
public class JSONParser {
    private JSONParser() {}
    private static String readAll(Reader br) throws Exception {
        StringBuilder jsonSB = new StringBuilder();
        while (br.read() != -1) {
            jsonSB.append((char) br.read());
        }
        return jsonSB.toString();
    }

    public static JSONObject readJSONFromURL(String url) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream(), Charset.forName("UTF-8")));
        String jsonTxt = readAll(br);
        JSONObject obj;
        obj = new JSONObject(jsonTxt);
        br.close();
        return obj;
    }
}
