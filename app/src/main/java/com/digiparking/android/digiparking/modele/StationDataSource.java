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

import static com.digiparking.android.digiparking.modele.ZoneDataSource.tranche;

/**
 * Created by milk1 on 4/23/2017.
 */

public class StationDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.KEY_ID, MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_STATION_ADRESS, MySQLiteHelper.KEY_ZONE};

    public StationDataSource(Context context){
        dbHelper = MySQLiteHelper.getInstance(context);
    }

    public void open() throws SQLException{
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean addStation(Station aStation){
        Cursor cursor = database.query(MySQLiteHelper.TABLE_STATION, allColumns, null, null, null, null, null);

        ContentValues stationValue = new ContentValues();
        stationValue.put(MySQLiteHelper.KEY_ID, cursor.getCount());
        stationValue.put(MySQLiteHelper.COLUMN_NAME, aStation.getNom());
        stationValue.put(MySQLiteHelper.COLUMN_STATION_ADRESS, aStation.getAdresse());
        stationValue.put(MySQLiteHelper.KEY_ZONE, aStation.getZone().get_id());

        cursor.close();
        long newRowId = database.insert(MySQLiteHelper.TABLE_STATION, null, stationValue);

        if (newRowId != -1) {
            aStation.set_id(newRowId);
            Log.i("insert station", "OK");
            return true;
        }else{
            Log.i("insert station", "KO");
            return false;
        }
    }

    public void deleteStation(Station theStation){
        String selection = MySQLiteHelper.KEY_ID + " = ?";
        String[] selectionArgs = {String.valueOf(theStation.get_id())};
        database.delete(MySQLiteHelper.TABLE_STATION, selection, selectionArgs);
    }

    public List<Station> getAllStation() throws JSONException {
        List<Station> stations = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_STATION, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            stations.add(buildObject(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        return stations;
    }

    private Station buildObject(Cursor cursor) throws JSONException {
        String query = "SELECT * FROM " + MySQLiteHelper.TABLE_STATION + " s" +
                " INNER JOIN " + MySQLiteHelper.TABLE_ZONE + " z ON z." + MySQLiteHelper.KEY_ID + " = s." + MySQLiteHelper.KEY_ZONE +
                " WHERE s." + MySQLiteHelper.KEY_ID + " = ?";
        Cursor c = database.rawQuery(query, new String[]{String.valueOf(cursor.getLong(0))});
        c.moveToFirst();

        Zone theZone = null;
        double[] prixParTranche = new double[6];
        JSONObject prix = new JSONObject(c.getString(c.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_ZONE_PRICE)));
        Log.i("tranche length : ", String.valueOf(tranche.length));
        for (int i=0; i<tranche.length; i++){
            prixParTranche[i] = prix.getDouble(tranche[i]);

            Log.i(tranche[i], String.valueOf(prixParTranche[i]));
        }
        if(prixParTranche.length != 6){
            Log.i("Errmsg", "getSQL zone Err");
        }else {
            theZone = new Zone(c.getString(c.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_NAME)), prixParTranche, c.getInt(c.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_ZONE_FREE_PERIODE)));
        }

        if (theZone == null){
            throw new NullPointerException("StationDataSource : Zone can't be build");
        }

        Station theStation = new Station(cursor.getString(1), cursor.getString(2), theZone);
        theStation.set_id(cursor.getLong(0));

        c.close();
        return theStation;
    }
}
