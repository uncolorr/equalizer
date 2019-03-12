package com.sap.uncolor.equalizer.Apis.response_models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Uncolor on 04.09.2018.
 */

public class VKMusicResponseModel {

    @Nullable
    @SerializedName("response")
    private VkResponse response;

    @Nullable
    @SerializedName("error")
    private CaptchaErrorResponse error;

    public VkResponse getResponse() {
        return response;
    }

    @Nullable
    public CaptchaErrorResponse getError() {
        return error;
    }
}
