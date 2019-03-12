package com.sap.uncolor.equalizer.Apis;


import com.sap.uncolor.equalizer.Apis.response_models.AuthResponseModel;
import com.sap.uncolor.equalizer.Apis.response_models.VKMusicResponseModel;
import com.sap.uncolor.equalizer.Apis.response_models.album_image_model.AlbumImageResponseModel;
import com.sap.uncolor.equalizer.Apis.response_models.user_info_model.UserInfoResponseModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Uncolor on 27.02.2018.
 */

public interface ApiSource {

    @GET
    @Headers("User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.1.2 Safari/605.1.15")
    @Streaming
    Call<ResponseBody> downloadFile(@Url String url);

    @FormUrlEncoded
    @POST("https://audio.bigbadbird.ru/src/examples/example_microg.php")
    Call<AuthResponseModel> login(@Field("login") String login,
                                  @Field("pass") String pass);

    @Headers("User-Agent: KateMobileAndroid/48.2 lite-433 (Android 8.1.0; SDK 27; arm64-v8a; Google Pixel 2 XL; en)")
    @GET("https://api.vk.com/method/audio.get")
    Call<VKMusicResponseModel> getVkMusic(@Query("access_token") String access_token,
                                          @Query("v") String v,
                                          @Query("offset") int offset,
                                          @Query("count") int count);

    @Headers("User-Agent: KateMobileAndroid/48.2 lite-433 (Android 8.1.0; SDK 27; arm64-v8a; Google Pixel 2 XL; en)")
    @GET("https://api.vk.com/method/audio.search")
    Call<VKMusicResponseModel> searchVkMusic(@Query("access_token") String access_token,
                                             @Query("q") CharSequence q,
                                             @Query("v") String v,
                                             @Query("offset") int offset,
                                             @Query("count") int count);

    @Headers("User-Agent: KateMobileAndroid/48.2 lite-433 (Android 8.1.0; SDK 27; arm64-v8a; Google Pixel 2 XL; en)")
    @GET("https://api.vk.com/method/audio.search")
    Call<VKMusicResponseModel> searchVkMusicWithCaptcha(@Query("access_token") String access_token,
                                                        @Query("q") CharSequence q,
                                                        @Query("v") String v,
                                                        @Query("offset") int offset,
                                                        @Query("count") int count,
                                                        @Query("captcha_sid") String captchaSID,
                                                        @Query("captcha_key") String captchaKey);

    @GET("https://ws.audioscrobbler.com/2.0/?format=json&api_key=859dd05988e7df407c03d6cb74e41477&method=track.getInfo")
    Call<AlbumImageResponseModel> getAlbumImage(@Query("artist") String artist,
                                                @Query("track") String track);

    @Headers("User-Agent: KateMobileAndroid/48.2 lite-433 (Android 8.1.0; SDK 27; arm64-v8a; Google Pixel 2 XL; en)")
    @GET("https://api.vk.com/method/users.get")
    Call<UserInfoResponseModel> getUserInfo(@Query("access_token") String access_token,
                                            @Query("fields") String fields,
                                            @Query("v") String v);


}


