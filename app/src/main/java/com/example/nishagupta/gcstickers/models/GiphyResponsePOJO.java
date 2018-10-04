package com.example.nishagupta.gcstickers.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nishagupta on 03/10/18.
 */

public class GiphyResponsePOJO {

    @SerializedName("data")
    public List<DataBean> data = new ArrayList<>();

    public List<DataBean> getData() {
        return data;
    }

    public void setData( List<DataBean> data ) {
        this.data = data;
    }

    public class DataBean {

        @SerializedName("id")
        private String id;
        @SerializedName("title")
        private String title;
        @SerializedName("url")
        private String url;

        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        public String getId() {
            return id;
        }

        public void setId( String id ) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle( String title ) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl( String url ) {
            this.url = url;
        }
    }

}
