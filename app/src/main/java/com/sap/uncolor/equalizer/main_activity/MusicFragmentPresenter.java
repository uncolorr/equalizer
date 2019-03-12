package com.sap.uncolor.equalizer.main_activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import com.sap.uncolor.equalizer.Apis.Api;
import com.sap.uncolor.equalizer.Apis.ApiResponse;
import com.sap.uncolor.equalizer.Apis.request_bodies.GetVkMusicBody;
import com.sap.uncolor.equalizer.Apis.request_bodies.SearchVkMusicBody;
import com.sap.uncolor.equalizer.Apis.response_models.VKMusicResponseModel;
import com.sap.uncolor.equalizer.Apis.response_models.album_image_model.AlbumImageResponseModel;
import com.sap.uncolor.equalizer.Apis.response_models.album_image_model.ImageInfo;
import com.sap.uncolor.equalizer.application.App;
import com.sap.uncolor.equalizer.application.AppPermissionManager;
import com.sap.uncolor.equalizer.models.BaseMusic;
import com.sap.uncolor.equalizer.models.VkMusic;
import com.sap.uncolor.equalizer.services.download.DownloadService;
import com.sap.uncolor.equalizer.services.download.NewMusicService;
import com.sap.uncolor.equalizer.universal_adapter.MusicAdapter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

public class MusicFragmentPresenter implements MusicFragmentContract.Presenter, ApiResponse.ApiFailureListener {

    private Context context;
    private MusicFragmentContract.View view;
    private Realm realm;


    public MusicFragmentPresenter(Context context, MusicFragmentContract.View view) {
        this.context = context;
        this.view = view;
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onUploadTrack(BaseMusic music) {
        if (!AppPermissionManager.checkIfAlreadyHavePermission(view.getViewActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AppPermissionManager.requestAppPermissions(view.getViewActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    AppPermissionManager.PERMISSION_REQUEST_CODE);
        } else {
            startDownload(music);
        }
    }

    @Override
    public void onFailure(int code, String message) {
        view.hideProgress();
        view.removeLoadMoreProgress();
    }

    @Override
    public void onDeleteTrack(BaseMusic music, int position) {
        view.deleteMusic((VkMusic) music, position);
    }

    private void startDownload(BaseMusic music) {
        App.Log("start Download");
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(DownloadService.ARG_MUSIC, music);
        context.startService(intent);
    }

    @Override
    public void onPlayTrack(BaseMusic music, int adapterPosition) {
        Intent intent = new Intent(context, NewMusicService.class);
        intent.setAction(NewMusicService.ACTION_PLAY);
        intent.putExtra(NewMusicService.ARG_MUSIC, music);
        intent.putParcelableArrayListExtra(NewMusicService.ARG_PLAYLIST, view.getMusic());
        intent.putExtra(NewMusicService.ARG_POSITION, adapterPosition);
        context.startService(intent);
    }

    @Override
    public void onLoadMusic(GetVkMusicBody getVkMusicBody, boolean isRefreshing) {
        if (App.isAuth()) {
            view.showProgress();
            view.addLoadMoreProgress();
            Api.getSource().getVkMusic(App.getToken(),
                    getVkMusicBody.getV(),
                    getVkMusicBody.getOffset(),
                    getVkMusicBody.getCount())
                    .enqueue(ApiResponse.getCallback(getMusicCallback(isRefreshing),
                            this));
        }
    }

    private ApiResponse.ApiResponseListener<VKMusicResponseModel> getMusicCallback(final boolean isRefreshing) {
        return new ApiResponse.ApiResponseListener<VKMusicResponseModel>() {
            @Override
            public void onResponse(VKMusicResponseModel result) throws IOException {
                view.hideProgress();
                view.removeLoadMoreProgress();
                if (result.getError() != null) {
                    switch (result.getError().getErrorCode()) {
                        case 5:
                            view.showFailureMessage();
                            break;
                    }
                }

                if (result.getResponse() != null) {
                    view.setMusicItems(result.getResponse().getItems(), isRefreshing);
                }
            }
        };
    }

    @Override
    public void onSearchMusic(SearchVkMusicBody searchVkMusicBody, int mode, boolean withCaptcha, boolean isRefreshing) {
        App.Log("Search offset: " + searchVkMusicBody.getOffset());
        if (App.isAuth()) {
            view.showProgress();
            switch (mode) {
                case MusicAdapter.MODE_CACHE:
                    RealmResults<VkMusic> results = realm.where(VkMusic.class)
                            .beginGroup()
                            .contains("artist", searchVkMusicBody.getQ().toString(), Case.INSENSITIVE)
                            .or()
                            .contains("title", searchVkMusicBody.getQ().toString(), Case.INSENSITIVE)
                            .endGroup()
                            .findAll();
                    view.setMusicItems(results, true);
                    view.hideProgress();
                    break;
                case MusicAdapter.MODE_ALL_MUSIC:
                    view.addLoadMoreProgress();
                    if (withCaptcha) {
                        Api.getSource().searchVkMusicWithCaptcha(
                                App.getToken(),
                                searchVkMusicBody.getQ(),
                                searchVkMusicBody.getV(),
                                searchVkMusicBody.getOffset(),
                                searchVkMusicBody.getCount(),
                                searchVkMusicBody.getCaptchaSid(),
                                searchVkMusicBody.getCaptchaKey())
                                .enqueue(ApiResponse.getCallback(getSearchMusicCallback(isRefreshing),
                                        this));
                    } else {
                        Api.getSource().searchVkMusic(
                                App.getToken(),
                                searchVkMusicBody.getQ(),
                                searchVkMusicBody.getV(),
                                searchVkMusicBody.getOffset(),
                                searchVkMusicBody.getCount())
                                .enqueue(ApiResponse.getCallback(getSearchMusicCallback(isRefreshing),
                                        this));
                    }
                    break;
            }
        }
    }

    private ApiResponse.ApiResponseListener<VKMusicResponseModel> getSearchMusicCallback(final boolean isRefreshing) {
        return new ApiResponse.ApiResponseListener<VKMusicResponseModel>() {
            @Override
            public void onResponse(VKMusicResponseModel result) throws IOException {
                if (result.getResponse() != null) {
                    App.Log("search not null");
                    view.setMusicItems(result.getResponse().getItems(), isRefreshing);
                } else if (result.getError() != null) {
                    switch (result.getError().getErrorCode()) {
                        case 5:
                            view.showFailureMessage();
                            break;
                        case 14:
                            view.showCaptchaDialog(result.getError(), isRefreshing);

                    }

                }
                view.hideProgress();
                view.removeLoadMoreProgress();
            }
        };
    }

    @Override
    public void onFindAlbumImageUrl(BaseMusic music, int adapterPosition) {
        Api.getSource().getAlbumImage(music.getArtist(), music.getTitle())
                .enqueue(ApiResponse.getCallback(getFindAlbumImageCallback(adapterPosition), this));
    }


    private ApiResponse.ApiResponseListener<AlbumImageResponseModel> getFindAlbumImageCallback(final int position) {
        return new ApiResponse.ApiResponseListener<AlbumImageResponseModel>() {
            @Override
            public void onResponse(AlbumImageResponseModel result) {
                if (result.getTrack() == null) {
                    return;
                }
                if (result.getTrack().getAlbum() != null) {
                    List<ImageInfo> images = result.getTrack().getAlbum().getImages();
                    for (int i = 0; i < images.size(); i++) {
                        if (Objects.equals(images.get(i).getSize(), "extralarge")) {
                            view.setAlbumImageForMusic(images.get(i).getUrl(), position);
                        }
                    }
                }

            }
        };
    }
}
