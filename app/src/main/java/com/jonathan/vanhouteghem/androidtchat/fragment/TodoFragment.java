package com.jonathan.vanhouteghem.androidtchat.fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.jonathan.vanhouteghem.androidtchat.Constants;
import com.jonathan.vanhouteghem.androidtchat.model.HttpResult;
import com.jonathan.vanhouteghem.androidtchat.JsonParser;
import com.jonathan.vanhouteghem.androidtchat.helper.NetworkHelper;
import com.jonathan.vanhouteghem.androidtchat.R;
import com.jonathan.vanhouteghem.androidtchat.Session;
import com.jonathan.vanhouteghem.androidtchat.Tache;
import com.jonathan.vanhouteghem.androidtchat.adapter.TachesAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class TodoFragment extends Fragment {

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
    private Button send;
    private EditText noteTxt;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //Toast.makeText(inflater.getContext(), "Token récupéré TodoFragment : " + Session.getInstance().getToken(), Toast.LENGTH_SHORT).show();
        final View v = inflater.inflate(R.layout.fragment_todo, container, false);

        // Affichage des messages
        listView = (ListView) v.findViewById(R.id.todoListView);
        adapter = new TachesAdapter(inflater.getContext());
        listView.setAdapter(adapter);

        // Ajout d'une interaction quand clic sur un message
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String idClicked =adapter.getClickedNoteId(position);
                getItemStatus(position, idClicked);

            }
        });*/
        // Ajout d'une interaction quand clic sur un message
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.getItem(position).getId();
                Toast.makeText(inflater.getContext(), "Clic2 : " + adapter.getItem(position).getId(), Toast.LENGTH_SHORT).show();
                new SendMessageAsyncTaskDone().execute(getInverse(adapter.getItem(position).getTacheStatus()), adapter.getItem(position).getId());//new SendMessageAsyncTaskDone().execute(adapter.getItem(position).getTacheStatus(), adapter.getItem(position).getId()); // doinbackground
            }
        });

        noteTxt=(EditText)v.findViewById(R.id.todoEditText);
        //Toast.makeText(TodoFragment.this.getActivity(), "noteTxt : " + noteTxt.getText(), Toast.LENGTH_SHORT).show();

        // Champ d'input pour le message
        msg = (EditText) v.findViewById(R.id.todoEditText);
        v.findViewById(R.id.todoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msg.getText().toString().isEmpty()) {
                    msg.setText("Please add a message");
                    return;
                }
                //Toast.makeText(inflater.getContext(), "Clic : " + " Value : " + noteTxt.getText().toString() + " Inverse : ", Toast.LENGTH_SHORT).show();
                new SendMessageAsyncTask().execute(msg.getText().toString());
                msg.setText("");
            };
        });


        return v;
        //return inflater.inflate(R.layout.fragment_todo, container, false);
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
            new GetMessagesAsyncTask(this.getContext()).execute();
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

                HttpResult result = NetworkHelper.doGet("http://cesi.cleverapps.io/notes", null, Session.getInstance().getToken());

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
                HttpResult result = NetworkHelper.doPost("http://cesi.cleverapps.io/notes", p, Session.getInstance().getToken());

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
                Toast.makeText(TodoFragment.this.getActivity(), "erreur", Toast.LENGTH_SHORT).show();
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
                HttpResult result = NetworkHelper.doPost("http://cesi.cleverapps.io/notes/"+params[1], p, Session.getInstance().getToken());

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
                Toast.makeText(TodoFragment.this.getActivity(), "erreur", Toast.LENGTH_SHORT).show();
            } else {
                //DO nothing
            }
        }
    }

}
