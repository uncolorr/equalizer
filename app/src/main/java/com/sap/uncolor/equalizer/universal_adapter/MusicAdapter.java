package com.sap.uncolor.equalizer.universal_adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sap.uncolor.equalizer.R;
import com.sap.uncolor.equalizer.main_activity.MusicFragmentPresenter;
import com.sap.uncolor.equalizer.models.BaseMusic;
import com.sap.uncolor.equalizer.models.VkMusic;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Uncolor on 24.08.2018.
 */

public class MusicAdapter<T extends BaseMusic> extends RecyclerView.Adapter {

    public static final int MODE_ALL_MUSIC = 1;
    public static final int MODE_CACHE = 2;

    private static final int ITEM_TYPE_MUSIC = 0;
    private static final int ITEM_TYPE_LOADING = 1;

    private List<T> items = new ArrayList<>();
    private MusicFragmentPresenter presenter;
    private BaseMusic currentMusic;
    private Realm realm;
    private int mode;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public MusicAdapter(MusicFragmentPresenter presenter) {
        this.presenter = presenter;
        realm = Realm.getDefaultInstance();
        this.mode = MODE_ALL_MUSIC;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId;
        if(viewType == ITEM_TYPE_LOADING){
            layoutId = R.layout.loading_item;
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(layoutId, parent, false);
            return new LoadingViewHolder(view);
        }
        else {
            layoutId = R.layout.music_item;
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(layoutId, parent, false);
            return new VkMusicViewHolder(view, presenter);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VkMusicViewHolder) {
            VkMusicViewHolder musicViewHolder = (VkMusicViewHolder) holder;
            musicViewHolder.bind(items.get(position), currentMusic);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(items.get(position) == null){
            return ITEM_TYPE_LOADING;
        }
        return ITEM_TYPE_MUSIC;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public int getMusicItemsCount(){
        int counter = 0;
        for (int i = 0; i < items.size(); i++) {
            if(items.get(i) != null){
                counter++;
            }
        }
        return counter;
    }

    public void add(List<T> musics) {
        removeLoadingItem();
        this.items.addAll(musics);
        checkCache(musics);
        if(mode == MODE_CACHE){
            Collections.reverse(this.items);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        this.items.clear();
        notifyDataSetChanged();
    }

    public void addLoadingItem(){
        if(!items.isEmpty()) {
            items.add(null);
            notifyItemInserted(items.size() - 1);
        }
    }

    public void removeLoadingItem(){
        if(!items.isEmpty()){
            if(items.get(items.size() - 1) == null){
                items.remove(items.size() - 1);
                notifyItemRemoved(items.size() - 1);
            }
        }
    }

    public void changeCurrentMusic(BaseMusic music) {
        this.currentMusic = music;
        notifyDataSetChanged();
    }

    public void startDownloadMusic(BaseMusic music) {
        for (int i = 0; i < items.size(); i++) {
            if (Objects.equals(items.get(i).getDownload(), music.getDownload())) {
                items.get(i).setState(BaseMusic.STATE_DOWNLOADING);
                notifyItemChanged(i);
            }
        }
    }

    public void setDefaultMusicState(BaseMusic music) {
        for (int i = 0; i < items.size(); i++) {
            if (Objects.equals(items.get(i).getDownload(), music.getDownload())) {
                items.get(i).setState(BaseMusic.STATE_DEFAULT);
                notifyItemChanged(i);
            }
        }
    }

    public void setAlbumImageUrl(String url, int position){
        if(mode == MODE_ALL_MUSIC && !items.isEmpty()) {
            items.get(position).setAlbumImageUrl(url);
            notifyItemChanged(position);
        }
    }

    public void completeDownloadMusic(BaseMusic music) {
        if(mode == MODE_ALL_MUSIC) {
            for (int i = 0; i < items.size(); i++) {
                if (Objects.equals(items.get(i).getDownload(), music.getDownload())) {
                    items.get(i).setState(BaseMusic.STATE_COMPLETED);
                    items.get(i).setLocalPath(music.getLocalPath());
                    notifyItemChanged(i);
                }
            }
        }else if (mode == MODE_CACHE){
            T vkMusic = (T)music;
            vkMusic.setState(BaseMusic.STATE_COMPLETED);
            vkMusic.setLocalPath(music.getLocalPath());
            items.add(0, vkMusic);
            notifyItemInserted(0);
        }
    }

    public void changeOnlineTrackStateAfterDelete(BaseMusic music){
        if(mode == MODE_ALL_MUSIC){
            for (int i = 0; i < items.size(); i++) {
                if(Objects.equals(items.get(i), music)){
                    items.get(i).setState(BaseMusic.STATE_DEFAULT);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void checkCache(){
        if(mode == MODE_ALL_MUSIC){
            for (int i = 0; i < items.size(); i++) {
                items.get(i).setState(BaseMusic.STATE_DEFAULT);
            }
        }
        else if(mode == MODE_CACHE){
            items.clear();
        }
        notifyDataSetChanged();
    }

    private void checkCache(List<T> musics) {
        if (!items.isEmpty()) {
            if (items.get(0) instanceof VkMusic) {
                if(!realm.isInTransaction()) {
                    realm.beginTransaction();
                }
                RealmResults<VkMusic> results = realm.where(VkMusic.class).findAll();
                for (int i = 0; i < musics.size(); i++) {
                    if (results.contains(musics.get(i))) {
                        VkMusic music = realm.where(VkMusic.class)
                                .equalTo("id", musics.get(i).getId())
                                .findFirst();
                        if (music != null) {
                            if (isFileExists(music.getLocalPath())) {
                                musics.get(i).setLocalPath(music.getLocalPath());
                                musics.get(i).setState(BaseMusic.STATE_COMPLETED);
                            }
                            else {
                                music.deleteFromRealm();
                                this.items.remove(i);
                            }
                        }
                    }
                }
                realm.commitTransaction();
            }
        }
    }

    private boolean isFileExists(String localPath) {
        File file = new File(localPath);
        return file.exists();
    }



    public List<T> getItems() {
        List<T> musicItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if(items.get(i) != null){
                musicItems.add(items.get(i));
            }
        }
        return musicItems;
    }

    public void deleteTrack(BaseMusic music, int position) {
        if (music instanceof VkMusic) {
            File file = new File(music.getLocalPath());
            if (file.exists()) {
                boolean isDeleted = file.delete();
                if (isDeleted) {
                    realm.beginTransaction();
                    RealmResults<VkMusic> results = realm.where(VkMusic.class)
                            .equalTo("url", music.getDownload())
                            .findAll();
                    results.deleteAllFromRealm();
                    realm.commitTransaction();
                    if(mode == MODE_CACHE){
                        items.remove(position);
                        notifyItemRemoved(position);
                    }
                    else if(mode == MODE_ALL_MUSIC){
                        items.get(position).setState(BaseMusic.STATE_DEFAULT);
                        notifyItemChanged(position);
                    }
                }
            }
        }
    }

    public void unselectCurrentTrack() {
        currentMusic = null;
        notifyDataSetChanged();
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return this.mode;
    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener){
        this.onLoadMoreListener = listener;
    }

    public RecyclerView.OnScrollListener getScrollListener(){
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(mode == MODE_ALL_MUSIC) {
                    int visibleThreshold = 1;
                    int lastVisibleItem, totalItemCount;
                    LinearLayoutManager linearLayoutManager =
                            (LinearLayoutManager) recyclerView.getLayoutManager();

                    if (dy > 0) {
                        totalItemCount = linearLayoutManager.getItemCount();
                        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                        if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            if (onLoadMoreListener != null) {
                                onLoadMoreListener.onLoadMore();
                            }
                            loading = true;
                        }
                    }
                }
            }
        };
    }
}
