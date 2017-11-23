package com.example.somnath.mymusic;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class MyMusicService extends Service {
    private ArrayList<Song> songs;
    private static final int NOTIFY_ID = 1;
    MediaPlayer player=new MediaPlayer();

    public MyMusicService() {

    }
    public void onCreate()
    {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int onStartCommand(Intent intent, int flags,int startId)
    {
        long id = (long) intent.getExtras().get("inservice");
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try
        {
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch (Exception e)
        {
            Log.e("MUSIC SERVICE", "Error Setting Data Source", e);
        }
        try
        {
            player.prepare();
        }
        catch (Exception e)
        {                Log.e("MUSIC SERVICE", "Error Setting Data Source", e);

        }
        player.start();

  return START_STICKY;
    }




     public void onDestroy()
     {    if(player.isPlaying())
         player.stop();


     }
}

