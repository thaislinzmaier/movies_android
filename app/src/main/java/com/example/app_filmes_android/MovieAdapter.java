package com.example.app_filmes_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private List<Movie> movies;

    public MovieAdapter(List<Movie> movies) {
        this.movies = movies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void updateMovies(List<Movie> updatedMovies) {
        movies = updatedMovies;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.movieTitleTextView);
        }

        public void bind(Movie movie) {
            titleTextView.setText(movie.getTitle());
        }
    }
}