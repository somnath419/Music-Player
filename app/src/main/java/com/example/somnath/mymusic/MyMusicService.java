package com.example.somnath.mymusic;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.somnath.mymusic.NowPlayingActivity.count;


public class MyMusicService extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener

{
    // This is the object that receives interactions from clients. See RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();
    private MediaPlayer player;
    private  Context context;
    private MediaPlayer.OnCompletionListener onCompletionListener;
    private boolean complete=false;
    private ArrayList<Song> list;
    private  Song song;
    private NowPlayingActivity nowPlayingActivity;
    private  long idsong,idsong2;


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
        player.setOnCompletionListener(this);







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
        player.reset();
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

    public int postion()
    {
       return player.getCurrentPosition();
    }

    public int totalduration()
    {
        return player.getDuration();
    }

    public void resume()
    {
        try
        {
            if(player !=null && !player.isPlaying())
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

    //return isplaying

    public  boolean isplaying()
    {
        return player.isPlaying();
    }

    public void seeking(int arg)
    {
        player.pause();
      player.seekTo(arg);
        resume();
    }


    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
          Toast.makeText(this,"Songs completed",Toast.LENGTH_SHORT).show();



        list=new ArrayList<Song>();

        getSongList();

        song=list.get(count=count+3);
        idsong=song.getId();

        player.stop();
        player.reset();

        play(idsong);


    }

    public void OnNewSong()
    {
        player.stop();
        player.reset();
    }


    public  MediaPlayer mediaPlayer()
    {return player;}


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


}

