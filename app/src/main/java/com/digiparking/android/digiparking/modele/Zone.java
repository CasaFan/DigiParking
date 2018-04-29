package com.digiparking.android.digiparking.modele;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by milk1 on 4/5/2017.
 */

public class Zone implements Parcelable{
    private long _id;
    private String nom;
    // prix par tranche de : 15mins / 30 mins / 45mins / 1h / 3h / 24h
    private double[] prixParTranche;
    private int dureeGratuitEnMinute;

    public Zone(String nom, double[] prixParTranche) {
        this.nom = nom;
        this.prixParTranche = prixParTranche;
        this.dureeGratuitEnMinute = 0;
    }

    public Zone(String nom, double[] prixParTranche, int dureeGratuitEnMinute) {
        this.nom = nom;
        this.prixParTranche = prixParTranche;
        this.dureeGratuitEnMinute = dureeGratuitEnMinute;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double[] getPrixParTranche() {
        return prixParTranche;
    }

    public void setPrixParTranche(double[] prixParTranche) {
        this.prixParTranche = prixParTranche;
    }

    public int getDureeGratuitEnMinute() {
        return dureeGratuitEnMinute;
    }

    public void setDureeGratuitEnMinute(int dureeGratuitEnMinute) {
        this.dureeGratuitEnMinute = dureeGratuitEnMinute;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(_id);
        dest.writeString(nom);
        dest.writeDoubleArray(prixParTranche);
        dest.writeInt(dureeGratuitEnMinute);
    }

    private Zone(Parcel in){
        this._id = in.readLong();
        this.nom = in.readString();
        this.prixParTranche = in.createDoubleArray();
        this.dureeGratuitEnMinute = in.readInt();

    }
    public static final Parcelable.Creator<Zone> CREATOR = new Parcelable.Creator<Zone>(){

        @Override
        public Zone createFromParcel(Parcel source) {
            return new Zone(source);
        }

        @Override
        public Zone[] newArray(int size) {
            return new Zone[size];
        }
    };
}
