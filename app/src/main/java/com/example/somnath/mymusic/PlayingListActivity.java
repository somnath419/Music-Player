package com.example.somnath.mymusic;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ListFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class PlayingListActivity extends AppCompatActivity{


    private MyMusicService mBoundService;
    private Context context;
    private boolean mIsBound = false;
    private ArrayList<String> arrayList1;
    private ListView listView;
    int position;


    //mconnection
    private ServiceConnection mConnection = new ServiceConnection()
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
        doBindService();
        setContentView(R.layout.nowplayinglist);

        context=getApplicationContext();

        listView=(ListView) findViewById(R.id.listsong);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        arrayList1 = (ArrayList<String>) args.getSerializable("index");
        position=args.getInt("position");

        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1,arrayList1);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mBoundService.playTrack((int)l);
                mBoundService.CustomNotification();
                mBoundService.updateNotification();
            }
        });

        timerDelayRunForScroll(100,position);

    }

    private void doBindService()
    {   Intent i = new Intent(this, MyMusicService.class);
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

    public void timerDelayRunForScroll(long time, final int post) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    listView.setSelection(post);
                } catch (Exception e) {}
            }
        }, time);
    }

    @Override
    public void onDestroy()
    {super.onDestroy();
        doUnbindService();
        Toast.makeText(context, "OnDestroy fragment", Toast.LENGTH_SHORT).show();
    }
}