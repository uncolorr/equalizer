package com.sap.uncolor.equalizer.utils;

public class TextFormatter {

    public static String getdBLabel(short frequency){
        int frequency_in_dB = frequency / 100;
        return (frequency_in_dB + "dB");
    }
}
