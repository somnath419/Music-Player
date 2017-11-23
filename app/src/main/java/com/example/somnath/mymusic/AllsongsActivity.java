package com.example.somnath.mymusic;

import android.Manifest;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static java.lang.System.err;


public class AllsongsActivity extends AppCompatActivity {


    private ArrayList<Song> songList;
    private ListView list;




    protected void onCreate( Bundle savedInstanceState)
    {         super.onCreate(savedInstanceState);
            setContentView(R.layout.player);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
// MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
// app-defined int constant
                return;
            }}


         list=(ListView) findViewById(R.id.list_item);
         songList = new ArrayList<Song>();



        getSongList();


        Collections.sort(songList, new Comparator<Song>(){
       public int compare(Song a, Song b){
    return a.getTitle().compareTo(b.getTitle());
               }
        });

        SongAdapter songAdt = new SongAdapter(this,songList);
        list.setAdapter(songAdt);




    }

    public void getSongList() {
          ContentResolver musicResolver = getContentResolver();
           Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
          Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

          if (musicCursor != null && musicCursor.moveToFirst()) {
//get columns
             int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
              int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
              int titleArtist=musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
              int titleAlbums=musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
              int titleGenres =musicCursor.getColumnIndex(MediaStore.Audio.Genres._ID);


              //add songs to list
              do {
                  String thisTitle = musicCursor.getString(titleColumn);
                  long id= musicCursor.getInt(idColumn);
                  String thisartist=musicCursor.getString(titleArtist);
                  String thisAlbums=musicCursor.getString(titleAlbums);
                  String this_genres=musicCursor.getString(titleGenres);

                  songList.add(new Song(id,thisartist,thisTitle,thisAlbums,this_genres));
                }

              while (musicCursor.moveToNext());
          }

          if(musicCursor!=null)
          {
              musicCursor.close();
          }

    }



}



