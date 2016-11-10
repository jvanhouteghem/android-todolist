package com.jonathan.vanhouteghem.androidtchat.model;

public class Message {

    public Message(String username, String message, long date) {
        this.username = username;
        this.msg = message;
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public String getMsg() {
        return msg;
    }

    String username;
    String msg;

    public long getDate() {
        return date;
    }

    long date;
}
