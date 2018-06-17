package com.example.somnath.mymusic;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.somnath.mymusic.adapters.TabPagerAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //This is our tablayout
    private TabLayout tabLayout;
    private Menu menu;
    private NavigationView navigationView;
    private MyMusicService mBoundService;
    private Context context;
    private boolean mIsBound;
    private View view;
    private TextView name_song_main, album_song_main;
    private ImageView album_img;
    private ArrayList<Song> listplay;
    private LinearLayout nav_header;
    private int cur_song;

    //This is our viewPager
    private ViewPager viewPager;
    //mconnection

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((MyMusicService.LocalBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
        }
    };

    @Override
    @TargetApi(23)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        view = (View) findViewById(R.id.viewnowPlaying);
        name_song_main = (TextView) findViewById(R.id.name_song_main);
        album_song_main = (TextView) findViewById(R.id.name_album_main);
        album_img = (ImageView) findViewById(R.id.img_main_activiy);
        nav_header = (LinearLayout) findViewById(R.id.nav_header);


        listplay = new ArrayList<Song>();

        startService(new Intent(MainActivity.this, MyMusicService.class));
        doBindService();

        //Adding toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //drawerlayout started
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Current playing on mainactivity
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, NowPlayingActivity.class);
                if (mBoundService.getTracklist().size() > 0) {
                    if (mBoundService.getStatus() == 1)
                        i.putExtra("from_main_playing", 2);
                    else
                        i.putExtra("from_main_not", 3);
                } else
                    i.putExtra("empty_list", 4);


                startActivity(i);
            }
        });

        /*nav_header click

        nav_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);

            }
        }); */

        //Initializing the tablayout
        viewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);


        //navigation view
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //service started

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new AllSongsFragment(), "SONGS");
        adapter.addFrag(new AlbumsFragment(), "ALBUMS");
        adapter.addFrag(new ArtistsFragment(), "ARTISTS");
        adapter.addFrag(new GenresFragment(), "GENRES");
        adapter.addFrag(new AllSongsFragment(), "PLAYLISTS");

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public void onResume() {
        super.onResume();
        DbNowplaying dbOpen = new DbNowplaying(context);
        SQLiteDatabase db = dbOpen.getReadableDatabase();
        Cursor c2 = db.query(DbNowplaying.TABLE_NAME, null, null, null, null, null, null);
        listplay.clear();

        while (c2.moveToNext()) {
            listplay.add(new Song(c2.getLong(0), c2.getString(1), c2.getString(2), c2.getString(3), c2.getString(4), c2.getString(5)));
        }


        c2.close();


        if (mBoundService != null)
            Toast.makeText(context, String.valueOf(mBoundService.getTracklist().size()), Toast.LENGTH_SHORT).show();

        Cursor cursor2 = db.query(DbNowplaying.TABLE2, null, null, null, null, null, null);

        while (cursor2.moveToNext()) {
            cur_song = cursor2.getInt(1);
        }


        cursor2.close();


        if (listplay.size() > 0) {
            name_song_main.setText(listplay.get(cur_song).getTitle());
            album_song_main.setText(listplay.get(cur_song).getArtist());

            String image =listplay.get(cur_song).getImg_Id();
            Bitmap bm = BitmapFactory.decodeFile(image);
            album_img.setImageBitmap(bm);
        }

        dbOpen.close();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);

        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_nowplaying) {
            Intent i = new Intent(MainActivity.this, NowPlayingActivity.class);

            if (mBoundService.getTracklist().size() > 0) {
                if (mBoundService.getStatus() == 1)
                    i.putExtra("from_main_playing", 2);
                else
                    i.putExtra("from_main_not", 3);
            } else
                i.putExtra("empty_list", 4);
            startActivity(i);

            return true;


        } else if (id == R.id.nav_scanmedia) {

            Intent i = new Intent(this, ScanActivity.class);
            startActivity(i);
            return true;

        } else if (id == R.id.nav_manage) {
            Intent i = new Intent(this, SettingActivity.class);
            startActivity(i);
            return true;

        } else if (id == R.id.equalizer) {
            Intent i = new Intent(this, EqualizerActivity.class);
            startActivity(i);
            return true;


        } else if (id == R.id.nav_share) {


            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "It is here my app. Give it a try! " +
                    " \n http://www.somgemyadav.tk");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.sendto)));
            return true;


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //service stopped
    }


    private void doBindService() {
        Intent i = new Intent(MainActivity.this, MyMusicService.class);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private void doUnbindService() {
        if (mIsBound) {   // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }


}
