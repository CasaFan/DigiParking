package com.digiparking.android.digiparking;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.digiparking.android.digiparking.modele.Station;
import com.digiparking.android.digiparking.modele.StationDataSource;
import com.digiparking.android.digiparking.modele.Ticket;
import com.digiparking.android.digiparking.modele.Voiture;
import com.digiparking.android.digiparking.modele.VoitureDataSource;
import com.digiparking.android.digiparking.util.StationAdapter;
import com.digiparking.android.digiparking.util.VoitureAdapter;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreationTicketActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinnerDuree;
    Spinner spinnerStation;
    Spinner spinnerVoiture;
    Button buttonValide;
    ImageView imageVoiture;

    Calendar c = Calendar.getInstance();
    static final long ONE_MINUTE_IN_MILLIS=60000;

    public static final String EXTRA_DUREE = "creationTicket.DUREE";
    public static final String EXTRA_PRIX_DU_PERIODE_CHOISI = "creationTicket.PRIX_DU_PERIODE_CHOISI";
    public static final String EXTRA_TICKET_OBJ = "creationTicket.ticketObject";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_ticket);

        spinnerDuree = (Spinner) findViewById(R.id.creation_ticket_spinnerHeure);
        spinnerStation = (Spinner) findViewById(R.id.creation_ticket_spinner_stationnement);
        spinnerVoiture = (Spinner) findViewById(R.id.creation_ticket_spinner_voiture);
        buttonValide = (Button) findViewById(R.id.creation_ticket_button_OK);
        imageVoiture = (ImageView) findViewById(R.id.creation_ticket_image_voiture);

        StationDataSource stationDataSource = new StationDataSource(this);
        stationDataSource.open();
        List<Station>stationList = null;
        try {
            stationList = stationDataSource.getAllStation();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        stationDataSource.close();
        if (stationList == null){
            throw new NullPointerException("Creation ticket : cant load staion list");
        }

        VoitureDataSource voitureDataSource = new VoitureDataSource(this);
        voitureDataSource.open();
        List<Voiture> voitureList = voitureDataSource.getAllVoitures();
        voitureDataSource.close();

        StationAdapter spinnerStationAdapt = new StationAdapter(this, android.R.layout.simple_spinner_item, stationList);
        spinnerStationAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStation.setAdapter(spinnerStationAdapt);
        spinnerStation.setOnItemSelectedListener(this);

        VoitureAdapter spinnerVoitureAdapt = new VoitureAdapter(this, android.R.layout.simple_spinner_item, (ArrayList<Voiture>) voitureList);
        spinnerVoitureAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVoiture.setAdapter(spinnerVoitureAdapt);
        spinnerVoiture.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Voiture v = (Voiture) parent.getItemAtPosition(position);
                if (v.getImageUri() != null && !v.getImageUri().matches("")) {
                    Uri uri = Uri.parse(v.getImageUri());
                    imageVoiture.setImageURI(uri);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        buttonValide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creerUnTicket(v);
            }
        });


    }

    //create intent
    public void creerUnTicket(View v){
        Intent intent_creation_ticket = new Intent(this, TicketDetailActivity.class);
        Voiture selectedVoiture = (Voiture) spinnerVoiture.getSelectedItem();
        Station selectedStation = (Station) spinnerStation.getSelectedItem();
        String heureDebut = getCurrentTime();
        String heureFin;

        int choixDuree = spinnerDuree.getSelectedItemPosition();
        long dureeEnMiliSec;
        double prixDuPeriodeChoisi = selectedStation.getZone().getPrixParTranche()[choixDuree];

        //put heureFin "HH:mm:ss" in extra
        switch (choixDuree){
            case 0: //15mins
                heureFin = getHeureFin(15);
                dureeEnMiliSec = 15*ONE_MINUTE_IN_MILLIS;
                break;
            case 1: //30mins
                heureFin = getHeureFin(30);
                dureeEnMiliSec = 30*ONE_MINUTE_IN_MILLIS;
                break;
            case 2: //45mins
                heureFin = getHeureFin(45);
                dureeEnMiliSec = 45*ONE_MINUTE_IN_MILLIS;
                break;
            case 3: //1h
                heureFin = getHeureFin(60);
                dureeEnMiliSec = 60*ONE_MINUTE_IN_MILLIS;
                break;
            case 4: //3h
                heureFin = getHeureFin(3*60);
                dureeEnMiliSec = 3*60*ONE_MINUTE_IN_MILLIS;
                break;
            case 5://24h
                heureFin = getHeureFin(24*60);
                dureeEnMiliSec = 24*60*ONE_MINUTE_IN_MILLIS;
                break;
            default:
                //throw Exception
                heureFin = null;
                dureeEnMiliSec = 0L;
                break;
        }


        //create ticket
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE);
        String formattedDate = df.format(c.getTime());
        Ticket aTicket = new Ticket(formattedDate, heureDebut, heureFin, selectedStation, selectedVoiture, prixDuPeriodeChoisi);
        Log.i("Ticket date : ", formattedDate);
        intent_creation_ticket.putExtra(EXTRA_TICKET_OBJ, aTicket);
        intent_creation_ticket.putExtra(EXTRA_DUREE, dureeEnMiliSec);
        intent_creation_ticket.putExtra(EXTRA_PRIX_DU_PERIODE_CHOISI, prixDuPeriodeChoisi);
        intent_creation_ticket.putExtra("activityFrom", "activity_creation_ticket");
        startActivity(intent_creation_ticket);
    }

    // get current time
    public static String getCurrentTime(){
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.FRANCE);
        return df.format(Calendar.getInstance().getTime());
    }

    //calculate heureFin
    public String getHeureFin(int dureeEnMinute){
        long curTimeInMiliSec = c.getTimeInMillis();
        Date dureeEnMiliSec = new Date(curTimeInMiliSec + (dureeEnMinute*ONE_MINUTE_IN_MILLIS));
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.FRANCE);
        return df.format(dureeEnMiliSec);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
