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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    //TextView resText;
    EditText login;
    EditText pass;
    Button submitBtn;
    Button closeBtn;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Intent i = getIntent();

        login=(EditText)findViewById(R.id.signupTextLogin);
        pass=(EditText)findViewById(R.id.signupTextPass);

        // Submit button
        submitBtn= (Button)findViewById(R.id.tchatListViewLireMessage);
        submitBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (login.getText().toString().isEmpty()) {
                     login.setError("Merci de remplir le champs");
                     Toast.makeText(SignupActivity.this, "champs vide", Toast.LENGTH_SHORT).show();
                 } else {
                    displayLoader(true);
                     Toast.makeText(SignupActivity.this, login.getText().toString() + " " + pass.getText().toString(), Toast.LENGTH_SHORT).show();
                    new HelloAsyncTask(v.getContext()).execute(login.getText().toString(), pass.getText().toString(), "vide");
                 }
             }
         });

        // Close button
        closeBtn = (Button) findViewById(R.id.signupButtonAnnuler);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupActivity.this.finish();
            }
        });
    }

    private void displayLoader(boolean toDisplay){
        if(toDisplay){
            progressDialog = new ProgressDialog(SignupActivity.this);
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

        public HelloAsyncTask(final Context context){
            this.context = context;
        }

        @Override
        protected HttpResult doInBackground(String... params) {
            // Si il y a le web ?
            if(!NetworkHelper.isInternetAvailable(context)){
                return new HttpResult(500, null);
            }
            try {
                Map<String, String> theMap = new HashMap<>();
                theMap.put("username", params[0]);
                theMap.put("pwd", params[1]);

                HttpResult result = NetworkHelper.doPost("http://cesi.cleverapps.io/signup", theMap, null);

                return result;
            } catch (Exception e) {
                Log.e("NetworkHelper", e.getMessage());
                return null;
            }
        }

        @Override
        public void onPostExecute(final HttpResult response){
            displayLoader(false);
            if(response.code == 200){
                Toast.makeText(SignupActivity.this, "signup ok " + response.json, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SignupActivity.this, "signup fail",Toast.LENGTH_LONG).show();
            }
        }
    }

}
