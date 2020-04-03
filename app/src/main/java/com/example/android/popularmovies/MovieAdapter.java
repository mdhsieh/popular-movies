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
import androidx.recyclerview.widget.RecyclerView;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private List<Movie> mData;

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

    /** Set the adapter data to a list of Movies given
     * the user's favorite movies.
     *
     * @param favoriteMovies the list of favorite movies observed
     * by LiveData
     */
    public void setFavoriteMovies(List<FavoriteMovie> favoriteMovies) {

        FavoriteMovie favoriteMovie;

        // Create a list of Movies matching the list of FavoriteMovies
        List<Movie> movieData = new ArrayList<Movie>();
        Movie movie;
        if (favoriteMovies != null) {
            for (int i = 0; i < favoriteMovies.size(); i++) {
                favoriteMovie = favoriteMovies.get(i);
                /* We only want the last part of the poster and backdrop URL when
                constructing a Movie object. We will already append
                the base and size URL in the Movie constructor, so we get
                a partial URL from the FavoriteMovie object instead.

                Using getPartialPosterURL and getPartialBackdropURL.
                 */
                movie = new Movie(
                        favoriteMovie.getId(),
                        favoriteMovie.getTitle(),
                        favoriteMovie.getPartialPosterURL(),
                        favoriteMovie.getSynopsis(),
                        favoriteMovie.getUserRating(),
                        favoriteMovie.getReleaseDate(),
                        favoriteMovie.getPartialBackdropURL());
                movieData.add(movie);
                //Log.d(TAG, "Added movie " + movie.getTitle());
            }
        }

        mData = movieData;
        notifyDataSetChanged();

        for (int i = 0; i < mData.size(); i++) {
            Log.d(TAG, "movie in data is " + mData.get(i).getTitle()
            + " with poster url " + mData.get(i).getPosterURL());
        }
        //Log.d(TAG, "Total number of movies in favorites collection is " + mData.size());
    }
}
