package com.digiparking.android.digiparking.modele;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by milk1 on 4/20/2017.
 */

public class VoitureDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.KEY_ID, MySQLiteHelper.COLUMN_CAR_BRAND, MySQLiteHelper.COLUMN_CAR_MODELE, MySQLiteHelper.COLUMN_CAR_IMMATRICULATION, MySQLiteHelper.COLUMN_CAR_IMAGE};

    public VoitureDataSource(Context context){
        dbHelper = MySQLiteHelper.getInstance(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean addVoiture(Voiture voiture){
        Cursor cursor = database.query(MySQLiteHelper.TABLE_VOITURE, allColumns, null, null, null, null, null);

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.KEY_ID, cursor.getCount());
        values.put(MySQLiteHelper.COLUMN_CAR_BRAND, voiture.getMarque());
        values.put(MySQLiteHelper.COLUMN_CAR_MODELE, voiture.getModele());
        values.put(MySQLiteHelper.COLUMN_CAR_IMMATRICULATION, voiture.getImmatriculation());
        values.put(MySQLiteHelper.COLUMN_CAR_IMAGE, voiture.getImageUri());

        cursor.close();
        long newRowId = database.insert(MySQLiteHelper.TABLE_VOITURE, null, values);
        if (newRowId != -1) {
            voiture.set_id(newRowId);
            Log.i("insert voiture", "OK");
            return true;
        }else{
            Log.i("insert voiture", "KO");
            return false;
        }
    }

    public void deleteVoiture(Voiture voiture){
        String selection = MySQLiteHelper.KEY_ID + " = ?";
        String[] selectionArgs = {String.valueOf(voiture.get_id())};
        database.delete(MySQLiteHelper.TABLE_VOITURE, selection, selectionArgs);
    }

    public List<Voiture> getAllVoitures() {
        List<Voiture> voitures = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_VOITURE, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            voitures.add(buildVoiture(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        return voitures;
    }

    public void updateVoiture(Voiture voiture){
        ContentValues newValues = new ContentValues();
        newValues.put(MySQLiteHelper.COLUMN_CAR_BRAND, voiture.getMarque());
        newValues.put(MySQLiteHelper.COLUMN_CAR_MODELE, voiture.getModele());
        newValues.put(MySQLiteHelper.COLUMN_CAR_IMMATRICULATION, voiture.getImmatriculation());
        newValues.put(MySQLiteHelper.COLUMN_CAR_IMAGE, voiture.getImageUri());

        String selection = MySQLiteHelper.KEY_ID + " = ?";
        String[] selectionArgs = {String.valueOf(voiture.get_id())};
        Log.i("voitureid à update", String.valueOf(voiture.get_id()) + voiture.getMarque());
        int count = database.update(MySQLiteHelper.TABLE_VOITURE, newValues, selection, selectionArgs);

        if (count != 1){
            Log.i("Update Err: ", String.valueOf(count) + "row effected");
            Log.i("row affected: ", String.valueOf(count));
        }else {
            Log.i("UPDATE", "OK with 1 row affected");
            String[] allColumns = {MySQLiteHelper.KEY_ID, MySQLiteHelper.COLUMN_CAR_BRAND, MySQLiteHelper.COLUMN_CAR_MODELE, MySQLiteHelper.COLUMN_CAR_IMMATRICULATION, MySQLiteHelper.COLUMN_CAR_IMAGE};
            Cursor c = database.query(MySQLiteHelper.TABLE_VOITURE, allColumns, null, null, null, null, null);
            c.moveToFirst();
            while (!c.isAfterLast()){
                Log.i("-", String.valueOf(c.getLong(0)) + c.getString(1) + c.getString(2)+ c.getString(3)+ c.getString(4));

                c.moveToNext();
            }

            c.close();
        }
    }

    private Voiture buildVoiture(Cursor cursor) {
        Voiture voiture = new Voiture(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        voiture.set_id(cursor.getLong(0));
        return voiture;
    }
}
