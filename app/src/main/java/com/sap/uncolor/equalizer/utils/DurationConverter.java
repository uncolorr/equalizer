package com.sap.uncolor.equalizer.utils;

import java.util.Locale;

/**
 * Created by Uncolor on 26.08.2018.
 */

public class DurationConverter {
    public static String getDurationFormat(int duration){
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }
}
