package com.digiparking.android.digiparking.modele;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by milk1 on 4/5/2017.
 */

public class Station implements Parcelable {
    private long _id;
    private String nom;
    private String adresse;
    private Zone zone;

    public Station(String nom, String adresse, Zone zone) {
        this.nom = nom;
        this.adresse = adresse;
        this.zone = zone;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public Zone getZone(){return this.zone;}

    public void setZone(Zone uneAutreZone){this.zone = uneAutreZone;}

    public String toString(){
        return nom;
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
        dest.writeString(nom);
        dest.writeString(adresse);
        dest.writeParcelable(zone, flags);

    }

    private Station(Parcel in){
        this._id = in.readLong();
        this.nom = in.readString();
        this.adresse = in.readString();
        this.zone = in.readParcelable(Zone.class.getClassLoader());
    }

    public static final Parcelable.Creator<Station> CREATOR = new Parcelable.Creator<Station>(){

        @Override
        public Station createFromParcel(Parcel source) {

            return new Station(source);
        }

        @Override
        public Station[] newArray(int size) {
            return new Station[size];
        }
    };
}
