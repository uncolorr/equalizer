package com.sap.uncolor.equalizer;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sap.uncolor.equalizer.main_activity.MusicFragment;
import com.sap.uncolor.equalizer.main_activity.SettingsFragment;

public class ViewPagerMusicFragmentAdapter extends FragmentPagerAdapter {

    private static final int FRAGMENT_COUNT = 3;

    private String[] titles = {"Музыка", "Скачанная", "Настройки"};

    public ViewPagerMusicFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                return MusicFragment.newInstance(MusicFragment.MODE_ONLINE_TRACKS);
            case 1:
                return MusicFragment.newInstance(MusicFragment.MODE_OFFLINE_TRACKS);
            case 2:
                return SettingsFragment.newInstance();
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
}
