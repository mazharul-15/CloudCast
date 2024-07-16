package com.example.cloudcast;

import android.net.Uri;

public class Weather {

    String day;
    double temp;
    String status;
    int img_id;

    static final int img[] = {R.drawable.sunnymini, R.drawable.rainymin, R.drawable.cloudymini,
            R.drawable.haze, R.drawable.stormmini};

    public Weather(String day, double temp, String status) {
        this.day = day;
        this.temp = temp;
        this.status = this.translateCondition(status);
    }

    public String getDay() {
        return day;
    }

    public double getTemp() {
        return temp;
    }

    public String getStatus() {
        return status;
    }

    public String translateCondition(String condition) {
        switch (condition.toLowerCase()) {
            case "clear":
                this.img_id = img[0];
                return "আকাশ পরিষ্কার";
            case "rain":
                this.img_id = img[1];
                return "বৃষ্টি হতে পারে";
            case "clouds":
                this.img_id = img[2];
                return "মেঘলা আকাশ";
            case "haze":
                this.img_id = img[3];
                return "কুয়াশা";
            case "thunderstorm":
                this.img_id = img[4];
                return "বগ্রপাত হতে পারে";
            default:
                return condition;
        }
    }
}
