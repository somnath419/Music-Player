package com.example.somnath.mymusic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MyMusicService extends Service {

    static public final int STOPED = -1, PAUSED = 0, PLAYING = 1;
    private MediaPlayer mediaPlayer;
    private ArrayList<Song> tracklist;
    private int status;
    private  int currentTrackPosition;
    private boolean taken;
    private IBinder playerBinder;
    private Context context;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    public class LocalBinder extends Binder
    {    public MyMusicService getService() {

            return MyMusicService.this;
        }
    }


    @Override
    public void onCreate() {

        super.onCreate();
        context=this;
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        mediaPlayer = new MediaPlayer();
        tracklist = new ArrayList<Song>();
        setStatus(STOPED);
        playerBinder = new LocalBinder();

        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                if (currentTrackPosition == tracklist.size()-1) {
                    stop();
                } else {
                    nextTrack();
                }
            }
        });
        Toast.makeText(MyMusicService.this, "OnCreate", Toast.LENGTH_SHORT).show();

        restoreTracklist();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return  START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {   CustomNotification();

        return playerBinder;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        return super.onUnbind(intent);
    }



    public void take() {
        taken = true;
    }

    private void untake() {
        synchronized (this) {
            taken = false;
            notifyAll();
        }
    }

    public boolean isTaken() {
        return taken;
    }

    private void setStatus(int s) {
        status = s;
    }

    public int getStatus() {
        return status;
    }

    public ArrayList<Song> getTracklist() {
        return tracklist;
    }

    public Song getTrack(int pos) {
        return tracklist.get(pos);
    }

    public void addTrack(Song track) {
        tracklist.add(track);
        untake();
    }

    public void addTrack(long id,String name) {
        tracklist.add(new Song(id,name));
        untake();
    }

    public String getCurrentTrack() {
        if (currentTrackPosition < 0) {
            return null;
        } else {
            return tracklist.get(currentTrackPosition).getTitle();
        }
    }

    public void setCurrentTrackPosition( int position)
    {  currentTrackPosition=position;

    }

    public int getCurrentTrackPosition() {
        return currentTrackPosition;
    }


    public void removeTrack(int pos) {
        if (pos == currentTrackPosition) {
            stop();
        }
        if (pos < currentTrackPosition) {
            currentTrackPosition--;
        }
        tracklist.remove(pos);
        untake();
    }

    public void clearTracklist() {
        if (status > STOPED) {
            stop();
        }
        tracklist.clear();
        untake();
    }

    public void playTrack(int pos) {
        if (status > STOPED) {
            stop();
        }
        Song song=tracklist.get(pos);
        long id =song.getId();

        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,id );
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try
        {
            mediaPlayer.setDataSource(getApplicationContext(), trackUri);
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Error Setting Data Source", Toast.LENGTH_SHORT).show();
        }
        try
        {
            mediaPlayer.prepare();
        }
        catch (Exception e)
        {                Log.e("MUSIC SERVICE", "Error Setting Data Source", e);

        }
        mediaPlayer.start();

        currentTrackPosition = pos;
        setStatus(PLAYING);
        untake();
    }

    public void play(int pos) {
        playTrack(pos);
    }

    public void play() {
        switch (status) {
            case STOPED:
                if (!tracklist.isEmpty()) {
                    playTrack(currentTrackPosition);
                }
                break;
            case PLAYING:
                mediaPlayer.pause();
                setStatus(PAUSED);
                break;
            case PAUSED:
                mediaPlayer.start();
                setStatus(PLAYING);
                break;
        }
        untake();
    }

    public void pause() {
        mediaPlayer.pause();
        setStatus(PAUSED);
        untake();
    }

    public void stop() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        currentTrackPosition = -1;
        setStatus(STOPED);
        untake();
    }

    public void nextTrack() {
        if (currentTrackPosition < tracklist.size()-1) {
            playTrack(currentTrackPosition+1);
        }
    }

    public void prevTrack() {
        if (currentTrackPosition > 0) {
            playTrack(currentTrackPosition-1);
        }
    }

    public int getCurrentTrackProgress() {
        if (status > STOPED) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public int getCurrentTrackDuration() {
        if (status > STOPED) {
            return mediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    public void seekTrack(int p) {
        if (status > STOPED) {
            mediaPlayer.seekTo(p);
            untake();
        }
    }

    public MediaPlayer mediaPlayer()
    {
        return  mediaPlayer;
    }

    public void storeTracklist(ArrayList<Song> tracklist) {
        DbNowplaying dbOpenHelper = new DbNowplaying(getApplicationContext());
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.onUpgrade(db, 1, 1);
        for (int i = 0; i < tracklist.size(); i++) {
            ContentValues c = new ContentValues();
            c.put(DbNowplaying.KEY_POSITION,tracklist.get(i).getId());
            c.put(DbNowplaying.KEY_FILE, tracklist.get(i).getTitle());
            c.put(DbNowplaying.IMAGE,tracklist.get(i).getImg_Id());
            c.put(DbNowplaying.KEY_ARTIST,tracklist.get(i).getArtist());
            db.insert(DbNowplaying.TABLE_NAME, null, c);
        }
        dbOpenHelper.close();

        restoreTracklist();

    }

    public void restoreTracklist() {
        DbNowplaying dbOpenHelper = new DbNowplaying(getApplicationContext());
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor c = db.query(DbNowplaying.TABLE_NAME, null, null, null, null, null, null);
        tracklist.clear();
        while (c.moveToNext()) {
            tracklist.add(new Song(c.getLong(0),c.getString(1),c.getString(2),c.getString(3)));
        }
        dbOpenHelper.close();
        currentTrackPosition=getCurrentTrackPosition();

    }

    public void CustomNotification() {
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.notification_view);
        // Open NotificationView Class on Notification Click
        Intent intent = new Intent(this,MainActivity.class);

        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                // Set Icon
                .setSmallIcon(R.drawable.ic_play_circle_outline_black_24dp)
                .setContentTitle("Now Playing")                // Set Ticker Message
                // Dismiss Notification
                .setAutoCancel(false)
                // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                // Set RemoteViews into Notification
                .setContent(remoteViews);


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

        remoteViews.setOnClickPendingIntent(R.id.previous_not,pre);
        remoteViews.setOnClickPendingIntent(R.id.playPause_not,playPause);
        remoteViews.setOnClickPendingIntent(R.id.next_not,next);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        startForeground(1337,builder.build());

    }

    @Override
    @TargetApi(24)
    public void onDestroy()
    {   super.onDestroy();

       stopSelf();
       stopForeground(STOP_FOREGROUND_REMOVE);

    }

}