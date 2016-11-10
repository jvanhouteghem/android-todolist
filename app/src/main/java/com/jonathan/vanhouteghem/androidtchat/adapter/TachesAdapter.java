package com.jonathan.vanhouteghem.androidtchat.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jonathan.vanhouteghem.androidtchat.R;
import com.jonathan.vanhouteghem.androidtchat.Tache;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

public class TachesAdapter extends BaseAdapter {

    private final Context context;

    public TachesAdapter(Context ctx){
        this.context = ctx;
    }

    List<Tache> taches = new LinkedList<>();

    public void addTache(List<Tache> taches){
        this.taches = taches;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(taches == null){
            return 0;
        }
        return taches.size();
    }

    @Override
    public Tache getItem(int position) {
        return taches.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // On utilise le ViewHolder pour améliorer les performances et ne pas recharger les mêmes messages quand scroll en bas ou en haut
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity)context).getLayoutInflater(); // layout inflater est un objet qui permet d'instancier une vue
            convertView = inflater.inflate(R.layout.item_tache, parent, false); // R c'est le résultat quand compile.
            vh = new ViewHolder();
            vh.id = (TextView) convertView.findViewById(R.id.todo_id);
            vh.username = (TextView) convertView.findViewById(R.id.todo_username);
            vh.message = (TextView) convertView.findViewById(R.id.todo_note);
            vh.tacheStatus = (TextView) convertView.findViewById(R.id.todo_done);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.id.setText(taches.get(position).getId());
        vh.username.setText(taches.get(position).getUsername());
        vh.message.setText(taches.get(position).getMsg());
        vh.tacheStatus.setText(taches.get(position).getTacheStatus());
        /*try {
            vh.date.setText(DateHelper.getFormattedDate(taches.get(position).getDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        return convertView;
    }

    private class ViewHolder{
        TextView id;
        TextView username;
        TextView message;
        TextView tacheStatus;
    }
}
