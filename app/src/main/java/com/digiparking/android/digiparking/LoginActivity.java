package com.digiparking.android.digiparking;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.digiparking.android.digiparking.modele.MySQLiteHelper;
import com.digiparking.android.digiparking.modele.Station;
import com.digiparking.android.digiparking.modele.StationDataSource;
import com.digiparking.android.digiparking.modele.Zone;
import com.digiparking.android.digiparking.modele.ZoneDataSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;
import static com.digiparking.android.digiparking.modele.MySQLiteHelper.SQL_CREATE_TABLE_STATION;
import static com.digiparking.android.digiparking.modele.MySQLiteHelper.SQL_CREATE_TABLE_TICKET;
import static com.digiparking.android.digiparking.modele.MySQLiteHelper.SQL_CREATE_TABLE_VOITURE;
import static com.digiparking.android.digiparking.modele.MySQLiteHelper.SQL_CREATE_TABLE_ZONE;
import static com.digiparking.android.digiparking.modele.MySQLiteHelper.TABLE_STATION;
import static com.digiparking.android.digiparking.modele.MySQLiteHelper.TABLE_TICKET;
import static com.digiparking.android.digiparking.modele.MySQLiteHelper.TABLE_VOITURE;
import static com.digiparking.android.digiparking.modele.MySQLiteHelper.TABLE_ZONE;
import static com.digiparking.android.digiparking.modele.ZoneDataSource.tranche;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 1;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView inscriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        //permission request
        if (Build.VERSION.SDK_INT < 23){

        }else {
            if (checkIfAlreadyhavePermission()){

            }
        }

        // /* load data for test */
        //cleanSqlDB();
        //getAllDB();

        //cleanSharedPref();


        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
/*
                Intent passIntent = new Intent(getApplicationContext(), CreationTicketActivity.class);
                startActivity(passIntent);


                Intent passIntent = new Intent(getApplicationContext(), TicketListActivity.class);
                passIntent.putExtra("INTENT_SOURCE", "LOGIN");
                startActivity(passIntent);
                */
                if (mEmailView.getText().toString().contentEquals("admin") && mPasswordView.getText().toString().contentEquals("admin")){
                    Intent toTicketWS = new Intent(getApplicationContext(), TicketListFromWSActivity.class);
                    startActivity(toTicketWS);
                }else{
                    // TODO identification sharedFref
                    Intent toTicketList = new Intent(getApplicationContext(), TicketListActivity.class);
                    toTicketList.putExtra("INTENT_SOURCE", "LOGIN");
                    startActivity(toTicketList);
                }

                //TODO : attemptLogin();
            }
        });

        inscriptionText = (TextView) findViewById(R.id.inscription_hypertext);
        inscriptionText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toAddUser = new Intent(getApplicationContext(), UserProfileAddOrModifyActivity.class);
                toAddUser.putExtra("INTENT_SOURCE", "FIRST_USE");
                startActivity(toAddUser);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void cleanSharedPref() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }

    private boolean checkIfAlreadyhavePermission() {
        int cameraPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int writeStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int vibrate = ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE);
        int location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int readStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPerm != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (writeStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (vibrate != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.VIBRATE);
        }
        if (location != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (readStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSION_REQUEST);
            return false;
        }

        return true;
    }

    private void cleanSqlDB() {
        Log.i("prixTab length : ", String.valueOf(tranche.length));
        MySQLiteHelper dbHelper = new MySQLiteHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOITURE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ZONE);

        db.execSQL(SQL_CREATE_TABLE_ZONE);
        db.execSQL(SQL_CREATE_TABLE_STATION);
        db.execSQL(SQL_CREATE_TABLE_VOITURE);
        db.execSQL(SQL_CREATE_TABLE_TICKET);
        // ZONE
        Zone z1 = new Zone("Z1", new double[]{0.7,0.8,1.2,1.7,5.7,17.5}, 0);
        Zone z2 = new Zone("Z1", new double[]{1.2,2.0,3.5,5.8,7.8,20.0}, 0);
        Zone z3 = new Zone("Z1", new double[]{0d,1.2,2.3,3.4,4.5,10.2}, 0);

        ZoneDataSource zoneData = new ZoneDataSource(this);
        zoneData.open();
        try {
            zoneData.addZone(z1);
            zoneData.addZone(z2);
            zoneData.addZone(z3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        zoneData.close();

        // STATION
        Station s1 = new Station("IM2AG", "60 Rue de la Chimie", z1);
        Station s2 = new Station("Verdun Prefecture", "3111 Pl. de Verdun", z2);
        Station s4 = new Station("Parking Lafayette", "Rue Raoul Blanchard", z2);
        Station s3 = new Station("GEANT Casino", "76 Avenue Gabriel Péri", z3);

        StationDataSource stationData = new StationDataSource(this);
        stationData.open();
        try {
            stationData.addStation(s1);
            stationData.addStation(s2);
            stationData.addStation(s3);
            stationData.addStation(s4);
        }catch (Exception e){
            e.printStackTrace();
        }

        stationData.close();

    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSION_REQUEST){
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Got ", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied. ", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public void getAllDB() {
        SQLiteDatabase db = new MySQLiteHelper(this).getWritableDatabase();
        /*
        db.execSQL("DROP TABLE IF EXISTS " + MySQLiteHelper.TABLE_TICKET);
        db.execSQL(MySQLiteHelper.SQL_CREATE_TABLE_TICKET);
        */
        //String[] allColumns = {MySQLiteHelper.KEY_ID, MySQLiteHelper.COLUMN_TICKET_DATE, MySQLiteHelper.COLUMN_TICKET_START_TIME, MySQLiteHelper.COLUMN_TICKET_END_TIME, MySQLiteHelper.COLUMN_TICKET_TOTAL_COAST, MySQLiteHelper.KEY_CAR, MySQLiteHelper.KEY_STATION};
        String[] allColumns = {MySQLiteHelper.KEY_ID, MySQLiteHelper.COLUMN_CAR_BRAND, MySQLiteHelper.COLUMN_CAR_MODELE, MySQLiteHelper.COLUMN_CAR_IMMATRICULATION, MySQLiteHelper.COLUMN_CAR_IMAGE};
        Cursor c = db.query(TABLE_VOITURE, allColumns, null, null, null, null, null);
        /*
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
                " INNER JOIN " + MySQLiteHelper.TABLE_ZONE + " AS z ON s." +MySQLiteHelper.KEY_ZONE + " = z." + MySQLiteHelper.KEY_ID;

        Cursor c = db.rawQuery(query,null);
*/
        c.moveToFirst();

        Log.i("col num " , String.valueOf(c.getColumnCount()));
        Log.i("li num " , String.valueOf(c.getCount()));
        Log.i("-", Arrays.toString(c.getColumnNames())) ;
        /*
        while (!c.isAfterLast()){
            Log.i("-", String.valueOf(c.getLong(0)) + c.getString(1) + c.getString(2)+ c.getString(3)+ String.valueOf(c.getInt(4)) + "vid-" + String.valueOf(c.getLong(5))+ " sid-" + String.valueOf(c.getLong(6))) ;

            c.moveToNext();
        }
        */
        while (!c.isAfterLast()){
            Log.i("-", String.valueOf(c.getLong(0)) + c.getString(1) + c.getString(2)+ c.getString(3)+Arrays.toString(c.getBlob(4))) ;

            c.moveToNext();
        }

        c.close();


    }

}

