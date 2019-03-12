package com.sap.uncolor.equalizer.utils;

import android.app.DownloadManager;
import android.content.IntentFilter;

import com.sap.uncolor.equalizer.main_activity.SettingsFragment;
import com.sap.uncolor.equalizer.services.download.DownloadService;
import com.sap.uncolor.equalizer.services.download.NewMusicService;


/**
 * Created by Uncolor on 10.09.2018.
 */

public class IntentFilterManager {
    public static IntentFilter getMusicIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NewMusicService.ACTION_PLAY);
        intentFilter.addAction(NewMusicService.ACTION_NEXT);
        intentFilter.addAction(NewMusicService.ACTION_PREVIOUS);
        intentFilter.addAction(NewMusicService.ACTION_PAUSE_OR_RESUME);
        intentFilter.addAction(NewMusicService.ACTION_TRACK_DELETED);
        intentFilter.addAction(NewMusicService.ACTION_BEGIN_PLAYING);
        intentFilter.addAction(NewMusicService.ACTION_CLOSE);
        intentFilter.addAction(NewMusicService.ACTION_MUSIC_PLAYER_STARTED);
        intentFilter.addAction(DownloadService.ACTION_DOWNLOAD_FAILURE);
        intentFilter.addAction(DownloadService.ACTION_DOWNLOAD_COMPLETED);
        intentFilter.addAction(DownloadService.ACTION_DOWNLOAD_STARTED);
        intentFilter.addAction(SettingsFragment.ACTION_CLEAR_CACHE);
        return intentFilter;
    }

    public static IntentFilter getDownloadIntentFilter(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        return intentFilter;
    }
}
