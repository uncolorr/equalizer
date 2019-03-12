package com.sap.uncolor.equalizer.Apis;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Uncolor on 27.02.2018.
 */

public class Api {

    private static Api api;
    private static ApiSource apiSource;
    private Retrofit retrofit;
    private static final String BASE_URL = "https://example.com/";

    private Api() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        Gson gson = new GsonBuilder()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();
        apiSource = retrofit.create(ApiSource.class);
    }

    public static void init() {
        if (api == null) {
            api = new Api();
        }
    }

    public static ApiSource getSource() {
        return apiSource;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }


}
