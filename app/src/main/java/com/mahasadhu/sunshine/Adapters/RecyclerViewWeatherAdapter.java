package com.mahasadhu.sunshine.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mahasadhu.sunshine.Objects.WeatherObj;
import com.mahasadhu.sunshine.R;

import java.util.List;

/**
 * Created by Mahasadhu on 12/4/2016.
 */

public class RecyclerViewWeatherAdapter extends RecyclerView.Adapter<RecyclerViewWeatherAdapter.WeatherViewholder> {

    private Activity activity;
    private List<WeatherObj> weatherObjs;
    final private ListItemClickListener listItemClickListener;

    public RecyclerViewWeatherAdapter(Activity activity, List<WeatherObj> weatherObjs, ListItemClickListener listItemClickListener) {
        this.activity = activity;
        this.listItemClickListener = listItemClickListener;
        this.weatherObjs = weatherObjs;
    }

    @Override
    public WeatherViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_row, parent, false);
        return new WeatherViewholder(view);
    }

    @Override
    public void onBindViewHolder(WeatherViewholder holder, int position) {
        WeatherObj weatherObj = weatherObjs.get(position);
        holder.textViewData.setText(weatherObj.getLocation() + " | Around " + String.valueOf(weatherObj.getTemp()) + " \u2103");
        holder.textViewDTRecyclerViewRow.setText(weatherObj.getDT());
        holder.textViewWeatherRecyclerViewRow.setText(weatherObj.getWeather());
    }

    @Override
    public int getItemCount() {
        return weatherObjs.size();
    }

    public class WeatherViewholder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView textViewData, textViewDTRecyclerViewRow, textViewWeatherRecyclerViewRow;

        public WeatherViewholder(View itemView) {
            super(itemView);
            textViewData = (TextView) itemView.findViewById(R.id.textViewRecyclerViewRow);
            textViewDTRecyclerViewRow = (TextView) itemView.findViewById(R.id.textViewDTRecyclerViewRow);
            textViewWeatherRecyclerViewRow = (TextView) itemView.findViewById(R.id.textViewWeatherRecyclerViewRow);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listItemClickListener.onListItemClick(getAdapterPosition());
        }
    }

    public interface ListItemClickListener{
        void onListItemClick(int index);
    }
}
