package com.digiparking.android.digiparking.modele;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by milk1 on 4/5/2017.
 */

public class Voiture implements Parcelable {

    private long _id;
    private String marque;
    private String modele;
    private String immatriculation;
    private String imageUri;

    public Voiture(String marque, String modele, String immatriculation, String imageUri) {
        this.marque = marque;
        this.modele = modele;
        this.immatriculation = immatriculation;
        this.imageUri = imageUri;
    }

    public Voiture(String marque, String modele, String immatriculation) {
        this.marque = marque;
        this.modele = modele;
        this.immatriculation = immatriculation;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String toString(){
        return marque + " " + modele;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public long get_id() {
        return _id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(_id);
        dest.writeString(marque);
        dest.writeString(modele);
        dest.writeString(immatriculation);
        dest.writeString(imageUri);

    }

    private Voiture(Parcel in){
        this._id = in.readLong();
        this.marque = in.readString();
        this.modele = in.readString();
        this.immatriculation = in.readString();
        this.imageUri = in.readString();
    }
    public static final Parcelable.Creator<Voiture> CREATOR = new Parcelable.Creator<Voiture>(){

        @Override
        public Voiture createFromParcel(Parcel source) {
            return new Voiture(source);
        }

        @Override
        public Voiture[] newArray(int size) {
            return new Voiture[size];
        }
    };
}
