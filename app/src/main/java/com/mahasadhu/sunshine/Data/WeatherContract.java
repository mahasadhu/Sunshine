package com.mahasadhu.sunshine.Data;

import android.provider.BaseColumns;

/**
 * Created by Mahasadhu on 12/9/2016.
 */

public class WeatherContract implements BaseColumns {

    public static final String tb_name = "tb_weather";
    public static final String column_location_name = "location";
    public static final String column_formattedDate_name = "formatted_date";
    public static final String column_weather_name = "weather";
    public static final String column_units_name = "units";
    public static final String column_pngId_name = "pngId";
    public static final String column_dt_name = "dt";
    public static final String column_temp_name = "temp";
    public static final String column_temp_min_name = "temp_min";
    public static final String column_temp_max_name = "temp_max";
    public static final String column_jsonObjectData_name = "json_object_data";
    public static final String column_timestamp_name = "timestamp";
}
