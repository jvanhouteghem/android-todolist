package com.jonathan.vanhouteghem.androidtchat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jonathan.vanhouteghem.androidtchat.helper.DatabaseMessageHelper;

import java.util.LinkedList;
import java.util.List;

public class MessageDao {

    private final Context context;
    private DatabaseMessageHelper dbHelper;

    public MessageDao(final Context context){
        this.context = context;
        dbHelper = new DatabaseMessageHelper(context, MessagesDB.TABLE_NAME, null, MessagesDB.DATABASE_VERSION);
    }

    public void writeMessages(List<Message> messages){
        // Récupération de la DB
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (Message msg : messages){
            writeMessage(msg, db);
        }
        db.close();
    }

    public void writeMessage(Message msg, SQLiteDatabase db){
        final ContentValues values = new ContentValues();
        values.put(MessagesDB.COLUMN_MESSAGE, msg.getMsg());
        values.put(MessagesDB.COLUMN_DATE, msg.getDate());
        values.put(MessagesDB.COLUMN_USER, msg.getUsername());

        try {
            db.insert(MessagesDB.TABLE_NAME, null, values);
        } catch (Exception e){
            Log.w("Database", "Les messsages sont déjà là?" + msg.getUsername() + " " + msg.getDate(), e);
        }

    }

    public List<Message> getMessages(){
        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        final String[] projection = {
                MessagesDB.COLUMN_MESSAGE,
                MessagesDB.COLUMN_USER,
                MessagesDB.COLUMN_DATE
        };

        final Cursor cursor = db.query(
                MessagesDB.TABLE_NAME, // Nom de la table
                projection, // Projection des résultats
                null, // WHERE Clause columns
                null, // WHERE Values
                null, // Group by
                null, // Group values
                MessagesDB.COLUMN_DATE +" DESC" // Order by
        );

        final List<Message> messages = cursorToMessages(cursor);

        cursor.close();
        db.close();
        return messages;
    }

    private List<Message> cursorToMessages(Cursor cursor) {

        final List<Message> messages = new LinkedList<>();
        while (!cursor.isLast()){
            cursor.moveToNext();
            messages.add(cursorToMessage(cursor));
        }
        return messages;
    }

    private Message cursorToMessage(Cursor cursor) {
        return new Message(
                cursor.getString(cursor.getColumnIndex(MessagesDB.COLUMN_USER)),
                cursor.getString(cursor.getColumnIndex(MessagesDB.COLUMN_MESSAGE)),
                cursor.getLong(cursor.getColumnIndex(MessagesDB.COLUMN_DATE))
        );

    }


}
