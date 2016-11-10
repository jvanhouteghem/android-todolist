package com.jonathan.vanhouteghem.androidtodolist.fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.jonathan.vanhouteghem.androidtodolist.utils.Constants;
import com.jonathan.vanhouteghem.androidtodolist.model.HttpResult;
import com.jonathan.vanhouteghem.androidtodolist.helper.JsonParser;
import com.jonathan.vanhouteghem.androidtodolist.model.Message;
import com.jonathan.vanhouteghem.androidtodolist.database.MessageDao;
import com.jonathan.vanhouteghem.androidtodolist.adapter.MessagesAdapter;
import com.jonathan.vanhouteghem.androidtodolist.helper.NetworkHelper;
import com.jonathan.vanhouteghem.androidtchat.R;
import com.jonathan.vanhouteghem.androidtodolist.session.Session;

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
public class TchatFragment extends Fragment {

    // Ce qui est appellé par le xml
    /*public TchatFragment() {
        // Required empty public constructor
    }*/
    ListView listView;
    MessagesAdapter adapter;
    EditText msg;

    SwipeRefreshLayout swipe;
    // Pour raffraichir les messages
    Timer timer;
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            refresh();
        }
    };
    // Plus propre car si l'utilisateur quitte l'application on coupe la requête (évite le mémory leak exception)
    private GetMessagesAsyncTask messagesAsyncTask;
    MessageDao dao;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_tchat, container, false);
        View v = inflater.inflate(R.layout.fragment_tchat, container, false);


        // Affichage des messages
        //swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);

        // L'inflateur récupère une vue
        listView = (ListView) v.findViewById(R.id.tchat_list);

        // Ajout d'une interaction quand clic sur un message
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(inflater.getContext(), "Clic", Toast.LENGTH_SHORT).show();
                /*Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"horacio.gonzales@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Android");
                intent.putExtra(Intent.EXTRA_TEXT, adapter.getItem(position).getMsg());
                intent.setType("text/plain");

                //Intent create chooser, creates a popin that displays available apps.
                // Second param is this popin title
                startActivity(Intent.createChooser(intent, "Send Email"));*/
            }
        });

        dao = new MessageDao(TchatFragment.this.getContext());

        // Champ d'input pour le message
        msg = (EditText) v.findViewById(R.id.tchat_msg);
        swipe = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        v.findViewById(R.id.tchat_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msg.getText().toString().isEmpty()) {
                    msg.setText("Please add a message");
                    return;
                }
                new SendMessageAsyncTask().execute(msg.getText().toString());
                msg.setText("");
            }
        });

        // Récupération de la liste des messages
        adapter = new MessagesAdapter(inflater.getContext());
        listView.setAdapter(adapter);

        /*swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);*/

        // Ajout du refreshlayout
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                refresh();
            }
        });
        swipe.setColorSchemeColors(this.getResources().getColor(R.color.colorAccent), this.getResources().getColor(R.color.colorPrimary));


        // ---

        return v;
    }


    public void refresh() {
        // optimisation pour ne pas lancer si est déjà lancé (utile si bcp de messages)
        if (messagesAsyncTask == null || messagesAsyncTask.getStatus() != AsyncTask.Status.RUNNING) {
            new GetMessagesAsyncTask(this.getContext()).execute();
            //  swipeRefreshLayout.setRefreshing(true);
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
    protected class GetMessagesAsyncTask extends AsyncTask<String, Void, List<Message>> {

        Context context;

        public GetMessagesAsyncTask(final Context context) {
            this.context = context;
        }

        @Override
        protected List<Message> doInBackground(String... params) {
            if (!NetworkHelper.isInternetAvailable(context)) {
                return dao.getMessages();
            }

            InputStream inputStream = null;

            try {

                HttpResult result = NetworkHelper.doGet(context.getString(R.string.url_msg), null, Session.getInstance().getToken());

                if (result.code == 200) {
                    // Convert the InputStream into a string
                    List <Message> messages = JsonParser.getMessages(result.json);
                    dao.writeMessages(messages);
                    return messages;
                    //return JsonParser.getMessages(result.json);
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
        public void onPostExecute(final List<Message> msgs) {
            int nb = 0;
            if (msgs != null) {
                nb = msgs.size();
            }
            Toast.makeText(TchatFragment.this.getActivity(), "loaded nb messages: " + nb, Toast.LENGTH_LONG).show();
            adapter.addMessage(msgs);
            swipe.setRefreshing(false);
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
                    p.put("message", params[0]);
                    HttpResult result = NetworkHelper.doPost(TchatFragment.this.getString(R.string.url_msg), p,  Session.getInstance().getToken());

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
                    Toast.makeText(TchatFragment.this.getActivity(), "erreur", Toast.LENGTH_SHORT).show();
                } else {
                    //DO nothing
                }
            }
        }




}
