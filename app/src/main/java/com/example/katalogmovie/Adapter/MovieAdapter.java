package com.example.katalogmovie.Adapter;

import com.example.katalogmovie.Model.Result;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.katalogmovie.R;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private Context context;
    private List<Result> movies;
    private OnItemClickListener listener;

    public MovieAdapter(Context context, List<Result> movies, OnItemClickListener listener) {
        this.context = context;
        this.movies = movies;
        this.listener = listener;
    }

    public void setMovies(List<Result> movies) {
        if (movies != null) {
            this.movies.clear();
            this.movies.addAll(movies);
            notifyDataSetChanged();
        }
    }

    public interface OnItemClickListener {
        boolean onCreateOptionsMenu(Menu menu);

        void onItemClick(Result movie);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgMovie;
        TextView tvTitle;
        TextView tvOverview;
        TextView tvRating;

        ViewHolder(View itemView) {
            super(itemView);
            imgMovie = itemView.findViewById(R.id.imgMovie);
            tvTitle = itemView.findViewById(R.id.tvjudul);
            tvOverview = itemView.findViewById(R.id.tvdes);
            tvRating = itemView.findViewById(R.id.tvrat);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Result movie = movies.get(position);
                listener.onItemClick(movie);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Result movie = movies.get(position);
        holder.tvTitle.setText(movie.getOriginalTitle());
        holder.tvOverview.setText(movie.getOverview());
        holder.tvRating.setText(String.valueOf(movie.getVoteAverage()));
        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500/" + movie.getPosterPath())
                .into(holder.imgMovie);
    }

    @Override
    public int getItemCount() {
        if (movies != null) {
            return movies.size();
        } else {
            return 0;
        }
    }
}
