package com.example.somnath.mymusic;

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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class NowPlayingActivity extends AppCompatActivity
{
    private Context context;
    private MyMusicService mBoundService;
    private boolean mIsBound = false;
    private  boolean isplaying;
    private long idsong;
    private  String strin;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {   super.onCreate(savedInstanceState);
        setContentView(R.layout.nowplaying_activity);

        context=this;
        Bundle intent= getIntent().getExtras();
         strin= intent.getString("song_string");

         idsong=intent.getLong("song_id");

        Toast.makeText(context,strin,Toast.LENGTH_SHORT).show();

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

                if(isplaYing())
                    basePause();
                else
                    baseResume();

            }
        });



        doBindService();

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


}

