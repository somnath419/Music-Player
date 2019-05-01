package com.example.somnath.mymusic;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class VideosList extends Fragment {


    private ArrayList<Song> videoslist;
    private Context context;
    private ListView listView;

    @Override
    public void onCreate(Bundle s) {
        super.onCreate(s);
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.videoslist, container, false);

        videoslist = new ArrayList<Song>();
        listView = (ListView) v.findViewById(R.id.recyclervideos);

        new List_All_Videos(context).execute();


        return v;
    }

    //starting of Asynctask

    class List_All_Videos extends AsyncTask<Void, Integer, ArrayList<Song>> {

        private Context context1;

        private List_All_Videos(Context context) {
            this.context1 = context;
        }

        protected void onPreExecute() {
        }

        protected ArrayList<Song> doInBackground(Void... params) {
            getVideosList();

            return videoslist;
        }

        protected void onProgressUpdate() {
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }


        protected void onPostExecute(ArrayList<Song> c) {
            super.onPostExecute(c);
            if (videoslist != null) {

                VideoAdapter videoAdapter = new VideoAdapter(context1, videoslist);
                listView.setAdapter(videoAdapter);


            }
        }


        private void getVideosList() {

            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.MINI_THUMB_MAGIC};
            Cursor cursor = context1.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    videoslist.add(new Song(cursor.getString(0), cursor.getString(1)));
                }
                cursor.close();
            }
        }

    }


    public class VideoAdapter extends BaseAdapter {
        private ArrayList<Song> songs;
        private LayoutInflater songInf;
        private Context context;


        private VideoAdapter(Context c, ArrayList<Song> theSongs) {
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
            RelativeLayout songLay = (RelativeLayout) songInf.inflate(R.layout.video_block, parent, false);
            //get title and artist views
            TextView songView = (TextView) songLay.findViewById(R.id.videoname);

            //get song using position
            final Song currSong = songs.get(position);
            //get title and artist string
            songView.setText(currSong.getVideoname());

            songLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intent = new Intent(context, Videoplayer.class);
                    intent.putExtra("songname", currSong.getVideodata());
                    context.startActivity(intent);


                }
            });
            //set position as tag
            songLay.setTag(position);
            return songLay;
        }


    }

}



