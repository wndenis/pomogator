package com.realitycheck.pomogatorandroidapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JSONPlaceHolderApi {
    @GET("/api/driver/{driver_id}?")
    public Call<Post> getPlan(@Path("driver_id") int id, @Query("lat") double lat, @Query("lng")double lng);

    @GET("/api/posts")
    public Call<List<Post>> getAllPosts();

    @GET("/api/posts")
    public Call<List<Post>> getPostOfUser(@Query("userId") int id);

    @POST("/api/posts")
    public Call<Post> postData(@Body Post data);
}