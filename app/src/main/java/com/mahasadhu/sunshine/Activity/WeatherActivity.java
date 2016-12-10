package com.mahasadhu.sunshine.Activity;

import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.mahasadhu.sunshine.R;
import com.squareup.picasso.Picasso;

public class WeatherActivity extends AppCompatActivity {

    TextView textViewWeatherActivity;
    ImageView imageViewWeatherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        setTitle("Weather Details");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageViewWeatherActivity = (ImageView) findViewById(R.id.imageViewWeatherActivity);

        Picasso.with(this).load("http://openweathermap.org/img/w/13n.png").into(imageViewWeatherActivity);
//        textViewWeatherActivity = (TextView) findViewById(R.id.textViewWeatherActivity);

        Intent intent = getIntent();
//        textViewWeatherActivity.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weather_menu, menu);
        return true;
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
        String text = "Cuaca Cerah";

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
