package com.sap.uncolor.equalizer.models;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;
import com.sap.uncolor.equalizer.universal_adapter.ItemModel;

import io.realm.RealmObject;

/**
 * Created by Uncolor on 04.09.2018.
 */

public class VkMusic extends RealmObject implements BaseMusic, ItemModel {

    public static final int TYPE = 1;

    @SerializedName("id")
    private long id;

    @SerializedName("artist")
    private String artist;

    @SerializedName("title")
    private String title;

    @SerializedName("duration")
    private int duration;

    @SerializedName("url")
    private String url;

    private String localPath;

    private String albumImageUrl;

    private int state;


    public VkMusic(){
        this.state = BaseMusic.STATE_DEFAULT;
    }

    protected VkMusic(Parcel in) {
        id = in.readLong();
        artist = in.readString();
        title = in.readString();
        duration = in.readInt();
        url = in.readString();
        localPath = in.readString();
        albumImageUrl = in.readString();
        state = in.readInt();
    }

    public static final Creator<VkMusic> CREATOR = new Creator<VkMusic>() {
        @Override
        public VkMusic createFromParcel(Parcel in) {
            return new VkMusic(in);
        }

        @Override
        public VkMusic[] newArray(int size) {
            return new VkMusic[size];
        }
    };

    public void setId(long id) {
        this.id = id;
    }


    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getArtist() {
        return artist;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public String getDownload() {
        return url;
    }

    @Override
    public String getLocalPath() {
        return localPath;
    }

    @Override
    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    public void setState(int state) {
        this.state = state;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(id);
        dest.writeString(artist);
        dest.writeString(title);
        dest.writeInt(duration);
        dest.writeString(url);
        dest.writeString(localPath);
        dest.writeString(albumImageUrl);
        dest.writeInt(state);
    }

    @Override
    public String getAlbumImageUrl() {
        return albumImageUrl;
    }

    @Override
    public void setAlbumImageUrl(String url) {
        this.albumImageUrl = url;
    }


    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(!(obj instanceof VkMusic)){
            return false;
        }
        VkMusic music = (VkMusic) obj;
        return music.getId() == id;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

    @Override
    public int getType() {
        return TYPE;
    }
}
