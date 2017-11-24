package com.example.somnath.mymusic.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.somnath.mymusic.MyMusicService;
import com.example.somnath.mymusic.NowPlayingActivity;
import com.example.somnath.mymusic.R;
import com.example.somnath.mymusic.Song;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by SOMNATH on 29-04-2017.
 */

public class SongAdapter extends BaseAdapter {
    private ArrayList<Song> songs;
    private LayoutInflater songInf;


    public SongAdapter(Context c, ArrayList<Song> theSongs){
        songs=theSongs;
        songInf=LayoutInflater.from(c);
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
        LinearLayout songLay = (LinearLayout)songInf.inflate(R.layout.artistlists, parent, false);
        //get title and artist views
        TextView songView = (TextView)songLay.findViewById(R.id.albums_text);
        TextView artistView= (TextView) songLay.findViewById(R.id.artist_albums) ;


        //get song using position
         Song currSong = songs.get(position);
        //get title and artist string
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());


        songView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Song currSong= songs.get(position);
                  long id=currSong.getId();
                 String strin=currSong.getTitle();



                Intent intent =new Intent(v.getContext(),MyMusicService.class);
                 intent.putExtra("inservice",id);
                 v.getContext().startService(intent);

                Intent intent1=new Intent(v.getContext(),NowPlayingActivity.class);
                intent1.putExtra("ins",strin);
                v.getContext().startActivity(intent1);

            }
        });

         //set position as tag
        songLay.setTag(position);
        return songLay;
    }




}




