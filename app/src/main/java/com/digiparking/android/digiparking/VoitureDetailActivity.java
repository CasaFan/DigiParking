package com.digiparking.android.digiparking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.digiparking.android.digiparking.modele.Voiture;
import com.digiparking.android.digiparking.modele.VoitureDataSource;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import static android.R.attr.width;
import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class VoitureDetailActivity extends AppCompatActivity {

    private ImageView mVoitureImg;
    private TextView mMarqueTextView;
    private TextView mModeleTextView;
    private TextView mImmaTextView;
    private Button validQRInfo;

    private Voiture mVoiture;
    VoitureDataSource voitureDataSource;
    private boolean isFromQRScan = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voiture_detail);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_delete);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toModify = new Intent(getApplicationContext(), VoitureAddOrModifiyActivity.class);
                toModify.putExtra(VoitureListActivity.VOITURE_OBJ, mVoiture);
                toModify.putExtra("INTENT_SOURCE", "VOITURE_DETAIL");
                startActivity(toModify);
            }
        });

        mVoitureImg = (ImageView) findViewById(R.id.voiture_image);
        mMarqueTextView = (TextView) findViewById(R.id.voiture_marque);
        mModeleTextView = (TextView) findViewById(R.id.voiture_modele);
        mImmaTextView = (TextView) findViewById(R.id.voiture_immatriculation);
        validQRInfo = (Button) findViewById(R.id.voiture_detail_btn_validation);

        final Intent intent = getIntent();
        if (intent != null){
            if (intent.getStringExtra("INTENT_SOURCE").contentEquals("QR_SCAN")){
                isFromQRScan = true;
                validQRInfo.setVisibility(View.VISIBLE);
            }
            mVoiture = intent.getParcelableExtra(VoitureListActivity.VOITURE_OBJ);
            if (mVoiture.getImageUri() != null && !mVoiture.getImageUri().matches("")) {

                mVoitureImg.setImageURI(Uri.parse(mVoiture.getImageUri()));
            }

            mMarqueTextView.setText(mVoiture.getMarque());
            mModeleTextView.setText(mVoiture.getModele());
            mImmaTextView.setText(mVoiture.getImmatriculation());
        }else{
            try {
                throw new Exception("No intent passed");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (isFromQRScan){
            validQRInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    voitureDataSource = new VoitureDataSource(getApplicationContext());
                    voitureDataSource.open();
                    voitureDataSource.addVoiture(mVoiture);
                    voitureDataSource.close();
                    Intent toVoitureList = new Intent(getApplicationContext(), VoitureListActivity.class);
                    toVoitureList.putExtra("INTENT_SOURCE", "ADD_VOITURE");
                    startActivity(toVoitureList);
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isFromQRScan) {
            getMenuInflater().inflate(R.menu.menu_delete, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete){
            voitureDataSource = new VoitureDataSource(this);
            voitureDataSource.open();
            voitureDataSource.deleteVoiture(mVoiture);
            voitureDataSource.close();
            Intent toVoitureList = new Intent(getApplicationContext(), VoitureListActivity.class);
            toVoitureList.putExtra("INTENT_SOURCE", "DELETE_VOITURE");
            startActivity(toVoitureList);
            return true;
        }else if (item.getItemId() == R.id.action_show_qrcode){
            Bitmap bm = null;
            try {
                String jsonString = "{marque : "+mVoiture.getMarque()+ ", modele : "+mVoiture.getModele()+", immatriculation : "+mVoiture.getImmatriculation()+"}";
                bm = generateQRCode_general(jsonString);

            } catch (WriterException e) { e.printStackTrace(); }

            AlertDialog.Builder qrDialoge = new AlertDialog.Builder(VoitureDetailActivity.this);
            if (bm != null){
                ImageView qrImage = new ImageView(this);
                qrImage.setImageBitmap(Bitmap.createScaledBitmap(bm, 800, 800, false));
                qrDialoge.setView(qrImage);
                qrDialoge.show();
            }
        }
        return false;
    }

    private Bitmap generateQRCode_general(String data)throws WriterException {
        com.google.zxing.Writer writer = new QRCodeWriter();
        String finaldata = Uri.encode(data, "utf-8");

        BitMatrix bm = writer.encode(finaldata, BarcodeFormat.QR_CODE,150, 150);
        Bitmap imageBitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < 150; i++) {//width
            for (int j = 0; j < 150; j++) {//height
                imageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK: Color.WHITE);
            }
        }

        return imageBitmap;
    }
}
