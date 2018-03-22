package com.example.somnath.mymusic.adapters;

/**
 * Created by SOMNATH on 28-10-2017.
 */

import android.content.Context;
import android.content.Intent;
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

import com.example.somnath.mymusic.AlbumsSongs;
import com.example.somnath.mymusic.R;
import com.example.somnath.mymusic.Song;
import java.util.ArrayList;



public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private  ArrayList<Song> listSong;
    String st;
    private Bitmap bm;


    public MyAdapter(Context context,ArrayList<Song> songs,String string) {
        mContext=context;
        listSong=songs;
        st=string;
    }

    private class ViewHolder0 extends RecyclerView.ViewHolder {

        private TextView mTextView,mTextView2;
        private ImageView imageView;
        private RelativeLayout relativeLayout;
        private ViewHolder0(View v){

            super(v);
            imageView=(ImageView) v.findViewById(R.id.grid_image);
            mTextView=(TextView)v.findViewById(R.id.albumname);
            mTextView2=(TextView) v.findViewById(R.id.artistname);
            relativeLayout=(RelativeLayout) v.findViewById(R.id.album_each_block);

        }
    }

    private class ViewHolder2 extends RecyclerView.ViewHolder {

        private TextView mTextView_Artist,mTextView2;
        private ViewHolder2(View v){
            super(v);
            mTextView_Artist=(TextView)v.findViewById(R.id.name_artist);

        }
    }

        @Override
        public int getItemViewType(int position) {
            // Just as an example, return 0 or 2 depending on position
            // Note that unlike in ListView adapters, types don't have to be contiguous

           if(st.equals("artists"))
               return 2;
            else
                return 0;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v1 = LayoutInflater.from(mContext).inflate(R.layout.album_block,parent,false);
            View v2 = LayoutInflater.from(mContext).inflate(R.layout.artist_block,parent,false);



            v2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Artists", Toast.LENGTH_SHORT).show();
                }
            });

            switch (viewType) {
                case 0:
                    return new ViewHolder0(v1);
                case 2:
                    return new ViewHolder2(v2);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    ViewHolder0 viewHolder0 = (ViewHolder0) holder;

                    final Song currSong = listSong.get(position);
                    viewHolder0.mTextView.setText(currSong.getAlbums());
                    viewHolder0.mTextView2.setText(currSong.getArtist());

                    String string=currSong.getAlbumart();
                    if(string!=null)
                     bm= BitmapFactory.decodeFile(currSong.getAlbumart());
                    else
                        bm= BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_library_music_black_24dp);
                    viewHolder0.imageView.setImageBitmap(bm);

                    viewHolder0.relativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(mContext, AlbumsSongs.class);
                            intent.putExtra("Id",currSong.getAlbum_ID());
                            intent.putExtra("album_art",currSong.getAlbumart());
                            mContext.startActivity(intent);


                        }
                    });

                    break;

                case 2:
                    ViewHolder2 viewHolder2 = (ViewHolder2) holder;
                    Song currSong2 = listSong.get(position);
                    viewHolder2.mTextView_Artist.setText(currSong2.getArtist());
                    break;
            }
        }


    @Override
    public int getItemCount() {
        return listSong.size();
    }

}





