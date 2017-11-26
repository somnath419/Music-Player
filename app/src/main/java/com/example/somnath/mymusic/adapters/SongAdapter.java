package com.example.somnath.mymusic.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.somnath.mymusic.NowPlayingActivity;
import com.example.somnath.mymusic.R;
import com.example.somnath.mymusic.Song;

import java.util.ArrayList;


/**
 * Created by SOMNATH on 29-04-2017.
 */

public class SongAdapter extends BaseAdapter {
    private ArrayList<Song> songs;
    private LayoutInflater songInf;
    private Context context;



    public SongAdapter(Context c, ArrayList<Song> theSongs) {
        songs = theSongs;
        songInf = LayoutInflater.from(c);
        context = c;

    }

    public  SongAdapter(Context c)
    {
        context=c;
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
        LinearLayout songLay = (LinearLayout) songInf.inflate(R.layout.artistlists, parent, false);
        //get title and artist views
        TextView songView = (TextView) songLay.findViewById(R.id.albums_text);
        TextView artistView = (TextView) songLay.findViewById(R.id.artist_albums);
        LinearLayout linearLayout = (LinearLayout) songLay.findViewById(R.id.iccc);

        //get song using position
        Song currSong = songs.get(position);
        //get title and artist string
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Song currSong = songs.get(position);
                long id = currSong.getId();
                String strin = currSong.getTitle();



                Intent intent1 = new Intent(v.getContext(), NowPlayingActivity.class);
                intent1.putExtra("ins", id);
                intent1.putExtra("isn", strin);
                v.getContext().startActivity(intent1);





            }
        });

        //set position as tag
        songLay.setTag(position);
        return songLay;
    }



}




