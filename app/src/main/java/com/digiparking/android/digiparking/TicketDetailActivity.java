package com.digiparking.android.digiparking;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.digiparking.android.digiparking.modele.Ticket;
import com.digiparking.android.digiparking.modele.TicketDataSource;
import com.digiparking.android.digiparking.service.NotificationService;
import com.digiparking.android.digiparking.util.CountDownReceiver;
import com.digiparking.android.digiparking.util.TimerUtil;

public class TicketDetailActivity extends AppCompatActivity {

    TextView adresseStation;
    TextView tarif;
    TextView debutHeure;
    TextView finHeure;
    TextView tempsReste;
    ImageButton locationBtn;
    Button terminerBtn;
    Button prolongerBtn;

    Ticket theClickedTicket;
    Ticket theFirstTicket;
    String stationAdress;
    double prixDuPeriodeChoisi;
    long dureeEnMilisec;
    CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        adresseStation = (TextView) findViewById(R.id.ticket_detail_adressStation);
        tarif = (TextView) findViewById(R.id.ticket_detail_tarifZone);
        debutHeure = (TextView) findViewById(R.id.ticket_detail_heureDebut);
        finHeure = (TextView) findViewById(R.id.ticket_detail_heureFin);
        tempsReste = (TextView) findViewById(R.id.ticket_detail_temps_reste);
        locationBtn = (ImageButton) findViewById(R.id.ticket_detail_button_location);
        terminerBtn = (Button) findViewById(R.id.ticket_detail_button_terminer);
        prolongerBtn = (Button) findViewById(R.id.ticket_detail_button_prolonger);

        Intent intent = getIntent();
        if(intent != null) {
            String activityFrom = intent.getStringExtra("activityFrom");
            if (activityFrom.contentEquals("activity_creation_ticket")) {

                startActivityFromCreationTicket(intent);

            }else if (activityFrom.contentEquals("firstTicketClicked")){
                startActivityFromFirstTicketClicked(intent);
                Log.i("intent from", "firstTicketActif");

            }else if(activityFrom.contentEquals("ADMIN_CHECK")) {
                startActivityFromAdminCheck(intent);

            }else{
                //hostory ticket clicked
                theFirstTicket = intent.getParcelableExtra(CreationTicketActivity.EXTRA_TICKET_OBJ);
                startActivityFromTicketListClicked(intent);
            }


        }else{
            try {
                throw new Exception("TicketDetail : intent null");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMap = new Intent(getApplicationContext(), MapsActivity.class);
                toMap.putExtra("INTENT_SOURCE", "TICKET_DETAIL_LOCATION_BTN");
                toMap.putExtra("ADDRESS", theClickedTicket.getStation().getAdresse());
                startActivity(toMap);
            }
        });
    }



    private void startActivityFromFirstTicketClicked(Intent intent) {
        //prolonger -> addTimer  || terminer -> return to TicketList
        theClickedTicket = intent.getParcelableExtra(CreationTicketActivity.EXTRA_TICKET_OBJ);
        if (theClickedTicket == null){
            try {
                throw new Exception("The clicked ticket info lost");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        boolean etatTicket = intent.getBooleanExtra("ETAT_TICKET", false);
        adresseStation.setText(theClickedTicket.getStation().getAdresse());
        tarif.setText(String.valueOf(theClickedTicket.getCoutTotal()));
        debutHeure.setText(theClickedTicket.getHeure_debut());
        finHeure.setText(theClickedTicket.getHeure_fin());

        if (etatTicket) {
            final CountDownReceiver mCountDownReceiver = new CountDownReceiver(this, tempsReste);
            IntentFilter filter = new IntentFilter("com.digiparking.receiveBroadCast");
            this.registerReceiver(mCountDownReceiver, filter);
            prolongerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add time to timer and to ticket( stop and  recreate)
                    double cout = theClickedTicket.getCoutTotal()+0.3;
                    theClickedTicket.setCoutTotal(cout);
                    tarif.setText(String.valueOf(cout));
                    long currentTimer = NotificationService.getCurrentMs();
                    Intent stopNotification = new Intent(getApplicationContext(), NotificationService.class);
                    stopService(stopNotification);
                    Intent reCreateNotification = new Intent(getApplicationContext(), NotificationService.class);
                    reCreateNotification.putExtra(NotificationService.KEY_EXTRA_LONG_TIME_IN_MS, currentTimer+900000);
                    startService(reCreateNotification);

                }
            });

            terminerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    theClickedTicket.setHeure_fin(CreationTicketActivity.getCurrentTime());
                    // stop service timer to 0 & update ticket (clos + sql)
                    Intent stopNotification = new Intent(getApplicationContext(), NotificationService.class);
                    stopService(stopNotification);

                    tempsReste.setText(getString(R.string.ticketClos));
                    TicketDataSource tSource = new TicketDataSource(getApplicationContext());
                    tSource.open();
                    Log.i("ticketToSave: ", String.valueOf(theClickedTicket.get_id())+ theClickedTicket.getDate()+ String.valueOf(theClickedTicket.getCoutTotal()) + String.valueOf(theClickedTicket.getStation().get_id())+ String.valueOf(theClickedTicket.getVoiture().get_id()));
                    tSource.addTicket(theClickedTicket);
                    tSource.close();
                    prolongerBtn.setText(getString(R.string.backBtn));
                    prolongerBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent backToTicketList = new Intent(getApplicationContext(), TicketListActivity.class);
                            backToTicketList.putExtra("INTENT_SOURCE", "TICKET_DETAIL_BACK_BTN_FIRST");
                            backToTicketList.putExtra("actif", false);
                            startActivity(backToTicketList);
                        }
                    });
                    v.setVisibility(View.INVISIBLE);
                }
            });
        }else {
            tempsReste.setText(getString(R.string.ticketClos));
            terminerBtn.setVisibility(View.INVISIBLE);
            prolongerBtn.setText(getString(R.string.backBtn));
            prolongerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent backToTicketList = new Intent(getApplicationContext(), TicketListActivity.class);
                    backToTicketList.putExtra("INTENT_SOURCE", "TICKET_DETAIL_BACK_BTN_FIRST");
                    backToTicketList.putExtra("actif", false);
                    startActivity(backToTicketList);
                }
            });
        }
    }

    private void startActivityFromCreationTicket(Intent intent){
        Log.i("intent from", " creation ticket");

        theClickedTicket = intent.getParcelableExtra(CreationTicketActivity.EXTRA_TICKET_OBJ);
        dureeEnMilisec = intent.getLongExtra(CreationTicketActivity.EXTRA_DUREE, -1);
        prixDuPeriodeChoisi = intent.getDoubleExtra(CreationTicketActivity.EXTRA_PRIX_DU_PERIODE_CHOISI, -1);

        mCountDownTimer = new CountDownTimer(dureeEnMilisec, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Integer[] duree = TimerUtil.getMinSec(millisUntilFinished);
                tempsReste.setText(getString(R.string.timeleft, duree[TimerUtil.HOUR], duree[TimerUtil.MINUTE], duree[TimerUtil.SECOND]));
                if (duree[TimerUtil.HOUR] == 0 && duree[TimerUtil.MINUTE] <= 5){
                    tempsReste.setTextColor(Color.RED);
                }
            }

            @Override
            public void onFinish() {
                //on fait rien?
            }
        }.start();

        Log.i("duree", String.valueOf(dureeEnMilisec));

        //if(stationAdress != null && debutHeure != null && finPeriode != null && prixDuPeriodeChoisi != -1 && nomVoiture != null && dureeEnMilisec != -1)
        if(theClickedTicket != null && dureeEnMilisec != -1) {

            Intent createService = new Intent(this, NotificationService.class);
            createService.putExtra(NotificationService.KEY_EXTRA_LONG_TIME_IN_MS, dureeEnMilisec);
            createService.putExtra(CreationTicketActivity.EXTRA_TICKET_OBJ, theClickedTicket);
            startService(createService);

            adresseStation.setText(theClickedTicket.getStation().getAdresse());
            debutHeure.setText(theClickedTicket.getHeure_debut());
            finHeure.setText(theClickedTicket.getHeure_fin());
            tarif.setText(String.valueOf(prixDuPeriodeChoisi));

            //setText(nomVoiture)

            Toast.makeText(this, "Ticket a été crée", Toast.LENGTH_SHORT).show();
            terminerBtn.setVisibility(View.INVISIBLE);
            //intent to ticket_list
            prolongerBtn.setText(getString(R.string.OK));
            prolongerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toTicketList = new Intent(getApplicationContext(), TicketListActivity.class);
                    toTicketList.putExtra("INTENT_SOURCE", "TICKET_DETAIL_CREATION");
                    toTicketList.putExtra(CreationTicketActivity.EXTRA_TICKET_OBJ, theClickedTicket);
                    startActivity(toTicketList);

                }
            });
        }else{
            try {
                throw new Exception("Data not passed correctly");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startActivityFromTicketListClicked(Intent intent){
        theClickedTicket = intent.getParcelableExtra("theClickedTicketHistoric");
        final boolean etatEncour = intent.getBooleanExtra("ETAT_TICKET", false);
        if (theClickedTicket != null) {
            adresseStation.setText(theClickedTicket.getStation().getAdresse());
            tarif.setText(String.valueOf(theClickedTicket.getCoutTotal()));
            debutHeure.setText(theClickedTicket.getHeure_debut());
            finHeure.setText(theClickedTicket.getHeure_fin());
            tempsReste.setText(getString(R.string.ticketClos));
            terminerBtn.setVisibility(View.INVISIBLE);
            prolongerBtn.setText(getString(R.string.backBtn));
            prolongerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent backToTicketList = new Intent(getApplicationContext(), TicketListActivity.class);
                    backToTicketList.putExtra("INTENT_SOURCE", "TICKET_DETAIL_BACK_BTN_LIST");
                    backToTicketList.putExtra("actif", false);
                    backToTicketList.putExtra(CreationTicketActivity.EXTRA_TICKET_OBJ, theFirstTicket);
                    backToTicketList.putExtra("ETAT_TICKET", etatEncour);
                    startActivity(backToTicketList);
                }
            });
        }
    }

    private void startActivityFromAdminCheck(final Intent intent) {
        theClickedTicket = intent.getParcelableExtra("theClickedTicketHistoric");
        if (theClickedTicket != null) {
            adresseStation.setText(theClickedTicket.getStation().getAdresse());
            tarif.setText(String.valueOf(theClickedTicket.getCoutTotal()));
            debutHeure.setText(theClickedTicket.getHeure_debut());
            finHeure.setText(theClickedTicket.getHeure_fin());
            tempsReste.setText(getString(R.string.ticketClos));
            terminerBtn.setVisibility(View.INVISIBLE);
            prolongerBtn.setText(getString(R.string.backBtn));
            prolongerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent backToTicketList = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0){
            // notification clicked
            super.onResume();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
