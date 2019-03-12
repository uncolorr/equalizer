package com.sap.uncolor.equalizer;

public interface PlayerPanelChangeListener {
    void onNext();
    void onPrevious();
    void onPlay();
    void onProgressChanged(int position);
    void onShuffleStateChanged();
    void onRepeatStateChanged();
}
