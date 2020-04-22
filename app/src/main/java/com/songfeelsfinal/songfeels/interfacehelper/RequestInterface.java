package com.songfeelsfinal.songfeels.interfacehelper;


import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface RequestInterface {
    @GET("youtube/v3/search")
    Call<JsonObject> getSong(@QueryMap Map<String,Object> parameters);

}
