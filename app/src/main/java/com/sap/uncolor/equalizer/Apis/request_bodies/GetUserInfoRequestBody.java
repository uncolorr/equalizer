package com.sap.uncolor.equalizer.Apis.request_bodies;

/**
 * Created by Uncolor on 17.09.2018.
 */

public class GetUserInfoRequestBody {

    private String fields;
    private String v;

    public GetUserInfoRequestBody() {
        this.fields = "photo_big";
        this.v = "5.64";
    }

    public String getFields() {
        return fields;
    }

    public String getV() {
        return v;
    }
}
