package com.sap.uncolor.equalizer.Apis.request_bodies;

/**
 * Created by Uncolor on 05.09.2018.
 */

public class GetVkMusicBody {
    private int offset;
    private int count;
    private String v;

    public GetVkMusicBody() {
        offset = 0;
        v = "5.64";
        count = 100;
    }

    public void incOffset(){
        offset += count;
    }

    public void resetOffset(){
        offset = 0;
    }

    public int getOffset() {
        return offset;
    }

    public int getCount() {
        return count;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getV() {
        return v;
    }
}
