package com.example.somnath.mymusic;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import com.example.somnath.mymusic.adapters.GenresAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class GenresFragment extends Fragment{


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
            }



        }

        list=(ListView)v.findViewById(R.id.grid_view);
        songList = new ArrayList<Song>();


        getGenreList();

        Collections.sort(songList, new Comparator<Song>(){
            public int compare(Song a, Song b){

                return a.getGenres().compareTo(b.getGenres());

            }});

        GenresAdapter songAdt = new GenresAdapter(getActivity(), songList);
        list.setAdapter(songAdt);


        return v;
    }



    public void getGenreList() {
         Cursor mediaCursor;
         Cursor genresCursor;

        String[] mediaProjection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE
        };
        String[] genresProjection = {
                MediaStore.Audio.Genres.NAME,
                MediaStore.Audio.Genres._ID
        };

            mediaCursor =getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    mediaProjection, null, null, null);

            int artist_column_index = mediaCursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            int album_column_index = mediaCursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
            int title_column_index = mediaCursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            int id_column_index = mediaCursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media._ID);

            if (mediaCursor.moveToFirst()) {
                do {
                    String title_ = mediaCursor.getString(title_column_index);
                    String album_= mediaCursor.getString(album_column_index);
                    String artists_=mediaCursor.getString(artist_column_index);

                    int musicId = Integer.parseInt(mediaCursor.getString(id_column_index));

                    Uri uri = MediaStore.Audio.Genres.getContentUriForAudioId("external", musicId);
                    genresCursor = getActivity(). getContentResolver().query(uri,
                            genresProjection, null, null, null);
                    int genre_column_index = genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);

                    if (genresCursor.moveToFirst()) {

                        do {
                            String genres_= genresCursor.getString(genre_column_index);
                            songList.add(new Song(musicId,artists_,title_,album_,genres_));
                        } while (genresCursor.moveToNext());
                    }


                } while (mediaCursor.moveToNext());
            }
        }



    }










