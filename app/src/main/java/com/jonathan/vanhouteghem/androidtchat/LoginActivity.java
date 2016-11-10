package com.jonathan.vanhouteghem.androidtchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jonathan.vanhouteghem.androidtchat.helper.NetworkHelper;
import com.jonathan.vanhouteghem.androidtchat.utils.PreferenceHelper;
import com.jonathan.vanhouteghem.androidtchat.model.HttpResult;
import com.jonathan.vanhouteghem.androidtchat.session.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText login;
    EditText pass;
    Button loginConnexion;
    Button createLogin;
    ProgressDialog progressDialog;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    //
        login=(EditText)findViewById(R.id.loginTextLogin);
        pass=(EditText)findViewById(R.id.loginTextPass);

        // créer un compte
        createLogin= (Button)findViewById(R.id.loginButtonCreate);
        createLogin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
               intent.putExtra("sended", login.getText().toString());
               startActivity(intent);
           }

           ;
       });

        // se connecter
        loginConnexion= (Button)findViewById(R.id.loginButtonConnexion);
        loginConnexion.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if (login.getText().toString().isEmpty()) {
                login.setError("Merci de remplir le champs");
                Toast.makeText(LoginActivity.this, "champs vide", Toast.LENGTH_SHORT).show();
            }else{

                displayLoader(true);
                Toast.makeText(LoginActivity.this, login.getText().toString() + " " + pass.getText().toString(), Toast.LENGTH_SHORT).show();
                new HelloAsyncTask(v.getContext()).execute(login.getText().toString(), pass.getText().toString());

                //Toast.makeText(LoginActivity.this, login.getText().toString() + " " + pass.getText().toString(), Toast.LENGTH_SHORT).show();
                // Si connexion ok alors renvoit vers la page de message
                //Intent intent = new Intent(LoginActivity.this, TchatActivity.class);
                //intent.putExtra("sended", login.getText().toString());
                //startActivity(intent);
            }
        }
    });
    }

    private void displayLoader(boolean toDisplay){
        if(toDisplay){
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setTitle("Chargement");
            progressDialog.setMessage("Envoi du hello world");
            progressDialog.show();
        }else{
            if(progressDialog !=null && progressDialog.isShowing()){
                progressDialog.cancel();
            }else{
                Toast.makeText(this, "pg inexistante", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class HelloAsyncTask extends AsyncTask<String, Void, HttpResult> {

        Context context;

        public HelloAsyncTask(final Context context) {
            this.context = context;
        }

        @Override
        protected HttpResult doInBackground(String... params) {
            // Si il y a le web ?
            if (!NetworkHelper.isInternetAvailable(context)) {
                return new HttpResult(500, null);
            }
            try {
                Map<String, String> theMap = new HashMap<>();
                theMap.put("username", params[0]);
                theMap.put("pwd", params[1]);

                HttpResult result = NetworkHelper.doPost("http://cesi.cleverapps.io/signin", theMap, null);

                return result;
            } catch (Exception e) {
                Log.e("NetworkHelper", e.getMessage());
                return null;
            }
        }

        public void onPostExecute(final HttpResult response){
            displayLoader(false);
            if(response.code == 200){
                Toast.makeText(context, "Vous êtes connecté", Toast.LENGTH_SHORT).show();
                String token="";
                try {
                    token = new JSONObject(response.json).optString("token");
                    Session.getInstance().setToken(token);
                    PreferenceHelper.setToken(LoginActivity.this, token);
                    Intent in = new Intent(LoginActivity.this, TodoActivity.class); //Intent in = new Intent(LoginActivity.this, TchatActivity.class);
                    //in.putExtra("token",token);
                    startActivity(in);
                } catch (JSONException e) {
                    Log.e("token","unable to get token",e);
                    e.printStackTrace();
                }
            } else {
                if (Session.getInstance().getToken() != null){
                    //token = Session.getInstance().getToken();
                    token = PreferenceHelper.getToken(this.context);
                    Session.getInstance().setToken(token);
                    Intent in = new Intent(LoginActivity.this, TchatActivity.class);
                    //in.putExtra("token",token);
                    startActivity(in);
                } else {
                    Toast.makeText(LoginActivity.this, "signin failed", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
