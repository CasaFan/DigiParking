package com.digiparking.android.digiparking.modele;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by milk1 on 4/19/2017.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {
    private static MySQLiteHelper mInstance = null;
    private Context mContext;

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Digiparking.db";

    //tables
    public static final String TABLE_ZONE = "zone";
    public static final String TABLE_STATION = "station";
    public static final String TABLE_VOITURE = "voiture";
    public static final String TABLE_TICKET = "ticket";

    //common column names
    public static final String KEY_ID = "_id";
    public static final String COLUMN_NAME = "nom";

    //table zone
    //public static final String COLUMN_ZONE_NAME = "nom";
    public static final String COLUMN_ZONE_PRICE = "prixParTranche";
    public static final String COLUMN_ZONE_FREE_PERIODE = "dureeGratuiteEnMinute";

    //table station
    //public static final String COLUMN_STATION_NAME = "nom";
    public static final String COLUMN_STATION_ADRESS = "adresse";
    public static final String KEY_ZONE = "zone_id";

    //table voiture
    //public static final String COLUMN_VOITURE_NAME = "nom";
    public static final String COLUMN_CAR_BRAND = "marque";
    public static final String COLUMN_CAR_MODELE = "modele";
    public static final String COLUMN_CAR_IMMATRICULATION = "immatriculation";
    public static final String COLUMN_CAR_IMAGE = "image";

    //table ticket
    public static final String COLUMN_TICKET_DATE = "date";
    public static final String COLUMN_TICKET_START_TIME = "heure_debut";
    public static final String COLUMN_TICKET_END_TIME = "heure_fin";
    public static final String COLUMN_TICKET_TOTAL_COAST = "coutTotal";

    public static final String KEY_STATION = "station_id";
    public static final String KEY_CAR = "voiture_id";

    //CREATE STATEMENT
    public static final String SQL_CREATE_TABLE_ZONE =
            "CREATE TABLE " + TABLE_ZONE + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_ZONE_PRICE + " TEXT NOT NULL, " +
                    COLUMN_ZONE_FREE_PERIODE + " INTEGER);";

    public static final String SQL_CREATE_TABLE_STATION =
            "CREATE TABLE " + TABLE_STATION + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_STATION_ADRESS + " TEXT, " +
                    KEY_ZONE + " INTEGER, " +
                    "FOREIGN KEY (" + KEY_ZONE + ") REFERENCES " + TABLE_ZONE + "(" + KEY_ID +"));";

    public static final String SQL_CREATE_TABLE_VOITURE =
            "CREATE TABLE " + TABLE_VOITURE + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_CAR_BRAND + " TEXT, " +
                    COLUMN_CAR_MODELE + " TEXT, " +
                    COLUMN_CAR_IMMATRICULATION + " TEXT NOT NULL, " +
                    COLUMN_CAR_IMAGE + " TEXT);";

    public static final String SQL_CREATE_TABLE_TICKET =
            "CREATE TABLE " + TABLE_TICKET + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_TICKET_DATE + " DATE NOT NULL, " +
                    COLUMN_TICKET_START_TIME + " TIME NOT NULL, " +
                    COLUMN_TICKET_END_TIME + " TIME, " +
                    COLUMN_TICKET_TOTAL_COAST + " DOUBLE, " +
                    KEY_CAR + " INTEGER, " +
                    KEY_STATION + " INTEGER, " +
                    "FOREIGN KEY (" + KEY_CAR + ") REFERENCES " + TABLE_VOITURE + "(" + KEY_ID +"), " +
                    "FOREIGN KEY (" + KEY_STATION + ") REFERENCES " + TABLE_STATION + "(" + KEY_ID +"));";

    public static MySQLiteHelper getInstance(Context context){
        if (mInstance == null) {
            mInstance = new MySQLiteHelper(context.getApplicationContext());
        }
        return mInstance;
    }
    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_ZONE);
        db.execSQL(SQL_CREATE_TABLE_STATION);
        db.execSQL(SQL_CREATE_TABLE_VOITURE);
        db.execSQL(SQL_CREATE_TABLE_TICKET);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOITURE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ZONE);

        onCreate(db);
    }
}
