package com.example.somnath.mymusic;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class GenresFragment extends Fragment {


    private ArrayList<Song> songList;
    private ListView list;
    private MyMusicService mBoundService;
    private Context context;
    private boolean mIsBound, flag_cancel;
    private ProgressBar progressBar;

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
        Activity activity = getActivity();

        doBindService();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.allsong_mainview, container, false);


        list = (ListView) v.findViewById(R.id.list_item);
        progressBar = (ProgressBar) v.findViewById(R.id.progressbar);
        songList = new ArrayList<Song>();
        progressBar.setVisibility(View.VISIBLE);


        new List_All_Genres(context).execute();


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public class SongAdapter extends BaseAdapter {
        private ArrayList<Song> songs;
        private LayoutInflater songInf;
        private Context context;


        public SongAdapter(Context c, ArrayList<Song> theSongs) {
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
            songView.setText(currSong.getGenres());
            artistView.setText("somnath");

            songLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            //set position as tag
            songLay.setTag(position);
            return songLay;
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

    //starting of Asynctask

    class List_All_Genres extends AsyncTask<Void, Integer, ArrayList<Song>> {

        private Context context1;

        private List_All_Genres(Context context) {
            this.context1 = context;
        }

        protected void onPreExecute() {
        }

        protected ArrayList<Song> doInBackground(Void... params) {
            getGenreList();
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

            if (songList != null) {

                SongAdapter songAdt = new SongAdapter(getContext(), songList);
                list.setAdapter(songAdt);
                progressBar.setVisibility(View.GONE);

            }
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


            mediaCursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
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
                    int musicId = Integer.parseInt(mediaCursor.getString(id_column_index));

                    Uri uri = MediaStore.Audio.Genres.getContentUriForAudioId("external", musicId);
                    genresCursor = context.getContentResolver().query(uri,
                            genresProjection, null, null, null);
                    int genre_column_index = genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);

                    if (genresCursor.moveToFirst()) {

                        String genre = genresCursor.getString(genre_column_index);

                        songList.add(new Song(genre));


                    }


                } while (mediaCursor.moveToNext());
            }

        }


    }


}



