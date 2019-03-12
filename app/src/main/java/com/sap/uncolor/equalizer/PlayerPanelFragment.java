package com.sap.uncolor.equalizer;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sap.uncolor.equalizer.application.App;
import com.sap.uncolor.equalizer.models.BaseMusic;
import com.sap.uncolor.equalizer.utils.DurationConverter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayerPanelFragment extends Fragment implements SeekBar.OnSeekBarChangeListener{

    @BindView(R.id.imageButtonPlayerPlay)
    ImageButton imageButtonPlayerPlay;

    @BindView(R.id.textViewPlayerSongTitle)
    TextView textViewPlayerSongTitle;

    @BindView(R.id.textViewPlayerArtist)
    TextView textViewPlayerArtist;

    @BindView(R.id.imageButtonShuffle)
    ImageButton imageButtonShuffle;

    @BindView(R.id.imageButtonRepeat)
    ImageButton imageButtonRepeat;

    @BindView(R.id.seekBar)
    SeekBar seekBar;

    @BindView(R.id.imageViewMusicPlate)
    ImageView imageViewMusicPlate;

    @BindView(R.id.textViewDuration)
    TextView textViewDuration;

    @BindView(R.id.textViewCurrentPosition)
    TextView textViewCurrentPosition;

    private PlayerPanelChangeListener playerPanelChangeListener;

    protected OnCreateViewCallback createViewCallback;

    public static PlayerPanelFragment newInstance() {
        Bundle args = new Bundle();
        PlayerPanelFragment fragment = new PlayerPanelFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_panel, container, false);
        ButterKnife.bind(this, view);
        seekBar.setOnSeekBarChangeListener(this);
        if (createViewCallback != null) {
            createViewCallback.onCreateView();
        }
        return view;

    }

    @OnClick(R.id.imageButtonPlayerNext)
    void onNextButtonClick() {
        if(playerPanelChangeListener != null) {
            playerPanelChangeListener.onNext();
        }
    }

    @OnClick(R.id.imageButtonPlayerPrevious)
    void onPreviousButtonClick() {
        if(playerPanelChangeListener != null) {
            playerPanelChangeListener.onPrevious();
        }
    }

    @OnClick(R.id.imageButtonPlayerPlay)
    void onPlayButtonClick(){
        if(playerPanelChangeListener != null) {
            playerPanelChangeListener.onPlay();
        }
    }

    @OnClick(R.id.imageButtonShuffle)
    void onShuffleButtonClick(){
        playerPanelChangeListener.onShuffleStateChanged();
    }

    @OnClick(R.id.imageButtonRepeat)
    void onRepeatButtonClick(){
        playerPanelChangeListener.onRepeatStateChanged();
    }

    public void setPlayButtons() {
        imageButtonPlayerPlay.setImageResource(R.drawable.play);

    }

    public void setPauseButton() {
        imageButtonPlayerPlay.setImageResource(R.drawable.pause);
    }

    public void setSongDescriptions(BaseMusic music) {
        textViewPlayerSongTitle.setText(music.getTitle());
        textViewPlayerArtist.setText(music.getArtist());
        textViewDuration.setText(DurationConverter.getDurationFormat(music.getDuration()));
        setDurationForBars(music.getDuration());
        setAlbumImage(music.getAlbumImageUrl());
    }

    public void setAlbumImage(String url) {
        Glide.with(this).load(url).into(imageViewMusicPlate);
    }

    public void setDurationForBars(int duration) {
        seekBar.setMax(duration);
    }

    public void changeRepeatButtonState(boolean isLooping) {
        if (isLooping) {
            imageButtonRepeat.setBackground(ContextCompat.getDrawable(App.getContext(),
                    R.drawable.button_repeat_activated));
            imageButtonRepeat.setColorFilter(ContextCompat.getColor(
                    App.getContext(),
                    android.R.color.white),
                    PorterDuff.Mode.SRC_IN);
        } else {
            imageButtonRepeat.setBackground(ContextCompat.getDrawable(App.getContext(),
                    R.drawable.button_repeat_not_activated));
            imageButtonRepeat.setColorFilter(ContextCompat.getColor(
                    App.getContext(),
                    R.color.colorMain),
                    PorterDuff.Mode.SRC_IN);
        }
    }

    public void changeShuffleButtonState(boolean isShuffling) {
        if (isShuffling) {
            imageButtonShuffle.setBackground(ContextCompat.getDrawable(App.getContext(),
                    R.drawable.button_repeat_activated));
            imageButtonShuffle.setColorFilter(ContextCompat.getColor(
                    App.getContext(),
                    android.R.color.white),
                    PorterDuff.Mode.SRC_IN);
        } else {
            imageButtonShuffle.setBackground(ContextCompat.getDrawable(App.getContext(),
                    R.drawable.button_repeat_not_activated));
            imageButtonShuffle.setColorFilter(ContextCompat.getColor(
                    App.getContext(),
                    R.color.colorMain),
                    PorterDuff.Mode.SRC_IN);
        }
    }

    public void setProgress(int playbackPosition) {
        seekBar.setProgress(playbackPosition);
        textViewCurrentPosition.setText(DurationConverter.getDurationFormat(playbackPosition));
    }

    public void setDefaultAlbumImage() {
        imageViewMusicPlate.setImageResource(R.drawable.album_default);
    }

    public void uploadAlbumImage(String url) {
        Glide.with(App.getContext()).load(url).into(imageViewMusicPlate);
    }

    public void setCreateViewCallback(OnCreateViewCallback createViewCallback) {
        this.createViewCallback = createViewCallback;
    }

    public void setPlayerPanelChangeListener(PlayerPanelChangeListener playerPanelChangeListener) {
        this.playerPanelChangeListener = playerPanelChangeListener;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser){
            if(playerPanelChangeListener != null) {
                App.Log("progress changed");
                playerPanelChangeListener.onProgressChanged(progress);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    public interface OnCreateViewCallback {
        void onCreateView();
    }

}
