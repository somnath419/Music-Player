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

public class PlayingListFragment extends ListFragment implements OnItemClickListener {


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
    @TargetApi(23)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getContext();
        doBindService();

        Bundle args = getArguments();
        if (args != null) {
            arrayList1 = args.getStringArrayList("index");
            position=args.getInt("position");

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.playing_list_fragment, container, false);

        listView=view.findViewById(android.R.id.list);


        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, arrayList1);
        setListAdapter(arrayAdapter);
        timerDelayRunForScroll(100,position);



        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);

        mBoundService.playTrack(position);
        mBoundService.CustomNotification();
        mBoundService.updateNotification();


    }


    private void doBindService()
    {   Intent i = new Intent(getActivity(), MyMusicService.class);
        getActivity().bindService(i, mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private void doUnbindService()
    {
        if (mIsBound)
        {   // Detach our existing connection.
            getActivity().unbindService(mConnection);
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