package com.digiparking.android.digiparking.service;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.digiparking.android.digiparking.R;
import com.digiparking.android.digiparking.modele.Station;
import com.digiparking.android.digiparking.modele.Ticket;
import com.digiparking.android.digiparking.modele.Voiture;
import com.digiparking.android.digiparking.modele.Zone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by milk1 on 4/28/2017.
 */

public class WebService {
    private final String url  = "http://miage-grenoble-android.16mb.com/api.php";
    HttpURLConnection urlConnection;
    private String andCarac = Uri.decode("%26");
    public WebService(){

    }

    ArrayList<Zone> zoneListWS = new ArrayList<>();
    private InputStream sendRequest(URL url) throws Exception {

        try {

            urlConnection = (HttpURLConnection) url.openConnection();
            return urlConnection.getInputStream();

        } catch (Exception e) {
            throw new Exception("Can't get web service");
        }
    }

    private OutputStream postRequest(URL url) throws Exception{
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            return urlConnection.getOutputStream();

        } catch (Exception e) {
            throw new Exception("Can't get web service");
        }
    }

    public ArrayList<Ticket> getTickets(){
        ArrayList<Ticket> mTickets;
        try {
            this.zoneListWS = getAllZone();
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            InputStream inputStream = sendRequest(new URL(url+"/ticket?transform=1"));

            if(inputStream != null) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String line = null;

                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("WSgot: ", sb.toString());
                mTickets = getAllTicketObj(sb.toString());
                return mTickets;
            }

        } catch (Exception e) {
            Log.e("WebService-ticket", "Impossible de rapatrier les données :(");
        }finally {
            urlConnection.disconnect();
        }
        return null;
    }

    private ArrayList<Ticket> getAllTicketObj(String s) {

        ArrayList<Ticket> ticketList = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(s);
            JSONArray items = json.getJSONArray("ticket");

            for (int i = 0; i < items.length(); i++) {
                JSONObject ticketObject = items.getJSONObject(i);
                Log.i("ticket- ", ticketObject.toString());
                String dateDebut = getDebutDate(ticketObject.getString("datetime_debut"));
                Log.i("date", dateDebut);
                Log.i("idZone", String.valueOf(ticketObject.getInt("zone")));
                Zone z = null;
                int y =0;
                while (y<this.zoneListWS.size()) {
                    if (zoneListWS.get(y).get_id() == ticketObject.getInt("zone")){
                        z = zoneListWS.get(y);
                    }
                    y++;
                }
                if (z!=null) {
                    Station station = new Station("station " + z.getNom(), ticketObject.getString("coordonnees"), z);
                    Voiture v = getTicketVoiture(ticketObject.getString("immatriculation"));
                    Ticket t = new Ticket(ticketObject.getInt("id"), dateDebut, ticketObject.getString("datetime_debut"), ticketObject.getString("datetime_fin"), station, v, ticketObject.getDouble("cout"));
                    ticketList.add(t);
                }else {
                    throw new Exception("Zone not found");
                }
            }

            return ticketList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Voiture getTicketVoiture(String immatriculation) {
        String getVoitureUrl = url+"/voiture?fliter=immatriculation,eq,"+immatriculation+ Uri.decode("%26")+"transform=1";
        try {
            InputStream inputStream = sendRequest(new URL(getVoitureUrl));

            if(inputStream != null) {
                // Lecture de l'inputStream dans un reader
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String line = null;

                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                JSONObject json = new JSONObject(sb.toString());
                JSONArray zoneArray = json.getJSONArray("voiture");
                JSONObject voitureObj = zoneArray.getJSONObject(0);
                return new Voiture(voitureObj.getString("marque"), voitureObj.getString("modele"), immatriculation);
            }

        } catch (Exception e) {
            Log.e("WebService-voiture", "Impossible de rapatrier les données :(");
        }finally {
            urlConnection.disconnect();
        }
        return null;
    }


    private String getDebutDate(String datetime_debut) {
        DateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.FRANCE);
        try {
            Date d = f.parse(datetime_debut);
            DateFormat date = new SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE);
            return date.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public ArrayList<Zone> getAllZone() {
        StringBuilder sb= new StringBuilder();
        String getZoneUrl = url+"/zone?transform=1";
        try {
            InputStream inputStream = sendRequest(new URL(getZoneUrl));

            if(inputStream != null) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;

                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("WSgot: ", sb.toString());
            }

        } catch (Exception e) {
            Log.e("WebService-ticket", "Impossible de rapatrier les données :(");
        }finally {
            urlConnection.disconnect();
        }
        try {
            JSONObject json = new JSONObject(sb.toString());
            JSONArray items = json.getJSONArray("zone");

            for (int i = 0; i < items.length(); i++) {
                JSONObject zoneObject = items.getJSONObject(i);
                if (zoneObject!=null) {
                    Zone aNewZone = new Zone(zoneObject.getString("nom"), new double[]{zoneObject.getDouble("tarif"), 0d, 0d});
                    aNewZone.set_id(zoneObject.getInt("id"));
                    this.zoneListWS.add(aNewZone);
                }else{
                    throw new Exception("Can't get zone from webService");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return zoneListWS;
    }

    // WS Post

    public void postUser(String nom, String prenom, String tel, String email, String mdp){
        String userJson = "nom="+nom+andCarac+"prenom="+prenom+andCarac+"tel="+tel+andCarac + "mail="+email+andCarac+"mdp="+mdp;
        String postUrl = url+"/usager";

        try {
            OutputStream out = new BufferedOutputStream(postRequest(new URL(postUrl)));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(out));
            bufferedWriter.write(userJson);
            bufferedWriter.flush();
            bufferedWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            this.urlConnection.disconnect();
        }

    }
}
