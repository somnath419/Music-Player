package com.example.somnath.mymusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;

import android.graphics.drawable.Drawable;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class AllSongsFragment extends Fragment {

    private ArrayList<Song> songList;
    private ListView list;
    private MyMusicService mBoundService;
    private Context context;
    private boolean mIsBound,flag_cancel;
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
        doBindService();
    }

    @Override
    public View onCreateView ( LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState ) {
        View v = inflater.inflate(R.layout.allsong_mainview, container, false);

        progressBar=(ProgressBar) v.findViewById(R.id.progressbar);
        list = (ListView) v.findViewById(R.id.list_item);
        songList = new ArrayList<Song>();

        new List_All_Songs(context).execute();

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
        doUnbindService();
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
            final RelativeLayout songLay = (RelativeLayout) songInf.inflate(R.layout.allsongs_block, parent, false);
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
                    Intent intent = new Intent(getActivity(), NowPlayingActivity.class);
                    intent.putExtra("from_allsong", 1);
                    intent.putExtra("songname", currSong.getTitle());
                    intent.putExtra("songimage", currSong.getImg_Id());
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

  private class List_All_Songs extends AsyncTask<Void, Integer, ArrayList<Song>> {

        private Context context1;

        private List_All_Songs(Context context) {
            this.context1 = context;
        }

        protected void onPreExecute() {

            progressBar.setVisibility(View.VISIBLE);

        }

        protected ArrayList<Song> doInBackground(Void... params) {
            getsongList();

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

            if(songList!= null) {
                getTopRecentAdded(songList);

                progressBar.setVisibility(View.GONE);

                SongAdapter songAdt = new SongAdapter(getContext(), songList);
                list.setAdapter(songAdt);

            }
        }


        private void getsongList() {

            Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor cursorAudio = context.getContentResolver().query(songUri, null, null, null, null);

            if (cursorAudio != null && cursorAudio.moveToFirst()) {
                do {
                      if(isCancelled())
                      {   cancel(true);
                         break;
                      }

                    long id = cursorAudio.getLong(cursorAudio.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    String tiitle = cursorAudio.getString(cursorAudio.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    long id_album = cursorAudio.getLong(cursorAudio.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                    String artists = cursorAudio.getString(cursorAudio.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    int getAdded=cursorAudio.getInt(cursorAudio.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED));

                    Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                            new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART, MediaStore.Audio.Albums.ALBUM},
                            MediaStore.Audio.Albums._ID + "=?",new String[]{String.valueOf(id_album)},null);

                    if (cursor.moveToFirst()) {
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                        // do whatever you need to do
                        songList.add(new Song(id, tiitle, artists, path,getAdded));
                    }
                }
                while (cursorAudio.moveToNext());
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
      }


    }



}



