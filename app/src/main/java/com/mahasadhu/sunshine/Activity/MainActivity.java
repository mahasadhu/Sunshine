package com.mahasadhu.sunshine.Activity;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.mahasadhu.sunshine.Adapters.RecyclerViewWeatherAdapter;
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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    ProgressDialog progressDialog;
    String message, nama;
    private static String response;
    int tinggi, umur;
    TextView textViewMain, textViewNama, textViewTinggiUmur;
    static String uri;

    List<WeatherObj> weatherObjs = new ArrayList<WeatherObj>();
    RecyclerView recyclerViewWeather;
    RecyclerViewWeatherAdapter recyclerViewWeatherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewMain = (TextView) findViewById(R.id.textViewMain);
        textViewNama = (TextView) findViewById(R.id.textViewNama);
        textViewTinggiUmur = (TextView) findViewById(R.id.textViewTinggiUmur);
        recyclerViewWeather = (RecyclerView) findViewById(R.id.recyclerViewDatas);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewWeatherAdapter = new RecyclerViewWeatherAdapter(this, weatherObjs, new RecyclerViewWeatherAdapter.ListItemClickListener() {
            @Override
            public void onListItemClick(int index) {
                Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, weatherObjs.get(index).getWeather());
                intent.putextra
                MainActivity.this.startActivity(intent);
                Toast.makeText(MainActivity.this, String.valueOf(index), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerViewWeather.setAdapter(recyclerViewWeatherAdapter);
        recyclerViewWeather.setLayoutManager(layoutManager);

        uri = Uri.parse(Utility.baseUrl).buildUpon()
                .appendQueryParameter(Utility.cityId, "1645528")
                .appendQueryParameter(Utility.units, "metric")
                .appendQueryParameter(Utility.appId, getResources().getString(R.string.weather_key))
                .build().toString();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.show();
        Log.e("onCreate", "onCreate");
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
        progressDialog.dismiss();
//        textViewMain.setText(data);
        try {
            JSONObject jsonObjectRes = new JSONObject(data);
            JSONArray jsonArrayList = jsonObjectRes.getJSONArray("list");
            for (int i = 0; i < jsonArrayList.length(); i++){
                WeatherObj weatherObj = new WeatherObj();
                weatherObj.setLocation(jsonObjectRes.getJSONObject("city").getString("name"));
                weatherObj.setDt(jsonArrayList.getJSONObject(i).getLong("dt"));
                JSONObject jsonObjectMain = jsonArrayList.getJSONObject(i).getJSONObject("main");
                weatherObj.setTemp(jsonObjectMain.getDouble("temp"));
                weatherObj.setTemp_min(jsonObjectMain.getDouble("temp_min"));
                weatherObj.setTemp_max(jsonObjectMain.getDouble("temp_max"));
                JSONObject jsonObjectWeather = jsonArrayList.getJSONObject(i).getJSONArray("weather").getJSONObject(0);
                weatherObj.setWeather(jsonObjectWeather.getString("main"));
                weatherObjs.add(weatherObj);
            }

            recyclerViewWeatherAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    private static class TesLoader extends AsyncTaskLoader<String> {

        public TesLoader(Context context) {
            super(context);
            Log.e("TesLoaderConstructor", "TesLoaderConstructor");
        }

        @Override
        public String loadInBackground() {
            Log.e("loadInBackground", "loadInBackground");
            URL url = null;
            HttpURLConnection urlConnection = null;
            try {
//                String urlStr = "http://192.168.43.147/tesasyncloader/data.json";
                url = new URL(uri);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                int status = urlConnection.getResponseCode();
                Log.e("STATUS", String.valueOf(status));
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
//                response = sb.toString();
                return sb.toString();
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
}
