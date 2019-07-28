package com.realitycheck.pomogatorandroidapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JSONPlaceHolderApi {
    @GET("/driver/{driver_id}")
    public Call<Post> getPlan(@Path("driver_id") int id);

    @GET("/posts")
    public Call<List<Post>> getAllPosts();

    @GET("/posts")
    public Call<List<Post>> getPostOfUser(@Query("userId") int id);

    @POST("/posts")
    public Call<Post> postData(@Body Post data);
}