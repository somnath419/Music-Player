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
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private static String strin;
    private Song song;
    private SeekBar seekBar1;
    private  TextView textView;
    boolean userTouch;
    private  ImageButton play_pause;
    private ArrayList<Song> arrayList;
    private ArrayList<String> arrayList1;

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
        context = this;
        doBindService();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        play_pause = (ImageButton) findViewById(R.id.play);

        ImageButton nextt = (ImageButton) findViewById(R.id.nex33t);

        final ImageButton pause = (ImageButton) findViewById(R.id.pause);
        ImageButton previous = (ImageButton) findViewById(R.id.previous);
        ImageButton next = (ImageButton) findViewById(R.id.next);

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
                args.putSerializable("index", arrayList);
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

    }

    @Override
    protected void onResume()
    {   super.onResume();
        play_pause.setVisibility(View.GONE);


String string=mBoundService.getCurrentTrack();
textView.setText(string);

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
