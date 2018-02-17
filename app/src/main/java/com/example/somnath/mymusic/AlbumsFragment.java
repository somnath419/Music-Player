package com.example.somnath.mymusic;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.example.somnath.mymusic.adapters.MyAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class AlbumsFragment extends Fragment {


    private ArrayList<Song> albumList;
    private RecyclerView recyclerView1,recyclerView2;
    private RecyclerView.Adapter mAdapter1,mAdapter2,mAdapter3,mAdapter4;
    private RecyclerView.LayoutManager mLayoutManager,mLayoutManager2,mLayoutManager3,mLayoutManager4;
    private GridView list;
    private Context context;

    @Override
    public void onCreate(Bundle s) {
        super.onCreate(s);
        context = getContext();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.albums_mainview, container, false);

        recyclerView1= (RecyclerView) v.findViewById(R.id.recycleralbum);

        albumList = new ArrayList<Song>();

        new List_All_Albums(context).execute();

        mLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView1.setLayoutManager(mLayoutManager);

        return v ;
    }


    private class List_All_Albums extends AsyncTask<Void, Integer, ArrayList<Song>> {

        private Context context1;

        private List_All_Albums(Context context) {
            this.context1 = context;
        }

        protected void onPreExecute() {

        }

        protected ArrayList<Song> doInBackground(Void... params) {
            getAlbumsList();
            return albumList;
        }

        protected void onProgressUpdate() {
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }


        protected void onPostExecute(ArrayList<Song> c) {
            super.onPostExecute(c);

            if(albumList != null) {
                Collections.sort(albumList, new Comparator<Song>(){
                    public int compare(Song a, Song b){
                        return a.getAlbums().compareTo(b.getAlbums());
                    }
                });
                mAdapter1 = new MyAdapter(getContext(), albumList,"albums");
                recyclerView1.setAdapter(mAdapter1);
            }
        }

        private void getAlbumsList() {
            ContentResolver musicResolver = getActivity().getContentResolver();

            Uri smusicUri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
            Cursor musicCursor =musicResolver.query(smusicUri,null
                    ,null, null, null);

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


                    albumList.add(new Song(this_albums,this_artists,this_albumart));
                }
                while (musicCursor.moveToNext());
            }

            if(musicCursor!=null)
            {
                musicCursor.close();
            }
        }
    }
}


