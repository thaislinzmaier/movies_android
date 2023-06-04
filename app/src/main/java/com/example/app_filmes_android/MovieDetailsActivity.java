package com.example.app_filmes_android;

import static com.example.app_filmes_android.ApiConfig.API_KEY;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvOverview;

    private ImageView tvPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // Recupere os dados do filme da Intent
        Intent intent = getIntent();
        String movieTitle = intent.getStringExtra("movieTitle");
        String movieOverview = intent.getStringExtra("movieOverview");
        ///String moviePoster = intent.getIntExtra("moviePoster", );

        // Inicialize as Views do layout
        tvTitle = findViewById(R.id.movieTitleTextView);
        tvOverview = findViewById(R.id.movieOverviewTextView);
        tvPoster = findViewById(R.id.moviePosterImageView);

        // Defina os dados do filme nas Views
        tvTitle.setText(movieTitle);
        tvOverview.setText(movieOverview);
        //tvPoster.setImageAlpha(moviePoster);
    }
}