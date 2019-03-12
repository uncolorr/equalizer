package com.sap.uncolor.equalizer.Apis.response_models.user_info_model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Uncolor on 17.09.2018.
 */

public class UserInfo {

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @Nullable
    @SerializedName("photo_big")
    private String photoUrl;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Nullable
    public String getPhotoUrl() {
        return photoUrl;
    }
}
