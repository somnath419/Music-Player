package com.example.somnath.mymusic;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
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
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static java.lang.System.err;


public class Albums extends AppCompatActivity{


    private ArrayList<Song> songList;
    private GridView list;

    protected void onCreate( Bundle savedInstanceState)
    {         super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_albums);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
// MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
// app-defined int constant
                return;
            }}


        list=(GridView) findViewById(R.id.grid_view);
        songList = new ArrayList<Song>();


        getAlbumsList();

        Collections.sort(songList, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.getAlbums().compareTo(b.getAlbums());

        }});

        AlbumAdapter songAdt = new AlbumAdapter(this, songList);
        list.setAdapter(songAdt);



    }

    private void getAlbumsList() {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
//get columns
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int titleId = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int titleArtist = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int titleAlbum_key = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY);
            int titleAlbums=musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
            int titleAlbumId = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int titleGenres=musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID);


            //add songs to list
            do {
                String thisTitle = musicCursor.getString(titleColumn);
                long thisId=musicCursor.getInt(titleId);
                String this_artists=musicCursor.getString(titleArtist);
                String this_albums=musicCursor.getString(titleAlbums);
                String this_genre=musicCursor.getString(titleGenres);

                songList.add(new Song(thisId,this_artists,thisTitle,this_albums,this_genre));
            }

            while (musicCursor.moveToNext());
        }

        if(musicCursor!=null)
        {
            musicCursor.close();
        }
    }

}


