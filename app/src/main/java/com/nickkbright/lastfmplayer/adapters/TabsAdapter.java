package com.nickkbright.lastfmplayer.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nickkbright.lastfmplayer.fragments.PlayerFragment;
import com.nickkbright.lastfmplayer.fragments.ProfileFragment;

import java.util.ArrayList;
import java.util.List;

public class TabsAdapter extends FragmentPagerAdapter {

    private int mTabNumber;
    private ProfileFragment profileFragment;
    private PlayerFragment playerFragment;

    public TabsAdapter(FragmentManager fm, int tabNumber) {
        super(fm);
        this.mTabNumber = tabNumber;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                this.profileFragment = new ProfileFragment();
                return this.profileFragment;
            case 1:
                this.playerFragment = new PlayerFragment();
                return this.playerFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return this.mTabNumber;
    }
}
