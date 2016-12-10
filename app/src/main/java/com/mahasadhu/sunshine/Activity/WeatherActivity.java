package com.mahasadhu.sunshine.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.mahasadhu.sunshine.R;
import com.mahasadhu.sunshine.Utility;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class WeatherActivity extends AppCompatActivity {

    TextView textViewDTWeatherActivity,
            textViewMaxWeatherActivity,
            textViewMinWeatherActivity,
            textViewWeatherDesc,
            textViewGroundPressure,
            textViewHumidity,
            textViewCloudiness,
            textViewWindSpeed,
            textViewRainVolume;
    ImageView imageViewWeatherActivity;

    JSONObject jsonObjectDetailWeather;
    SharedPreferences sharedPreferences;
    String windUnit = "",
            desc = "",
            place = "",
            time = "",
            temp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        setTitle("Weather Details");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getString(Utility.units, "").equals("imperial")) windUnit = "miles/hour";
        else windUnit = "meter/sec";

        imageViewWeatherActivity = (ImageView) findViewById(R.id.imageViewWeatherActivity);
        textViewDTWeatherActivity = (TextView) findViewById(R.id.textViewDTWeatherActivity);
        textViewMaxWeatherActivity = (TextView) findViewById(R.id.textViewMaxWeatherActivity);
        textViewMinWeatherActivity = (TextView) findViewById(R.id.textViewMinWeatherActivity);
        textViewWeatherDesc = (TextView) findViewById(R.id.textViewWeatherDesc);
        textViewGroundPressure = (TextView) findViewById(R.id.textViewGroundPressure);
        textViewHumidity = (TextView) findViewById(R.id.textViewHumidity);
        textViewCloudiness = (TextView) findViewById(R.id.textViewCloudiness);
        textViewWindSpeed = (TextView) findViewById(R.id.textViewWindSpeed);
        textViewRainVolume = (TextView) findViewById(R.id.textViewRainVolume);

        Intent intent = getIntent();
        try {
            jsonObjectDetailWeather = new JSONObject(intent.getStringExtra(Intent.EXTRA_TEXT));

            desc = jsonObjectDetailWeather.getJSONArray("weather").getJSONObject(0).getString("description");
            place = sharedPreferences.getString(Utility.cityQuery, "Denpasar");
            time = getFormattedDate(jsonObjectDetailWeather.getLong("dt"));
            temp = String.valueOf(jsonObjectDetailWeather.getJSONObject("main").getDouble("temp"));

            Picasso.with(this).load("http://openweathermap.org/img/w/"+jsonObjectDetailWeather.getJSONArray("weather").getJSONObject(0).getString("icon")+".png").into(imageViewWeatherActivity);
            textViewDTWeatherActivity.setText(place +
                    ",\n" +
                    time);
            textViewCloudiness.setText("Cloudiness: " +
                    String.valueOf(jsonObjectDetailWeather.getJSONObject("clouds").getDouble("all")) +
                    "%");
            textViewMaxWeatherActivity.setText(String.valueOf(jsonObjectDetailWeather.getJSONObject("main").getDouble("temp_max")) +
                    "°");
            textViewMinWeatherActivity.setText(String.valueOf(jsonObjectDetailWeather.getJSONObject("main").getDouble("temp_min")) +
                    "°");
            textViewWeatherDesc.setText(jsonObjectDetailWeather.getJSONArray("weather").getJSONObject(0).getString("main") +
                    "\n(" +
                    desc +
                    ")");
            textViewGroundPressure.setText("Ground Pressure: " +
                    String.valueOf(jsonObjectDetailWeather.getJSONObject("main").getDouble("grnd_level")) +
                    " hPa");
            textViewHumidity.setText("Humidity: " +
                    String.valueOf(jsonObjectDetailWeather.getJSONObject("main").getDouble("humidity")) +
                    "%");
            textViewWindSpeed.setText("Wind Speed: " +
                    String.valueOf(jsonObjectDetailWeather.getJSONObject("wind").getDouble("speed")) +
                    " " +
                    windUnit);
            textViewRainVolume.setText("Rain Volume: "
                    + String.valueOf(jsonObjectDetailWeather.getJSONObject("rain").getDouble("3h"))
                    + " mm");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getFormattedDate(long dt) {
        Date date = new Date(dt*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm\n(z)");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8")); // give a timezone reference for formating (see comment at the bottom
        return sdf.format(date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weather_menu, menu);
        return true;
    }

    String getDegree(){
        if (sharedPreferences.getString(getString(R.string.pref_temp_unit_key), "metric").equals("metric")) return "\u00B0C";
        else if (sharedPreferences.getString(getString(R.string.pref_temp_unit_key), "metric").equals("imperial")) return "\u00B0F";
        else return "\u00B0K";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuShare){
            shareWeather();
        }
        else if (item.getItemId() == R.id.menuSettings){
            startActivity(new Intent(WeatherActivity.this, SettingsActivity.class));
        }
        else if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    void shareWeather(){
        String mimeType = "text/plain";
        String title = "Share The Weather to...";
        String text = "It's around " + temp + getDegree() + " with " + desc + " at " + place + " \non " + time + "\n\n\n#SunshineApp #AndroidKejar #Udacity";

        Intent intentShare = ShareCompat.IntentBuilder.from(this)
                .setChooserTitle(title)
                .setType(mimeType)
                .setText(text)
                .createChooserIntent();

        if (intentShare.resolveActivity(getPackageManager()) != null){
            startActivity(intentShare);
        }
    }
}
