package com.sap.uncolor.equalizer.Apis;

import android.support.annotation.NonNull;

import com.sap.uncolor.equalizer.application.App;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Uncolor on 21.07.2018.
 */

public class ApiResponse {

    public static <T> Callback<T> getCallback(final ApiResponseListener<T> apiResponseListener,
                                              final ApiFailureListener apiFailureListener) {
        return new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call,
                                   @NonNull Response<T> response) {
                T result;
                int code = response.code();

                if (code == 200) {
                    result = response.body();
                    try {
                        apiResponseListener.onResponse(result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    App.Log(Integer.toString(code));
                    apiFailureListener.onFailure(code, "Что то не так");
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call,
                                  @NonNull Throwable t) {
                App.Log(t.getMessage());
                App.Log(t.toString());
                apiFailureListener.onFailure(500, "Неизвестная ошибка");
            }
        };
    }

    public interface ApiFailureListener {
        void onFailure(int code, String message);
    }

    public interface ApiResponseListener<E> {

        void onResponse(E result) throws IOException;

    }
}

