package com.mahasadhu.sunshine.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.mahasadhu.sunshine.R;

public class WeatherActivity extends AppCompatActivity {

    TextView textViewWeatherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        textViewWeatherActivity = (TextView) findViewById(R.id.textViewWeatherActivity);

        Intent intent = getIntent();
        textViewWeatherActivity.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
    }
}
