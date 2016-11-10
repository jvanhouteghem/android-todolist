package com.jonathan.vanhouteghem.androidtchat.model;

// username
// urlPhoto
// date
public class User {

    public User(String username, long date) {
        this.username = username;
        this.urlPhoto = urlPhoto;
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    String username;
    String urlPhoto;

    public long getDate() {
        return date;
    }

    long date;


}
