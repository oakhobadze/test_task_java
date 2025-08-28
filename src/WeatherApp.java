import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherApp {
    private static final String BASE_URL = "http://api.weatherapi.com/v1/forecast.json";

    public static void main(String[] args) {
        String apiKey = System.getenv("WEATHER_API_KEY");

        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("Error: API_KEY is not set");
            return;
        }

        String[] cities = {"Chisinau", "Madrid", "Kyiv", "Amsterdam"};

        String forecastDate = getForecastDate(apiKey, cities[0]);

        System.out.printf("%-12s | %-10s | %-10s | %-10s | %-12s | %-10s | %-10s%n",
                "City", "Date", "Min Temp", "Max Temp", "Humidity %", "Wind kp/h", "Wind dir");
        System.out.println("-------------------------------------------------------------------------------------------------");

        for (String city : cities) {
            try {
                fetchAndPrintWeather(apiKey, city, forecastDate);
            } catch (Exception e) {
                System.out.println("Error fetching weather for " + city + ": " + e.getMessage());
            }
        }
    }

    private static String getForecastDate(String apiKey, String city) {
        try {
            String url = BASE_URL + "?key=" + apiKey + "&q=" + city + "&days=2&aqi=no&alerts=no";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String body = response.body();
                return body.split("\"date\":\"")[2].split("\"")[0];
            }
        } catch (Exception ignored) {}
        return "Unknown";
    }

    private static void fetchAndPrintWeather(String apiKey, String city, String forecastDate) throws IOException, InterruptedException {
        String url = BASE_URL + "?key=" + apiKey + "&q=" + city + "&days=2&aqi=no&alerts=no";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.out.println("Failed request for " + city + ": " + response.statusCode());
            return;
        }

        String body = response.body();
        String mintemp = body.split("\"mintemp_c\":")[2].split(",")[0];
        String maxtemp = body.split("\"maxtemp_c\":")[2].split(",")[0];
        String humidity = body.split("\"avghumidity\":")[2].split(",")[0];
        String wind = body.split("\"maxwind_kph\":")[2].split(",")[0];
        String windDir = body.split("\"wind_dir\":\"")[1].split("\"")[0];

        System.out.printf("%-12s | %-10s | %-10s | %-10s | %-12s | %-10s | %-10s%n",
                city, forecastDate, mintemp, maxtemp, humidity, wind, windDir);
    }
}
