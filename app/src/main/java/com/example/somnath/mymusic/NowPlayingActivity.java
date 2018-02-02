package com.example.somnath.mymusic;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.ArrayList;

public class NowPlayingActivity extends Activity

{
    private Context context;
    private  static MyMusicService mBoundService;
    private boolean mIsBound = false;
    private static String strin;
    private Song song;
    private SeekBar seekBar1;
    private  TextView textView;
    boolean userTouch;
    private  ImageButton play_pause;
    private ArrayList<Song> arrayList;
    private ArrayList<String> arrayList1;
    private TextView name_song ;

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

        doBindService();



        setContentView(R.layout.nowplaying_activity);
        context = this;



        play_pause = (ImageButton) findViewById(R.id.play);

        ImageButton nextt = (ImageButton) findViewById(R.id.nex33t);

        final ImageButton pause = (ImageButton) findViewById(R.id.pause);
        ImageButton previous = (ImageButton) findViewById(R.id.previous);
        ImageButton next = (ImageButton) findViewById(R.id.next);
        ImageView album_icon=(ImageView) findViewById(R.id.song_icon);
        name_song=(TextView) findViewById(R.id.nameofsong);


        //image album


        //name of current song


        Button nowPlayingList=(Button) findViewById(R.id.nowPlayinglist);
        nowPlayingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList=mBoundService.getTracklist();
                arrayList1=new ArrayList<String>();
                for(int i=0;i<arrayList.size();i++)
                   arrayList1.add(arrayList.get(i).getTitle());

                PlayingListFragment playingListFragment=new PlayingListFragment();
                Bundle args = new Bundle();
                args.putSerializable("index", arrayList1);
                playingListFragment.setArguments(args);

                getFragmentManager().beginTransaction().replace(R.id.song_list,playingListFragment).commit();
            }
        });

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
                arrayList=mBoundService.getTracklist();
                String name=arrayList.get(mBoundService.getCurrentTrackPosition()).getTitle();
                name_song.setText(name);

            }
        });

        //play previous song
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mBoundService.prevTrack();
                arrayList=mBoundService.getTracklist();
                String name=arrayList.get(mBoundService.getCurrentTrackPosition()).getTitle();
                name_song.setText(name);

            }
        });

        play_pause.setVisibility(View.GONE);

    }

    @Override
    protected void onStart()
    {   super.onStart();

    }

    @Override
    protected void onResume()
    {   super.onResume();



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

    private void doBindService()
    {   Intent i = new Intent(getApplicationContext(), MyMusicService.class);
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
