package com.jonathan.vanhouteghem.androidtchat;


// On retrouve ici ce à quoi ressemble la DB

public class MessagesDB {

    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "messages";
    public static final String COLUMN_MESSAGE = "messages";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_USER = "user";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_MESSAGE + " TEXT, "
            + COLUMN_DATE + " INTEGER,"
            + COLUMN_USER + " TEXT,"
            + "PRIMARY KEY ("+COLUMN_DATE+", " +COLUMN_USER + ", "+COLUMN_USER+"));";



}
