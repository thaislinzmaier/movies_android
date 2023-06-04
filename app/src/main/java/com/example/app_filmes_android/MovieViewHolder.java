package com.example.app_filmes_android;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class MovieViewHolder extends RecyclerView.ViewHolder{
    public ImageView imageView;
    public View btnDetails;
    private TextView textViewTitle;
    private TextView textViewOverview;

    public MovieViewHolder(View itemView) {
        super(itemView);

        // Inicialização das views dentro do item do filme
        imageView = itemView.findViewById(R.id.moviePosterImageView);
        textViewTitle = itemView.findViewById(R.id.movieTitleTextView);
        textViewOverview = itemView.findViewById(R.id.movieOverviewTextView);
    }

    // Métodos para atualizar as views com os dados do filme
    public void bind(MovieResponse movie) {
        // Atualize as views com os dados do filme
        Glide.with(itemView.getContext())
                .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                .placeholder(R.drawable.placeholder_image) // Recurso de imagem de placeholder enquanto a imagem é carregada
                .into(imageView);

        textViewTitle.setText(movie.getTitle());
        textViewOverview.setText(movie.getOverview());
    }
}
