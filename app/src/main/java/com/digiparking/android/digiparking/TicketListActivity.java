package com.digiparking.android.digiparking;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.digiparking.android.digiparking.modele.Station;
import com.digiparking.android.digiparking.modele.Ticket;
import com.digiparking.android.digiparking.modele.TicketDataSource;
import com.digiparking.android.digiparking.modele.Voiture;
import com.digiparking.android.digiparking.modele.Zone;
import com.digiparking.android.digiparking.service.WebService;
import com.digiparking.android.digiparking.util.CountDownReceiver;
import com.digiparking.android.digiparking.util.TicketListAdapter;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.List;

public class TicketListActivity extends AppCompatActivity {

    private int GET_DETAIL_REQUEST = 2;
    FrameLayout frameLayout;
    FloatingActionsMenu fabMenu;
    FloatingActionButton fab_gestionVoiture;
    FloatingActionButton fab_profile;
    FloatingActionButton fab_prendreTicket;
    LinearLayout mFirstTicketLayout;

    //TextView TicketName;
    TextView etatTicket;
    TextView timerOrDate;
    TextView adressStation;
    TextView firstTicketName;
    ListView mTicketList;
    ProgressDialog dialog;

    Ticket theFirstTicket;
    ArrayList<Ticket> ticketHistoryList;
    TicketDataSource ticketDataSource;
    TicketListAdapter ticketListAdapter;
    boolean ticketEnCour;
    boolean adminMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_list);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etatTicket = (TextView) findViewById(R.id.ticket_list_state);
        adressStation = (TextView) findViewById(R.id.ticket_list_stationAdress);
        timerOrDate = (TextView) findViewById(R.id.ticket_list_timeOrDate);
        firstTicketName = (TextView) findViewById(R.id.ticket_list_first_name) ;

        final Intent intent = getIntent();
        String intentSender = intent.getStringExtra("INTENT_SOURCE");
        ticketEnCour = true;

        if(intentSender.contentEquals("TICKET_DETAIL_CREATION")){
            ticketEnCour = true;
            theFirstTicket = intent.getParcelableExtra(CreationTicketActivity.EXTRA_TICKET_OBJ);

            //startFrom_Ticket_Detail_Creation(intent);
        }else if(intentSender.contentEquals("TICKET_DETAIL_BACK_BTN_FIRST")){
            ticketEnCour = intent.getBooleanExtra("actif", false);
        }else if (intentSender.contentEquals("TICKET_DETAIL_BACK_BTN_LIST")){
            ticketEnCour = intent.getBooleanExtra("ETAT_TICKET", false);
            theFirstTicket = intent.getParcelableExtra(CreationTicketActivity.EXTRA_TICKET_OBJ);
        }else if(intentSender.contentEquals("USER_PROFILE") || intentSender.contentEquals("TICKET_LIST")){
            ticketEnCour = intent.getBooleanExtra("TICKET_ENCOUR", false);
            theFirstTicket = intent.getParcelableExtra(CreationTicketActivity.EXTRA_TICKET_OBJ);
        }else{
            ticketEnCour = false;
        }

        mTicketList = (ListView) findViewById(R.id.ticket_list);
        mFirstTicketLayout = (LinearLayout) findViewById(R.id.first_ticket);
        if (!adminMode) {
            // ticket list adapter and onclickedListenner
            // researche ticket historique
            ticketDataSource = new TicketDataSource(this);
            ticketDataSource.open();
            ticketHistoryList = ticketDataSource.getAllTicket();
            ticketDataSource.close();
            if (!ticketEnCour) {
                try {
                    firstTicketName.setText("Last ticket");
                    firstTicketName.setTextSize(20);

                    theFirstTicket = ticketHistoryList.get(ticketHistoryList.size() - 1);
                    Log.i("theFirstTicket :", String.valueOf(theFirstTicket.get_id()) + " " + theFirstTicket.getDate() + " " + theFirstTicket.getStation().getAdresse() + " " + theFirstTicket.getVoiture().getMarque());
                    etatTicket.setText(getString(R.string.ticketDate));
                    adressStation.setText(theFirstTicket.getStation().getAdresse());
                    timerOrDate.setText(theFirstTicket.getDate());
                    timerOrDate.setTextSize(20);
                    mFirstTicketLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent toTicketDetail = new Intent(getApplicationContext(), TicketDetailActivity.class);
                            toTicketDetail.putExtra(CreationTicketActivity.EXTRA_TICKET_OBJ, theFirstTicket);
                            toTicketDetail.putExtra("ETAT_TICKET", ticketEnCour);
                            toTicketDetail.putExtra("activityFrom", "firstTicketClicked");
                            toTicketDetail.putExtra(CreationTicketActivity.EXTRA_TICKET_OBJ, theFirstTicket);

                            startActivity(toTicketDetail);
                        }
                    });
                }catch (NullPointerException e){
                    e.printStackTrace();
                    mFirstTicketLayout.setVisibility(View.INVISIBLE);
                }catch (ArrayIndexOutOfBoundsException er){
                    Toast.makeText(getApplicationContext(), "Aucun Ticket Trouvé",Toast.LENGTH_SHORT).show();
                    er.printStackTrace();
                    mFirstTicketLayout.setVisibility(View.INVISIBLE);
                }
            } else {

                etatTicket.setText("ENCOUR");
                adressStation.setText(theFirstTicket.getStation().getAdresse());

                IntentFilter filter = new IntentFilter("com.digiparking.receiveBroadCast");
                this.registerReceiver(new CountDownReceiver(this, timerOrDate), filter);

                mFirstTicketLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // intent to ticketDetail of 1st ticket
                        Intent toTicketDetail = new Intent(getApplicationContext(), TicketDetailActivity.class);
                        toTicketDetail.putExtra(CreationTicketActivity.EXTRA_TICKET_OBJ, theFirstTicket);
                        toTicketDetail.putExtra("ETAT_TICKET", ticketEnCour);
                        toTicketDetail.putExtra("activityFrom", "firstTicketClicked");

                        startActivity(toTicketDetail);
                    }
                });
            }
            if (!ticketHistoryList.isEmpty()) {
                try {
                    Log.i("TicketList - ticketid: ", ticketHistoryList.get(0).get_id() + ticketHistoryList.get(0).getDate() + ticketHistoryList.get(0).getHeure_debut() + ticketHistoryList.get(0).getHeure_fin());
                    ticketListAdapter = new TicketListAdapter(this, ticketHistoryList);
                    mTicketList.setAdapter(ticketListAdapter);
                    mTicketList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intentToDetail = new Intent(getApplicationContext(), TicketDetailActivity.class);
                            Ticket theClickedTicket = (Ticket) mTicketList.getItemAtPosition(position);
                            intentToDetail.putExtra("theClickedTicketHistoric", theClickedTicket);
                            intentToDetail.putExtra("activityFrom", "listItemClicked");
                            intentToDetail.putExtra("ETAT_TICKET", ticketEnCour);
                            intentToDetail.putExtra(CreationTicketActivity.EXTRA_TICKET_OBJ, theFirstTicket);
                            startActivity(intentToDetail);
                        }
                    });
                }catch (NullPointerException e){
                    e.printStackTrace();
                    mTicketList.setVisibility(View.INVISIBLE);
                    TextView notFound = (TextView) findViewById(R.id.ticket_list_not_found);
                    notFound.setVisibility(View.VISIBLE);
                }
            }


            frameLayout = (FrameLayout) findViewById(R.id.fab_frameLayout);
            frameLayout.getBackground().setAlpha(0);
            fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
            fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
                @Override
                public void onMenuExpanded() {

                    frameLayout.getBackground().setAlpha(240);
                    frameLayout.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            fabMenu.collapse();
                            return true;
                        }
                    });
                }

                @Override
                public void onMenuCollapsed() {

                    frameLayout.getBackground().setAlpha(0);
                    frameLayout.setOnTouchListener(null);
                }
            });

            fab_gestionVoiture = (FloatingActionButton) findViewById(R.id.fab_gestion_voiture);
            fab_prendreTicket = (FloatingActionButton) findViewById(R.id.fab_prendreTicket);
            fab_profile = (FloatingActionButton) findViewById(R.id.fab_user_profile);

            fab_gestionVoiture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toVoitureList = new Intent(getApplicationContext(), VoitureListActivity.class);
                    toVoitureList.putExtra("INTENT_SOURCE", "TICKET_LIST");
                    toVoitureList.putExtra("TICKET_ENCOUR", ticketEnCour);
                    toVoitureList.putExtra(CreationTicketActivity.EXTRA_TICKET_OBJ, theFirstTicket);
                    startActivity(toVoitureList);
                }
            });

            fab_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toUserProfile = new Intent(getApplicationContext(), UserProfileActivity.class);
                    toUserProfile.putExtra("INTENT_SOURCE", "TICKET_LIST");
                    toUserProfile.putExtra("TICKET_ENCOUR", ticketEnCour);
                    toUserProfile.putExtra(CreationTicketActivity.EXTRA_TICKET_OBJ, theFirstTicket);
                    startActivity(toUserProfile);
                }
            });
            if (!ticketEnCour) {
                fab_prendreTicket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toCreateTicket = new Intent(getApplicationContext(), CreationTicketActivity.class);
                        startActivity(toCreateTicket);
                    }
                });
            }else {
                fab_prendreTicket.setVisibility(View.INVISIBLE);
            }
        }else {
            mFirstTicketLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_DETAIL_REQUEST){
            if (resultCode == RESULT_OK) {
                TicketListAdapter ticketListAdapter = new TicketListAdapter(getApplicationContext(), ticketHistoryList);
                mTicketList.setAdapter(ticketListAdapter);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Recherche par immatriculation ou date");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mFirstTicketLayout.setVisibility(View.INVISIBLE);
                ticketListAdapter.search(newText);

                return false;
            }
        });
        return true;
    }
}
