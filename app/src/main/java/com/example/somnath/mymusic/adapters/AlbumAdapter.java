package com.example.somnath.mymusic.adapters;

/**
 * Created by SOMNATH on 28-10-2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.somnath.mymusic.R;
import com.example.somnath.mymusic.Song;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder>{
    private Context mContext;
    private ArrayList<Song> songslist;


    public AlbumAdapter(Context context , ArrayList<Song> arrayList)
    {
         mContext=context;
        songslist=arrayList;

    }
    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextView,mTextView2;
        public RelativeLayout mLinearLayout,mLinearLayout2;
        public ImageView imageView,imageView2;
        public ViewHolder(View v){

            super(v);
           imageView=(ImageView) v.findViewById(R.id.grid_image);
            mTextView=(TextView)v.findViewById(R.id.albumname);
            mTextView2=(TextView) v.findViewById(R.id.artistname);




        }
    }
    @Override
    public AlbumAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.album_block,parent,false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

         Song currSong = songslist.get(position);
         holder.mTextView.setText(currSong.getAlbums());
         holder.mTextView2.setText(currSong.getArtist());


        Bitmap bm= BitmapFactory.decodeFile(currSong.getAlbumart());

        holder.imageView.setImageBitmap(bm);



    }


    @Override
    public int getItemCount() {
        return songslist.size();
    }


}