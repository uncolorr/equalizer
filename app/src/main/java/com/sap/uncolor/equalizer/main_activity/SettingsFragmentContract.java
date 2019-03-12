package com.sap.uncolor.equalizer.main_activity;

public interface SettingsFragmentContract {

    interface View{
        void showUserInfo(String name, String avatarUrl);
        void showToast(String message);
        void logOut();
    }

    interface Presenter {
        void onLoadUserInfo();
        void onClearCache();
        void showClearCacheDialog();
        void showExitDialog();
    }
}