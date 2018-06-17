package com.example.somnath.mymusic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SOMNATH on 06-01-2018.
 */

public class DbNowplaying extends SQLiteOpenHelper {


    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "player";
    public static final String ID_SONG = "position";
    public static final String KEY_FILE = "file";
    public static final String IMAGE= "image";
    public static final String KEY_ARTIST="artist";
    public static final String KEY_ALBUMS="albums";
    public static final String KEY_GENRES="genres";
    public static final String TABLE_NAME = "tracklist";
    public static final String CURR_SONG="CURR_SONG";
    public static final String CURR_ID="ID";
    public static final String TABLE2="current_song";
    public static final String TABLE_CREATE ="CREATE TABLE "+TABLE_NAME+"("+ID_SONG+" TEXT, "+KEY_FILE+" TEXT, "+KEY_ARTIST+" TEXT, "+IMAGE+" TEXT, "+KEY_GENRES+" TEXT, "+KEY_ALBUMS+" TEXT);";
    public static final String TABLE_CREATE2="CREATE TABLE "+TABLE2+"("+CURR_ID+" INTEGER PRIMARY KEY, "+CURR_SONG+" INTEGER);";


    SQLiteDatabase database=null;

    DbNowplaying(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        db.execSQL(TABLE_CREATE2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE2);

        onCreate(db);
    }
}
