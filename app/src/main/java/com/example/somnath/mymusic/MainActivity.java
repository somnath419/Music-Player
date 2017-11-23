package com.example.somnath.mymusic;

import android.app.ListActivity;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        GridView mylist = (GridView) findViewById(R.id.grid_view1);

        String[] library = new String[]{"Tracks", "Albums", "Artists", "Genres", "Playlists", "Folders"};

        ArrayAdapter<String> myadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, library);

        mylist.setAdapter(myadapter);




        mylist.setOnItemClickListener(  new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)

            {

                if (position == 0) {
                    Intent myIntent = new Intent(getApplicationContext(), AllsongsActivity.class);
                    startActivity(myIntent);
                }

                if (position == 1) {
                    Intent myIntent = new Intent(getApplicationContext(), Albums.class);
                    startActivity(myIntent);
                }

                if (position == 2) {
                    Intent myIntent = new Intent(view.getContext(), Artists.class);
                    startActivityForResult(myIntent, 0);
                }

                if (position == 3) {
                    Intent myIntent = new Intent(getApplicationContext(), Genres.class);
                    startActivity(myIntent);
                }

                if (position == 4) {
                    Intent myIntent = new Intent(view.getContext(), ScanActivity.class);
                    startActivityForResult(myIntent, 0);
                }

                if (position == 5) {
                    Intent myIntent = new Intent(view.getContext(), ScanActivity.class);
                    startActivityForResult(myIntent, 0);
                }


            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



    }

    @Override
    public void onResume() {
        super.onResume();

    }
    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_nowplaying) {

            Intent i = new Intent(this, NowPlayingActivity.class);
            startActivity(i);


        } else if (id == R.id.nav_scanmedia) {

            Intent i = new Intent(this, ScanActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_manage) {


            Intent i = new Intent(this, SettingActivity.class);
            startActivity(i);


        } else if (id == R.id.equalizer) {
            Intent i = new Intent(this, EqualizerActivity.class);
            startActivity(i);


        } else if (id == R.id.nav_share) {


            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "It is here my app. Give it a try! " +
                    " \n http://www.somgemyadav.tk");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.sendto)));


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;


    }





}
