package com.example.cloudcast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.time.format.DateTimeFormatter;


public class nextDays extends AppCompatActivity {

    double LON, LAT;
    String API_KEY = "cd4edd043586c2cc364eb85d4f185e9f";

    private ImageView backArrow;
    private ListView weatherListView;
    private WeatherAdapter weatherAdapter;
    private List<Weather> weatherList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_days);

        Bundle bundle = getIntent().getExtras();
        LON = bundle.getDouble("lon", 0);
        LAT = bundle.getDouble("lat", 0);

        backArrow = findViewById(R.id.back_arrow);
        weatherListView = findViewById(R.id.list_view);

        // back arrow implementation
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        weatherAdapter = new WeatherAdapter(this, weatherList);
        weatherListView.setAdapter(weatherAdapter);

        new FetchWeatherTask().execute();
    }

    private class FetchWeatherTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String response = "";
            try {
                URL url = new URL("https://api.openweathermap.org/data/2.5/forecast?lat=" + LAT + "&lon=" + LON + "&appid=" + API_KEY + "&units=metric");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                urlConnection.disconnect();
                response = content.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                JSONArray listArray = jsonResponse.getJSONArray("list");
                for (int i = 0; i < listArray.length(); i += 8) { // API returns data in 3-hour intervals; 8 intervals per day
                    JSONObject day = listArray.getJSONObject(i);
                    long dt = day.getLong("dt");
                    JSONObject main = day.getJSONObject("main");
                    JSONArray weatherArray = day.getJSONArray("weather");
                    JSONObject weather = weatherArray.getJSONObject(0);

                    double dayTemp = main.getDouble("temp");
                    String description = weather.getString("main");

                    // Convert Unix timestamp to LocalDateTime
                    LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(dt), ZoneId.systemDefault());
                    // Format the date and time
                    String formattedDate = dateTime.format(DateTimeFormatter.ofPattern("EEEE", Locale.getDefault()));

                    weatherList.add(new Weather(formattedDate, dayTemp, description));
                }
                weatherAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
                // Handle error
            }
        }
    }
}