package com.example.app_filmes_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private List<MovieResponse> movies;
    private OnItemClickListener itemClickListener;
    private OnItemClickListener onItemClickListener;

    private Button btnDetails;

    public MovieResponse getMovie(int position) {
        return getMovie(position);
    }

    public interface OnItemClickListener {
        void onDetailsClick(String movieId);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }


    public MovieAdapter(List<MovieResponse> movies) {

        this.movies = movies;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_movie, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAbsoluteAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    MovieResponse movie = movies.get(position);
                    String movieId = movie.getId();
                    itemClickListener.onDetailsClick(movieId);
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MovieResponse movie = movies.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void updateMovies(List<MovieResponse> updatedMovies) {
        movies = updatedMovies;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private Button btnDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.movieTitleTextView);
            btnDetails = itemView.findViewById(R.id.btnDetails);
            btnDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        MovieResponse movie = movies.get(position);
                        String movieId = movie.getId();
                        onItemClickListener.onDetailsClick(movieId);
                    }
                }
            });
        }

        public void bind(MovieResponse movie) {
            titleTextView.setText(movie.getTitle());
        }
    }

}