package com.jonathan.vanhouteghem.androidtchat;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TodoActivity extends AppCompatActivity {

    private String token;
    ListView listView;
    TachesAdapter adapter;
    private GetMessagesAsyncTask messagesAsyncTask;
    EditText msg;
    //SwipeRefreshLayout swipe;

    Timer timer;
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            refresh();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        // Récupération de la variable de test Login
        //token = this.getIntent().getExtras().getString(Constants.INTENT_TOKEN); //token = this.getIntent().getExtras().getString(Constants.INTENT_TOKEN); // "token ?"
        //Toast.makeText(this, "Token récupéré : " + token, Toast.LENGTH_SHORT).show();
        token =  Session.getInstance().getToken(); //PreferenceHelper.getToken(TodoActivity.this); //Session.getInstance().getToken(); //this.getIntent().getExtras().getString("token"); //token = this.getIntent().getExtras().getString(Constants.INTENT_TOKEN); // "token ?"
        Toast.makeText(this, "token récupéré : " + token, Toast.LENGTH_SHORT).show();

        // Affichage des messages
        listView = (ListView) findViewById(R.id.todoListView);
        adapter = new TachesAdapter(this);
        listView.setAdapter(adapter);

        // Ajout d'une interaction quand clic sur un message
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.getItem(position).getId();
                //Toast.makeText(getApplicationContext(), "Clic2 : " + adapter.getItem(position).getId() + " Value : " + adapter.getItem(position).getTacheStatus(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Clic : " + " Value : " + adapter.getItem(position).getTacheStatus() + " Inverse : " +getInverse(adapter.getItem(position).getTacheStatus()), Toast.LENGTH_SHORT).show();
                new SendMessageAsyncTaskDone().execute(getInverse(adapter.getItem(position).getTacheStatus()), adapter.getItem(position).getId());//new SendMessageAsyncTaskDone().execute(adapter.getItem(position).getTacheStatus(), adapter.getItem(position).getId()); // doinbackground
            }
        });

        // Champ d'input pour le message
        msg = (EditText) findViewById(R.id.todoEditText);
        findViewById(R.id.todoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msg.getText().toString().isEmpty()) {
                    msg.setText("Please add a message");
                    return;
                }
                new SendMessageAsyncTask().execute(msg.getText().toString());
                msg.setText("");
            };
        });

    }

    public String getInverse(String input){
        String result = null;
        if (input.equals("true")){
            result = "false";
        } else {
            result = "true";
        }
        return result;
    }

    public void refresh() {
        // optimisation pour ne pas lancer si est déjà lancé (utile si bcp de messages)
        if (messagesAsyncTask == null || messagesAsyncTask.getStatus() != AsyncTask.Status.RUNNING) {
            new GetMessagesAsyncTask(this).execute();
            //swipeRefreshLayout.setRefreshing(true);
        }
    }

    // Obligatoire, provoque le refresh()
    @Override
    public void onResume() {
        super.onResume();
        //start polling
        timer = new Timer();
        // first start in 500 ms, then update every TIME_POLLING
        try {
            timer.schedule(task, 500, 5000);
        } catch (Exception e) {
            Log.e(Constants.TAG, "Tchat timertask error", e);
        }
    }

    // Obligatoire
    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
        //messagesAsyncTask.cancel(true); // Coupe la requête quand pause (propre)
    }

    /**
     * AsyncTask for sign-in
     */
    protected class GetMessagesAsyncTask extends AsyncTask<String, Void, List<Tache>> {

        Context context;

        public GetMessagesAsyncTask(final Context context) {
            this.context = context;
        }

        @Override
        protected List<Tache> doInBackground(String... params) {
            if (!NetworkHelper.isInternetAvailable(context)) {
                return null;
            }

            InputStream inputStream = null;

            try {

                HttpResult result = NetworkHelper.doGet("http://cesi.cleverapps.io/notes", null, token);

                if (result.code == 200) {
                    // Convert the InputStream into a string
                    return JsonParser.getTaches(result.json);
                }
                return null;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } catch (Exception e) {
                Log.e("NetworkHelper", e.getMessage());
                return null;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.e("NetworkHelper", e.getMessage());
                    }
                }
            }
        }

        @Override
        public void onPostExecute(final List<Tache> taches) {
            int nb = 0;
            if (taches != null) {
                nb = taches.size();
            }
            //Toast.makeText(TodoActivity.this, "loaded nb messages: " + nb, Toast.LENGTH_LONG).show();
            //Toast.makeText(TodoActivity.this, "Name: " + taches.get(0).getUsername(), Toast.LENGTH_LONG).show();
            adapter.addTache(taches);
            //swipe.setRefreshing(false);
        }

    }

    /**
     * AsyncTask for sign-in
     */
    // Nb INTERDICTION de mettre un toast dedans sous peine de faire buger l'affichage de la liste
    public class SendMessageAsyncTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            InputStream inputStream = null;

            try {
                // Envoit des paramètres stockés dans une map
                Map<String, String> p = new HashMap<>();
                p.put("note", params[0]);
                HttpResult result = NetworkHelper.doPost("http://cesi.cleverapps.io/notes", p, token);

                return result.code;

            } catch (Exception e) {
                Log.e("NetworkHelper", e.getMessage());
                return null;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.e("NetworkHelper", e.getMessage());
                    }
                }
            }
        }

        @Override
        public void onPostExecute(Integer status) {
            if (status != 200) {
                Toast.makeText(TodoActivity.this, "erreur", Toast.LENGTH_SHORT).show();
            } else {
                //DO nothing
            }
        }
    }

    /**
     * AsyncTask for sign-in
     */
    // Nb INTERDICTION de mettre un toast dedans sous peine de faire buger l'affichage de la liste
    public class SendMessageAsyncTaskDone extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            InputStream inputStream = null;

            try {
                // Envoit des paramètres stockés dans une map
                Map<String, String> p = new HashMap<>();
                p.put("done", params[0]);
                HttpResult result = NetworkHelper.doPost("http://cesi.cleverapps.io/notes/"+params[1], p, token);

                return result.code;

            } catch (Exception e) {
                Log.e("NetworkHelper", e.getMessage());
                return null;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.e("NetworkHelper", e.getMessage());
                    }
                }
            }
        }

        @Override
        public void onPostExecute(Integer status) {
            if (status != 200) {
                Toast.makeText(TodoActivity.this, "erreur", Toast.LENGTH_SHORT).show();
            } else {
                //DO nothing
            }
        }
    }


}