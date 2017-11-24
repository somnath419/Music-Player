package com.example.somnath.mymusic.adapters;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.somnath.mymusic.AlbumsFragment;
import com.example.somnath.mymusic.AllSongsFragment;
import com.example.somnath.mymusic.ArtistsFragment;
import com.example.somnath.mymusic.GenresFragment;


//Extending FragmentStatePagerAdapter
public class TabPagerAdapter extends FragmentStatePagerAdapter {

    //integer to count number of tabs
   private int tabCount;

    //Constructor to the class
    public TabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position)
        {
            case 0:
                return new AllSongsFragment();
        }

                return new AlbumsFragment();
        }


    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }
}