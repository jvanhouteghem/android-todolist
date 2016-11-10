package com.jonathan.vanhouteghem.androidtchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.jonathan.vanhouteghem.androidtchat.utils.PreferenceHelper;

public class TodoActivity extends AppCompatActivity {

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        //Session.getInstance().setToken(token);
        // Récupération du token
        token =  PreferenceHelper.getToken(TodoActivity.this); //Session.getInstance().getToken(); //this.getIntent().getExtras().getString("token"); //token = this.getIntent().getExtras().getString(Constants.INTENT_TOKEN); // "token ?"
        Toast.makeText(this, "token récupéré : " + token, Toast.LENGTH_SHORT).show();

    }




}