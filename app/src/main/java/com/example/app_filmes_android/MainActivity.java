package com.example.app_filmes_android;

import static com.example.app_filmes_android.ApiConfig.API_KEY;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.app_filmes_android.MovieAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import com.example.app_filmes_android.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private EditText etMovieTitle;
    private Button btnSearch;
    private ApiService apiService;
    private DatabaseHelper dbHelper;

    private RelativeLayout layoutSearch;
    private RelativeLayout layoutResults;

    private void showSearchLayout() {
        ViewFlipper viewFlipper = findViewById(R.id.viewFlipper);
        viewFlipper.showPrevious();

        layoutSearch.setVisibility(View.VISIBLE);
        layoutResults.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*setContentView(R.layout.activity_main);*/

        recyclerView = binding.recyclerView;
        etMovieTitle = binding.editTextMovieTitle;
        btnSearch = binding.btnSearch;

        apiService = ApiClient.getRetrofit().create(ApiService.class);
        dbHelper = new DatabaseHelper(getApplicationContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new MovieAdapter(new ArrayList<>());
        recyclerView.setAdapter(movieAdapter);

        layoutSearch = findViewById(R.id.layoutSearch);
        layoutResults = findViewById(R.id.layoutResults);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String movieTitle = etMovieTitle.getText().toString();
                searchMovies(movieTitle);
            }
        });

        FloatingActionButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchLayout(); // Método para mostrar o layout de pesquisa
            }
        });


        /*recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        movieAdapter = new MovieAdapter(new ArrayList<>());
        recyclerView.setAdapter(movieAdapter);

        EditText etMovieTitle = findViewById(R.id.etMovieTitle);
        Button btnSearch = findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String movieTitle = etMovieTitle.getText().toString();
                searchMovies(movieTitle);
            }
        });*/

        /*DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar.toolbar);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    private void searchMovies(String movieTitle) {
        apiService.searchMovies(API_KEY, movieTitle).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful()) {
                    MovieResponse movieResponse = response.body();
                    List<Movie> movies = movieResponse.getMovies();

                    movieAdapter.updateMovies(movies);
                    movieAdapter.notifyDataSetChanged();
                    dbHelper.insertMovies(movies);

                    layoutSearch.setVisibility(View.GONE);
                    layoutResults.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                // Trate o erro, se necessário
            }
        });
    }
}