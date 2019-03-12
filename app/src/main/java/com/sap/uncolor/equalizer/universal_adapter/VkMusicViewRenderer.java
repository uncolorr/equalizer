package com.sap.uncolor.equalizer.universal_adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.sap.uncolor.equalizer.R;
import com.sap.uncolor.equalizer.main_activity.MusicFragmentPresenter;
import com.sap.uncolor.equalizer.models.VkMusic;

public class VkMusicViewRenderer extends ViewRenderer<VkMusic, VkMusicViewHolder> {

    private MusicFragmentPresenter presenter;

    public VkMusicViewRenderer(int type, Context context, MusicFragmentPresenter presenter) {
        super(type, context);
        this.presenter = presenter;
    }

    @Override
    public void bindView(@NonNull VkMusic model, @NonNull VkMusicViewHolder holder) {
      //  holder.bind(model);
    }

    @NonNull
    @Override
    public VkMusicViewHolder createViewHolder(@Nullable ViewGroup parent) {
        return new VkMusicViewHolder(LayoutInflater.from(context).inflate(R.layout.music_item, parent,
                false), presenter);
    }
}
