package com.sap.uncolor.equalizer.Apis.response_models.album_image_model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Uncolor on 14.09.2018.
 */

public class Track {

    @SerializedName("album")
    private Album album;

    public Album getAlbum() {
        return album;
    }
}
