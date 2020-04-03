package com.example.android.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.database.FavoriteMovie;
import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> mData;

    // list of favorite movies
    //private LiveData<List<FavoriteMovie>> favoriteMovies;

    private MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler
    {
        void onItemClick(Movie movie);
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler)
    {
        mClickHandler = clickHandler;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private ImageView moviePosterDisplay;

        public MovieViewHolder(View itemView)
        {
            super(itemView);
            moviePosterDisplay = itemView.findViewById(R.id.iv_movie_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickHandler != null)
            {
                int adapterPosition = getAdapterPosition();
                Movie movie = mData.get(adapterPosition);
                mClickHandler.onItemClick(movie);
            }
        }
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.recyclerview_row;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = mData.get(position);
        Picasso.get().load(movie.getPosterURL()).into(holder.moviePosterDisplay);
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    public void setMovieData(List<Movie> movieData) {
        mData = movieData;
        notifyDataSetChanged();
    }

    public void setFavoriteMovies(List<FavoriteMovie> favoriteMovies) {
        // create a list of Movies matching the list of FavoriteMovies
        FavoriteMovie favoriteMovie;

        List<Movie> movieData = new ArrayList<Movie>();
        Movie movie;
        if (favoriteMovies != null) {
            for (int i = 0; i < favoriteMovies.size(); i++) {
                favoriteMovie = favoriteMovies.get(i);
                movie = new Movie(
                        favoriteMovie.getId(),
                        favoriteMovie.getTitle(),
                        favoriteMovie.getPosterURL(),
                        favoriteMovie.getSynopsis(),
                        favoriteMovie.getUserRating(),
                        favoriteMovie.getReleaseDate(),
                        favoriteMovie.getBackdropURL());
                movieData.add(movie);
            }
        }
        mData = movieData;
        notifyDataSetChanged();
    }
}
