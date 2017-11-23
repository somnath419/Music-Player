package com.example.somnath.mymusic;

import android.content.Context;
import android.content.Intent;
import android.test.suitebuilder.TestMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by SOMNATH on 29-04-2017.
 */

public class ArtistAdapter extends BaseAdapter {
    private ArrayList<Song> songs;
    private LayoutInflater songInf;

    public ArtistAdapter(Context c, ArrayList<Song> theSongs){
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
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        LinearLayout songLay = (LinearLayout)songInf.inflate(R.layout.albums_list, parent, false);
        //get title and artist views

        final TextView albumView= (TextView) songLay.findViewById(R.id.albums_text) ;
        TextView artist=(TextView) songLay.findViewById(R.id.artist_albums);



        //get song using position
        final Song currSong = songs.get(position);
        //get albums strings


        artist.setText(currSong.getArtist());
        final String str =currSong.getTitle();

        artist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent =new Intent(v.getContext(),ArtistSongs.class);
                myIntent.putExtra("message",str);
                v.getContext().startActivity(myIntent);


            }
        });


        //set position as tag
        songLay.setTag(position);
        return songLay;
    }


}




