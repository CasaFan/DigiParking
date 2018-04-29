package com.digiparking.android.digiparking.modele;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by milk1 on 4/20/2017.
 */

public class ZoneDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    public static final String[] tranche = {"15mins", "30mins", "45mins", "1h", "3h", "24h"};
    private String[] allColumns = { MySQLiteHelper.KEY_ID, MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_ZONE_PRICE, MySQLiteHelper.COLUMN_ZONE_FREE_PERIODE };

    public ZoneDataSource(Context context){
        dbHelper = MySQLiteHelper.getInstance(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean addZone(Zone zone) throws Exception {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ZONE, allColumns, null, null, null, null, null);

        // parse prixParTranche en Json
        JSONObject prixParTranche = parsePrix(zone);
        ContentValues value = new ContentValues();
        value.put(MySQLiteHelper.KEY_ID, cursor.getCount());
        value.put(MySQLiteHelper.COLUMN_NAME, zone.getNom());
        value.put(MySQLiteHelper.COLUMN_ZONE_PRICE, prixParTranche.toString());
        value.put(MySQLiteHelper.COLUMN_ZONE_FREE_PERIODE, zone.getDureeGratuitEnMinute());

        cursor.close();
        long newRowId = database.insert(MySQLiteHelper.TABLE_ZONE, null, value);
        if (newRowId != -1) {
            zone.set_id(newRowId);
            Log.i("insert statuts", "OK");
            return true;
        }else{
            Log.i("insert statuts", "KO");
            return false;
        }
    }

    public void deleteZone(Zone zone){
        String selection = MySQLiteHelper.KEY_ID + " = ?";
        String[] selectionArgs = {String.valueOf(zone.get_id())};
        database.delete(MySQLiteHelper.TABLE_ZONE, selection, selectionArgs);

    }

    public List<Zone> getAllZones() throws JSONException {
        List<Zone> zones = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_ZONE, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            zones.add(buildZone(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        return zones;
    }

    private Zone buildZone(Cursor cursor) throws JSONException {
        // parse json
        double[] prixParTranche = new double[6];
        JSONObject prix = new JSONObject(cursor.getString(2));
        for (int i=0; i<tranche.length; i++){
            prixParTranche[i] = prix.getDouble(tranche[i]);

            Log.i(tranche[i], String.valueOf(prixParTranche[i]));
        }
        if(prixParTranche.length != 6){
            Log.i("Errmsg", "getSQL zone Err");
        }else {
            Zone zone = new Zone(cursor.getString(1), prixParTranche, cursor.getInt(3));
            return zone;
        }
        return null;
    }

    private JSONObject parsePrix(Zone zone) throws Exception {
        JSONObject prixParTranche = new JSONObject();
        double[] prix = zone.getPrixParTranche();

        if(tranche.length == prix.length) {
            for (int i = 0; i < prix.length; i++) {

                prixParTranche.put(tranche[i], prix[i]);
            }

            return prixParTranche;
        }else{
            throw  new Exception("zone prix not matche !");
        }
    }
}
