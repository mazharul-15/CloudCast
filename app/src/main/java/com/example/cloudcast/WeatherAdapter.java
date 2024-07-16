package com.example.cloudcast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class WeatherAdapter extends BaseAdapter {

    private Context context;
    private List<Weather> weatherList;


    public WeatherAdapter(Context context, List<Weather> weatherList) {
        this.context = context;
        this.weatherList = weatherList;

    }
    @Override
    public int getCount() {
        return weatherList.size();
    }

    @Override
    public Object getItem(int position) {
        return weatherList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_view, parent, false);
        }

        Weather currentWeather = (Weather) getItem(position);

        TextView dateTextView = convertView.findViewById(R.id.dateTextView);
        TextView descriptionTextView = convertView.findViewById(R.id.descriptionTextView);
        ImageView imgView = convertView.findViewById(R.id.item_img);
        TextView tempTextView = convertView.findViewById(R.id.tempTextView);

        dateTextView.setText(currentWeather.getDay());
        descriptionTextView.setText(currentWeather.getStatus());
        tempTextView.setText(String.format("%.2f°C", currentWeather.getTemp()));
        imgView.setImageResource(currentWeather.getImg_id());

        return convertView;
    }

}
