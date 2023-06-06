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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnItemClickListener {
    private ActivityMainBinding binding;
    ActivityMovieDetailsBinding bindingDetails;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private EditText etMovieTitle;
    private EditText pesquisaBdLocal;
    private Button btnSearch;

    private Button btnSalvar;
    private Button botaoListaFilmes;

    private Button btnPesquisaPorTitulo;
    private Button btnDetails;
    private Button backToResultsButton;
    private ApiService apiService;
    private DatabaseHelper dbHelper;
    private RelativeLayout layoutDetalhes;
    private RelativeLayout layoutSearch;
    private RelativeLayout layoutResults;
    private RelativeLayout layoutMovies;
    ArrayList<Movie> listaMovies;

    private ArrayList<Movie> filmesPesquisados;
    private ImageView networkStatusIcon;
    private TextView networkStatusText;

    private int lastUsedId;

    private String novoMovieId;

    private BroadcastReceiver networkStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateNetworkStatus();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        bindingDetails = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStatusReceiver, intentFilter);

        recyclerView = binding.recyclerView;
        etMovieTitle = binding.editTextMovieTitle;
        pesquisaBdLocal = binding.pesquisaBdLocal;
        btnSearch = binding.btnSearch;
        botaoListaFilmes = binding.botaoListaFilmes;
        btnPesquisaPorTitulo = binding.btnPesquisaPorTitulo;

        apiService = ApiClient.getRetrofit().create(ApiService.class);
        dbHelper = new DatabaseHelper(this);
        dbHelper.onCreate(dbHelper.getWritableDatabase());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new MovieAdapter(new ArrayList<>());
        movieAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(movieAdapter);

        networkStatusIcon = findViewById(R.id.networkStatusIcon);
        networkStatusText = findViewById(R.id.networkStatusText);
        layoutSearch = findViewById(R.id.layoutSearch);
        layoutResults = findViewById(R.id.layoutResults);
        layoutDetalhes = findViewById(R.id.layoutDetalhes);

        updateNetworkStatus();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String movieTitle = etMovieTitle.getText().toString();
                searchMovies(movieTitle);
            }
        });

        btnPesquisaPorTitulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String movieTitle = pesquisaBdLocal.getText().toString();
                procuraFilmeBdLocal(movieTitle);
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
                onBackPressed();
            }
        });

        btnDetails = findViewById(R.id.btnDetails);

        btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        movieAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkStatusReceiver);
    }

    private void updateNetworkStatus() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            networkStatusIcon.setImageResource(R.drawable.ic_network_online);
            networkStatusText.setText("Online");
        } else {
            networkStatusIcon.setImageResource(R.drawable.ic_network_offline);
            networkStatusText.setText("Offline");
        }
    }
    public void procuraFilmeBdLocal(String movieTitle){
        DatabaseHelper dbhelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {"id", "title"};
        String selection = "title LIKE ?";
        String[] selectionArgs = {"%" + movieTitle + "%"};

        Cursor cursor = db.query("movies", projection, selection, selectionArgs, null, null, null);

        ArrayList<Movie> filmesEncontrados = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String filmeTitulo = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            Movie filme = new Movie();
            filme.setId(id);
            filme.setTitle(filmeTitulo);
            filmesEncontrados.add(filme);
        }

        cursor.close();
        db.close();

        ViewFlipper viewFlipper = findViewById(R.id.viewFlipper);

        ListView lista = (ListView) findViewById(R.id.lvMovies);
        FilmeAdapter adapter = new FilmeAdapter(this, filmesEncontrados);
        lista.setAdapter(adapter);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditarFilmeActivity.class);
                intent.putExtra("ID", filmesEncontrados.get(position).getId());
                startActivity(intent);
            }
        });

        layoutMovies = findViewById(R.id.layoutMovies);
        layoutSearch.setVisibility(View.GONE);
        layoutMovies.setVisibility(View.VISIBLE);
    }

    private void searchMovies(String movieTitle) {
        apiService.searchMovies(API_KEY, movieTitle).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful()) {
                    MovieResponse movieResponse = response.body();
                    List<MovieResponse> movies = movieResponse.getMovies();
                    ArrayList<Movie> movieList = new ArrayList<>();
                    for (MovieResponse movieResponse2 : movies) {
                        Movie movie = new Movie();
                        movie.setId(Integer.parseInt(movieResponse2.getId()));
                        movie.setTitle(movieResponse2.getTitle());
                        movie.setOverview(movieResponse2.getOverview());
                        movieList.add(movie);
                    }
                    filmesPesquisados = movieList;

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
        apiService.getMovieDetails(movieId, API_KEY).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful()) {
                    MovieResponse movieResponse = response.body();
                    String title = movieResponse.getTitle();
                    String overview = movieResponse.getOverview();
                    String url_poster = movieResponse.getPosterPath();

                    bindingDetails.movieTitleTextView.setText(title);
                    bindingDetails.movieOverviewTextView.setText(overview);

                    new NetworkRequestTask(MainActivity.this).execute(movieId);
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
            }
        });
    }

    void showDetailsLayout(ActivityMovieDetailsBinding bindingDetails, String posterUrl, String title, String overview, String movieId) {
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
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(bindingDetails.moviePosterImageView);

        bindingDetails.backToResultsButton.setVisibility(View.VISIBLE);
        bindingDetails.btnSalvar.setVisibility(View.VISIBLE);

        bindingDetails.btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindingDetails.btnSalvar.setVisibility(View.VISIBLE);
                String title = bindingDetails.movieTitleTextView.getText().toString();
                String overview = bindingDetails.movieOverviewTextView.getText().toString();
                Movie movie = new Movie();
                movie.setId(Integer.parseInt(movieId));
                movie.setTitle(title);
                movie.setOverview(overview);
                dbHelper.insertMovie(movie);

            }
        });

        bindingDetails.backToResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void showSearchLayout() {
        ViewFlipper viewFlipper = findViewById(R.id.viewFlipper);
        viewFlipper.showPrevious();

        layoutSearch.setVisibility(View.VISIBLE);
        layoutResults.setVisibility(View.GONE);
    }

    private void showResultsLayout() {
        ViewFlipper viewFlipper = findViewById(R.id.viewFlipper);
        View resultsView = findViewById(R.id.layoutResults);

        int indexOfResultsView = viewFlipper.indexOfChild(resultsView);
        viewFlipper.setDisplayedChild(indexOfResultsView);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        if (recyclerView.getAdapter() == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(movieAdapter);
        }
    }

    private void showMoviesLayout() {
        ViewFlipper viewFlipper = findViewById(R.id.viewFlipper);

        ListView lista = (ListView) findViewById(R.id.lvMovies);
        listaMovies = dbHelper.getAllMovies();
        FilmeAdapter adapter = new FilmeAdapter(this, listaMovies);
        lista.setAdapter(adapter);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditarFilmeActivity.class);
                intent.putExtra("ID", listaMovies.get(position).getId());
                startActivity(intent);
            }
        });

        layoutMovies = findViewById(R.id.layoutMovies);
        layoutSearch.setVisibility(View.GONE);
        layoutMovies.setVisibility(View.VISIBLE);
    }
}

