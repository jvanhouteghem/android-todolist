package com.jonathan.vanhouteghem.androidtchat.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jonathan.vanhouteghem.androidtchat.R;
import com.jonathan.vanhouteghem.androidtchat.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class UsersAdapter extends BaseAdapter {
    private final Context context;

    public UsersAdapter(Context ctx){this.context=ctx;}
    List<User> users = new LinkedList<>();

    public void setUsers(List<User>users){
        this.users = users;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(users == null)
            return 0;
        return users.size();
    }

    @Override
    public User getItem(int i) {
        return users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater =((Activity)context).getLayoutInflater();
        view= inflater.inflate(R.layout.item_user, viewGroup,false);
        TextView user =(TextView) view.findViewById(R.id.list_user);
        TextView url =(TextView) view.findViewById(R.id.list_url);
        TextView date =(TextView) view.findViewById(R.id.list_date);

        user.setText(getItem(i).getUsername());
        url.setText(getItem(i).getUrlPhoto());

        DateFormat dateForm = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date netDate = (new Date(getItem(i).getDate()));
        date.setText(String.valueOf(dateForm.format(netDate)));
        //date.setText(String.valueOf(getItem(i).getDate()));

        return view;
    }


}