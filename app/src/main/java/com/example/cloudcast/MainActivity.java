package com.example.cloudcast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    EditText location;
    TextView weather_status, temperature, rain, wind, humidity;
    ImageView search_by_city;
    String loc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weather_status =findViewById(R.id.weather_status);
        rain = findViewById(R.id.rain_value);
        wind = findViewById(R.id.wind_value);
        humidity = findViewById(R.id.humidity_value);
        location = findViewById(R.id.city);
        temperature = findViewById(R.id.temperature);
        //loc = city.getText().toString();
        search_by_city = findViewById(R.id.search_by_city);

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
                    JSONObject main = jsonObject.getJSONObject("main");
                    JSONObject status = jsonObject.getJSONObject("weather");

                    double temp = main.getDouble("temp") - 273.00;
                    String tempValue = R.string.temperature + ": " + String.format("%.2f", temp) +"°C";

                    String weather_condition = R.string.condition + ": " + translateCondition(status.getString("main"));

                    weather_status.setText(weather_condition);
                    temperature.setText(tempValue);

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
                    return getString(R.string.clear);
                case "rain":
                    return getString(R.string.rain);
                case "snow":
                    return getString(R.string.snow);
                case "clouds":
                    return getString(R.string.cloudy);
                default:
                    return condition;
            }
        }
    }
}