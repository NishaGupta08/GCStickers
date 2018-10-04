package com.example.nishagupta.gcstickers.utils;

import com.example.nishagupta.gcstickers.ApiInteface;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by nishagupta on 03/10/18.
 */

public class Api {


    private static Retrofit retrofit = null;
    public static ApiInteface getClient() {

        // change your base URL
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.giphy.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        //Creating object for our interface
        ApiInteface api = retrofit.create(ApiInteface.class);
        return api; // return the APIInterface object
    }
}
