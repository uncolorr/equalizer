package com.sap.uncolor.equalizer.Apis.request_bodies;

/**
 * Created by Uncolor on 13.09.2018.
 */

public class SearchVkMusicBody {
    private int offset;
    private int count;
    private String v;
    private CharSequence q;
    private String captchaSid;
    private String captchaKey;

    public SearchVkMusicBody() {
        offset = 0;
        v = "5.64";
        q = "";
        count = 100;
    }

    public void incOffset(){
        offset += count;
    }

    public void resetOffset(){
        offset = 0;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public int getCount() {
        return count;
    }

    public String getV() {
        return v;
    }

    public CharSequence getQ() {
        return q;
    }

    public void setQ(CharSequence q) {
        this.q = q;
    }

    public String getCaptchaSid() {
        return captchaSid;
    }

    public String getCaptchaKey() {
        return captchaKey;
    }

    public void setCaptchaSid(String captchaSid) {
        this.captchaSid = captchaSid;
    }

    public void setCaptchaKey(String captchaKey) {
        this.captchaKey = captchaKey;
    }
}
