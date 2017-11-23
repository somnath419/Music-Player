package com.example.somnath.mymusic;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class NowPlayingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nowplaying_activity);
        Intent intent= getIntent();
        String strin= intent.getStringExtra("ins");


        Animation animationToLeft = new TranslateAnimation(400, -400, 0, 0);
        animationToLeft.setDuration(12000);
        animationToLeft.setRepeatMode(Animation.RESTART);
        animationToLeft.setRepeatCount(Animation.INFINITE);



        TextView textView=(TextView) findViewById(R.id.nameofsong);
        textView.setAnimation(animationToLeft);

        textView.setText(strin);






    }
    public void onclick( View v)

    {
        Intent intent=new Intent(v.getContext(),MyMusicService.class);
        stopService(intent);
    }

}
