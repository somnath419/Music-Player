package com.example.somnath.mymusic;

import android.app.Service;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;


public class MyMusicService extends Service
{
    // This is the object that receives interactions from clients. See RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();
    private MediaPlayer player;
    private  Context context;

    /**
     * Class for clients to access. Because we know this service always runs in
     * the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder
    {
        MyMusicService getService()
        {
            return MyMusicService.this;
        }
    }

    @Override
    public void onCreate()
    {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // We want this service to continue running until it is explicitly stopped, so return sticky.
        player=new MediaPlayer();

        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        destroy();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }


    public void play(long res)
    {
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, res);
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
        player.setLooping(true);
        player.start();
    }


    public void pause()
    {
        if(null != player && player.isPlaying())
        {
            player.pause();
            player.seekTo(player.getCurrentPosition());
        }
    }


    public void resume()
    {
        try
        {
            if(null != player && !player.isPlaying())
            {
                player.start();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public void destroy()
    {
        if(null != player)
        {
            if(player.isPlaying())
            {
                player.stop();
            }

            player.release();
            player = null;
        }
    }

    public  boolean isplaying()
    {
        return player.isPlaying();
    }

}