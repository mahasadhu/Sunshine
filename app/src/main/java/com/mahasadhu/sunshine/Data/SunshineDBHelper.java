package com.mahasadhu.sunshine.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mahasadhu on 12/9/2016.
 */

public class SunshineDBHelper extends SQLiteOpenHelper {

    private static final String db_name = "sunshine.db";
    private static final int db_version = 2;

    public SunshineDBHelper(Context context) {
        super(context, db_name, null, db_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String createWeatherTable = "CREATE TABLE " +
                WeatherContract.tb_name + " (" +
                WeatherContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WeatherContract.column_location_name + " TEXT NOT NULL, " +
                WeatherContract.column_formattedDate_name + " TEXT NOT NULL, " +
                WeatherContract.column_weather_name + " TEXT NOT NULL, " +
                WeatherContract.column_units_name + " TEXT NOT NULL, " +
                WeatherContract.column_pngId_name + " TEXT NOT NULL, " +
                WeatherContract.column_dt_name + " LONG NOT NULL, " +
                WeatherContract.column_temp_name + " DOUBLE NOT NULL, " +
                WeatherContract.column_temp_min_name + " DOUBLE NOT NULL, " +
                WeatherContract.column_temp_max_name + " DOUBLE NOT NULL, " +
                WeatherContract.column_jsonObjectData_name + " TEXT NOT NULL, " +
                WeatherContract.column_timestamp_name + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        db.execSQL(createWeatherTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.tb_name);
        onCreate(db);
    }
}
