package com.sap.uncolor.equalizer;

public interface EqualizerPanelChangeListener {

    void onBandLevelChanged(short band, short progress);

    void onPresetChanged(short preset);

    void onTurnButtonStateChanged(boolean isEqualizerEnabled);
}
