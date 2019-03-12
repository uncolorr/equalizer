package com.sap.uncolor.equalizer.Apis.response_models.album_image_model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Uncolor on 14.09.2018.
 */

public class ImageInfo {

    @SerializedName("#text")
    private String url;

    @SerializedName("size")
    private String size;


    public String getUrl() {
        return url;
    }

    public String getSize() {
        return size;
    }
}
