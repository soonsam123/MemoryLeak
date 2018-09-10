package com.example.karat.memoryleak.service;

import com.example.karat.memoryleak.models.Show;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TvMazeService {

    String BASE_URL = "http://api.tvmaze.com/";

    @GET("shows")
    Call<List<Show>> getShows();
}
