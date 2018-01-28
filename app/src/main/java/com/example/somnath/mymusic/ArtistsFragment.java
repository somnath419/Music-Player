package com.example.somnath.mymusic;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.somnath.mymusic.adapters.MyAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class ArtistsFragment extends Fragment{


    private ArrayList<Song> songList;
    private RecyclerView.LayoutManager mLayoutManager,mLayoutManager2,mLayoutManager3,mLayoutManager4;

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.artist_mainview, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
              // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
             // app-defined int constant
                return v;
            }}

        recyclerView=(RecyclerView)v.findViewById(R.id.recyclerArtist);
        songList = new ArrayList<Song>();


        getAritistsList();

        Collections.sort(songList, new Comparator<Song>(){
            public int compare(Song a, Song b){

                return a.getArtist().compareTo(b.getArtist());

            }});

        mLayoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(mLayoutManager);


        MyAdapter songAdt=new MyAdapter(getContext(),songList,"artists");
        recyclerView.setAdapter(songAdt);


        return v;
    }



    private void getAritistsList() {
        ContentResolver musicResolver = getContext().getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
//get columns
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int titleId = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int titleArtist = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int titleAlbums=musicCursor.getColumnIndex(MediaStore.Audio.Albums._ID);
            int titleAlbumId = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int titleGenres=musicCursor.getColumnIndex(MediaStore.Audio.Genres._ID);


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


