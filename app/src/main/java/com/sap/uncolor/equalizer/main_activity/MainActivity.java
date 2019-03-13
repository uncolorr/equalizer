package com.sap.uncolor.equalizer.main_activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sap.uncolor.equalizer.EqualizerPanelChangeListener;
import com.sap.uncolor.equalizer.EqualizerPanelFragment;
import com.sap.uncolor.equalizer.PlayerPanelChangeListener;
import com.sap.uncolor.equalizer.PlayerPanelFragment;
import com.sap.uncolor.equalizer.PlayerPanelsFragmentAdapter;
import com.sap.uncolor.equalizer.R;
import com.sap.uncolor.equalizer.ViewPagerMusicFragmentAdapter;
import com.sap.uncolor.equalizer.application.App;
import com.sap.uncolor.equalizer.models.BaseMusic;
import com.sap.uncolor.equalizer.services.download.NewMusicService;
import com.sap.uncolor.equalizer.utils.IntentFilterManager;
import com.sap.uncolor.equalizer.widgets.StaticViewPager;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.tabLayoutPlayerPanels)
    TabLayout tabLayoutPlayerPanels;

    @BindView(R.id.viewPagerPanels)
    ViewPager viewPagerPanels;

    @BindView(R.id.viewPager)
    StaticViewPager viewPager;

    @BindView(R.id.bigPlayerPanel)
    LinearLayout bigPlayerPanel;

    @BindView(R.id.playerPanel)
    LinearLayout playerPanel;

    @BindView(R.id.imageViewPanelAlbum)
    RoundedImageView imageViewPanelAlbum;

    @BindView(R.id.textViewPanelArtist)
    TextView textViewPanelArtist;

    @BindView(R.id.textViewPanelSongTitle)
    TextView textViewPanelSongTitle;

    @BindView(R.id.imageButtonPanelPlay)
    ImageButton imageButtonPanelPlay;

    @BindView(R.id.progressBarMusic)
    ProgressBar progressBarMusic;

    @BindView(R.id.adView)
    AdView adView;

    private ViewPagerMusicFragmentAdapter adapter;

    private PlayerPanelsFragmentAdapter playerPanelsAdapter;

    private BottomSheetBehavior<LinearLayout> sheetBehavior;

    private BroadcastReceiver musicReceiver;

    private Runnable musicPositionRunnable;

    private Handler handler;

    private int musicDuration;

    private ServiceConnection serviceConnectionForMusic;

    private NewMusicService newMusicService;

    private boolean isBoundedMusic = false;

    private PlayerPanelFragment playerPanelFragment;

    private EqualizerPanelFragment equalizerPanelFragment;

    private boolean isPlayerPanelLocked = false;


    public static Intent getInstance(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.Log("onCreate main");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playerPanelsAdapter = new PlayerPanelsFragmentAdapter(getSupportFragmentManager());
        playerPanelFragment = playerPanelsAdapter.getPlayerPanelFragment();
        equalizerPanelFragment = playerPanelsAdapter.getEqualizerPanelFragment();

        playerPanelFragment.setCreateViewCallback(new PlayerPanelFragment.OnCreateViewCallback() {
            @Override
            public void onCreateView() {
                bindPlayerState();
            }
        });

        equalizerPanelFragment.setOnCreateViewCallback(new EqualizerPanelFragment.OnCreateViewCallback() {
            @Override
            public void onCreateView() {
                if(isBoundedMusic) {
                    bindEqualizerState();
                }
            }
        });
        playerPanelFragment.setPlayerPanelChangeListener(getPlayPanelChangeListener());
        ButterKnife.bind(this);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        sheetBehavior = BottomSheetBehavior.from(bigPlayerPanel);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    if(isPlayerPanelLocked) {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        hidePlayer();
        hidePlayerPanel();
        adapter = new ViewPagerMusicFragmentAdapter(getSupportFragmentManager());
        viewPagerPanels.setAdapter(playerPanelsAdapter);
        viewPagerPanels.addOnPageChangeListener(getOnPlayerPanelPageChangeListener());
        tabLayoutPlayerPanels.setupWithViewPager(viewPagerPanels);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
        musicReceiver = getMusicReceiver();
        registerReceiver(musicReceiver, IntentFilterManager.getMusicIntentFilter());
        handler = new Handler();
        musicPositionRunnable = getMusicPositionRunnable();

        serviceConnectionForMusic = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                App.Log("onServiceConnected");
                newMusicService = ((NewMusicService.MusicBinder) service).getService();
                isBoundedMusic = true;
                if (playerPanelFragment.getView() == null) {
                    return;
                }
                bindPlayerState();
                if (equalizerPanelFragment.getView() == null) {
                    return;
                }
                bindEqualizerState();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                App.Log("onServiceDisconnected");
                isBoundedMusic = false;
            }
        };
    }

    private void bindEqualizerState() {
        App.Log("Bind Equalizer state");
        setPresetNamesIntoEqualizerPanel();
        if(!equalizerPanelFragment.isBandsConfigured()) {
            equalizerPanelFragment.setBandLevelRange(newMusicService.getEqualizerController().getMinBandLevelRange(),
                    newMusicService.getEqualizerController().getMaxBandLevelRange());
        }
        equalizerPanelFragment.setEqualizerPanelChangeListener(getEqualizerPanelChangeListener());
        equalizerPanelFragment.setEqualizerEnabledState(newMusicService.getEqualizerController().isEnabled());
        if(newMusicService.getEqualizerController().isEnabled()){
            short currentPresetIndex = newMusicService.getEqualizerController().getCurrentPresetIndex();
            equalizerPanelFragment.setCurrentPreset(currentPresetIndex);
            equalizerPanelFragment.setBandLevels(newMusicService.getEqualizerController().getBandLevels());
        }
    }

    private EqualizerPanelChangeListener getEqualizerPanelChangeListener() {
        return new EqualizerPanelChangeListener() {
            @Override
            public void onBandLevelChanged(short band, short level) {
                newMusicService.getEqualizerController().setBandLevel(band, level);
            }

            @Override
            public void onPresetChanged(short preset) {
                newMusicService.getEqualizerController().setPreset(preset);
                newMusicService.getEqualizerController().setCurrentPresetIndex(preset);
                equalizerPanelFragment.setBandLevels(newMusicService.getEqualizerController().getBandLevels());
            }

            @Override
            public void onTurnButtonStateChanged(boolean isEqualizerEnabled) {
                newMusicService.getEqualizerController().setEnabled(isEqualizerEnabled);
                equalizerPanelFragment.setEqualizerEnabledState(isEqualizerEnabled);
            }
        };
    }

    private ViewPager.OnPageChangeListener getOnPlayerPanelPageChangeListener() {
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if(i == 0){
                    isPlayerPanelLocked = false;
                }
                else if(i == 1){
                    isPlayerPanelLocked = true;
                }
                App.Log("onPageSelected: " + i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        };
    }

    private PlayerPanelChangeListener getPlayPanelChangeListener() {
        return new PlayerPanelChangeListener() {
            @Override
            public void onNext() {
                onNextButtonClick();
            }

            @Override
            public void onPrevious() {
                onPreviousButtonClick();
            }

            @Override
            public void onPlay() {
                onPlayButtonClick();
            }

            @Override
            public void onProgressChanged(int position) {
                onPanelProgressChanged(position);
            }

            @Override
            public void onShuffleStateChanged() {
                App.Log("onShuffleStateChanged");
                if(isBoundedMusic) {
                    changeShuffleButtonState(!newMusicService.isShuffling());
                    playerPanelFragment.changeShuffleButtonState(newMusicService.isShuffling());
                }
            }

            @Override
            public void onRepeatStateChanged() {
                App.Log("onRepeatStateChanged");
                if(isBoundedMusic) {
                    changeRepeatButtonState(!newMusicService.isLooping());
                    playerPanelFragment.changeRepeatButtonState(newMusicService.isLooping());
                }
            }
        };
    }

    private void bindPlayerState() {
        App.Log("bindPlayerState");
        if (isBoundedMusic) {
            if (newMusicService.isPlaying()) {
                BaseMusic music = newMusicService.getCurrentMusic();
                App.Log("player playing");
                showPlayerPanel();
                setPauseButtons();
                setSongDescriptions(music);
                setRepeatButtonState(newMusicService.isLooping());
                setShuffleButtonState(newMusicService.isShuffling());
                handler.post(musicPositionRunnable);
            }
        }
    }

    private void setPresetNamesIntoEqualizerPanel(){
        equalizerPanelFragment.setPresetsNames(newMusicService
                .getEqualizerController()
                .getPresetsNames());
    }

    @OnClick(R.id.playerPanel)
    void onPlayerPanelClick() {
        showPlayer();
    }

    @OnClick(R.id.imageButtonHide)
    void onImageButtonHideClick() {
        hidePlayer();
    }


    @OnClick(R.id.imageButtonPanelPlay)
    void onPlayButtonClick() {
        if (isBoundedMusic) {
            if (newMusicService.isPlaying()) {
                newMusicService.pause();
                handler.removeCallbacks(musicPositionRunnable);
                setPlayButtons();
            } else {
                newMusicService.resume();
                handler.post(musicPositionRunnable);
                setPauseButtons();
            }
        }
    }

    @OnClick(R.id.imageButtonPanelNext)
    void onNextButtonClick() {
        if (isBoundedMusic) {
            BaseMusic music = newMusicService.next(true);
            if (music != null) {
                setSongDescriptions(music);
                setPauseButtons();
                setPlaybackPosition(0);
                handler.removeCallbacks(musicPositionRunnable);
                Intent musicIntent = new Intent(NewMusicService.ACTION_NEXT);
                musicIntent.putExtra(NewMusicService.ARG_MUSIC, music);
                sendBroadcast(musicIntent);
            }
        }
    }

    private void onPreviousButtonClick(){
          if (isBoundedMusic) {
            BaseMusic music = newMusicService.previous();
            setSongDescriptions(music);
            setPauseButtons();
            setPlaybackPosition(0);
            handler.removeCallbacks(musicPositionRunnable);
            Intent musicIntent = new Intent(NewMusicService.ACTION_PREVIOUS);
            musicIntent.putExtra(NewMusicService.ARG_MUSIC, music);
            App.getContext().sendBroadcast(musicIntent);
        }
    }


    private void onPanelProgressChanged(int progress) {
            setPlaybackPosition(progress);
            newMusicService.seekTo(progress);
    }


    private boolean isPlayerPanelHidden() {
        return sheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN;
    }

    private void showPlayer() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void hidePlayer() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    @Override
    public void onBackPressed() {
        if (!isPlayerPanelHidden()) {
            hidePlayer();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(musicReceiver);
    }

    private Runnable getMusicPositionRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                if (isBoundedMusic) {
                    int currentPlaybackPosition = newMusicService.getCurrentPlaybackPosition();
                    progressBarMusic.setProgress(currentPlaybackPosition);
                    setPlaybackPosition(currentPlaybackPosition);
                    if (currentPlaybackPosition < musicDuration) {
                        handler.postDelayed(musicPositionRunnable, 1000);
                    }
                }
            }
        };
    }

    private BroadcastReceiver getMusicReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                App.Log("onReceive");
                if (intent == null) {
                    App.Log("intent null");
                }
                String action = intent.getAction();
                App.Log("point");
                if (Objects.equals(action, NewMusicService.ACTION_PLAY)) {
                    App.Log("On Action play");
                    Intent serviceIntent = new Intent(MainActivity.this, NewMusicService.class);
                    bindService(serviceIntent, serviceConnectionForMusic, 0);
                    BaseMusic music = intent.getParcelableExtra(NewMusicService.ARG_MUSIC);
                    setSongDescriptions(music);
                    setPauseButtons();
                    setPlaybackPosition(0);
                    showPlayerPanel();
                    handler.removeCallbacks(musicPositionRunnable);
                }

                if (Objects.equals(action, NewMusicService.ACTION_PAUSE_OR_RESUME)) {
                    App.Log("new pause resume");
                    changePlayingState();
                }

                if (Objects.equals(action, NewMusicService.ACTION_NEXT)) {
                    App.Log("On Action next");
                    BaseMusic music = intent.getParcelableExtra("music");
                    setSongDescriptions(music);
                    setPauseButtons();
                    setPlaybackPosition(0);
                    handler.removeCallbacks(musicPositionRunnable);
                }

                if (Objects.equals(action, NewMusicService.ACTION_PREVIOUS)) {
                    App.Log("On Action previous");
                    BaseMusic music = intent.getParcelableExtra("music");
                    setSongDescriptions(music);
                    setPauseButtons();
                    setPlaybackPosition(0);
                    handler.removeCallbacks(musicPositionRunnable);
                }

                if (Objects.equals(action, NewMusicService.ACTION_BEGIN_PLAYING)) {
                    App.Log("On Action begin playing");
                    handler.removeCallbacks(musicPositionRunnable);
                    setPauseButtons();
                    setPlaybackPosition(0);
                    handler.post(musicPositionRunnable);
                }

                if (Objects.equals(action, NewMusicService.ACTION_CLOSE)) {
                    App.Log("on Action close");
                    handler.removeCallbacks(musicPositionRunnable);
                    hidePlayerPanel();
                }

            }
        };
    }

    private void changePlayingState() {
        if (isBoundedMusic) {
            if (newMusicService.isPlaying()) {
                App.Log("change playing");
                handler.post(musicPositionRunnable);
                setPauseButtons();
            } else {
                App.Log("change not playing");
                handler.removeCallbacks(musicPositionRunnable);
                setPlayButtons();
            }
        }
    }

    private void changeRepeatButtonState(boolean isLooping) {
        if (isBoundedMusic) {
            newMusicService.setLooping(isLooping);
            playerPanelFragment.changeRepeatButtonState(isLooping);

        }

    }

    private void setRepeatButtonState(boolean isLooping) {
        if (isBoundedMusic) {
            playerPanelFragment.changeRepeatButtonState(isLooping);

        }

    }

    private void changeShuffleButtonState(boolean isShuffling) {
        if(isBoundedMusic) {
            if(newMusicService.isShuffling()){
                newMusicService.unshuffle();
            }else {
                newMusicService.shuffle();
            }
            playerPanelFragment.changeShuffleButtonState(isShuffling);
        }
    }

    private void setShuffleButtonState(boolean isShuffling) {
        if(isBoundedMusic) {
            playerPanelFragment.changeShuffleButtonState(isShuffling);
        }
    }

    private void showPlayerPanel() {
        progressBarMusic.setVisibility(View.VISIBLE);
        playerPanel.setVisibility(View.VISIBLE);
    }

    private void hidePlayerPanel() {
        progressBarMusic.setVisibility(View.GONE);
        playerPanel.setVisibility(View.GONE);
    }

    private void setPlayButtons() {
        imageButtonPanelPlay.setImageResource(R.drawable.play);
        playerPanelFragment.setPlayButtons();
    }

    private void setPauseButtons() {
        imageButtonPanelPlay.setImageResource(R.drawable.pause);
        playerPanelFragment.setPauseButton();
    }

    private void setSongDescriptions(BaseMusic music) {
        textViewPanelSongTitle.setText(music.getTitle());
        textViewPanelArtist.setText(music.getArtist());
        musicDuration = music.getDuration();
        playerPanelFragment.setSongDescriptions(music);
        setDurationForBars(music.getDuration());
        setAlbumImage(music.getAlbumImageUrl());
    }

    private void setPlaybackPosition(int playbackPosition) {
        progressBarMusic.setProgress(playbackPosition);
        playerPanelFragment.setProgress(playbackPosition);
    }

    public void setAlbumImage(String url) {
        if (url == null) {
            playerPanelFragment.setDefaultAlbumImage();
            imageViewPanelAlbum.setImageResource(R.drawable.album_default);
            return;
        }
        playerPanelFragment.uploadAlbumImage(url);
        Glide.with(this).load(url).into(imageViewPanelAlbum);


    }

    public void setDurationForBars(int duration) { //
        playerPanelFragment.setDurationForBars(duration);
        progressBarMusic.setMax(duration);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent serviceIntent = new Intent(this, NewMusicService.class);
        bindService(serviceIntent, serviceConnectionForMusic, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isBoundedMusic) return;
        unbindService(serviceConnectionForMusic);
        isBoundedMusic = false;
        handler.removeCallbacks(musicPositionRunnable);
    }


}
