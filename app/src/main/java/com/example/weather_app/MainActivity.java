package com.example.weather_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.weather_app.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Button newYork, london, tokyo, colombo, dubai;
    TextView show;
    String baseUrl = "https://api.openweathermap.org/data/2.5/weather?q=";
    String apiKey = "f5ff5620592334bc20d1390cd9c66778"; // Replace with your OpenWeatherMap API key

    // AsyncTask for fetching weather data
    class getWeather extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);

                // Extract weather details
                JSONObject main = jsonObject.getJSONObject("main");
                String temperature = String.format("%.2f", main.getDouble("temp") - 273.15) + " °C"; // Convert Kelvin to Celsius
                String humidity = "Humidity: " + main.getString("humidity") + "%";

                JSONObject wind = jsonObject.getJSONObject("wind");
                String windSpeed = "Wind Speed: " + wind.getString("speed") + " m/s";

                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");

                // Display formatted weather details
                show.setText("Temperature: " + temperature + "\n" +
                        "Description: " + description + "\n" +
                        windSpeed + "\n" +
                        humidity);
            } catch (Exception e) {
                e.printStackTrace();
                show.setText("Error fetching weather data.");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind UI elements
        newYork = findViewById(R.id.new_york);
        london = findViewById(R.id.london);
        tokyo = findViewById(R.id.tokyo);
        colombo = findViewById(R.id.colombo);
        dubai = findViewById(R.id.dubai);
        show = findViewById(R.id.weather);

        // Set onClickListeners for each button
        newYork.setOnClickListener(v -> fetchWeather("New York"));
        london.setOnClickListener(v -> fetchWeather("London"));
        tokyo.setOnClickListener(v -> fetchWeather("Tokyo"));
        colombo.setOnClickListener(v -> fetchWeather("Colombo"));
        dubai.setOnClickListener(v -> fetchWeather("Dubai"));
    }

    private void fetchWeather(String city) {
        String url = baseUrl + city + "&appid=" + apiKey;
        new getWeather().execute(url);
    }
}
