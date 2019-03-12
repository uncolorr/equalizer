package com.sap.uncolor.equalizer.universal_adapter;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sap.uncolor.equalizer.R;
import com.sap.uncolor.equalizer.application.App;
import com.sap.uncolor.equalizer.main_activity.MusicFragmentPresenter;
import com.sap.uncolor.equalizer.models.BaseMusic;
import com.sap.uncolor.equalizer.utils.DurationConverter;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VkMusicViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.textViewSongTitle)
    TextView textViewSongTitle;

    @BindView(R.id.textViewArtist)
    TextView textViewArtist;

    @BindView(R.id.textViewTotalTime)
    TextView textViewTotalTime;

    @BindView(R.id.imageButtonDownload)
    ImageButton imageButtonDownload;

    @BindView(R.id.imageViewDownloaded)
    ImageView imageViewDownloaded;

    @BindView(R.id.imageViewAlbum)
    ImageView imageViewAlbum;

    @BindView(R.id.progressBarDownloading)
    ProgressBar progressBarDownloading;

    @BindView(R.id.linearLayoutBackground)
    LinearLayout linearLayoutBackground;

    private MusicFragmentPresenter presenter;

    private BaseMusic music;

    public VkMusicViewHolder(@NonNull View itemView, MusicFragmentPresenter presenter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.presenter = presenter;
    }

    @OnClick(R.id.imageButtonDownload)
    void onImageButtonDownloadClick(){
        if(music.getState() == BaseMusic.STATE_COMPLETED){
             presenter.onDeleteTrack(music, getAdapterPosition());
        }
        else if(music.getState() == BaseMusic.STATE_DEFAULT) {
                presenter.onUploadTrack(music);
        }
    }

    @OnClick(R.id.linearLayoutBackground)
    void onPlayTrackClick(){
         presenter.onPlayTrack(music, getAdapterPosition());
    }

    public void bind(BaseMusic music, BaseMusic currentMusic) {
        this.music = music;
        textViewSongTitle.setText(music.getTitle());
        textViewArtist.setText(music.getArtist());
        textViewTotalTime.setText(DurationConverter.getDurationFormat(music.getDuration()));

        bindTrackSelection(currentMusic);
        bindState();
        if(this.music.getAlbumImageUrl() == null) {
            imageViewAlbum.setImageResource(R.drawable.album_default);
            presenter.onFindAlbumImageUrl(this.music, getAdapterPosition());
        }
        else {
            Glide.with(App.getContext())
                    .load(this.music.getAlbumImageUrl())
                    .into(imageViewAlbum);
        }
    }

    private void bindTrackSelection(BaseMusic currentMusic){
        if (currentMusic == null) {
            return;
        }
        if (Objects.equals(music.getDownload(), currentMusic.getDownload())) {
            linearLayoutBackground.setBackground(ContextCompat.getDrawable(itemView.getContext(),
                    R.drawable.track_selected_drawable));
        } else {
            linearLayoutBackground.setBackground(ContextCompat.getDrawable(itemView.getContext(),
                    R.drawable.track_not_selected_drawable));
        }
    }


    private void bindState(){
        switch (music.getState()){
            case BaseMusic.STATE_DEFAULT:
                imageViewDownloaded.setVisibility(View.GONE);
                progressBarDownloading.setVisibility(View.GONE);
                imageButtonDownload.setImageResource(R.drawable.download);
                imageButtonDownload.setEnabled(true);
                break;
            case BaseMusic.STATE_DOWNLOADING:
                imageViewDownloaded.setVisibility(View.GONE);
                progressBarDownloading.setVisibility(View.VISIBLE);
                imageButtonDownload.setImageResource(R.drawable.download);
                imageButtonDownload.setEnabled(false);
                break;
            case BaseMusic.STATE_COMPLETED:
                imageViewDownloaded.setVisibility(View.VISIBLE);
                progressBarDownloading.setVisibility(View.GONE);
                imageButtonDownload.setImageResource(R.drawable.ic_trash);
                imageButtonDownload.setEnabled(true);
                break;
        }
    }
}
