package com.sap.uncolor.equalizer.main_activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sap.uncolor.equalizer.Apis.ApiResponse;
import com.sap.uncolor.equalizer.Apis.request_bodies.GetVkMusicBody;
import com.sap.uncolor.equalizer.Apis.request_bodies.SearchVkMusicBody;
import com.sap.uncolor.equalizer.Apis.response_models.CaptchaErrorResponse;
import com.sap.uncolor.equalizer.R;
import com.sap.uncolor.equalizer.application.App;
import com.sap.uncolor.equalizer.database.DbManager;
import com.sap.uncolor.equalizer.models.BaseMusic;
import com.sap.uncolor.equalizer.models.VkMusic;
import com.sap.uncolor.equalizer.services.download.DownloadService;
import com.sap.uncolor.equalizer.services.download.NewMusicService;
import com.sap.uncolor.equalizer.universal_adapter.MusicAdapter;
import com.sap.uncolor.equalizer.universal_adapter.OnLoadMoreListener;
import com.sap.uncolor.equalizer.utils.IntentFilterManager;
import com.sap.uncolor.equalizer.utils.LoadingDialog;
import com.sap.uncolor.equalizer.utils.MessageReporter;
import com.sap.uncolor.equalizer.widgets.CaptchaDialog;
import com.sap.uncolor.equalizer.widgets.ResignInDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sap.uncolor.equalizer.services.download.NewMusicService.ARG_MUSIC;

public class MusicFragment extends Fragment implements MusicFragmentContract.View,
        ApiResponse.ApiFailureListener{

    public static final String ARG_MODE = "mode";

    public static final int MODE_ONLINE_TRACKS = 1;
    public static final int MODE_OFFLINE_TRACKS = 2;

    @BindView(R.id.editTextSearch)
    EditText editTextSearch;

    @BindView(R.id.recyclerViewTracks)
    RecyclerView recyclerViewTracks;

    @BindView(R.id.progressBarLoading)
    ProgressBar progressBarLoading;

    @BindView(R.id.linearLayoutFailure)
    LinearLayout linearLayoutFailure;

    private BroadcastReceiver musicReceiver;

    private int mode;

    private MusicAdapter<VkMusic> adapter;

    private DbManager dbManager;

    private SearchVkMusicBody searchVkMusicBody;

    private Runnable searchRunnable;

    private MusicFragmentPresenter presenter;

    private GetVkMusicBody getVkMusicBody;

    private AlertDialog dialogProcessing;

    private ResignInDialog resignInDialog;

    private CaptchaDialog captchaDialog;



    public static MusicFragment newInstance(int mode) {
        App.Log("init music fragment");
        Bundle args = new Bundle();
        MusicFragment fragment = new MusicFragment();
        args.putInt(ARG_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        App.Log("onCreateView music");
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        ButterKnife.bind(this, view);
        if(getArguments() != null) {
            mode = getArguments().getInt(ARG_MODE);
            initFragment();
        }
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.Log("onCreate music");
    }

    private void initFragment(){
        App.Log("init fragment music");
        presenter = new MusicFragmentPresenter(getContext(), this);
        searchVkMusicBody = new SearchVkMusicBody();
        getVkMusicBody = new GetVkMusicBody();
        adapter = new MusicAdapter<>(presenter);
        adapter.setMode(mode);
        adapter.setOnLoadMoreListener(getOnLoadMoreListener());
        dialogProcessing = LoadingDialog.newInstanceWithoutCancelable(getContext(), LoadingDialog.LABEL_LOADING);
        recyclerViewTracks.setAdapter(adapter);
        recyclerViewTracks.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        recyclerViewTracks.addOnScrollListener(adapter.getScrollListener());
        editTextSearch.addTextChangedListener(getTempTextWatcher());
        musicReceiver = getMusicReceiver();
        getContext().registerReceiver(musicReceiver, IntentFilterManager.getMusicIntentFilter());
        switch (mode){
            case MODE_ONLINE_TRACKS:
                GetVkMusicBody getVkMusicBody = new GetVkMusicBody();
                presenter.onLoadMusic(getVkMusicBody, true);
                //upload from server
                break;

            case MODE_OFFLINE_TRACKS:
                dbManager = new DbManager();
                List<VkMusic> cacheMusic;
                cacheMusic = dbManager.findAll();
                addList(cacheMusic);
                hideProgress();
                break;
        }
        searchRunnable = getSearchRunnable();
    }


    @Override
    public void onFailure(int code, String message) {
        //show error message
    }



    private void addList(List<VkMusic> musicList){
        adapter.add(musicList);
    }

    @Override
    public ArrayList<VkMusic> getMusic() {
        ArrayList<VkMusic> musics = new ArrayList<>();
        for (int i = 0; i <adapter.getItems().size() ; i++) {
            musics.add((VkMusic) adapter.getItems().get(i));
        }
        return musics;
    }


    @Override
    public void showProcess() {
        if(adapter.getItemCount() == 0) {
            dialogProcessing.show();
        }
    }

    @Override
    public void hideProcess() {
        dialogProcessing.dismiss();
    }

    @Override
    public void showErrorMessage() {
        MessageReporter.showMessage(getContext(), "Ошибка", "Ошибка при авторизации");
    }

    private Runnable getSearchRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                if (searchVkMusicBody.getQ().length() == 0) {
                    switch (adapter.getMode()) {
                        case MusicAdapter.MODE_CACHE:
                            searchVkMusicBody.resetOffset();
                            presenter.onSearchMusic(searchVkMusicBody,
                                    adapter.getMode(), false, true);
                            break;
                        case MusicAdapter.MODE_ALL_MUSIC:
                            getVkMusicBody = new GetVkMusicBody();
                            presenter.onLoadMusic(getVkMusicBody, true);
                            break;
                    }
                } else {
                    switch (adapter.getMode()) {
                        case MusicAdapter.MODE_CACHE:
                           List<VkMusic> cachedTracks = dbManager.search(editTextSearch.getText().toString());
                           adapter.clear();
                           adapter.add(cachedTracks);
                            break;
                        case MusicAdapter.MODE_ALL_MUSIC:
                            searchVkMusicBody.resetOffset();
                            presenter.onSearchMusic(searchVkMusicBody,
                                    adapter.getMode(), false, true);
                            break;
                    }
                }
            }
        };
    }

    private TextWatcher getTempTextWatcher() {
        return new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence q, int start, int before, int count) {
                searchVkMusicBody.setQ(q);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            private Timer timer = new Timer();
            private final long DELAY = 500; // milliseconds

            @Override
            public void afterTextChanged(final Editable s) {
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(searchRunnable);
                                }
                            }
                        }, DELAY);
            }
        };

    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.buttonResignIn)
    void onResignInButtonClick(){
        resignInDialog.show();
    }

    private BroadcastReceiver getMusicReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                App.Log("on fragment receive");
                String action = intent.getAction();
                if (Objects.equals(action, DownloadService.ACTION_DOWNLOAD_STARTED)) {
                    BaseMusic music = intent.getParcelableExtra(DownloadService.ARG_MUSIC);
                    adapter.startDownloadMusic(music);
                } else if (Objects.equals(action, DownloadService.ACTION_DOWNLOAD_COMPLETED)) {
                    BaseMusic music = intent.getParcelableExtra(DownloadService.ARG_MUSIC);
                    adapter.completeDownloadMusic(music);
                }else if (Objects.equals(action, DownloadService.ACTION_DOWNLOAD_FAILURE)) {
                    BaseMusic music = intent.getParcelableExtra(DownloadService.ARG_MUSIC);
                    adapter.setDefaultMusicState(music);
                    showErrorToast("Ошибка при скачивании трека");
                } else if (Objects.equals(action, NewMusicService.ACTION_CLOSE)) {
                    adapter.unselectCurrentTrack();
                } else if (Objects.equals(action, SettingsFragment.ACTION_CLEAR_CACHE)) {
                    adapter.checkCache();
                } else if (Objects.equals(action, NewMusicService.ACTION_TRACK_DELETED)) {
                    BaseMusic music = intent.getParcelableExtra(NewMusicService.ARG_MUSIC);
                    adapter.changeOnlineTrackStateAfterDelete(music);

                } else {
                    BaseMusic music = intent.getParcelableExtra(ARG_MUSIC);
                    if (music == null) {
                        return;
                    }
                    adapter.changeCurrentMusic(music);
                }
            }
        };
    }

    @Override
    public void showProgress() {
        progressBarLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBarLoading.setVisibility(View.GONE);
    }

    @Override
    public void addLoadMoreProgress() {
        adapter.addLoadingItem();
    }

    @Override
    public void removeLoadMoreProgress() {
        adapter.removeLoadingItem();
    }

    @Override
    public void showFailureMessage() {
        linearLayoutFailure.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideFailureMessage() {
        linearLayoutFailure.setVisibility(View.GONE);
        resignInDialog.clear();
    }

    @Override
    public void showReSignInDialog() {
        resignInDialog.show();
    }

    @Override
    public void hideReSignInDialog() {
        resignInDialog.dismiss();
    }

    @Override
    public void setMusicItems(List<VkMusic> items, boolean isRefreshing) {
        if (isRefreshing) {
            adapter.clear();
        }
        adapter.add(items);
        adapter.setLoaded();
    }

    @Override
    public void deleteMusic(VkMusic music, int position) {
        Intent musicIntent = new Intent(NewMusicService.ACTION_TRACK_DELETED);
        musicIntent.putExtra(ARG_MUSIC, music);
        App.getContext().sendBroadcast(musicIntent);
        adapter.deleteTrack(music, position);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getContext() != null) {
            getContext().unregisterReceiver(musicReceiver);
        }
    }

    @Override
    public void showErrorToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showCaptchaDialog(CaptchaErrorResponse captchaErrorResponse, boolean isRefreshing) {
        captchaDialog = new CaptchaDialog(getContext(), captchaErrorResponse);
        captchaDialog.setOnSendClickListener(getOnSendCaptchaClickListener(captchaErrorResponse, isRefreshing));
        captchaDialog.show();
    }

    @Override
    public void setAlbumImageForMusic(String url, int position) {
        adapter.setAlbumImageUrl(url, position);
    }

    private OnLoadMoreListener getOnLoadMoreListener() {
        return new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                App.Log("onLoadMore");
                if (editTextSearch.getText().toString().isEmpty()) {
                    getVkMusicBody.setOffset(adapter.getMusicItemsCount());
                    presenter.onLoadMusic(getVkMusicBody, false);
                } else {
                    searchVkMusicBody.setOffset(adapter.getMusicItemsCount());
                    presenter.onSearchMusic(searchVkMusicBody, adapter.getMode(),
                            false, false);
                }
            }
        };
    }

    private View.OnClickListener getOnSendCaptchaClickListener(final CaptchaErrorResponse captchaErrorResponse,
                                                               final boolean isRefreshing) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!captchaDialog.getCaptcha().isEmpty()) {
                    searchVkMusicBody.setCaptchaKey(captchaDialog.getCaptcha());
                    searchVkMusicBody.setCaptchaSid(captchaErrorResponse.getCaptchaSID());
                    presenter.onSearchMusic(searchVkMusicBody, adapter.getMode(),
                            true, isRefreshing);
                    captchaDialog.dismiss();
                }
            }
        };
    }


    @Override
    public Activity getViewActivity() {
        return getActivity();
    }
}

