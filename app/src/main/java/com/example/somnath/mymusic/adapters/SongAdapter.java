package com.example.somnath.mymusic.adapters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.somnath.mymusic.AllSongsFragment;
import com.example.somnath.mymusic.MyMusicService;
import com.example.somnath.mymusic.NowPlayingActivity;
import com.example.somnath.mymusic.R;
import com.example.somnath.mymusic.Song;

import java.io.FileInputStream;
import java.util.ArrayList;


/**
 * Created by SOMNATH on 29-04-2017.
 */

public class SongAdapter extends BaseAdapter {
    private ArrayList<Song> songs;
    private LayoutInflater songInf;
    private Context context;
    private MyMusicService mBoundService;
     private  ServiceConnection serviceConnection;


    public SongAdapter(Context c, ArrayList<Song> theSongs) {
        songs = theSongs;
        songInf = LayoutInflater.from(c);
        context = c;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        //map to song layout
        RelativeLayout songLay = (RelativeLayout) songInf.inflate(R.layout.layout_allsong_texts, parent, false);
        //get title and artist views
        TextView songView = (TextView) songLay.findViewById(R.id.albums_text);
        TextView artistView = (TextView) songLay.findViewById(R.id.artist_albums);
        //get song using position
        Song currSong = songs.get(position);
        //get title and artist string
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());

        songLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("pos");
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                broadcastIntent.putExtra("posit",position);
                LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);

                Intent intent=new Intent(context,NowPlayingActivity.class);
                context.startActivity(intent);
            }
        });

        //set position as tag
        songLay.setTag(position);
        return songLay;
    }



}




