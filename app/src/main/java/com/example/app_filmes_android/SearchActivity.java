package com.example.app_filmes_android;

import static com.example.app_filmes_android.ApiConfig.API_KEY;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.example.app_filmes_android.ApiClient;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_filmes_android.ApiConfig;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

        private RecyclerView recyclerView;
        private MovieAdapter movieAdapter;
        private EditText editTextMovieTitle;
        private Button buttonSearch;
        private ListView listViewMovies;

        private ApiService apiService;
        private DatabaseHelper dbHelper;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            recyclerView = findViewById(R.id.recyclerView);
            editTextMovieTitle = findViewById(R.id.editTextMovieTitle);
            buttonSearch = findViewById(R.id.btnSearch);

            apiService = ApiClient.getRetrofit().create(ApiService.class);
            dbHelper = new DatabaseHelper(getApplicationContext());

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            movieAdapter = new MovieAdapter(new ArrayList<>());
            recyclerView.setAdapter(movieAdapter);


            buttonSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String movieTitle = editTextMovieTitle.getText().toString();
                    searchMovies(movieTitle);
                }
            });
        }

    private void searchMovies(String movieTitle) {
        apiService.searchMovies(API_KEY, movieTitle).enqueue(new Callback<MovieResponse>() {
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful()) {
                    MovieResponse movieResponse = response.body();
                    List<MovieResponse> movies = movieResponse.getMovies();
                    movieAdapter.updateMovies(movies);
                    movieAdapter.notifyDataSetChanged();
                }
            }

            public void onFailure(Call<MovieResponse> call, Throwable t) {
                // Trate o erro, se necessário
            }
        });
    }
}
