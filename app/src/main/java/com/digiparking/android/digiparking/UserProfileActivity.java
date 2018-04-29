package com.digiparking.android.digiparking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.digiparking.android.digiparking.modele.Ticket;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    public static String USER_NAME = "USER_PROFILE_NAME";
    public static String USER_LASTNAME = "USER_PROFILE_LASTNAME";
    public static String USER_TEL = "USER_PROFILE_TEL";
    public static String USER_MAIL = "USER_PROFILE_MAIL";
    public static String USER_IMAGE = "USER_PROFILE_IMAGE";
    CircleImageView mImageView;
    TextView mNomView;
    TextView mPrenomView;
    TextView mTelView;
    TextView mMailView;
    Button valideBtn;

    private String nom;
    private String prenom;
    private String tel;
    private String mail;
    private String imageUri;
    private boolean ticketEncour;
    private Ticket theFirstTicket;
    private boolean firstUse = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        ticketEncour = intent.getBooleanExtra("TICKET_ENCOUR", false);
        if (intent.getStringExtra("INTENT_SOURCE").contentEquals("FIRST_USE")){
            firstUse = true;
        }

        if (intent.getStringExtra("INTENT_SOURCE").contentEquals("TICKET_LIST")){
            theFirstTicket = intent.getParcelableExtra(CreationTicketActivity.EXTRA_TICKET_OBJ);
        }

        mImageView = (CircleImageView) findViewById(R.id.user_profile_photo);
        mNomView = (TextView) findViewById(R.id.user_profile_nom);
        mPrenomView = (TextView) findViewById(R.id.user_profile_prenom);
        mTelView = (TextView) findViewById(R.id.user_profile_tel);
        mMailView = (TextView) findViewById(R.id.user_profile_mail);
        valideBtn = (Button) findViewById(R.id.user_profile_btn_valider);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        nom = sharedPreferences.getString("nom", null);
        prenom = sharedPreferences.getString("prenom", null);
        tel = sharedPreferences.getString("tel", null);
        mail = sharedPreferences.getString("email", null);
        imageUri = sharedPreferences.getString("imageURI", null);

        if (nom!=null && prenom!= null && tel!=null && mail!=null){
            mNomView.setText(nom);
            mPrenomView.setText(prenom);
            mTelView.setText(tel);
            mMailView.setText(mail);
            if (imageUri!=null){
                Uri uri = Uri.parse(imageUri);
                //getApplicationContext().grantUriPermission("com.digiparking.android.digiparking", uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                mImageView.setImageURI(uri);
            }
            valideBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toTicketList;
                    if (firstUse){
                        toTicketList = new Intent(getApplicationContext(), VoitureAddOrModifiyActivity.class);
                        toTicketList.putExtra("INTENT_SOURCE", "FIRST_USE");
                    }else{
                        toTicketList = new Intent(getApplicationContext(), TicketListActivity.class);
                        toTicketList.putExtra("INTENT_SOURCE", "USER_PROFILE");
                        toTicketList.putExtra("TICKET_ENCOUR", ticketEncour);
                        toTicketList.putExtra(CreationTicketActivity.EXTRA_TICKET_OBJ, theFirstTicket);
                    }

                    startActivity(toTicketList);
                }
            });
        }else{
            try {
                throw new Exception("SharedPref Not Found !");
            } catch (Exception e) {
                Log.i("nom :", nom);
                Log.i("prenom :", prenom);
                Log.i("tel :", tel);
                Log.i("mail :", mail);
                Log.i("imageUri :", imageUri);

                e.printStackTrace();
            }
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toModifyProfile = new Intent(getApplicationContext(), UserProfileAddOrModifyActivity.class);
                toModifyProfile.putExtra("INTENT_SOURCE", "PROFILE_DETAIL_MODIFY");
                toModifyProfile.putExtra(USER_NAME, nom);
                toModifyProfile.putExtra(USER_LASTNAME, prenom);
                toModifyProfile.putExtra(USER_TEL, tel);
                toModifyProfile.putExtra(USER_MAIL, mail);
                toModifyProfile.putExtra("TICKET_ENCOUR", ticketEncour);
                toModifyProfile.putExtra(USER_IMAGE, imageUri);
                startActivity(toModifyProfile);
            }
        });

    }

}
