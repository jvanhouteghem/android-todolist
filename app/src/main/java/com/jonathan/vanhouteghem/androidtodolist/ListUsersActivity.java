package com.jonathan.vanhouteghem.androidtodolist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.jonathan.vanhouteghem.androidtchat.R;
import com.jonathan.vanhouteghem.androidtodolist.session.Session;

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
