package com.example.somnath.mymusic;

import android.Manifest;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.somnath.mymusic.adapters.SongAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class AllSongsFragment extends Fragment {


    private ArrayList<Song> songList;
    private ListView list;
    private MyMusicService mBoundService;
    private SongsPosition receiver;
    private Context context;

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

    @Override
    public void onCreate(Bundle s)
    {
        super.onCreate(s);
        context=getContext();

        Intent playerServiceIntent = new Intent(getContext(), MyMusicService.class);
        getActivity().bindService(playerServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
        IntentFilter filter = new IntentFilter("pos");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver=new SongsPosition();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver,filter);



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.allsong_mainview, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant
                return v;
            }
        }

        list=(ListView)v. findViewById(R.id.list_item);
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

    @Override
    public  void onResume()
    {
        super.onResume();

    }

    @Override
    public void onPause()
    {
        super.onPause();

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

    public  class SongsPosition extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int songposition=intent.getIntExtra("posit",0);
            mBoundService.storeTracklist(songList);

            Toast.makeText(context,"Hello",Toast.LENGTH_SHORT).show();
            mBoundService.play(songposition);
        }
    }

    @Override
    public  void  onDestroy()
    {   super.onDestroy();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);

    }

}



