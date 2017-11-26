package com.example.somnath.mymusic;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class NowPlayingActivity extends AppCompatActivity{
            private  MediaPlayer player;
       private Context context;
    private  boolean isplaying;


    public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    private ServiceConnection serviceConnection = new AudioPlayerServiceConnection();
    private  MyMusicService audioPlayer;
    private Intent audioPlayerIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nowplaying_activity);
        Intent intent= getIntent();
        String strin= intent.getStringExtra("isn");
        context=this;

        long id = (long) intent.getExtras().get("ins");

        player=new MediaPlayer();
        isplaying=player.isPlaying();



        Animation animationToLeft = new TranslateAnimation(400, -400, 0, 0);
        animationToLeft.setDuration(12000);
        animationToLeft.setRepeatMode(Animation.RESTART);
        animationToLeft.setRepeatCount(Animation.INFINITE);

        TextView textView=(TextView) findViewById(R.id.nameofsong);
        textView.setAnimation(animationToLeft);
        textView.setText(strin);

        Button button= (Button)findViewById(R.id.pause);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(isplaying)
                {
                    Intent intent = new Intent(MyMusicService.PAUSE_TRACK);
                    intent.putExtra(MyMusicService.PAUSE_TRACK,"Pause");
                    context.sendBroadcast(intent);
                }
                else
                    {  Intent intent = new Intent(MyMusicService.RESUME_TRACK);
                        intent.putExtra(MyMusicService.RESUME_TRACK,"RESUME");
                        context.sendBroadcast(intent);


                    }
            }
        });



        Intent msgIntent = new Intent(context, MyMusicService.class);
        msgIntent.putExtra(MyMusicService.PLAY_TRACK, id);
        context.startService(msgIntent);

        Intent intentr = new Intent(MyMusicService.PLAY_TRACK);
        intentr.putExtra("songIndex", id);
        context.sendBroadcast(intentr);




        audioPlayerIntent = new Intent(this, MyMusicService.class);
        bindService(audioPlayerIntent, serviceConnection, Context.BIND_AUTO_CREATE);




    }

    private final class AudioPlayerServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName className, IBinder baBinder) {
            audioPlayer = ((MyMusicService.AudioPlayerBinder) baBinder).getService();
            startService(audioPlayerIntent);
        }

        public void onServiceDisconnected(ComponentName className) {
            audioPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        unbindService(serviceConnection);
        super.onDestroy();

    }


}

