package com.sap.uncolor.equalizer.main_activity;

import android.app.Activity;

import com.sap.uncolor.equalizer.Apis.request_bodies.GetVkMusicBody;
import com.sap.uncolor.equalizer.Apis.request_bodies.SearchVkMusicBody;
import com.sap.uncolor.equalizer.Apis.response_models.CaptchaErrorResponse;
import com.sap.uncolor.equalizer.models.BaseMusic;
import com.sap.uncolor.equalizer.models.VkMusic;

import java.util.ArrayList;
import java.util.List;

public interface MusicFragmentContract {

    interface View{
        void showProgress();
        void hideProgress();
        void addLoadMoreProgress();
        void removeLoadMoreProgress();
        void showFailureMessage();
        void hideFailureMessage();
        void showReSignInDialog();
        void hideReSignInDialog();
        void setMusicItems(List<VkMusic> items, boolean isRefreshing);
        void deleteMusic(VkMusic music, int position);
        void showErrorToast(String message);
        void showCaptchaDialog(CaptchaErrorResponse captchaErrorResponse, boolean isRefreshing);
        void setAlbumImageForMusic(String url, int position);
        ArrayList<VkMusic> getMusic();
        Activity getViewActivity();
        void hideProcess();
        void showErrorMessage();
        void showProcess();
    }

    interface Presenter {
        void onDeleteTrack(BaseMusic music, int adapterPosition);
        void onUploadTrack(BaseMusic music);

        void onPlayTrack(BaseMusic music, int adapterPosition);
        void onLoadMusic(GetVkMusicBody requestBody, boolean isRefreshing);
        void onSearchMusic(SearchVkMusicBody requestBody, int mode, boolean withCaptcha, boolean isRefreshing);

        void onFindAlbumImageUrl(BaseMusic music, int adapterPosition);
       /* void onLoadMusic(GetVkMusicBody requestBody, boolean isRefreshing);
        void onSearchMusic(SearchVkMusicBody requestBody, int mode, boolean withCaptcha, boolean isRefreshing);
        void onSignInButtonClick(String login, String password);*/

    }
}
