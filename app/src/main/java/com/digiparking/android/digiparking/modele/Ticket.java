package com.digiparking.android.digiparking.modele;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by milk1 on 4/5/2017.
 */

public class Ticket implements Parcelable {
    private long _id;
    private String date;
    private String heure_debut;
    private String heure_fin;

    private Station station;
    private Voiture voiture;
    //Zone zone;
    private double cout;
    private double coutTotal;

    public Ticket(long id, String date, String heure_debut, String heure_fin, Station station, Voiture voiture, double cout) {
        this._id = id;
        this.date = date;
        this.heure_debut = heure_debut;
        this.heure_fin = heure_fin;
        this.station = station;
        this.voiture = voiture;
        this.cout = cout;
        this.coutTotal = cout;
    }

    public Ticket(String date, String heure_debut, String heure_fin, Station station, Voiture voiture, double cout) {
        this.date = date;
        this.heure_debut = heure_debut;
        this.heure_fin = heure_fin;
        this.station = station;
        this.voiture = voiture;
        this.cout = cout;
        this.coutTotal = cout;
    }

    public String getDate() {
        return date;
    }

    public String getHeure_debut() {
        return heure_debut;
    }

    public String getHeure_fin() {
        return heure_fin;
    }

    public Station getStation() {
        return station;
    }

    public Voiture getVoiture() {
        return voiture;
    }

    public double getCout() {
        return cout;
    }

    public double getCoutTotal() {
        return coutTotal;
    }

    public void setHeure_fin(String heure_fin) {
        this.heure_fin = heure_fin;
    }

    public void setCoutTotal(double coutTotal) {
        this.coutTotal = coutTotal;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(_id);
        dest.writeString(date);
        dest.writeString(heure_debut);
        dest.writeString(heure_fin);
        dest.writeParcelable(station, flags);
        dest.writeParcelable(voiture, flags);
        dest.writeDouble(cout);
        dest.writeDouble(coutTotal);
    }

    private Ticket(Parcel in){
        this._id = in.readLong();
        this.date = in.readString();
        this.heure_debut = in.readString();
        this.heure_fin = in.readString();
        this.station = in.readParcelable(Station.class.getClassLoader());
        this.voiture = in.readParcelable(Voiture.class.getClassLoader());
        this.cout = in.readDouble();
        this.coutTotal = in.readDouble();
    }
    public static final Parcelable.Creator<Ticket> CREATOR = new Parcelable.Creator<Ticket>(){

        @Override
        public Ticket createFromParcel(Parcel source) {
            return new Ticket(source);
        }

        @Override
        public Ticket[] newArray(int size) {
            return new Ticket[size];
        }
    };
}
