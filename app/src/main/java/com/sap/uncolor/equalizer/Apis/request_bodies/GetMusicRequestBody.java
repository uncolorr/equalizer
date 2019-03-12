package com.sap.uncolor.equalizer.Apis.request_bodies;

/**
 * Created by Uncolor on 26.08.2018.
 */

public class GetMusicRequestBody {
    private String query;
    private int page;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
