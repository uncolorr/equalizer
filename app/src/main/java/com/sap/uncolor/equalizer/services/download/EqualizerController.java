package com.sap.uncolor.equalizer.services.download;

import android.media.audiofx.Equalizer;

import com.sap.uncolor.equalizer.application.App;

import java.util.ArrayList;
import java.util.List;

public class EqualizerController {

    private int audioSessionId;

    private Equalizer equalizer;

    private short currentPresetIndex = 0;

    public EqualizerController(int audioSessionId) {
        App.Log("Equalizer controller created");
        this.audioSessionId = audioSessionId;
        equalizer = new Equalizer(0, this.audioSessionId);
    }

    public List<String> getPresetsNames(){
        List<String> presetNames = new ArrayList<>();
        int numberOfPresets = equalizer.getNumberOfPresets();
        for (int i = 0; i < numberOfPresets; i++) {
            presetNames.add(equalizer.getPresetName((short) i));
        }
        return presetNames;
    }

    public void setBandLevel(short band, short level){
        equalizer.setBandLevel(band, level);
    }

    public void setPreset(short preset){
        equalizer.usePreset(preset);

    }

    public short[] getBandLevels(){
        short[] bandLevels = new short[equalizer.getNumberOfBands()];
        for (short band = 0; band < equalizer.getNumberOfBands(); band++) {
            bandLevels[band] = equalizer.getBandLevel(band);
        }
        return bandLevels;
    }

    public short getCurrentPresetIndex(){
        return currentPresetIndex;
    }

    public short getMaxBandLevelRange(){
        return equalizer.getBandLevelRange()[1];
    }

    public short getMinBandLevelRange(){
        return equalizer.getBandLevelRange()[0];
    }

    public void setEnabled(boolean enabled){
        equalizer.setEnabled(enabled);
    }

    public boolean isEnabled(){
        return equalizer.getEnabled();
    }

    public void setCurrentPresetIndex(short currentPresetIndex) {
        this.currentPresetIndex = currentPresetIndex;
    }
}
