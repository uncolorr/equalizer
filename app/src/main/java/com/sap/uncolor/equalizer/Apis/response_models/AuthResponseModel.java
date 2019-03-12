package com.sap.uncolor.equalizer.Apis.response_models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Uncolor on 04.09.2018.
 */

public class AuthResponseModel {

    @SerializedName("status")
    private String status;

    @Nullable
    @SerializedName("token")
    private String token;

    public String getStatus() {
        return status;
    }

    @Nullable
    public String getToken() {
        return token;
    }
}
