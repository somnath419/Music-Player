package com.example.somnath.mymusic;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



public class NowPlayingActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener
{
    private Context context;
    private static MyMusicService mBoundService;
    private boolean mIsBound = false;
    private static long idsong;
    private static String strin;
    private static ArrayList<Song> list;
    private static Song song;
    public static int count=0;
    private SeekBar seekBar1;
    boolean userTouch;
    private  static  TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nowplaying_activity);


        doBindService();

       textView = (TextView) findViewById(R.id.nameofsong);

        ImageView imageView = (ImageView) findViewById(R.id.largeimage);


        final ImageButton play_pause = (ImageButton) findViewById(R.id.play);
        ImageButton stop = (ImageButton) findViewById(R.id.stop);


        ImageButton nextt = (ImageButton) findViewById(R.id.nex33t);

        nextt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mBoundService.seeking((int) (mBoundService.postion() + 50000));
                seekBar1.setProgress((int) (mBoundService.postion() + 50000));

            }
        });

        final ImageButton pause = (ImageButton) findViewById(R.id.pause);
        play_pause.setVisibility(View.GONE);

        ImageButton previous = (ImageButton) findViewById(R.id.previous);
        ImageButton next = (ImageButton) findViewById(R.id.next);

        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        seekBar1.setOnSeekBarChangeListener(this);
        seekBar1.setProgress(0);


        context = this;

        list = new ArrayList<Song>();

        getSongList();

        if ((getIntent().getIntExtra("f", 0) == 1)) {
            baseStop();
            doUnbindService();
            doBindService();
            Bundle intent = getIntent().getExtras();
            strin = intent.getString("song_string");
            idsong = intent.getLong("song_id");
            textView.setText(strin);
        } else

        {
            baseStop();
            doUnbindService();
            doBindService();
            song = list.get(count);
            idsong = song.getId();
            strin = song.getTitle();
            textView.setText(strin);

        }


        //play or pause button

        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isplaYing()) {
                    baseResume();
                    pause.setVisibility(View.VISIBLE);
                    play_pause.setVisibility(View.GONE);
                }

            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isplaYing()) {
                    basePause();
                    pause.setVisibility(View.GONE);
                    play_pause.setVisibility(View.VISIBLE);
                }

            }
        });

        //stop the service

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseStop();
                doUnbindService();
            }
        });

        //play next song


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count < list.size())
                    baseStop();
                doUnbindService();
                doBindService();
                song = list.get(count = count + 5);
                idsong = song.getId();

                strin = song.getTitle();
                textView.setText(strin);

            }
        });


        //play previous song
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count > 0) {
                    baseStop();
                    doUnbindService();
                    doBindService();
                    song = list.get(count = count - 1);
                    idsong = song.getId();

                    strin = song.getTitle();
                    textView.setText(strin);
                }

            }
        });


        CustomNotification();




    }


    public void getSeekBarStatus(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                int total=mBoundService.totalduration();
                int CurrentPosition=0;
                seekBar1.setMax(total);

                while(CurrentPosition<total)
                {
                    try {
                        Thread.sleep(1000);
                        CurrentPosition=mBoundService.postion();
                    } catch (InterruptedException e) {
                        return;
                    }
                    seekBar1.setProgress(CurrentPosition);
                }
            }
        }).start();



    }



    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if(fromUser){

              mBoundService.seeking(progress);
        }

        final long Minutes=((progress/1000)/60);
        final int Seconds=((progress/1000)%60);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }



    @Override
    protected void onStart()
    {
        super.onStart();

        seekBar1.setProgress(20);

        try
        {
            if(mBoundService!=null)
               mBoundService.play(idsong);

        }
        catch(Exception e)
        {   e.printStackTrace();
        }



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


    private static void baseResume()
    {
        try
        {
            if(mBoundService!=null)
            {
                mBoundService.resume();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


      private static void basePause()
    {
        try
        {
            if(mBoundService!=null)
            {
                mBoundService.pause();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static boolean isplaYing()
    {
        return mBoundService.isplaying();
    }

    private static void baseStop()
    {
        try
        {
            if(mBoundService!=null)
            {
                mBoundService.destroy();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    //mconnection
    private static ServiceConnection mConnection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder service)
        {mBoundService = ((MyMusicService.LocalBinder) service).getService();
            if(mBoundService!=null)
                mBoundService.play(idsong);
        }

        public void onServiceDisconnected(ComponentName className)
        {mBoundService.destroy();

        }
    };




    private  void doBindService()
    {  Intent i = new Intent(getApplicationContext(), MyMusicService.class);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);
        startService(i);
        mIsBound = true;
    }

    private void doUnbindService()
    {
        if (mIsBound)
        {   // Detach our existing connection.
            unbindService(mConnection);
            stopService(new Intent(context,MyMusicService.class));
            mIsBound = false;
        }
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }


        private void getSongList() {

        ContentResolver musicResolver =getContentResolver();
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

    public void CustomNotification() {

        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.notification_view);


        // Open NotificationView Class on Notification Click
        Intent intent = new Intent(this,MainActivity.class);
        // Send data to NotificationView Class

        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                // Set Icon
                .setSmallIcon(R.drawable.play)
                .setContentTitle("Now Playing")
                // Set Ticker Message
                // Dismiss Notification
                .setAutoCancel(false)
                // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                // Set RemoteViews into Notification
                .setContent(remoteViews);

        remoteViews.setTextViewText(R.id.notification_title,strin);




        Intent previousclick = new Intent("previous");
        previousclick.putExtra("previous",1);


        Intent nextclick = new Intent("next");
        nextclick.putExtra("extra", "hello");

        Intent play_pause = new Intent("play");
        play_pause.putExtra("play",3);

        Intent stop = new Intent("stop");
        stop.putExtra("stop",4);


        PendingIntent pre = PendingIntent.getBroadcast(this, 0,previousclick, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent next = PendingIntent.getBroadcast(this,0,nextclick, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent playPause = PendingIntent.getBroadcast(this,0,play_pause, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent  Stop = PendingIntent.getBroadcast(this,0,stop,PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.previous_not,pre);
        remoteViews.setOnClickPendingIntent(R.id.playPause_not,playPause);
        remoteViews.setOnClickPendingIntent(R.id.next_not,next);
        remoteViews.setOnClickPendingIntent(R.id.stop_not,Stop);


        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());

    }




        public static class NotificationListener extends BroadcastReceiver {

            @Override
            public void onReceive(Context context, Intent intent)
            {
                String string=intent.getAction();

                if(string.equals("previous")){

                    Toast.makeText(context,"Previous Clicked",Toast.LENGTH_SHORT).show();


                }

                if(string .equals("next"))
                {

                    if (count < list.size()) {
                        basePause();

                        song = list.get(count = count + 5);
                        idsong = song.getId();
                        strin = song.getTitle();
                        textView.setText(strin);

                        mBoundService.play(idsong);
                    }
                }

                if(string.equals("play"))
                {

                    if (isplaYing()) {
                        basePause();

                    }
                    else
                        baseResume();
                }
                if(string.equals("stop"))
                {
                    Toast.makeText(context,"Stop Clicked",Toast.LENGTH_SHORT).show();

                }

            }
        }

}