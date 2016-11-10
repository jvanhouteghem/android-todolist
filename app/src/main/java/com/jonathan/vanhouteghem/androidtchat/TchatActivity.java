package com.jonathan.vanhouteghem.androidtchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.jonathan.vanhouteghem.androidtchat.utils.PreferenceHelper;
import com.jonathan.vanhouteghem.androidtchat.session.Session;

/**
 * Récapitulatif des méthodes :
 * - TchatActivity :
 * -- onCreate : La méthode principale de la classe TchatActivity
 * -- refresh : Relancer GetMessagesAsyncTask();
 * -- SendMessageAsyncTask : Envoit du message
 * -- onPostExecute : Si erreur (i.e != 200) renvoit toast
 * -- GetMessagesAsyncTask : Récupération des messages
 * -- doInBackground : connexion http
 * -- onPostExecute : toast (nb message) + adapter.addMessage(msgs);
 * -- onResume() et onPause() : indispensables pour afficher les messages
 */
public class TchatActivity extends ActionBarActivity {

    String token;

    @Override
    public void onCreate(Bundle savedInstance) {
        // Par défaut
        super.onCreate(savedInstance);

        // Création du ContentView
        setContentView(R.layout.activity_tchat);


        // ---

        // Récupération du token
        token =  PreferenceHelper.getToken(TchatActivity.this); //Session.getInstance().getToken(); //this.getIntent().getExtras().getString("token"); //token = this.getIntent().getExtras().getString(Constants.INTENT_TOKEN); // "token ?"
        Toast.makeText(this, "token récupéré : " + token, Toast.LENGTH_SHORT).show();
        if (token == null ||token.isEmpty()) {
            Toast.makeText(this, "error no token", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(TchatActivity.this, LoginActivity.class));
            finish();
        }
        Session.getInstance().setToken(token);

        //Navigation
        /*NavigationView nav = (NavigationView) findViewById(R.id.new_view);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.menu_tchat_users:
                        Intent intent = new Intent(TchatActivity.this, ListUsersActivity.class);
                        intent.putExtra("token", token);
                        startActivity(intent);
                        return true;
                    case R.id.menu_tchat_disconnect:
                        TchatActivity.this.finish();
                        return true;
                    case R.id.menu_tchat_refresh:
                        //(TchatFragment) getFragmentManager().findFragmentById(R.id.tchat_fragment);//refresh();
                        ((TchatFragment)getSupportFragmentManager().findFragmentById(R.id.tchat_fragment)).refresh();//refresh();
                        return true;
                }
                return false;
            }
        });*/
    }


    // Ajout du menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tchat, menu);
        return true;
    }

    // Quand clic sur le menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_tchat_users:
                //new GetMessagesAsyncTask(this).execute();
                Toast.makeText(this, "Clic sur le menu", Toast.LENGTH_SHORT).show();

                // Création intent + add token
                Intent intent = new Intent(TchatActivity.this, ListUsersActivity.class);
                intent.putExtra("token", token);
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tchat, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.tchat_refresh:
                new GetMessagesAsyncTask(this).execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

}

