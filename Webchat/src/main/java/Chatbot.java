import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Gosia on 2017-01-16.
 */
public class Chatbot {
    private Weather weather = new Weather();
    public String answer(String msg) {
        System.out.print(msg);
        if(msg.equals("Która godzina?")) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            return "Jest " + sdf.format(calendar.getTime()) + ".";
        }
        else if(msg.equals("Jaki dziś dzień tygodnia?")) {
            String[] dayOfTheWeek = {"poniedziałek", "wtorek", "środa", "czwartek", "piątek", "sobota", "niedziela"};
            return "Dziś jest " + dayOfTheWeek[Integer.parseInt(new SimpleDateFormat("u").format(new Date())) - 1] + ".";
        }
        else if(msg.equals("Jaka jest pogoda w Krakowie?")) {
            try {
                weather.get();
            } catch (Exception e) {
                System.err.println("Błąd w aktualizowaniu pogody.");
            }
            if(weather.isUpdated)
                return weather.toString();
            else return "Nie mogę podać aktualnej pogody.";
        }
        else return "Nie rozumiem pytania. ";
    }
}
