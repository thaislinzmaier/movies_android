package com.example.app_filmes_android;

import static com.example.app_filmes_android.ApiConfig.API_KEY;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.app_filmes_android.databinding.ActivityMovieDetailsBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_filmes_android.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnItemClickListener{
    private ActivityMainBinding binding;

    ActivityMovieDetailsBinding bindingDetails;

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private EditText etMovieTitle;
    private Button btnSearch;
    private Button botaoListaFilmes;
    private Button btnDetails;
    private Button backToResultsButton;
    private ApiService apiService;
    private DatabaseHelper dbHelper;

    private RelativeLayout layoutDetalhes;
    private RelativeLayout layoutSearch;
    private RelativeLayout layoutResults;

    private RelativeLayout layoutMovies;

    ArrayList<Movie> listaMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.getLogger("TAG").setLevel(Level.ALL);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        bindingDetails = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = binding.recyclerView;
        etMovieTitle = binding.editTextMovieTitle;
        btnSearch = binding.btnSearch;
        botaoListaFilmes = binding.botaoListaFilmes;

        apiService = ApiClient.getRetrofit().create(ApiService.class);
        dbHelper = new DatabaseHelper(this);
        dbHelper.onCreate(dbHelper.getWritableDatabase());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new MovieAdapter(new ArrayList<>());
        movieAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(movieAdapter);


        layoutSearch = findViewById(R.id.layoutSearch);
        layoutResults = findViewById(R.id.layoutResults);
        layoutDetalhes = findViewById(R.id.layoutDetalhes);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String movieTitle = etMovieTitle.getText().toString();
                searchMovies(movieTitle);
            }
        });

        botaoListaFilmes = findViewById(R.id.botaoListaFilmes);
        botaoListaFilmes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoviesLayout();
            }
        });

        FloatingActionButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchLayout(); // Método para mostrar o layout de pesquisa
            }
        });

        btnDetails = findViewById(R.id.btnDetails);

        btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        backToResultsButton = findViewById(R.id.backToResultsButton);

        backToResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showResultsLayout();// Método para mostrar o layout dos resultados
            }
        });

        movieAdapter.setOnItemClickListener(this);
    }

    private void searchMovies(String movieTitle) {
        apiService.searchMovies(API_KEY, movieTitle).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful()) {
                    MovieResponse movieResponse = response.body();
                    List<MovieResponse> movies = movieResponse.getMovies();
                    //String posterPath = movieResponse.getPosterPath();

                    movieAdapter.updateMovies(movies);
                    movieAdapter.notifyDataSetChanged();

                    layoutSearch.setVisibility(View.GONE);
                    layoutResults.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
            }
        });
    }

    @Override
    public void onDetailsClick(String movieId) {
        Log.w("MainActivity", "PASSOU AQUI");
        /*String movieId = movie.getId();*/
        apiService.getMovieDetails(movieId, API_KEY).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful()) {
                    Log.w("TAG", "PASSOU AQUI");
                    MovieResponse movieResponse = response.body();
                    String title = movieResponse.getTitle();
                    String overview = movieResponse.getOverview();
                    String url_poster = movieResponse.getPosterPath();


                    bindingDetails.movieTitleTextView.setText(title);
                    bindingDetails.movieOverviewTextView.setText(overview);

                    new NetworkRequestTask(MainActivity.this).execute(movieId);

                    Movie movie = new Movie();
                    movie.setTitle(title);
                    movie.setOverview(overview);
                    movie.setUrlPoster(url_poster);
                    // Defina outras informações do filme, se necessário

                    dbHelper.insertMovie(movie);

                } else {
                    Log.w("TAG", "PASSOU AQUI no erro");
                    // Trate o caso em que a resposta não é bem-sucedida
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                // Trate o erro, se necessário
            }
        });
    }
    void showDetailsLayout(ActivityMovieDetailsBinding bindingDetails, String posterUrl, String title, String overview) {
        ViewFlipper viewFlipper = findViewById(R.id.viewFlipper);
        viewFlipper.showNext();
        setContentView(bindingDetails.getRoot());

        layoutSearch.setVisibility(View.GONE);
        layoutResults.setVisibility(View.GONE);
        layoutDetalhes.setVisibility(View.VISIBLE);

        bindingDetails.movieTitleTextView.setText(title);
        bindingDetails.movieOverviewTextView.setText(overview);

        Glide.with(this)
                .load(posterUrl)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e("TAG", "Erro ao carregar a imagem: " + e.getMessage());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("TAG", "Imagem carregada com sucesso");
                        return false;
                    }
                })
                .into(bindingDetails.moviePosterImageView);

        bindingDetails.backToResultsButton.setVisibility(View.VISIBLE);
        bindingDetails.btnSalvar.setVisibility(View.VISIBLE);
    }

    private void showSearchLayout() {
        ViewFlipper viewFlipper = findViewById(R.id.viewFlipper);
        viewFlipper.showPrevious();

        layoutSearch.setVisibility(View.VISIBLE);
        layoutResults.setVisibility(View.GONE);
    }

    private void showResultsLayout() {
        ViewFlipper viewFlipper = findViewById(R.id.viewFlipper);
        viewFlipper.setDisplayedChild(1); // Índice do layout dos resultados no ViewFlipper

        layoutSearch.setVisibility(View.GONE);
        layoutResults.setVisibility(View.VISIBLE);
        layoutDetalhes.setVisibility(View.GONE);
    }

    private void showMoviesLayout(){
        ViewFlipper viewFlipper = findViewById(R.id.viewFlipper);

        ListView lista = (ListView) findViewById(R.id.lvMovies);
        listaMovies = dbHelper.getAllMovies();
        FilmeAdapter adapter = new FilmeAdapter(this, listaMovies);
        lista.setAdapter(adapter);

        layoutMovies = findViewById(R.id.layoutMovies);

        layoutSearch.setVisibility(View.GONE);
        layoutMovies.setVisibility(View.VISIBLE);
    }


}