package com.example.android.popularmovies;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.database.FavoriteMovie;
import com.example.android.popularmovies.database.MovieViewModel;
import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private MovieViewModel viewModel;

    // movie loaded from database by id
    //private LiveData<FavoriteMovie> movieById;

    // star button that user can press to mark movie as a favorite
    private ImageView favoritesImageView;
    // boolean to mark as favorite or not favorite
    private boolean isMarkedAsFavorite = false;

    private ImageView backdropImageDisplay;
    private TextView titleDisplay;
    private TextView synopsisDisplay;
    private TextView userRatingDisplay;
    private TextView releaseDateDisplay;

    private Movie movie;

    // Movie key when retrieving Intent
    private static final String EXTRA_MOVIE = "Movie";

    private String backdropURL;
    private int id;
    private String title;
    private String synopsis;
    private int userRating;
    private String releaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // ViewModel to insert or delete movies from database
        viewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        favoritesImageView = findViewById(R.id.iv_favorite_button);

        backdropImageDisplay = findViewById(R.id.image_iv);
        titleDisplay = findViewById(R.id.tv_display_title);
        synopsisDisplay = findViewById(R.id.tv_display_synopsis);
        userRatingDisplay = findViewById(R.id.tv_display_user_rating);
        releaseDateDisplay = findViewById(R.id.tv_display_release_date);

        // get the Movie from Intent
        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra(EXTRA_MOVIE))
        {
            movie = intentThatStartedThisActivity.getParcelableExtra(EXTRA_MOVIE);

            if (movie != null) {

                // get the movie's data
                backdropURL = movie.getBackdropURL();

                id = movie.getId();
                title = movie.getTitle();
                synopsis = movie.getSynopsis();
                userRating = movie.getUserRating();
                releaseDate = movie.getReleaseDate();

                // set the displays accordingly, casting int to String if necessary
                Picasso.get().load(backdropURL).into(backdropImageDisplay);

                titleDisplay.setText(title);
                synopsisDisplay.setText(synopsis);
                // display user rating / 10
                userRatingDisplay.setText(String.format(getString(R.string.out_of_10), userRating));
                releaseDateDisplay.setText(releaseDate);

                //Log.d(TAG, "id is " + id);
                /*movieById = viewModel.getMovieById(id);
                if (movieById != null && movieById.getValue() != null) {
                    Log.d(TAG, "Got movie " + id + ". Movie loaded from id is " + movieById.getValue().getTitle());
                    isMarkedAsFavorite = true;
                    favoritesImageView.setImageResource(R.drawable.ic_star_yellow_24dp);
                    Log.d(TAG, "movie already exists in favorites list");
                }
                else
                {
                    isMarkedAsFavorite = false;
                    favoritesImageView.setImageResource(R.drawable.ic_star_border_yellow_24dp);
                    Log.d(TAG, "movie does not exist in favorites list");
                }*/

                /*if (movieById != null)
                {
                    movieById.observe(this, new Observer<FavoriteMovie>() {
                        @Override
                        public void onChanged(FavoriteMovie favoriteMovie) {
                            if (favoriteMovie != null) {
                                Log.d(TAG, "movie " + favoriteMovie.getId() + " with title " + favoriteMovie.getTitle() + " has changed");
                            }
                        }
                    });
                }*/

                /* Check if this movie already exists in the favorite movies database.
                   Mark the favorites button if it does, otherwise un-mark. */
                viewModel.getAllMovies().observe(this, new Observer<List<FavoriteMovie>>() {
                    @Override
                    public void onChanged(@Nullable final List<FavoriteMovie> favoriteMovies) {
                        if (favoriteMovies != null) {
                            for (int i = 0; i < favoriteMovies.size(); i++) {
                                /*Log.d(TAG, "movie " + favoriteMovies.get(i).getId()
                                        + " is " + favoriteMovies.get(i).getTitle());*/

                                if (favoriteMovies.get(i).getId() == id)
                                {
                                    isMarkedAsFavorite = true;
                                    favoritesImageView.setImageResource(R.drawable.ic_star_yellow_24dp);
                                    Log.d(TAG, "movie already exists in favorites list");
                                }
                            }
                        }
                    }
                });

                // click on favorites button
                favoritesImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // create a FavoriteMovie matching the Movie object
                        FavoriteMovie favoriteMovie = new FavoriteMovie(id,
                        title, movie.getPosterURL(), synopsis, userRating, releaseDate, backdropURL);
                        //Log.d(TAG, "possible favorite movie's poster url is " + favoriteMovie.getPosterURL());

                        if (!isMarkedAsFavorite)
                        {
                            // mark as favorite and insert into favorites database
                            favoritesImageView.setImageResource(R.drawable.ic_star_yellow_24dp);
                            isMarkedAsFavorite = true;

                            viewModel.addMovie(favoriteMovie);
                            Log.d(TAG, "added movie to favorites");
                            //Toast.makeText(getApplicationContext(),"added movie to favorites", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            // un-mark as favorite and delete from favorites database
                            favoritesImageView.setImageResource(R.drawable.ic_star_border_yellow_24dp);
                            isMarkedAsFavorite = false;

                            viewModel.deleteMovie(favoriteMovie);
                            Log.d(TAG, "deleted movie from favorites");
                        }
                    }
                });
            }
            else
            {
                Log.e(TAG, "Movie is null");
            }
        }
    }
}
