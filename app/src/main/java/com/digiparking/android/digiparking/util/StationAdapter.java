package com.digiparking.android.digiparking.util;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.digiparking.android.digiparking.modele.Station;

import java.util.List;

/**
 * Created by milk1 on 4/5/2017.
 */

public class StationAdapter extends ArrayAdapter<Station> {
    private Context context;
    private List<Station> stationList;
    /*
    private static class ViewHolder{
        private Spinner spinnerStation;
    }

    ViewHolder viewHolder;
    */

    public StationAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Station> stations) {
        super(context, resource, stations);
        this.context = context;
        this.stationList = stations;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setText(stationList.get(position).getNom());
        label.setGravity(Gravity.LEFT);

        return label;
    }

}
