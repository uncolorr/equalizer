package com.sap.uncolor.equalizer.Apis.response_models;

import com.google.gson.annotations.SerializedName;
import com.sap.uncolor.equalizer.models.VkMusic;

import java.util.List;

/**
 * Created by Uncolor on 05.09.2018.
 */

public class VkResponse {
    @SerializedName("count")
    private int count;

    @SerializedName("items")
    private List<VkMusic> items;

    public int getCount() {
        return count;
    }

    public List<VkMusic> getItems() {
        return items;
    }
}
