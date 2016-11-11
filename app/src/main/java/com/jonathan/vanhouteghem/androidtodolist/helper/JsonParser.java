package com.jonathan.vanhouteghem.androidtodolist.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import com.jonathan.vanhouteghem.androidtodolist.model.Tache;
import com.jonathan.vanhouteghem.androidtodolist.model.User;

/**
 * Created by sca on 03/06/15.
 */
public class JsonParser {

    /*public static List<Message> getMessages(String json) throws JSONException {
        List<Message> messages = new LinkedList<>();
        JSONArray array = new JSONArray(json);
        JSONObject obj;
        Message msg;
        for(int i=0; i < array.length(); i++){
            obj = array.getJSONObject(i);
            msg = new Message(obj.optString("username"), obj.optString("message"), obj.optLong("date"));
            messages.add(msg);
        }

        return messages;
    }*/

    public static List<Tache> getTaches(String json) throws JSONException {
        List<Tache> taches = new LinkedList<>();
        JSONArray array = new JSONArray(json);
        JSONObject obj;
        Tache tache;
        for(int i=0; i < array.length(); i++){
            obj = array.getJSONObject(i);
            tache = new Tache(obj.optString("id"), obj.optString("username"), obj.optString("note"), obj.optString("done"));//tache = new Message(obj.optString("username"), obj.optString("message"), obj.optLong("date"));
            taches.add(tache);
        }

        return taches;
    }

    // username
    // urlPhoto
    // date
    public static List<User> getUsers(String json) throws JSONException {
        List<User> users = new LinkedList<>();
        JSONArray array = new JSONArray(json);
        JSONObject obj;
        User usr;
        for(int i=0; i < array.length(); i++){
            obj = array.getJSONObject(i);
            usr = new User(obj.optString("username"), obj.optLong("date")); //usr = new User(obj.optString("username"), obj.optString("urlPhoto"), obj.optLong("date"));
            users.add(usr);
        }

        return users;
    }

    public static String getToken(String response) throws JSONException {
        return new JSONObject(response).optString("token");
    }
}