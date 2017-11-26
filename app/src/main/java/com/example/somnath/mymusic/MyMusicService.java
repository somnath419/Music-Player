package com.example.somnath.mymusic;

/**
 * Created by SOMNATH on 04-11-2017.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;


public class MyMusicService extends Service implements MediaPlayer.OnCompletionListener {
    public static final String INTENT_BASE_NAME =     "com.example.serviceaudio.MusicService";
    public static final String PLAY_TRACK = INTENT_BASE_NAME + ".PLAY_TRACK";
    public static  final String STOP_TRACK="STOP";
    public static  final String PAUSE_TRACK="PAUSE";
    public static  final String RESUME_TRACK="RESUME";


    private ArrayList<HashMap<String, String >> songList = new ArrayList<HashMap<String, String>>();
    private MediaPlayer mp;
    private AudioPlayerBroadcastReceiver broadcastReceiver = new     AudioPlayerBroadcastReceiver();

    private final String TAG = "MusicService";
    private  Context context;
    private  MediaPlayer player;



    public class AudioPlayerBinder extends Binder {
        public MyMusicService getService() {
            Log.v(TAG, "AudioPlayerBinder: getService() called");
            return MyMusicService.this;
        }
    }

    private final IBinder audioPlayerBinder = new AudioPlayerBinder();

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "AudioPlayer: onBind() called");
        return audioPlayerBinder;
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "AudioPlayer: onCreate() called");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub


        context=this;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PLAY_TRACK);
        registerReceiver(broadcastReceiver, intentFilter);

        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction(PAUSE_TRACK);
        registerReceiver(broadcastReceiver, intentFilter2);

        Intent intent3 = new Intent(context, NowPlayingActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent3, 0);

        // Create Notification using NotificationCompat.Builde
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context)
                // Set Icon
                .setSmallIcon(R.drawable.ic_menu_share)
                // Set Ticker Message
                .setTicker("Hello")
                // Set Title
                .setContentTitle(context.getString(R.string.notificationtitle))
                // Set Text
                .setContentText("Somnath")
                // Add an Action Button below Notification
                .addAction(R.drawable.ic_menu_send, "Action Button", pIntent)
                // Set PendingIntent into Notification
                .setContentIntent(pIntent);
        // Dismiss Notification

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());
        return super.onStartCommand(intent, flags, startId);

    }


    @Override
    public void onDestroy() {
        Log.i(TAG, "AudioPlayer: onDestroy() called");

    }


    @Override
    public void onLowMemory() {
        stopSelf();
    }

    public void onCompletion(MediaPlayer _mediaPlayer) {

    }







    private void  playSong(long songIndex){
        player=new MediaPlayer();
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songIndex);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try
        {
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Error Setting Data Source", Toast.LENGTH_SHORT).show();
        }
        try
        {
            player.prepare();
        }
        catch (Exception e)
        {                Log.e("MUSIC SERVICE", "Error Setting Data Source", e);

        }
    }


    private class AudioPlayerBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String action2=intent.getAction();

            long currentSongIndex = (long) intent.getExtras().get("songIndex");
            String strin= intent.getStringExtra(MyMusicService.PAUSE_TRACK);


            Toast.makeText(context, "Received intent for action " + intent.getAction() + " for id: " + currentSongIndex,Toast.LENGTH_SHORT).show();

            if( PLAY_TRACK.equals(action)) {
                playSong(currentSongIndex);
                player.start();
            }
            else if(PAUSE_TRACK.equals(action2))
            {
                player.pause();
                player.seekTo(player.getCurrentPosition());

            }
            else
            {
                player.start();

            }


        }

    }
}