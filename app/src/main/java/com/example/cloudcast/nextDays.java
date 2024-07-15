package com.example.cloudcast;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

public class nextDays extends AppCompatActivity {

    TextView lon, lat;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_days);

        lon = findViewById(R.id.lon);
        lat = findViewById(R.id.lat);
        // getting longitude & latitude
        Bundle bundle = getIntent().getExtras();
        double lonV = bundle.getDouble("lon", 0);
        double latV = bundle.getDouble("lat", 0);

        lon.setText(String.format("%.2f", lonV));
        lat.setText(String.format("%.2f", latV));
    }
}