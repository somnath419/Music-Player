package com.example.somnath.mymusic;

import android.Manifest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class AllSongsFragment extends Fragment {


    private ArrayList<Song> songList;
    private ListView list;
    private MyMusicService mBoundService;
    private Context context;
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

        Activity activity = getActivity();

      doBindService();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.allsong_mainview, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant
                return v;
            }
        }

        list = (ListView) v.findViewById(R.id.list_item);
        songList = new ArrayList<Song>();

        getSongList();

        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        SongAdapter songAdt = new SongAdapter(getContext(), songList);
        list.setAdapter(songAdt);


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause()
    {
        super.onPause();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void getSongList() {

        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursorAudio = context.getContentResolver().query(songUri, null, null, null, null);


        Cursor cursorAlbum = null;
        if (cursorAudio != null && cursorAudio.moveToFirst()) {

            do {
                long id = cursorAudio.getLong(cursorAudio.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String tiitle = cursorAudio.getString(cursorAudio.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                long id_album=cursorAudio.getLong(cursorAudio.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                String artists=cursorAudio.getString(cursorAudio.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));


                Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        new String[] {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART,MediaStore.Audio.Albums.ALBUM},
                        MediaStore.Audio.Albums._ID+ "=?",
                        new String[] {String.valueOf(id_album)},
                        null);

                if (cursor.moveToFirst()) {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                    // do whatever you need to do
                    songList.add(new Song(id,tiitle,artists,path));
                }


            }
            while (cursorAudio.moveToNext());
        }


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
            RelativeLayout songLay = (RelativeLayout) songInf.inflate(R.layout.layout_allsong_texts, parent, false);
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
                    mBoundService.storeTracklist(songList);
                    Intent intent=new Intent(context, NowPlayingActivity.class);
                    intent.putExtra("from_allsong",1);
                    intent.putExtra("songname",currSong.getTitle());
                    intent.putExtra("songimage",currSong.getImg_Id());
                    context.startActivity(intent);

                    mBoundService.playTrack(position);
                }
            });
            //set position as tag
            songLay.setTag(position);
            return songLay;
        }


    }


    private void doBindService()
    {   Intent i = new Intent(context, MyMusicService.class);
        context.bindService(i, mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private void doUnbindService()
    {
        if (mIsBound)
        {   // Detach our existing connection.
            context.unbindService(mConnection);
            mIsBound = false;
        }
    }


}



