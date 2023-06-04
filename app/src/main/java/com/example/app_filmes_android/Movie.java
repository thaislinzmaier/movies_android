package com.example.app_filmes_android;

public class Movie {
    private String title;
    private String overview;

    private String url_poster;

    private String id;

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

    public void setUrlPoster(String url_poster) {
        this.url_poster = url_poster;
    }

    public String getId() {
        return id;
    }
}