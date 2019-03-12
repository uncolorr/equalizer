package com.sap.uncolor.equalizer.database;

import com.sap.uncolor.equalizer.models.VkMusic;

import java.io.File;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

public class DbManager {

    private Realm realm;

    public DbManager() {
        this.realm = Realm.getDefaultInstance();
    }

    public List<VkMusic> search(String query){
        return realm.where(VkMusic.class)
                .beginGroup()
                .contains("artist", query, Case.INSENSITIVE)
                .or()
                .contains("title", query, Case.INSENSITIVE)
                .endGroup()
                .findAll();
    }

    public List<VkMusic> findAll(){
        return realm.where(VkMusic.class).findAll();
    }

    public void clearCache(){
        realm.beginTransaction();
        RealmResults<VkMusic> results = realm.where(VkMusic.class).findAll();
        for (int i = 0; i < results.size(); i++) {
            File file = new File(results.get(i).getLocalPath());
            if (file.exists()) {
                file.delete();
            }
        }
        results.deleteAllFromRealm();
        realm.commitTransaction();
    }

    public void save(VkMusic vkMusic){
        realm.beginTransaction();
        realm.copyToRealm(vkMusic);
        realm.commitTransaction();
    }

    public void close(){
        if(realm.isClosed()){
            return;
        }
        realm.close();
    }
}
