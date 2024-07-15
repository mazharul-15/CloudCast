package com.example.cloudcast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    double lat, lon;
    EditText location;
    TextView weather_status, temperature, rain, wind, humidity, min_temp, max_temp, next_7_days;
    ImageView search_by_city, weather_status_img;
    String loc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search_by_city = findViewById(R.id.search_by_city);

        weather_status =findViewById(R.id.weather_status);
        weather_status_img = findViewById(R.id.weather_status_img);

        location = findViewById(R.id.city);
        temperature = findViewById(R.id.temperature);

        min_temp = findViewById(R.id.min_tmp);
        max_temp = findViewById(R.id.max_tmp);

        rain = findViewById(R.id.rain_value);
        wind = findViewById(R.id.wind_value);
        humidity = findViewById(R.id.humidity_value);
        next_7_days = findViewById(R.id.next_seven_days);
        //loc = city.getText().toString();


        /// Default Location is KHULNA;
        String city = "Khulna";
        String apiKey = "cd4edd043586c2cc364eb85d4f185e9f";
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+apiKey;
        //new GetWeatherInfo().execute(url);
        GetWeatherInfo weather = new GetWeatherInfo();
        weather.execute(url);
        // Default area end
        search_by_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();

                String apiKey = "cd4edd043586c2cc364eb85d4f185e9f";
                String city = location.getText().toString();
                String newURL = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+apiKey;
                GetWeatherInfo weather2 = new GetWeatherInfo();
                weather2.execute(newURL);
            }
        });

        /// implementation of next seven days weather forecast
        next_7_days.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), nextDays.class);

                intent.putExtra("lon", lon);
                intent.putExtra("lat", lat);

                startActivity(intent);

            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    private class GetWeatherInfo extends AsyncTask<String, Void, String > {

        @Override
        protected String doInBackground(String... urls) {

            HttpsURLConnection connection = null;
            BufferedReader reader = null;

            try {

                URL url = new URL(urls[0]);
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String line;

                while((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                return buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }

                try {
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    JSONObject latLon = jsonObject.getJSONObject("coord");
                    JSONObject main = jsonObject.getJSONObject("main");
                    JSONArray weatherArray = jsonObject.getJSONArray("weather");
                    JSONObject weatherObject = weatherArray.getJSONObject(0);
                    JSONObject windInfo = jsonObject.getJSONObject("wind");

                    // longitude and latitude
                    lon = latLon.getDouble("lon");
                    lat = latLon.getDouble("lat");

                    // current weather condition
                    String weather_condition = getString(R.string.condition) + ": " + translateCondition(weatherObject.getString("main"));
                    weather_status.setText(weather_condition);

                    // current temperature
                    double temp = main.getDouble("temp") - 273.00;
                    String tempValue = getString(R.string.temperature) + ": " + String.format("%.2f", temp) +"°C";
                    temperature.setText(tempValue);

                    // min temperature
                    double minTemp = main.getDouble("temp_min") - 273.00;
                    String minTempVal =getString(R.string.min_temp) +": "+ String.format("%.0f",minTemp) + "°C";
                    min_temp.setText(minTempVal);

                    // max temperature
                    double maxTemp = main.getDouble("temp_max") - 273.00;
                    String maxTempVal = getString(R.string.max_temp) + ": "+String.format("%.0f",maxTemp) + "°C";
                    max_temp.setText(maxTempVal);

                    // rain data
                    if(jsonObject.has("rain")) {
                        JSONObject rainObject = jsonObject.getJSONObject("rain");
                        double rainVal = rainObject.has("1h") ? rainObject.getDouble("1h") : 0;
                        String rainD = String.format("%.1f", rainVal) +"mm";
                        rain.setText(rainD);
                    }

                    // wind speed
                    double windSpeed = windInfo.getDouble("speed");
                    String windVal = String.format("%.2f", windSpeed) + "kmph";
                    wind.setText(windVal);
                    // humidity data
                    double humidityD = main.getDouble("humidity");
                    String humidityVal = String.format("%.2f", humidityD) + "%";
                    humidity.setText(humidityVal);

                    //String tempValue = Double.toString(temp);
                    //String tempValue = "" + temp;
                    //double originalValue = 10.56789;
                    //String roundedValueString = String.format("%.2f", originalValue); // Rounds to two decimal places
                    //double roundedValue = Double.parseDouble(roundedValueString);

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private String translateCondition(String condition) {
            switch (condition.toLowerCase()) {
                case "clear":
                    weather_status_img.setImageResource(R.drawable.sunnylarge);
                    return getString(R.string.clear);
                case "rain":
                    weather_status_img.setImageResource(R.drawable.rain_1);
                    return getString(R.string.rain);
                case "snow":
                    weather_status_img.setImageResource(R.drawable.cloudy_sunny);
                    return getString(R.string.snow);
                case "clouds":
                    weather_status_img.setImageResource(R.drawable.cloudy);
                    return getString(R.string.cloudy);
                case "haze":
                    weather_status_img.setImageResource(R.drawable.haze);
                    return getString(R.string.haze);
                case "thunderstorm":
                    weather_status_img.setImageResource(R.drawable.thunderstorm);
                    return getString(R.string.thunderstorm);
                default:
                    return condition;
            }
        }
    }
}