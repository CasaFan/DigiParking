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

public class TicketDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.KEY_ID, MySQLiteHelper.COLUMN_TICKET_DATE, MySQLiteHelper.COLUMN_TICKET_START_TIME, MySQLiteHelper.COLUMN_TICKET_END_TIME, MySQLiteHelper.COLUMN_TICKET_TOTAL_COAST, MySQLiteHelper.KEY_CAR, MySQLiteHelper.KEY_STATION};

    public TicketDataSource(Context context) {
        dbHelper = MySQLiteHelper.getInstance(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean addTicket(Ticket ticket){
        //set ticketId -> table size
        Cursor cursor = database.query(MySQLiteHelper.TABLE_TICKET, allColumns, null, null, null, null, null);

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.KEY_ID, cursor.getCount());
        values.put(MySQLiteHelper.COLUMN_TICKET_DATE, ticket.getDate());
        values.put(MySQLiteHelper.COLUMN_TICKET_START_TIME, ticket.getHeure_debut());
        values.put(MySQLiteHelper.COLUMN_TICKET_END_TIME, ticket.getHeure_fin());
        values.put(MySQLiteHelper.KEY_STATION, ticket.getStation().get_id());
        values.put(MySQLiteHelper.KEY_CAR, ticket.getVoiture().get_id());
        values.put(MySQLiteHelper.COLUMN_TICKET_TOTAL_COAST, ticket.getCoutTotal());


        long newRowId = database.insert(MySQLiteHelper.TABLE_TICKET, null, values);
        if (newRowId != -1) {
            ticket.set_id(newRowId);
            cursor = database.query(MySQLiteHelper.TABLE_TICKET, allColumns, null, null, null, null, null);
            cursor.moveToLast();
            Log.i("inserted curLen :", String.valueOf(cursor.getCount()));
            Log.i("inserted :", String.valueOf(cursor.getLong(0)) + cursor.getString(1)+ String.valueOf(cursor.getDouble(4)) + String.valueOf(cursor.getInt(5))+String.valueOf(cursor.getInt(6)));
            Log.i("insert ticket", "OK");cursor.close();
            //Log.i("insertTicket : ", )
            return true;
        }else{
            Log.i("insert ticket", "KO");cursor.close();
            return false;
        }

    }

    public void deleteTicket(Ticket ticket) {
        String selection = MySQLiteHelper.KEY_ID + " = ?";
        String[] selectionArgs = {String.valueOf(ticket.get_id())};
        database.delete(MySQLiteHelper.TABLE_TICKET, selection, selectionArgs);
    }

    public ArrayList<Ticket> getAllTicket() {
        ArrayList<Ticket> tickets = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_TICKET, allColumns, null, null, null, null, null);
        if (cursor != null) {
            Log.i("Ticket cursor leg :", String.valueOf(cursor.getCount()));
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                tickets.add(buildObject(cursor));
                Log.i("curs getTicket", "OK");
                cursor.moveToNext();
            }
            cursor.close();
        }

        return tickets;
    }

    private Ticket buildObject(Cursor cursor) {
        //cherche station & voiture
        String query = "SELECT v._id as vid, t._id as tid, s._id as sid, z._id as zid, z.nom as znom, s.nom as snom, " +
                MySQLiteHelper.COLUMN_CAR_BRAND + ", " +
                MySQLiteHelper.COLUMN_CAR_MODELE + ", " +
                MySQLiteHelper.COLUMN_CAR_IMMATRICULATION + ", " +
                MySQLiteHelper.COLUMN_CAR_IMAGE + ", " +
                MySQLiteHelper.COLUMN_ZONE_PRICE + ", " +
                MySQLiteHelper.COLUMN_ZONE_FREE_PERIODE + ", " +
                MySQLiteHelper.COLUMN_STATION_ADRESS + ", " +
                MySQLiteHelper.KEY_ZONE + ", " +
                MySQLiteHelper.COLUMN_TICKET_DATE + ", " +
                MySQLiteHelper.COLUMN_TICKET_START_TIME + ", " +
                MySQLiteHelper.COLUMN_TICKET_END_TIME + ", " +
                MySQLiteHelper.COLUMN_TICKET_TOTAL_COAST + ", " +
                MySQLiteHelper.KEY_CAR + ", " +
                MySQLiteHelper.KEY_STATION +
                " FROM " +
                MySQLiteHelper.TABLE_TICKET + " AS t" +
                " INNER JOIN " + MySQLiteHelper.TABLE_VOITURE + " AS v ON t." + MySQLiteHelper.KEY_CAR + " = v." + MySQLiteHelper.KEY_ID +
                " INNER JOIN " + MySQLiteHelper.TABLE_STATION + " AS s ON t." +MySQLiteHelper.KEY_STATION + " = s." + MySQLiteHelper.KEY_ID +
                " INNER JOIN " + MySQLiteHelper.TABLE_ZONE + " AS z ON s." +MySQLiteHelper.KEY_ZONE + " = z." + MySQLiteHelper.KEY_ID +
                " WHERE tid = ?";

        Cursor c = database.rawQuery(query, new String[]{String.valueOf(cursor.getLong(0))});
        Log.i("cursor c size:", String.valueOf(c.getCount()));
        Log.i("ticket id ", String.valueOf(cursor.getInt(0)));
        if (c != null && c.moveToFirst()) {
            Log.i("Cursor", "OK");
            Voiture theVoiture = new Voiture(c.getString(c.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_CAR_BRAND)),
                    c.getString(c.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_CAR_MODELE)),
                    c.getString(c.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_CAR_IMMATRICULATION)),
                    c.getString(c.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_CAR_IMAGE)));
            theVoiture.set_id(c.getLong(c.getColumnIndexOrThrow(MySQLiteHelper.KEY_CAR)));
            Log.i("voiture id", String.valueOf(c.getLong(c.getColumnIndexOrThrow(MySQLiteHelper.KEY_CAR))));
            double[] prixDuZone = new double[6];
            try {
                JSONObject prixJson = new JSONObject(c.getString(c.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_ZONE_PRICE)));
                for (int i = 0; i < ZoneDataSource.tranche.length; i++) {
                    prixDuZone[i] = prixJson.getDouble(ZoneDataSource.tranche[i]);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Zone theZone = new Zone(c.getString(c.getColumnIndexOrThrow("z" + MySQLiteHelper.COLUMN_NAME)), prixDuZone, c.getInt(c.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_ZONE_FREE_PERIODE)));
            theZone.set_id(c.getLong(c.getColumnIndexOrThrow(MySQLiteHelper.KEY_ZONE)));
            Station theStation = new Station(c.getString(c.getColumnIndexOrThrow("s" + MySQLiteHelper.COLUMN_NAME)), c.getString(c.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_STATION_ADRESS)), theZone);
            theStation.set_id(c.getLong(c.getColumnIndexOrThrow(MySQLiteHelper.KEY_STATION)));
            Ticket aNewTicket = new Ticket(cursor.getString(1), cursor.getString(2), cursor.getString(3), theStation, theVoiture, cursor.getDouble(4));
            aNewTicket.set_id(cursor.getLong(0));
            Log.i("NewTicketId ", String.valueOf(cursor.getLong(0)));
            c.close();
            return aNewTicket;
        }else{
            Log.i("Cursor" , "null");
            try {
                throw new Exception("can't build obj");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    // public void updateTicket()
}
