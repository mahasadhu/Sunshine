package com.mahasadhu.sunshine.Activity;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mahasadhu.sunshine.Adapters.RecyclerViewWeatherAdapter;
import com.mahasadhu.sunshine.Data.SunshineDBHelper;
import com.mahasadhu.sunshine.Data.WeatherContract;
import com.mahasadhu.sunshine.Objects.WeatherObj;
import com.mahasadhu.sunshine.R;
import com.mahasadhu.sunshine.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>, SharedPreferences.OnSharedPreferenceChangeListener {

    TextView textViewMain, textViewNama, textViewTinggiUmur, textViewFailed;
    static String uri;

    List<WeatherObj> weatherObjs = new ArrayList<WeatherObj>();
    RecyclerView recyclerViewWeather;
    RecyclerViewWeatherAdapter recyclerViewWeatherAdapter;
    SwipeRefreshLayout swipeRefreshMainActivity;
    SharedPreferences sharedPreferences;

    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewMain = (TextView) findViewById(R.id.textViewMain);
        textViewNama = (TextView) findViewById(R.id.textViewNama);
        textViewTinggiUmur = (TextView) findViewById(R.id.textViewTinggiUmur);
        recyclerViewWeather = (RecyclerView) findViewById(R.id.recyclerViewDatas);
        swipeRefreshMainActivity = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshMainActivity);
        textViewFailed = (TextView) findViewById(R.id.textViewFailed);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        SunshineDBHelper sunshineDBHelper = new SunshineDBHelper(this);
        sqLiteDatabase = sunshineDBHelper.getWritableDatabase();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewWeatherAdapter = new RecyclerViewWeatherAdapter(this, weatherObjs, new RecyclerViewWeatherAdapter.ListItemClickListener() {
            @Override
            public void onListItemClick(int index) {
                Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, weatherObjs.get(index).getJsonObjectData().toString());
                MainActivity.this.startActivity(intent);
            }
        });
        recyclerViewWeather.setAdapter(recyclerViewWeatherAdapter);
        recyclerViewWeather.setLayoutManager(layoutManager);

        swipeRefreshMainActivity.setRefreshing(true);
        swipeRefreshMainActivity.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWeatherData();
            }
        });

        getWeatherData();

        Log.e("onCreate", "onCreate");
    }

    long addWeatherData(String location,
                        String formattedDate,
                        long dt,
                        String weather,
                        String units,
                        String pngid,
                        double temp,
                        double temp_min,
                        double temp_max,
                        String jsonObj){

        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContract.column_location_name, location);
        contentValues.put(WeatherContract.column_formattedDate_name, formattedDate);
        contentValues.put(WeatherContract.column_dt_name, dt);
        contentValues.put(WeatherContract.column_weather_name, weather);
        contentValues.put(WeatherContract.column_units_name, units);
        contentValues.put(WeatherContract.column_pngId_name, pngid);
        contentValues.put(WeatherContract.column_temp_name, temp);
        contentValues.put(WeatherContract.column_temp_min_name, temp_min);
        contentValues.put(WeatherContract.column_temp_max_name, temp_max);
        contentValues.put(WeatherContract.column_jsonObjectData_name, jsonObj);

        return sqLiteDatabase.insert(WeatherContract.tb_name, null, contentValues);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    void getWeatherData(){
        uri = Uri.parse(Utility.baseUrl).buildUpon()
                .appendQueryParameter(Utility.cityQuery, sharedPreferences.getString(getString(R.string.pref_location_key), "Denpasar"))
                .appendQueryParameter(Utility.units, sharedPreferences.getString(getString(R.string.pref_temp_unit_key), "metric"))
                .appendQueryParameter(Utility.appId, getResources().getString(R.string.weather_key))
                .build().toString();

        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        TesLoader tesLoader = new TesLoader(this);
        Log.e("onCreateLoader", "onCreateLoader");
        return tesLoader;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        swipeRefreshMainActivity.setRefreshing(false);
        if (data != null){
            try {
                weatherObjs.clear();
                Log.e("DATA", data);
                JSONObject jsonObjectRes = new JSONObject(data);
                JSONArray jsonArrayList = jsonObjectRes.getJSONArray("list");
                sqLiteDatabase.delete(WeatherContract.tb_name, null, null);
                for (int i = 0; i < jsonArrayList.length(); i++){
                    WeatherObj weatherObj = new WeatherObj();
                    weatherObj.setLocation(jsonObjectRes.getJSONObject("city").getString("name"));
                    weatherObj.setJsonObjectData(jsonArrayList.getJSONObject(i));
                    weatherObj.setDt(jsonArrayList.getJSONObject(i).getLong("dt"));
                    JSONObject jsonObjectMain = jsonArrayList.getJSONObject(i).getJSONObject("main");
                    weatherObj.setTemp(jsonObjectMain.getDouble("temp"));
                    weatherObj.setTemp_min(jsonObjectMain.getDouble("temp_min"));
                    weatherObj.setTemp_max(jsonObjectMain.getDouble("temp_max"));
                    JSONObject jsonObjectWeather = jsonArrayList.getJSONObject(i).getJSONArray("weather").getJSONObject(0);
                    weatherObj.setWeather(jsonObjectWeather.getString("main"));
                    weatherObj.setUnits(getDegree());
                    weatherObj.setPngId(jsonObjectWeather.getString("icon"));
                    weatherObjs.add(weatherObj);

                    long weatherID = addWeatherData(weatherObj.getLocation(),
                            weatherObj.getFormattedDate(),
                            weatherObj.getDt(),
                            weatherObj.getWeather(),
                            weatherObj.getUnits(),
                            weatherObj.getPngId(),
                            weatherObj.getTemp(),
                            weatherObj.getTemp_min(),
                            weatherObj.getTemp_max(),
                            weatherObj.getJsonObjectData().toString());

                    Log.e("PRIMARYKEY", String.valueOf(weatherID));
                }

                recyclerViewWeatherAdapter.notifyDataSetChanged();
                textViewFailed.setVisibility(View.INVISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            weatherObjs.clear();

            Cursor cursorData = sqLiteDatabase.query(WeatherContract.tb_name,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
            int count = cursorData.getCount();
            Log.e("COUNT", String.valueOf(count));

            for (int i = 0; i<cursorData.getCount(); i++){
                cursorData.moveToPosition(i);
                Log.e("DATA "+String.valueOf(i), cursorData.getString(cursorData.getColumnIndex(WeatherContract.column_weather_name)));
            }

            for (int i = 0; i<cursorData.getCount(); i++){
                try {
                    JSONObject jsonObjectCursor = new JSONObject(cursorData.getString(cursorData.getColumnIndex(WeatherContract.column_jsonObjectData_name)));
                    cursorData.moveToPosition(i);
                    WeatherObj weatherObj = new WeatherObj();
                    weatherObj.setLocation(cursorData.getString(cursorData.getColumnIndex(WeatherContract.column_location_name)));
                    weatherObj.setJsonObjectData(jsonObjectCursor);
                    weatherObj.setDt(cursorData.getLong(cursorData.getColumnIndex(WeatherContract.column_dt_name)));
                    weatherObj.setTemp(cursorData.getDouble(cursorData.getColumnIndex(WeatherContract.column_temp_name)));
                    weatherObj.setTemp_min(cursorData.getDouble(cursorData.getColumnIndex(WeatherContract.column_temp_min_name)));
                    weatherObj.setTemp_max(cursorData.getDouble(cursorData.getColumnIndex(WeatherContract.column_temp_max_name)));
                    weatherObj.setWeather(cursorData.getString(cursorData.getColumnIndex(WeatherContract.column_weather_name)));
                    weatherObj.setUnits(getDegree());
                    weatherObj.setPngId(cursorData.getString(cursorData.getColumnIndex(WeatherContract.column_pngId_name)));
                    weatherObjs.add(weatherObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

//            textViewFailed.setVisibility(View.VISIBLE);
            recyclerViewWeatherAdapter.notifyDataSetChanged();
            cursorData.close();
        }
    }

    String getDegree(){
        if (sharedPreferences.getString(getString(R.string.pref_temp_unit_key), "metric").equals("metric")) return "\u00B0C";
        else if (sharedPreferences.getString(getString(R.string.pref_temp_unit_key), "metric").equals("imperial")) return "\u00B0F";
        else return "\u00B0K";
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_temp_unit_key))) Log.e("GANTI", "GANTI");
        getWeatherData();
    }

    private static class TesLoader extends AsyncTaskLoader<String> {

        public TesLoader(Context context) {
            super(context);
            Log.e("TesLoaderConstructor", "TesLoaderConstructor");
        }

        @Override
        public String loadInBackground() {
            Log.e("loadInBackground", "loadInBackground");
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(uri);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                int status = urlConnection.getResponseCode();
                Log.e("STATUS", String.valueOf(status));
                if (status == 200){
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                    return sb.toString();
                }
                else return null;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("MalformedURLException", "MalformedURLException");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("IOException", "IOException");
            }
            finally {
                urlConnection.disconnect();
            }
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuSettings){
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }
        else if (item.getItemId() == R.id.menuMaps){
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("geo")
                    .path("0,0")
                    .query("1600 Amphitheatre Parkway, CA");
            Uri uri = builder.build();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("geo:0,0?q="+sharedPreferences.getString(getString(R.string.pref_location_key), "Denpasar")));

            if (intent.resolveActivity(getPackageManager()) != null){
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
