package com.digiparking.android.digiparking.util;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.digiparking.android.digiparking.modele.Voiture;

import java.util.ArrayList;

/**
 * Created by milk1 on 4/5/2017.
 */

public class VoitureAdapter extends ArrayAdapter<Voiture> {

    private Context context;
    private ArrayList<Voiture> voitureList;

    public VoitureAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Voiture> voitureList) {
        super(context, resource, voitureList);
        this.context = context;
        this.voitureList = voitureList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        label.setText(voitureList.get(position).toString());
        label.setGravity(Gravity.LEFT);

        return label;
    }

}
