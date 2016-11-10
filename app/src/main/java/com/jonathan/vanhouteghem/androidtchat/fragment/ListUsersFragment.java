package com.jonathan.vanhouteghem.androidtchat.fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jonathan.vanhouteghem.androidtchat.HttpResult;
import com.jonathan.vanhouteghem.androidtchat.JsonParser;
import com.jonathan.vanhouteghem.androidtchat.NetworkHelper;
import com.jonathan.vanhouteghem.androidtchat.R;
import com.jonathan.vanhouteghem.androidtchat.Session;
import com.jonathan.vanhouteghem.androidtchat.User;
import com.jonathan.vanhouteghem.androidtchat.UsersAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListUsersFragment extends Fragment {


    ListView listView;
    UsersAdapter adapter;
    SwipeRefreshLayout swipe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        // Affichage des utilisateurs
        //swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        View v = inflater.inflate(R.layout.fragment_list_users, container, false);

        listView = (ListView) v.findViewById(R.id.listView);

        // Récupération des utilisateurs
        adapter = new UsersAdapter(inflater.getContext());
        new GetListUserAsyncTask(this.getContext()).execute();
        listView.setAdapter(adapter);

        // Ajout du refreshlayout
        swipe = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //new GetListUserAsyncTask(this).execute();
            }
        });
        swipe.setColorSchemeColors(this.getResources().getColor(R.color.colorAccent), this.getResources().getColor(R.color.colorPrimary));

        //return inflater.inflate(R.layout.fragment_list_users, container, false);
        return v;
    }

    // Récupération de la liste des utilisateurs
    protected class GetListUserAsyncTask extends AsyncTask<String, Void, List<User>> {

        Context context;

        public GetListUserAsyncTask(final Context context) {
            this.context = context;
        }


        @Override
        protected List<User> doInBackground(String... params) {
            if (!NetworkHelper.isInternetAvailable(context)) {
                return null;
            }

            InputStream inputStream = null;

            try {
                HttpResult result = NetworkHelper.doGet("http://cesi.cleverapps.io/users", null, Session.getInstance().getToken());

                if (result.code == 200) {
                    // Convert the InputStream into a string
                    return JsonParser.getUsers(result.json);
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
        public void onPostExecute(final List<User> usr){
            int nb = 0;
            if(usr != null){
                nb = usr.size();
            }
            //Toast.makeText(ListUsersActivity.this, "loaded nb messages: "+nb, Toast.LENGTH_LONG).show();
            adapter.setUsers(usr);
            //swipe.setRefreshing(false);
        }
    }

}
