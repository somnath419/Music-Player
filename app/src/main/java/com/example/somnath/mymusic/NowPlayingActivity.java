package com.example.somnath.mymusic;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class NowPlayingActivity extends Activity implements SeekBar.OnSeekBarChangeListener {

    private Context context;
    private  static MyMusicService mBoundService;
    private boolean mIsBound = false;
    private SeekBar seekBar1;
    private  TextView textView;
    boolean userTouch;
    private  ImageButton play,pause,previous,next;
    private ArrayList<Song> arrayList;
    private ArrayList<String> arrayList1;
    private ArrayList<Song> arraylist_from_other;
    private TextView name_song ;
    private ImageView album_icon;
    int set_list=0;
    private int curr;
    private Handler mHandler = new Handler();;

    //mconnection
    private  ServiceConnection mConnection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder service)
        {  mBoundService = ((MyMusicService.LocalBinder) service).getService();
        }
        public void onServiceDisconnected(ComponentName className)
        {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.nowplaying_activity);
        doBindService();

        context = this;

        play = (ImageButton) findViewById(R.id.play);
        pause = (ImageButton) findViewById(R.id.pause);
        previous = (ImageButton) findViewById(R.id.previous);
        next = (ImageButton) findViewById(R.id.next);
        album_icon = (ImageView) findViewById(R.id.song_icon);
        name_song = (TextView) findViewById(R.id.nameofsong);
        seekBar1=(SeekBar) findViewById(R.id.seekBar1);


        if (getIntent().getIntExtra("from_allsong", 0) == 1) {
            String name_image = getIntent().getStringExtra("songimage");
            String name_of_song = getIntent().getStringExtra("songname");

            name_song.setText(name_of_song);
            Bitmap bm = BitmapFactory.decodeFile(name_image);
            album_icon.setImageBitmap(bm);
            pause.setVisibility(View.VISIBLE);
            play.setVisibility(View.GONE);

        }

        if (getIntent().getIntExtra("from_main_playing", 0) == 2) {
            pause.setVisibility(View.VISIBLE);
            play.setVisibility(View.GONE);

        }
        if (getIntent().getIntExtra("from_main_not", 0) == 3)
        {
            pause.setVisibility(View.GONE);
            play.setVisibility(View.VISIBLE);
        }



        View nowPlayingList=(View) findViewById(R.id.nowPlayinglist);

        final FrameLayout frameLayout=(FrameLayout)findViewById(R.id.song_list);
        frameLayout.setVisibility(View.GONE);

        nowPlayingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayout.setVisibility(View.VISIBLE);

                pause.setVisibility(View.GONE);
                play.setVisibility(View.GONE);
                previous.setVisibility(View.GONE);
                next.setVisibility(View.GONE);
                album_icon.setVisibility(View.GONE);
                name_song.setVisibility(View.GONE);
                seekBar1.setVisibility(View.GONE);

                arrayList=mBoundService.getTracklist();
                arrayList1=new ArrayList<String>();
                for(int i=0;i<arrayList.size();i++)
                   arrayList1.add(arrayList.get(i).getTitle());

                int pos=mBoundService.getCurrentTrackPosition();

                PlayingListFragment playingListFragment=new PlayingListFragment();
                Bundle args = new Bundle();
                args.putSerializable("index", arrayList1);
                args.putInt("position",pos);
                playingListFragment.setArguments(args);

                getFragmentManager().beginTransaction().replace(R.id.song_list,playingListFragment).commit();
            }
        });

        //play or pause button
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mBoundService.play();
                pause.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBoundService.play();
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
            }
        });

        //play next song
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                mBoundService.nextTrack();

                pause.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);

                arrayList=mBoundService.getTracklist();
                String name=arrayList.get(mBoundService.getCurrentTrackPosition()).getTitle();
                String image=arrayList.get(mBoundService.getCurrentTrackPosition()).getImg_Id();
                Bitmap bm= BitmapFactory.decodeFile(image);
                album_icon.setImageBitmap(bm);
                name_song.setText(name);
            }
        });

        //play previous song
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBoundService.prevTrack();
                pause.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);

                arrayList=mBoundService.getTracklist();
                String name=arrayList.get(mBoundService.getCurrentTrackPosition()).getTitle();
                name_song.setText(name);
                String image=arrayList.get(mBoundService.getCurrentTrackPosition()).getImg_Id();
                Bitmap bm= BitmapFactory.decodeFile(image);
                album_icon.setImageBitmap(bm);
            }
        });

    }


    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }
    // Background Runnable thread
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {

            long totalDuration = mBoundService.getCurrentTrackDuration();
            long currentDuration = mBoundService.getCurrentTrackProgress();
            // Updating progress bar
            int progress = (int)(getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            seekBar1.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    //When user starts moving the progress handler
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    //When user stops moving the progress hanlder

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mBoundService.getCurrentTrackDuration();
        int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mBoundService.seekTrack(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    @Override
    protected void onStart()
    {   super.onStart();
    }

    @Override
    protected void onResume()
    {   super.onResume();

      if(mBoundService!=null) {

          if(mBoundService.getTracklist()!=null) {
              arrayList = mBoundService.getTracklist();
              String name = arrayList.get(mBoundService.getCurrentTrackPosition()).getTitle();
              name_song.setText(name);

              String image = arrayList.get(mBoundService.getCurrentTrackPosition()).getImg_Id();
              Bitmap bm = BitmapFactory.decodeFile(image);
              album_icon.setImageBitmap(bm);
              name_song.setText(name);

              if (mBoundService.getStatus() == 1) {
                  play.setVisibility(View.GONE);
                  pause.setVisibility(View.VISIBLE);
              } else {
                  pause.setVisibility(View.GONE);
                  play.setVisibility(View.VISIBLE);
              }
          }

      }

        seekBar1.setOnSeekBarChangeListener(this);
      updateProgressBar();



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

    private void doBindService()
    {   Intent i = new Intent(NowPlayingActivity.this, MyMusicService.class);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private void doUnbindService()
    {
        if (mIsBound)
        {   // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }



    public static class NotificationListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String string = intent.getAction();

            if (string.equals("previous")) {
                mBoundService.prevTrack();
            }
            else
            if (string.equals("next"))
            {  mBoundService. nextTrack();
            }
            else if(string.equals("play"))
            {
                mBoundService.play();
            }

        }
    }

    public static class PlayClickSong extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String string = intent.getAction();



        }
    }

    public int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage =(((double)currentSeconds)/totalSeconds)*100;

        // return percentage
        return percentage.intValue();
    }

    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    @Override
    public void onDestroy()
    {super.onDestroy();
        doUnbindService();
    }




}
