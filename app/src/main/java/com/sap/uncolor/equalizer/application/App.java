package com.sap.uncolor.equalizer.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.orhanobut.hawk.Hawk;
import com.sap.uncolor.equalizer.Apis.Api;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Uncolor on 25.08.2018.
 */

public class App extends Application {

    private static App instance;
    public static final String APP_PREFERENCES_TOKEN = "app_token";


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Hawk.init(this).build();
        Api.init();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("music.realm").build();
        Realm.setDefaultConfiguration(config);

    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }


    public static void Log(String message){
        if(message == null){
            return;
        }
        if(message.isEmpty()){
            return;
        }
        Log.i("fg", message);
    }

    public static boolean isAuth() {
        return Hawk.contains(APP_PREFERENCES_TOKEN);
    }

    public static void saveToken(String token) {
        Hawk.put(APP_PREFERENCES_TOKEN, token);
    }

    public static String getToken(){
        return Hawk.get(APP_PREFERENCES_TOKEN);
    }


    public static void logOut() {
        Hawk.delete(APP_PREFERENCES_TOKEN);
    }

   /* public static String getProviderAuthority() {
        return "com.comandante.uncolor.vkmusic.fileprovider";
    }*/
}
