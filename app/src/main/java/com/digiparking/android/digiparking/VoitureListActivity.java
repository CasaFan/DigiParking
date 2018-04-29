package com.digiparking.android.digiparking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.digiparking.android.digiparking.modele.Ticket;
import com.digiparking.android.digiparking.modele.Voiture;
import com.digiparking.android.digiparking.modele.VoitureDataSource;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class VoitureListActivity extends AppCompatActivity {

    public static String VOITURE_OBJ = "VOITURE_OBJ";
    private List<Voiture> mVoitureList;
    private Ticket theTicket;
    private boolean ticketEncour = false;

    private FrameLayout frameLayout;
    private FloatingActionsMenu fabMenu;
    private com.getbase.floatingactionbutton.FloatingActionButton fab_addVoiture;
    private com.getbase.floatingactionbutton.FloatingActionButton fab_addQR;
    private com.getbase.floatingactionbutton.FloatingActionButton fab_ticket_list;
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voiture_list);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final ListView mVoitureListView = (ListView) findViewById(R.id.voiture_list);
        TextView notFound = (TextView) findViewById(R.id.voitureNotFound);
        qrScan = new IntentIntegrator(this);

        Intent intentFrom = getIntent();
        if (intentFrom.getStringExtra("INTENT_SOURCE").contentEquals("ADD_VOITURE")){
            Toast.makeText(getApplicationContext(), "Voiture a été ajoutée.", Toast.LENGTH_SHORT).show();
        }
        if (intentFrom.getStringExtra("INTENT_SOURCE").contentEquals("MODIFY_VOITURE")){
            Toast.makeText(getApplicationContext(), "Voiture a été modifiée.", Toast.LENGTH_SHORT).show();
        }
        if (intentFrom.getStringExtra("INTENT_SOURCE").contentEquals("DELETE_VOITURE")){
            Toast.makeText(getApplicationContext(), "Voiture a été supprimée.", Toast.LENGTH_SHORT).show();
        }
        if (intentFrom.getStringExtra("INTENT_SOURCE").contentEquals("TICKET_LIST")){
            ticketEncour = intentFrom.getBooleanExtra("TICKET_ENCOUR", false);
            theTicket = intentFrom.getParcelableExtra(CreationTicketActivity.EXTRA_TICKET_OBJ);
        }
        VoitureDataSource voitureDataSource = new VoitureDataSource(this);
        voitureDataSource.open();
        mVoitureList = voitureDataSource.getAllVoitures();
        voitureDataSource.close();
        Log.i("voitureList :", mVoitureList.get(0).getMarque());
        if (!mVoitureList.isEmpty()){
            ArrayAdapter<Voiture> voitureArrayAdapter = new ArrayAdapter<>(this, R.layout.row_voiture_list, R.id.voiture_list_item, mVoitureList);
            mVoitureListView.setAdapter(voitureArrayAdapter);
            mVoitureListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent toVoitureDetail = new Intent(getApplicationContext(), VoitureDetailActivity.class);
                    Voiture v = (Voiture) mVoitureListView.getItemAtPosition(position);
                    toVoitureDetail.putExtra(VOITURE_OBJ, v);
                    toVoitureDetail.putExtra("INTENT_SOURCE", "LIST_ITEM_CLICKED");
                    startActivity(toVoitureDetail);
                }
            });
        }else{
            notFound.setVisibility(View.VISIBLE);
        }

        frameLayout = (FrameLayout) findViewById(R.id.fab_frameLayout_voiture_list);
        frameLayout.getBackground().setAlpha(0);
        fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu_voiture_list);
        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener(){

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

        fab_addVoiture = (FloatingActionButton) findViewById(R.id.fab_add_car);
        fab_addVoiture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent toAddVoiture = new Intent(getApplicationContext(), VoitureAddOrModifiyActivity.class);
                toAddVoiture.putExtra("INTENT_SOURCE", "VOITURE_LIST");
                startActivity(toAddVoiture);
            }
        });

        fab_addQR = (FloatingActionButton) findViewById(R.id.fab_add_car_QR);
        fab_addQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.initiateScan(IntentIntegrator.QR_CODE_TYPES);
            }
        });

        fab_ticket_list = (FloatingActionButton) findViewById(R.id.fab_retrun_to_ticket_list);
        fab_ticket_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toTicketList = new Intent(getApplicationContext(), TicketListActivity.class);
                toTicketList.putExtra("INTENT_SOURCE", "VOITURE_LIST");
                toTicketList.putExtra("TICKET_ENCOUR", ticketEncour);
                toTicketList.putExtra(CreationTicketActivity.EXTRA_TICKET_OBJ, theTicket);
                startActivity(toTicketList);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "NO data Found", Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject voiture = new JSONObject(result.getContents());
                    Voiture v = new Voiture(voiture.getString("marque"), voiture.getString("modele"), voiture.getString("immatriculation"), null);
                    Intent toVoitureDetail = new Intent(this, VoitureAddOrModifiyActivity.class);
                    toVoitureDetail.putExtra("INTENT_SOURCE", "QR_SCAN");
                    toVoitureDetail.putExtra(VOITURE_OBJ, v);
                    startActivity(toVoitureDetail);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
