package com.example.somnath.mymusic.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.example.somnath.mymusic.AlbumsFragment;
import com.example.somnath.mymusic.AllSongsFragment;
import com.example.somnath.mymusic.ArtistsFragment;


//Extending FragmentStatePagerAdapter
public class TabPagerAdapter extends FragmentPagerAdapter {

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

        Fragment fragment=null;
        switch (position)
        {
            case 0:
                fragment= new AllSongsFragment();
                break;
            case 1:
                fragment= new AlbumsFragment();
                break;
            case 2:
                fragment= new AlbumsFragment();
                break;
            case 3:
                fragment= new AllSongsFragment();
                break;
            case 4:
                fragment= new AllSongsFragment();
                break;
        }
        return fragment;

    }


    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }
}