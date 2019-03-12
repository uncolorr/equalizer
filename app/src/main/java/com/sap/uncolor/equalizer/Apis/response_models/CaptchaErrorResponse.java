package com.sap.uncolor.equalizer.Apis.response_models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Uncolor on 13.09.2018.
 */

public class CaptchaErrorResponse implements Serializable {

    @SerializedName("error_code")
    private int errorCode;

    @SerializedName("error_msg")
    private String errorMessage;

    @SerializedName("captcha_sid")
    private String captchaSID;

    @SerializedName("captcha_img")
    private String captchaImage;

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getCaptchaSID() {
        return captchaSID;
    }

    public String getCaptchaImage() {
        return captchaImage;
    }
}
