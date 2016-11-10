package com.jonathan.vanhouteghem.androidtchat;

public class Tache {

    String id;
    String username;
    String msg;
    String tacheStatus;

    public Tache(String id, String username, String message, String tacheStatus) {
        this.id = id;
        this.username = username;
        this.msg = message;
        this.tacheStatus = tacheStatus;
    }

    public String getId(){return id;}

    public String getUsername() {
        return username;
    }

    public String getMsg() {
        return msg;
    }

    public String getTacheStatus() {
        return tacheStatus;
    }


}

