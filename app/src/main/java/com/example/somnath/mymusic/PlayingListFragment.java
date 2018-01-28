package com.example.somnath.mymusic;

import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class PlayingListFragment extends ListFragment implements OnItemClickListener {


    private MyMusicService mBoundService;
    private Context context;
    private boolean mIsBound = false;
    private ArrayList<String> arrayList1;


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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         context=getContext();

        Bundle args = getArguments();
        if (args != null) {
            arrayList1 = args.getStringArrayList("index");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.playing_list_fragment, container, false);


        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, arrayList1);
        setListAdapter(arrayAdapter);


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
}