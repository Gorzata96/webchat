import org.json.JSONObject;

/**
 * Created by Gosia on 2017-01-16.
 */
public class Weather {
    private final String url = "http://api.openweathermap.org/data/2.5/weather?q=Krakow&APPID=de689db2f4dbe9e93da35904cbe3c043";
    private double temp;
    private int pressure;
    private int humidity;
    boolean isUpdated;

    public void get() throws Exception {
        JSONObject obj = JSONParser.readJSONFromURL(url);
        JSONObject main = obj.getJSONObject("main");

        temp = main.getDouble("temp");
        pressure = main.getInt("pressure");
        humidity = main.getInt("humidity");

        isUpdated = true;
    }

    public String getTemp() {
        return Double.toString(temp - 273.15) + "C";
    }

    public String getPressure() {
        return Integer.toString(pressure) + "hPa";
    }

    public String getHumidity() {
        return Integer.toString(humidity) + "%";
    }

    public String toString() {
        return "Obecnie w Krakowie jest: " + this.getTemp() + ", ciśnienie: " + this.getPressure() + ", wilgotność powietrza: " + this.getHumidity();
    }
}
