package com.sap.uncolor.equalizer.Apis.response_models.album_image_model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Uncolor on 14.09.2018.
 */

public class Album {

    @SerializedName("image")
    private List<ImageInfo> images;

    public List<ImageInfo> getImages() {
        return images;
    }
}
