package com.example.somnath.mymusic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by SOMNATH on 30-04-2017.
 */

public class ArtistSongs extends AppCompatActivity {
    protected void onCreate( Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        ListView listView=(ListView) findViewById(R.id.list_item);

        Intent intent=getIntent();
        String str= intent.getStringExtra("message");

        String []str1={str};





        ArrayAdapter<String> songAdt = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,str1);
        listView.setAdapter(songAdt);


    }


}
