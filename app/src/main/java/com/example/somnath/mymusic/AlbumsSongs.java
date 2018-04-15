package com.example.somnath.mymusic;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.example.somnath.mymusic.AllSongsFragment.SongAdapter;


/**
 * Created by SOMNATH on 30-04-2017.
 */

public class AlbumsSongs extends AppCompatActivity {

    private Context context;
    private ArrayList<Song> album_songs_list;
    private ProgressBar progressBar;
    private ListView listView;
    private long albumId;
    private MyMusicService mBoundService;
    private boolean mIsBound;
    private String album_image;

    //mconnection
    private ServiceConnection mConnection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder service)
        {  mBoundService = ((MyMusicService.LocalBinder) service).getService();
        }
        public void onServiceDisconnected(ComponentName className)
        {

        }
    };


    private void doBindService() {
        Intent i = new Intent(AlbumsSongs.this, MyMusicService.class);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private void doUnbindService() {
        if (mIsBound) {   // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    protected void onCreate( Bundle savedInstanceState)
    {
        context=this;
        doBindService();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.allsong_mainview);
        listView=(ListView) findViewById(R.id.list_item);

        album_songs_list = new ArrayList<Song>();
        progressBar=(ProgressBar) findViewById(R.id.progressbar);

        albumId=getIntent().getLongExtra("Id",0);
        album_image=getIntent().getStringExtra("album_art");

        new List_All_Songs(context).execute();


    }


    private class List_All_Songs extends AsyncTask<Void, Integer, ArrayList<Song>> {

        private Context context1;

        private List_All_Songs(Context context) {
            this.context1 = context;
        }

        protected void onPreExecute() {

            progressBar.setVisibility(View.VISIBLE);

        }

        protected ArrayList<Song> doInBackground(Void... params) {
            getalbum_songs_list();

            return album_songs_list;
        }

        protected void onProgressUpdate() {
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }


        protected void onPostExecute(ArrayList<Song> c) {
            super.onPostExecute(c);

            if(album_songs_list!= null) {
                getTopRecentAdded(album_songs_list);

                AlbumSongsAdapter songAdt = new AlbumSongsAdapter(context, album_songs_list);
                listView.setAdapter(songAdt);

                progressBar.setVisibility(View.GONE);
            }
        }


        private void getalbum_songs_list(){

        ContentResolver contentResolver = getContentResolver();
        Uri mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor mediaCursor = contentResolver.query(mediaUri, null, null, null, null);

        // if the cursor is null.
        if(mediaCursor != null && mediaCursor.moveToFirst())
        {
            //get Columns
            int titleColumn = mediaCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = mediaCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistColumn = mediaCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int album_id = mediaCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);




            // Store the title, id and artist name in Song Array list.
            do
            {
                long thisId = mediaCursor.getLong(idColumn);
                long thisalbumId = mediaCursor.getLong(album_id);
                String thisTitle = mediaCursor.getString(titleColumn);
                String thisArtist = mediaCursor.getString(artistColumn);
                int getAdded=mediaCursor.getInt(mediaCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED));



                // Add the info to our array.
            if(albumId == thisalbumId)
                {
                    album_songs_list.add(new Song(thisId, thisTitle, thisArtist,album_image,getAdded));
                }
            }
            while (mediaCursor.moveToNext());

            // For best practices, close the cursor after use.
            mediaCursor.close();
            }
        }

        public void getTopRecentAdded(ArrayList<Song> list) {
            Collections.sort(list, new Comparator<Song>() {
                @Override
                public int compare(Song left, Song right) {
                    return left.getDate_added() - right.getDate_added();
                }
            });

            Collections.reverse(list);
        }}


        public class AlbumSongsAdapter extends BaseAdapter {
            private ArrayList<Song> songs;
            private LayoutInflater songInf;
            private Context context;


            private AlbumSongsAdapter(Context c, ArrayList<Song> theSongs) {
                songs = theSongs;
                songInf = LayoutInflater.from(c);
                context = c;
            }

            @Override
            public int getCount() {
                return songs.size();
            }

            @Override
            public Object getItem(int arg0) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public long getItemId(int arg0) {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {


                //map to song layout
                RelativeLayout songLay = (RelativeLayout) songInf.inflate(R.layout.allsongs_block, parent, false);
                //get title and artist views
                TextView songView = (TextView) songLay.findViewById(R.id.albums_text);
                TextView artistView = (TextView) songLay.findViewById(R.id.artist_albums);
                //get song using position
                final Song currSong = songs.get(position);
                //get title and artist string
                songView.setText(currSong.getTitle());
                artistView.setText(currSong.getArtist());

                songLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mBoundService.storeTracklist(songs);

                        Intent intent = new Intent(context, NowPlayingActivity.class);
                        intent.putExtra("from_allsong", 1);
                        intent.putExtra("songname", currSong.getTitle());
                        intent.putExtra("songimage",currSong.getImg_Id());
                        context.startActivity(intent);
                        mBoundService.playTrack(position);
                        mBoundService.CustomNotification();
                        mBoundService.updateNotification();

                    }
                });
                //set position as tag
                songLay.setTag(position);
                return songLay;
            }


        }

    }


