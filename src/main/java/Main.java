import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args) {
        String url = "https://api.weather.yandex.ru/v2/forecast?lat=55.75&lon=37.62&limit=7";
        HttpURLConnection connection = null;

        try {
            URL address = new URL(url);
            connection = (HttpURLConnection) address.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Yandex-Weather-Key", "a820a964-bea1-4f00-bfdb-e0bb4f7f8c14");

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            System.out.println("Response Code: " + responseCode);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                String jsonResponse = response.toString();
                System.out.println("Response: " + jsonResponse);

                JSONObject jsonObject = new JSONObject(jsonResponse);
                int currentTemperature = jsonObject.getJSONObject("fact").getInt("temp");
                System.out.printf("Temperature: %dC%n", currentTemperature);

                JSONArray forecastsArray = jsonObject.getJSONArray("forecasts");
                double totalTemperature = 0;
                int count = forecastsArray.length();

                for (int i = 0; i < count; i++) {
                    JSONObject dailyForecast = forecastsArray.getJSONObject(i);
                    int dayTemp = dailyForecast.getJSONObject("parts").getJSONObject("day").getInt("temp_avg");
                    totalTemperature += dayTemp;
                }

                double averageTemperature = totalTemperature / count;
                System.out.printf("Average temperature: %.2fC%n", averageTemperature);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}