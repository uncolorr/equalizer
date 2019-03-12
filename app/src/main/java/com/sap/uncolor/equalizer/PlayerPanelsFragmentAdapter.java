package com.sap.uncolor.equalizer;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PlayerPanelsFragmentAdapter extends FragmentPagerAdapter {

    private static final int FRAGMENT_COUNT = 2;

    private String[] titles = {"Плеер", "Эквалайзер"};

    private PlayerPanelFragment playerPanelFragment;
    private EqualizerPanelFragment equalizerPanelFragment;


    public PlayerPanelsFragmentAdapter(FragmentManager fm) {
        super(fm);
        playerPanelFragment = PlayerPanelFragment.newInstance();
        equalizerPanelFragment = EqualizerPanelFragment.newInstance();
    }


    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                return playerPanelFragment;
            case 1:
                return equalizerPanelFragment;
        }
        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    public PlayerPanelFragment getPlayerPanelFragment() {
        return playerPanelFragment;
    }

    public EqualizerPanelFragment getEqualizerPanelFragment() {
        return equalizerPanelFragment;
    }
}
