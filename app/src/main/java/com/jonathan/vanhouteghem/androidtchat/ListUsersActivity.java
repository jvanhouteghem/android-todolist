package com.jonathan.vanhouteghem.androidtchat;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListUsersActivity extends AppCompatActivity {

    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        // Récupération du token
        token = Session.getInstance().getToken(); // this.getIntent().getExtras().getString("token"); //token = this.getIntent().getExtras().getString(Constants.INTENT_TOKEN); // "token ?"
        Toast.makeText(this, "token récupéré dans ListUserActivity: " + token, Toast.LENGTH_SHORT).show();
        if(token == null){
            Toast.makeText(this, "error no token", Toast.LENGTH_SHORT).show();
            //finish();
        }

    }

    /*private void refresh() {
        // optimisation pour ne pas lancer si est déjà lancé (utile si bcp de messages)
        if (GetListUserAsyncTask == null || GetListUserAsyncTask.getStatus() != AsyncTask.Status.RUNNING) {
            new GetListUserAsyncTask(this).execute();
            //  swipeRefreshLayout.setRefreshing(true);
        }
    }*/


}
