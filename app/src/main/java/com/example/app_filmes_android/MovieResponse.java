package com.example.app_filmes_android;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResponse {
    @SerializedName("results")
    private List<MovieResponse> movies;
    @SerializedName("poster_path")
    private String posterPath;
    private String overview;
    private String title;
    private String id;

    public String getPosterPath() {
        return posterPath;
    }

    public List<com.example.app_filmes_android.MovieResponse> getMovies() {
        return movies;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }
}
