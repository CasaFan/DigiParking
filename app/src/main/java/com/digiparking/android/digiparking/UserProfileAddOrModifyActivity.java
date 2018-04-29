package com.digiparking.android.digiparking;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.digiparking.android.digiparking.service.WebService;
import com.digiparking.android.digiparking.util.ImageUtil;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileAddOrModifyActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_CAMERA = 1;
    private static final int PICK_IMAGE_GALLERY = 2;

    CircleImageView userImageView;
    EditText userNomView;
    EditText userPrenomView;
    EditText userTelView;
    EditText userMailView;
    Button valideBtn;
    LinearLayout psdLayout;
    Bitmap mImage;

    String uNom;
    String uPrenom;
    String uTel;
    String uMail;
    String mdpHash;
    private boolean ticketEncour = false;
    Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add_or_modify);

        userImageView = (CircleImageView) findViewById(R.id.user_profile_add_modify_photo);
        userNomView = (EditText) findViewById(R.id.user_profile_add_modify_nom);
        userPrenomView = (EditText) findViewById(R.id.user_profile_add_modify_prenom);
        userTelView = (EditText) findViewById(R.id.user_profile_add_modify_tel);
        userMailView = (EditText) findViewById(R.id.user_profile_add_modify_mail);
        valideBtn = (Button) findViewById(R.id.user_profile_add_modif_btn_OK);
        psdLayout = (LinearLayout) findViewById(R.id.user_profile_add_modif_psdLayout);

        Intent intent = getIntent();
        if (intent.getStringExtra("INTENT_SOURCE").contentEquals("FIRST_USE")) {
            psdLayout.setVisibility(View.VISIBLE);
            final EditText vPsd = (EditText) findViewById(R.id.user_profile_add_modify_password);

            valideBtn.setText(R.string.valider);
            valideBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uNom = userNomView.getText().toString();
                    uPrenom = userPrenomView.getText().toString();
                    uTel = userTelView.getText().toString();
                    uMail = userMailView.getText().toString();
                    String uPsd = vPsd.getText().toString();

                    if (!uNom.matches("") && !uPrenom.matches("") && !uTel.matches("") && !uMail.matches("")) {
                        mdpHash = new String(Hex.encodeHex(DigestUtils.sha256(uPsd)));
                        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("nom", uNom);
                        editor.putString("prenom", uPrenom);
                        editor.putString("tel", uTel);
                        editor.putString("email", uMail);
                        editor.putString("password", mdpHash);

                        if (mImage!=null){
                            Uri mImageUri = ImageUtil.getImageUri(getApplicationContext(), mImage, "UserProfileImage");
                            editor.putString("imageURI", mImageUri.toString());
                            Log.i("camera ", mImageUri.toString());
                        }else if(selectedImage!=null){
                            editor.putString("imageURI", selectedImage.toString());
                            Log.i("gallery ", selectedImage.toString());
                        }
                        Log.i("psd :", mdpHash);
                        editor.commit();
                        AddUserToWS ws = new AddUserToWS();
                        ws.execute();
                    }

                    Intent toUserProfile = new Intent(getApplicationContext(), UserProfileActivity.class);
                    toUserProfile.putExtra("INTENT_SOURCE", "FIRST_USE");
                    toUserProfile.putExtra("TICKET_ENCOUR", ticketEncour);
                    startActivity(toUserProfile);
                }
            });

        }else {
            ticketEncour = intent.getBooleanExtra("TICKET_ENCOUR", false);
            uNom = intent.getStringExtra(UserProfileActivity.USER_NAME);
            uPrenom = intent.getStringExtra(UserProfileActivity.USER_LASTNAME);
            uTel = intent.getStringExtra(UserProfileActivity.USER_TEL);
            uMail = intent.getStringExtra(UserProfileActivity.USER_MAIL);
            String imageUri = intent.getStringExtra(UserProfileActivity.USER_IMAGE);
            
            userNomView.setText(uNom);
            userPrenomView.setText(uPrenom);
            userTelView.setText(uTel);
            userMailView.setText(uMail);
            Uri uri = Uri.parse(imageUri);
            getApplicationContext().grantUriPermission("com.android.App1.app", uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            userImageView.setImageURI(Uri.parse(imageUri));
            
            valideBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uNom = userNomView.getText().toString();
                    uPrenom = userPrenomView.getText().toString();
                    uTel = userTelView.getText().toString();
                    uMail = userMailView.getText().toString();
                    if (!uNom.matches("") && !uPrenom.matches("") && !uTel.matches("") && !uMail.matches("")) {
                        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("nom", uNom);
                        editor.putString("prenom", uPrenom);
                        editor.putString("tel", uTel);
                        editor.putString("email", uMail);
                        if (mImage!=null){
                            Uri mImageUri = ImageUtil.getImageUri(getApplicationContext(), mImage, "UserProfileImage");
                            editor.putString("imageURI", mImageUri.toString());
                        }else if(selectedImage!=null){
                            editor.putString("imageURI", selectedImage.toString());
                        }

                        editor.commit();
                    }

                    Intent toUserProfile = new Intent(getApplicationContext(), UserProfileActivity.class);
                    toUserProfile.putExtra("INTENT_SOURCE", "USER_PROFILE_MODIFY");
                    toUserProfile.putExtra("TICKET_ENCOUR", ticketEncour);
                    startActivity(toUserProfile);
                }
            });
        }

        userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

    }

    private void selectImage() {
            try {
                PackageManager pm = getPackageManager();
                int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
                if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                    final CharSequence[] options = {"Prendre une photo", "Choose From Gallery","Annuler"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileAddOrModifyActivity.this);
                    builder.setTitle("Select Option");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("Prendre une photo")) {
                                dialog.dismiss();
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, PICK_IMAGE_CAMERA);
                            } else if (options[item].equals("Choose From Gallery")) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_IMAGE_CAMERA:
                if (resultCode == RESULT_OK) {
                    mImage = (Bitmap) data.getExtras().get("data");
                    userImageView.setImageBitmap(mImage);
                }
                break;
            case PICK_IMAGE_GALLERY:
                if (resultCode == RESULT_OK) {
                    selectedImage = data.getData();
                    userImageView.setImageURI(selectedImage);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private class AddUserToWS extends AsyncTask<Object, Object, Boolean> {

        @Override
        protected Boolean doInBackground(Object... params) {
            new WebService().postUser(uNom, uPrenom, uTel, uMail, mdpHash);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            if (aVoid){
                Log.i("WS", " OK");
            }
            Toast.makeText(getApplicationContext(), "Usager a été enregistré.", Toast.LENGTH_SHORT).show();
        }
    }
}
