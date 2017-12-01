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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.somnath.mymusic.adapters.AlbumAdapter;
import com.example.somnath.mymusic.adapters.SongAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class AllSongsFragment extends Fragment {


    private ArrayList<Song> songList;
    private ListView list;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.listviewlist, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant
                return v;
            }}


        list=(ListView)v. findViewById(R.id.grid_view);
        songList = new ArrayList<Song>();



        getSongList();


        Collections.sort(songList, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        SongAdapter songAdt = new SongAdapter(getContext(),songList);
        list.setAdapter(songAdt);


        return v;
    }






    public void getSongList() {
          ContentResolver musicResolver = getActivity().getContentResolver();
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



