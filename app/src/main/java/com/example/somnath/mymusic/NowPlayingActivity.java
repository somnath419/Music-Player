package com.example.somnath.mymusic;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;



public class NowPlayingActivity extends AppCompatActivity
{
    private Context context;
    private MyMusicService mBoundService;
    private boolean mIsBound = false;
    private long idsong;
    private  String strin;
    private ArrayList<Song> list;
    private  Song song;
    private  static int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nowplaying_activity);

        doBindService();

        final TextView textView = (TextView) findViewById(R.id.nameofsong);

        final ImageView imageView = (ImageView) findViewById(R.id.largeimage);

        Button play_pause = (Button) findViewById(R.id.play_pause);
        Button stop = (Button) findViewById(R.id.stop);
        Button previous = (Button) findViewById(R.id.previous);
        Button next = (Button) findViewById(R.id.next);

        context = this;

        list = new ArrayList<Song>();
        getSongList();

        if ((getIntent().getIntExtra("f", 0) == 1)) {
            Bundle intent = getIntent().getExtras();
            strin = intent.getString("song_string");
            idsong = intent.getLong("song_id");
            textView.setText(strin);
        } else {
            song = list.get(count);
            idsong = song.getId();
            strin = song.getTitle();
            textView.setText(strin);

        }

        //play or pause button

        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isplaYing())
                    basePause();
                else
                    baseResume();

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
                baseStop();
                doUnbindService();
                doBindService();
                song = list.get(count = count + 10);
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

        Toast.makeText(context, String.valueOf(idsong), Toast.LENGTH_SHORT).show();

        CustomNotification();
    }



    @Override
    protected void onStart()
    {
        super.onStart();
        try
        {   if(mBoundService!=null)
            {
                mBoundService.play(idsong);
            }
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



    protected void baseResume()
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


    protected void basePause()
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

    protected boolean isplaYing()
    {
        return mBoundService.isplaying();
    }

    protected void baseStop()
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





    private ServiceConnection mConnection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service. Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((MyMusicService.LocalBinder) service).getService();

            if(mBoundService!=null)
            {
                mBoundService.play(idsong);
            }
        }

        public void onServiceDisconnected(ComponentName className)
        {  // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.

            mBoundService.destroy();

        }
    };




    private void doBindService()
    {
        // Establish a connection with the service. We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).

        Intent i = new Intent(getApplicationContext(), MyMusicService.class);
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


    public void getSongList() {
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
                .setSmallIcon(R.drawable.ic_menu_share)
                .setContentTitle("Now Playing")
                // Set Ticker Message
                // Dismiss Notification
                .setAutoCancel(false)
                // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                // Set RemoteViews into Notification
                .setContent(remoteViews);

        remoteViews.setTextViewText(R.id.notification_title,strin);

        Intent previousclick = new Intent(context,NotificationListener.class);
        previousclick.putExtra("previous",1);


       Intent nextclick =new Intent("someAction");

        Intent play_pause = new Intent(context,NotificationListener.class);
        play_pause.putExtra("play",3);

        Intent stop = new Intent(context,NotificationListener.class);
        stop.putExtra("stop",4);

        PendingIntent previous = PendingIntent.getBroadcast(context,0,previousclick, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent next = PendingIntent.getBroadcast(context,0,nextclick, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent playPause = PendingIntent.getBroadcast(context,0,play_pause, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent  Stop = PendingIntent.getBroadcast(context,0,stop,PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setPendingIntentTemplate(R.id.previous_not,previous);
        remoteViews.setPendingIntentTemplate(R.id.playPause_not,playPause);
        remoteViews.setPendingIntentTemplate(R.id.next_not,next);
        remoteViews.setPendingIntentTemplate(R.id.stop_not,Stop);








        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());

    }

    public class NotificationListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

           if(intent.getIntExtra("previous",0)==1)

            Toast.makeText(context,"Received", Toast.LENGTH_SHORT).show();




        }

    }

}

