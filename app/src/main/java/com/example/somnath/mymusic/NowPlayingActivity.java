package com.example.somnath.mymusic;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import org.w3c.dom.Text;
import java.util.ArrayList;

public class NowPlayingActivity extends AppCompatActivity
{
    private Context context;
    private  static MyMusicService mBoundService;
    private boolean mIsBound = false;
    private static long idsong;
    private static String strin;
    private ArrayList<Song> list;
    private static Song song;
    public static int count=0;
    private SeekBar seekBar1;
    private  TextView textView;
    boolean userTouch;
    private  ImageButton play_pause;

    //mconnection
    private  ServiceConnection mConnection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder service)
        {  mBoundService = ((MyMusicService.LocalBinder) service).getService();

        }

        public void onServiceDisconnected(ComponentName className)
        {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nowplaying_activity);


        doBindService();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textView = (TextView) findViewById(R.id.nameofsong);

         play_pause = (ImageButton) findViewById(R.id.play);
        ImageButton stop = (ImageButton) findViewById(R.id.stop);
        ImageButton nextt = (ImageButton) findViewById(R.id.nex33t);
        final ImageButton pause = (ImageButton) findViewById(R.id.pause);
        ImageButton previous = (ImageButton) findViewById(R.id.previous);
        ImageButton next = (ImageButton) findViewById(R.id.next);

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBoundService.restoreTracklist();
            }
        });


        context = this;
        list = new ArrayList<Song>();
        getSongList();


        //play or pause button
        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBoundService.play();
                pause.setVisibility(View.VISIBLE);
                play_pause.setVisibility(View.GONE);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBoundService.play();
                pause.setVisibility(View.GONE);
                play_pause.setVisibility(View.VISIBLE);

            }
        });

        //play next song
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                mBoundService.nextTrack();
            }
        });

        //play previous song
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mBoundService.prevTrack();
            }
        });



    }



    @Override
    protected void onStart()
    {   super.onStart();
        play_pause.setVisibility(View.GONE);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
    }

    private  void doBindService()
    {  Intent i = new Intent(getApplicationContext(), MyMusicService.class);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private void doUnbindService()
    {
        if (mIsBound)
        {   // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy()
    {super.onDestroy();
    }


    private void getSongList() {
        ContentResolver musicResolver = getContentResolver();
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

                list.add(new Song(id,thisartist,thisTitle,thisAlbums,this_genres));
                }

            while (musicCursor.moveToNext());
        }

        if(musicCursor!=null)
        {
            musicCursor.close();
        }
    }


    // Custom Notification


    public static class NotificationListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String string = intent.getAction();

            if (string.equals("previous")) {
                mBoundService.prevTrack();
            }
            else
            if (string.equals("next"))
            {  mBoundService. nextTrack();
            }
            else if(string.equals("play"))
            {
                mBoundService.play();
            }




        }
    }


}
