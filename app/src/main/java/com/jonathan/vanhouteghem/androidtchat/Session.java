package com.jonathan.vanhouteghem.androidtchat;

// Singleton pour avoir le token en commun
public class Session {

    static Session session = null;
    String token = null;

    public static Session getInstance(){
        if(session == null){
            session = new Session();
        }
        return session;
    }

    public String getToken(){
        return token;
    }

    public void setToken(String token){
        this.token = token;
    }
}
