package com.digiparking.android.digiparking;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static int MY_MAP_PERMISSION_REQUEST = 1;
    private GoogleMap mMap;
    private String adress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra("INTENT_SOURCE").contentEquals("TICKET_DETAIL_LOCATION_BTN")) {
                adress = intent.getStringExtra("ADDRESS");
                adress += ", GRENOBLE";
            } else {
                Log.i("MAP :", "Adress not found");
            }
        } else {
            try {
                throw new Exception("Ilegal acces activity");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng position = getStationLatLng(adress);
        if (position != null) {
            Marker mMarker = mMap.addMarker(new MarkerOptions().position(position).title("Ma voiture location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestMapPermission();
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMarker.showInfoWindow();

        }
    }

    private void requestMapPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_MAP_PERMISSION_REQUEST);
    }

    private LatLng getStationLatLng(String adress){
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;
        LatLng position = null;

        try{
            addresses = geocoder.getFromLocationName(adress, 5);
            position = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
            Log.i("MAP : Adress got ", String.valueOf(addresses.get(0).getLatitude()) +" | " + String.valueOf(addresses.get(0).getLongitude()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return position;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_MAP_PERMISSION_REQUEST){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission Got ", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied. ", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
