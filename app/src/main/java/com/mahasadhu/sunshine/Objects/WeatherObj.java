package com.mahasadhu.sunshine.Objects;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Mahasadhu on 12/4/2016.
 */

public class WeatherObj {
    private String location;
    private String weather;
    private String units;
    private String pngId;
    private long dt;
    private double temp, temp_min, temp_max;
    private JSONObject jsonObjectData;

    public String getPngId() {
        return pngId;
    }

    public void setPngId(String pngId) {
        this.pngId = pngId;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public JSONObject getJsonObjectData() {
        return jsonObjectData;
    }

    public void setJsonObjectData(JSONObject jsonObjectData) {
        this.jsonObjectData = jsonObjectData;
    }

    public String getFormattedDate() {
        Date date = new Date(dt*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm (z)");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8")); // give a timezone reference for formating (see comment at the bottom
        return sdf.format(date);
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getDt() {
        return dt;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getTemp_min() {
        return temp_min;
    }

    public void setTemp_min(double temp_min) {
        this.temp_min = temp_min;
    }

    public double getTemp_max() {
        return temp_max;
    }

    public void setTemp_max(double temp_max) {
        this.temp_max = temp_max;
    }

    public WeatherObj() {

    }
}
