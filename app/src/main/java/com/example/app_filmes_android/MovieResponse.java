package com.example.app_filmes_android;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResponse {
    @SerializedName("results")
    private List<Movie> movies;

    public List<com.example.app_filmes_android.Movie> getMovies() {
        return movies;
    }
}
