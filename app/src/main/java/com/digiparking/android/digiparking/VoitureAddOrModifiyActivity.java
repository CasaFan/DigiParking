package com.digiparking.android.digiparking;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.digiparking.android.digiparking.modele.Voiture;
import com.digiparking.android.digiparking.modele.VoitureDataSource;
import com.digiparking.android.digiparking.util.ImageUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import static com.digiparking.android.digiparking.R.id.imageView;

//TODO : save ImageUri in database

public class VoitureAddOrModifiyActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_CAMERA = 1;
    private static final int PICK_IMAGE_GALLERY = 2;
    EditText mMarque;
    EditText mModele;
    EditText mImmat;
    ImageView mImage;
    Button validBtn;
    Bitmap voitureImage;
    Uri selectedImage;

    private boolean isAdd;
    private Voiture mVoiture;
    private String imageUri;
    private boolean firstUsage = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voiture_add_modify);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_qr_scan);
        setSupportActionBar(toolbar);

        mMarque = (EditText) findViewById(R.id.voiture_add_modif_marque);
        mModele = (EditText) findViewById(R.id.voiture_add_modif_modele);
        mImmat = (EditText) findViewById(R.id.voiture_add_modif_immatriculation);
        mImage = (ImageView) findViewById(R.id.voiture_add_modif_image);
        validBtn = (Button) findViewById(R.id.voiture_add_modif_btn_OK);

        Intent intent = getIntent();
        if (intent != null){
            if(intent.getStringExtra("INTENT_SOURCE").contentEquals("QR_SCAN")){
                isAdd = true;
                mVoiture = intent.getParcelableExtra(VoitureListActivity.VOITURE_OBJ);
                mMarque.setText(mVoiture.getMarque());
                mModele.setText(mVoiture.getModele());
                mImmat.setText(mVoiture.getImmatriculation());
            }
            if (intent.getStringExtra("INTENT_SOURCE").contentEquals("VOITURE_DETAIL")) {
                isAdd = false;
                mVoiture = intent.getParcelableExtra(VoitureListActivity.VOITURE_OBJ);
                mMarque.setText(mVoiture.getMarque());
                mModele.setText(mVoiture.getModele());
                mImmat.setText(mVoiture.getImmatriculation());
                if (mVoiture.getImageUri() != null && !mVoiture.getImageUri().matches("")) {
                    mImage.setImageURI(Uri.parse(mVoiture.getImageUri()));
                }
            }else{

                if (intent.getStringExtra("INTENT_SOURCE").contentEquals("FIRST_USE")){
                    firstUsage = true;
                    startActivityFromFirstUsage();
                    Toast.makeText(getApplicationContext(), "Veuillez ajouter une voiture ", Toast.LENGTH_SHORT).show();
                }
                isAdd = true;
                mVoiture = null;
            }
        }else {
            try {
                throw new Exception("Illegal acces activity");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!firstUsage) {
            mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage();
                }
            });

            if (isAdd) {
                validBtn.setText(getString(R.string.voiture_add));
            }
            validBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean textVide = mMarque.getText().toString().matches("") &&
                            mModele.getText().toString().matches("") &&
                            mImmat.getText().toString().matches("");
                    if (textVide) {
                        Toast.makeText(getApplicationContext(), "Un champ n'a pas été saisi", Toast.LENGTH_SHORT).show();
                    } else {

                        if (voitureImage!=null){
                            Uri mImageUri = ImageUtil.getImageUri(getApplicationContext(), voitureImage, "VoitureImage");
                            imageUri = mImageUri.toString();
                        }else if(selectedImage!=null){
                            imageUri = selectedImage.toString();
                        }else {
                            imageUri = null;
                        }
                        Intent toTicketList = new Intent(getApplicationContext(), VoitureListActivity.class);
                        VoitureDataSource voitureDataSource = new VoitureDataSource(getApplicationContext());
                        voitureDataSource.open();
                        mVoiture = new Voiture(mMarque.getText().toString(), mModele.getText().toString(), mImmat.getText().toString(), imageUri);
                        if (isAdd) {
                            voitureDataSource.addVoiture(mVoiture);
                            toTicketList.putExtra("INTENT_SOURCE", "ADD_VOITURE");
                        } else {

                            voitureDataSource.updateVoiture(mVoiture);
                            toTicketList.putExtra("INTENT_SOURCE", "MODIFY_VOITURE");
                        }
                        voitureDataSource.close();

                        startActivity(toTicketList);
                    }
                }
            });
        }

    }

    private void startActivityFromFirstUsage() {
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        validBtn.setText(getString(R.string.voiture_add));
        validBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean textVide = mMarque.getText().toString().matches("") &&
                        mModele.getText().toString().matches("") &&
                        mImmat.getText().toString().matches("");
                if (textVide){
                    Toast.makeText(getApplicationContext(), "Un champ n'a pas été saisi", Toast.LENGTH_SHORT).show();
                }else{
                    if (voitureImage!=null){
                        Uri mImageUri = ImageUtil.getImageUri(getApplicationContext(), voitureImage, "UserProfileImage");
                        imageUri = mImageUri.toString();
                    }else if(selectedImage!=null) {
                        imageUri = selectedImage.toString();
                    }else {
                        imageUri = null;
                    }
                    Intent toCreationTicket = new Intent(getApplicationContext(), CreationTicketActivity.class);
                    VoitureDataSource voitureDataSource = new VoitureDataSource(getApplicationContext());
                    voitureDataSource.open();
                    mVoiture = new Voiture(mMarque.getText().toString(), mModele.getText().toString(), mImmat.getText().toString(), imageUri);

                    voitureDataSource.addVoiture(mVoiture);

                    voitureDataSource.close();

                    startActivity(toCreationTicket);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_qr_scan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_qr_scan){
            IntentIntegrator qrScan = new IntentIntegrator(this);
            qrScan.initiateScan();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case PICK_IMAGE_CAMERA:
                if(resultCode == RESULT_OK){
                    voitureImage = (Bitmap) data.getExtras().get("data");
                    mImage.setImageBitmap(voitureImage);
                }
                break;
            case PICK_IMAGE_GALLERY:
                if(resultCode == RESULT_OK){
                    selectedImage = data.getData();
                    mImage.setImageURI(selectedImage);
                }
                break;
            case IntentIntegrator.REQUEST_CODE: //QR scan
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (result != null) {

                    if (result.getContents() == null) {
                        Toast.makeText(this, "NO data Found", Toast.LENGTH_LONG).show();
                    } else {
                        try {

                            JSONObject voiture = new JSONObject(result.getContents());
                            mMarque.setText(voiture.getString("marque"));
                            mModele.setText(voiture.getString("modele"));
                            mImmat.setText(voiture.getString("immatriculation"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                        }
                    }
                }else super.onActivityResult(requestCode, resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }

    }

    private void selectImage() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Prendre une photo", "Selectionner dans le gallery","Annuler"};
                AlertDialog.Builder builder = new AlertDialog.Builder(VoitureAddOrModifiyActivity.this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Prendre une photo")) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, PICK_IMAGE_CAMERA);
                        } else if (options[item].equals("Selectionner dans le gallery")) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                        } else if (options[item].equals("Annuler")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 111);
                Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
