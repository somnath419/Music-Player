package com.example.somnath.mymusic;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.somnath.mymusic.adapters.MyAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class ArtistsFragment extends Fragment{


    private ArrayList<Song> songList;
    private RecyclerView.LayoutManager mLayoutManager;
    private Context context;
    private RecyclerView recyclerView;
    private MyMusicService mBoundService;
    private boolean mIsBound;


    //mconnection
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((MyMusicService.LocalBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
        }
    };

    @Override
    public void onCreate(Bundle s) {
        super.onCreate(s);
        context = getContext();
        doBindService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.artist_mainview, container, false);

        recyclerView=(RecyclerView)v.findViewById(R.id.recyclerArtist);

        songList = new ArrayList<Song>();

        new List_All_Artists(context).execute();

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        return v;
    }

    //starting of Asynctask

    class List_All_Artists extends AsyncTask<Void, Integer, ArrayList<Song>> {

        private Context context1;
        private List_All_Artists(Context context) {
            this.context1 = context;
        }

        protected void onPreExecute() {
        }

        protected ArrayList<Song> doInBackground(Void... params) {
            getArtistsList();

            return songList;
        }

        protected void onProgressUpdate() {
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }


        protected void onPostExecute(ArrayList<Song> c) {
            super.onPostExecute(c);
            if(songList != null) {


                MyAdapter songAdt=new MyAdapter(context,songList,"artists");
                recyclerView.setAdapter(songAdt);
            }
        }


        private void getArtistsList() {

                ContentResolver musicResolver = getActivity().getContentResolver();

                Uri smusicUri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
                Cursor musicCursor =musicResolver.query(smusicUri,null,null, null, null);

                if (musicCursor != null && musicCursor.moveToFirst()) {
                    //get columns
                    int titleAlbums=musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
                    int titleArtist=musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                    int AlbumArt=musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);

                    //add songs to list
                    do {
                        String this_albums=musicCursor.getString(titleAlbums);
                        String this_albumart=musicCursor.getString(AlbumArt);
                        String this_artists=musicCursor.getString(titleArtist);

                        songList.add(new Song(this_artists,this_albums));
                    }
                    while (musicCursor.moveToNext());
                }

                if(musicCursor!=null)
                {
                    musicCursor.close();
                }
            }

    }

    private void doBindService() {
        Intent i = new Intent(context, MyMusicService.class);
        context.bindService(i, mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private void doUnbindService() {
        if (mIsBound) {   // Detach our existing connection.
            context.unbindService(mConnection);
            mIsBound = false;
        }
    }

}


