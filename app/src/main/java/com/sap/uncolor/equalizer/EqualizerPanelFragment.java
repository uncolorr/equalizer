package com.sap.uncolor.equalizer;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sap.uncolor.equalizer.application.App;
import com.sap.uncolor.equalizer.utils.TextFormatter;
import com.sap.uncolor.equalizer.widgets.VerticalSeekBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EqualizerPanelFragment extends Fragment {

    @BindView(R.id.textViewPresetName)
    TextView textViewPresetName;

    @BindView(R.id.imageViewPresetIcon)
    ImageView imageViewPresetIcon;

    @BindView(R.id.imageButtonTurnEqualizer)
    ImageButton imageButtonTurnEqualizer;

    @BindView(R.id.imageButtonNextPreset)
    ImageButton imageButtonNextPreset;

    @BindView(R.id.imageButtonPreviousPreset)
    ImageButton imageButtonPreviousPreset;



    @BindView(R.id.seekBar60hz)
    VerticalSeekBar seekBar60hz;

    @BindView(R.id.seekBar230hz)
    VerticalSeekBar seekBar230hz;

    @BindView(R.id.seekBar910hz)
    VerticalSeekBar seekBar910hz;

    @BindView(R.id.seekBar3600hz)
    VerticalSeekBar seekBar3600hz;

    @BindView(R.id.seekBar14000hz)
    VerticalSeekBar seekBar14000hz;



    @BindView(R.id.textView60hz)
    TextView textView60hz;

    @BindView(R.id.textView230hz)
    TextView textView230hz;

    @BindView(R.id.textView910hz)
    TextView textView910hz;

    @BindView(R.id.textView3600hz)
    TextView textView3600hz;

    @BindView(R.id.textView14000hz)
    TextView textView14000hz;


    private int minLevelFrequency = 0;

    private int maxLevelFrequency = 0;

    protected OnCreateViewCallback onCreateViewCallback;

    private EqualizerPanelChangeListener equalizerPanelChangeListener;

    private int currentPresetIndex;

    private List<String> presetsNames = new ArrayList<>();

    private boolean isEqualizerEnabled;

    private boolean isBandsConfigured = false;

    public static EqualizerPanelFragment newInstance() {
        Bundle args = new Bundle();
        EqualizerPanelFragment fragment = new EqualizerPanelFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private short countFrequency(int seekBarProgress){
        return (short) (seekBarProgress + minLevelFrequency);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        App.Log("onCreateView equalizer fragment");
        View view = inflater.inflate(R.layout.fragment_equalizer_panel, container, false);
        ButterKnife.bind(this, view);
        onCreateViewCallback.onCreateView();
        return view;
    }

    private SeekBar.OnSeekBarChangeListener getOnLevelChangeListener(final short band) {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(!fromUser){
                    equalizerPanelChangeListener.onBandLevelChanged(band, countFrequency(seekBar.getProgress()));
                    switch (band) {
                        case 0:
                            textView60hz.setText(TextFormatter
                                    .getdBLabel(countFrequency(progress)));
                            break;

                        case 1:
                            textView230hz.setText(TextFormatter
                                    .getdBLabel(countFrequency(seekBar.getProgress())));
                            break;

                        case 2:
                            textView910hz.setText(TextFormatter
                                    .getdBLabel(countFrequency(seekBar.getProgress())));
                            break;

                        case 3:
                            textView3600hz.setText(TextFormatter
                                    .getdBLabel(countFrequency(seekBar.getProgress())));
                            break;

                        case 4:
                            textView14000hz.setText(TextFormatter
                                    .getdBLabel(countFrequency(seekBar.getProgress())));
                            break;
                    }
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

    }

    public void setEqualizerEnabledState(boolean isEqualizerEnabled){
        this.isEqualizerEnabled = isEqualizerEnabled;
        bindEqualizerEnabledState();
    }

    @Override
    public void onResume() {
        super.onResume();
        App.Log("current preset index in onResume: " + currentPresetIndex);

    }

    private void bindEqualizerEnabledState() {
        seekBar60hz.setEnabled(isEqualizerEnabled);
        seekBar230hz.setEnabled(isEqualizerEnabled);
        seekBar910hz.setEnabled(isEqualizerEnabled);
        seekBar3600hz.setEnabled(isEqualizerEnabled);
        seekBar14000hz.setEnabled(isEqualizerEnabled);
        imageButtonNextPreset.setEnabled(isEqualizerEnabled);
        imageButtonPreviousPreset.setEnabled(isEqualizerEnabled);
        if(isEqualizerEnabled){
            textViewPresetName.setText(getCurrentPresetName());
            int color = ResourcesCompat.getColor(getResources(), R.color.colorMain, null);
            imageButtonTurnEqualizer.setColorFilter(color);
            imageButtonNextPreset.setColorFilter(color);
            imageButtonPreviousPreset.setColorFilter(color);
        }
        else {
            textViewPresetName.setText("Выкл");
            int color = ResourcesCompat.getColor(getResources(), R.color.colorEqualizerDisabled, null);
            imageButtonTurnEqualizer.setColorFilter(color);
            imageButtonNextPreset.setColorFilter(color);
            imageButtonPreviousPreset.setColorFilter(color);
        }
    }

    public void setPresetsNames(List<String> presetsNames) {
        App.Log("set presets names");
        if(this.presetsNames.isEmpty()) {
            this.presetsNames = presetsNames;
            currentPresetIndex = 0;
            textViewPresetName.setText(presetsNames.get(currentPresetIndex));
        }
    }

    private String getCurrentPresetName(){
        if(presetsNames.isEmpty()){
            return "Выкл";
        }
        return presetsNames.get(currentPresetIndex);
    }

    public void setOnCreateViewCallback(OnCreateViewCallback onCreateViewCallback) {
        this.onCreateViewCallback = onCreateViewCallback;
    }

    public void setEqualizerPanelChangeListener(EqualizerPanelChangeListener equalizerPanelChangeListener) {
        this.equalizerPanelChangeListener = equalizerPanelChangeListener;
    }

    public void setBandLevelRange(short min, short max){
        App.Log("set band level range");
        minLevelFrequency = min;
        maxLevelFrequency = max;

        seekBar60hz.setMax(-min + max);
        seekBar60hz.setProgress(seekBar60hz.getMax() / 2);
        seekBar60hz.setOnSeekBarChangeListener(getOnLevelChangeListener((short) 0));

        seekBar230hz.setMax(-min + max);
        seekBar230hz.setProgress(seekBar230hz.getMax() / 2);
        seekBar230hz.setOnSeekBarChangeListener(getOnLevelChangeListener((short) 1));

        seekBar910hz.setMax(-min + max);
        seekBar910hz.setProgress(seekBar910hz.getMax() / 2);
        seekBar910hz.setOnSeekBarChangeListener(getOnLevelChangeListener((short) 2));

        seekBar3600hz.setMax(-min + max);
        seekBar3600hz.setProgress(seekBar3600hz.getMax() / 2);
        seekBar3600hz.setOnSeekBarChangeListener(getOnLevelChangeListener((short) 3));

        seekBar14000hz.setMax(-min + max);
        seekBar14000hz.setProgress(seekBar14000hz.getMax() / 2);
        seekBar14000hz.setOnSeekBarChangeListener(getOnLevelChangeListener((short) 4));

        isBandsConfigured = true;
    }


    @OnClick(R.id.imageButtonNextPreset)
    void onNextPresetButtonClick(){
        if(presetsNames.isEmpty()){
            return;
        }
        if(currentPresetIndex == presetsNames.size() - 1){
            currentPresetIndex = 0;
            textViewPresetName.setText(presetsNames.get(currentPresetIndex));
        }
        else {
            currentPresetIndex++;
            textViewPresetName.setText(presetsNames.get(currentPresetIndex));
        }
        equalizerPanelChangeListener.onPresetChanged((short) currentPresetIndex);
        App.Log("current preset index: " + currentPresetIndex);
    }

    @OnClick(R.id.imageButtonPreviousPreset)
    void onPreviousPresetButtonClick(){
        if(presetsNames.size() == 0){
            return;
        }
        if(currentPresetIndex == 0){
            currentPresetIndex = presetsNames.size() - 1;
            textViewPresetName.setText(presetsNames.get(currentPresetIndex));
        }
        else {
            currentPresetIndex--;
            textViewPresetName.setText(presetsNames.get(currentPresetIndex));
        }
        equalizerPanelChangeListener.onPresetChanged((short) currentPresetIndex);
        App.Log("current preset index: " + currentPresetIndex);
    }

    @OnClick(R.id.imageButtonTurnEqualizer)
    void onTurnEqualizerButtonClick(){
        App.Log("current preset index(0): " + currentPresetIndex);
        isEqualizerEnabled = !isEqualizerEnabled;
        equalizerPanelChangeListener.onTurnButtonStateChanged(isEqualizerEnabled);
        App.Log("current preset index(1): " + currentPresetIndex);
        if(isEqualizerEnabled) {
            equalizerPanelChangeListener.onPresetChanged((short) currentPresetIndex);
        }
        bindEqualizerEnabledState();
        App.Log("current preset index(2): " + currentPresetIndex);
    }


    public void setBandLevels(short[] bandLevels) {
        App.Log("Set band levels");
        animateProgression(seekBar60hz, bandLevels[0] + maxLevelFrequency);
        animateProgression(seekBar230hz, bandLevels[1] + maxLevelFrequency);
        animateProgression(seekBar910hz, bandLevels[2] + maxLevelFrequency);
        animateProgression(seekBar3600hz, bandLevels[3] + maxLevelFrequency);
        animateProgression(seekBar14000hz, bandLevels[4] + maxLevelFrequency);

       // App.Log(Integer.toString(bandLevels[0] + maxLevelFrequency));
      //  App.Log(Integer.toString(bandLevels[1] + maxLevelFrequency));
      //  App.Log(Integer.toString(bandLevels[2] + maxLevelFrequency));
      //  App.Log(Integer.toString(bandLevels[3] + maxLevelFrequency));
      //  App.Log(Integer.toString(bandLevels[4] + maxLevelFrequency));
     //   App.Log(" ");
    }

    private void animateProgression(SeekBar seekBar, int progress) {
        final ObjectAnimator animation = ObjectAnimator.ofInt(seekBar, "progress", seekBar.getProgress(), progress);
        animation.setDuration(500);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
        seekBar.clearAnimation();
    }

    public void setCurrentPreset(short currentPreset) {
        if(presetsNames.isEmpty()){
           return;
        }
        this.currentPresetIndex = currentPreset;
        textViewPresetName.setText(presetsNames.get(currentPreset));
    }

    public boolean isBandsConfigured() {
        return isBandsConfigured;
    }

    public interface OnCreateViewCallback {
        void onCreateView();
    }
}
