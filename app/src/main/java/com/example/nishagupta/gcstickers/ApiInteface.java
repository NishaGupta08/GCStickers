package com.example.nishagupta.gcstickers;

import com.example.nishagupta.gcstickers.models.GiphyResponsePOJO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by nishagupta on 03/10/18.
 */

public interface ApiInteface {

    @GET("/v1/gifs/{type}?api_key=nA8cJOOQzmBbSCQuZvcHGVTlFKSfq8ym&limit=10")
    Call<GiphyResponsePOJO> getStickersList(@Path("type") String type, @Query("offset") int offset, @Query("q") String keyword);
    //Call<List<GiphyResponsePOJO>> getUsersList();
}
