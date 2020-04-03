package com.example.android.popularmovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.database.FavoriteMovie;
import com.example.android.popularmovies.database.MovieViewModel;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.MovieJsonUtils;
import com.example.android.popularmovies.utilities.TLSSocketFactory;

import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;

    private TextView errorMessageDisplay;
    private ProgressBar loadingIndicator;

    private TextView noFavoritesMessageDisplay;

    // key when storing or retrieving the selected option
    private static final String STATE_OPTION = "option";

    // load movies by most popular, highest rated, or favorites
    private String option;

    // strings that option can be set to
    private static final String STRING_POPULAR = "popular";
    private static final String STRING_HIGHEST_RATED = "highest rated";
    private static final String STRING_FAVORITES = "favorites";

    // Movie key when using Intent
    private static final String EXTRA_MOVIE = "Movie";

    private MovieViewModel viewModel;

    // list of all favorite movies in Room database
    List<FavoriteMovie> allFavoriteMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Android APIs >= 16 & < 22 have TLS 1.2 disabled by default,
        so we enable TLS on older devices.

        Otherwise an SSL handshake aborted error will
        occur and the app won't connect to the Internet.
        */
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
            try {
                SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, null, null);
                SSLSocketFactory tlsSocketFactory = null;

                tlsSocketFactory = new TLSSocketFactory(sslContext.getSocketFactory());

                // using HttpsURLConnection to set the socket factory to TLS
                HttpsURLConnection.setDefaultSSLSocketFactory(tlsSocketFactory);
            } catch (Exception e) {
                Log.e("TLS", "Error while setting TLSv1.2");
            }
        }

        recyclerView = findViewById(R.id.rv_movies);

        errorMessageDisplay = findViewById(R.id.tv_error_message_display);

        noFavoritesMessageDisplay = findViewById(R.id.tv_empty_favorites_message_display);

        loadingIndicator = findViewById(R.id.pb_loading_indicator);

        // 2 column grid layout
        GridLayoutManager layoutManager =
                new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);

        movieAdapter = new MovieAdapter(this);
        recyclerView.setAdapter(movieAdapter);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore option from saved state
            option = savedInstanceState.getString(STATE_OPTION);
        } else {
            // default option is most popular
            option = STRING_POPULAR;
        }


        viewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        viewModel.getAllMovies().observe(this, new Observer<List<FavoriteMovie>>() {
            @Override
            public void onChanged(@Nullable final List<FavoriteMovie> favoriteMovies) {
                //Log.d(TAG, "favorites list has changed");
                if (favoriteMovies != null) {
                    allFavoriteMovies = favoriteMovies;

                    // reload favorites if returning from details activity or rotating screen
                    if (option.equals(STRING_FAVORITES))
                    {
                        Log.d(TAG, "observed and loading favorites collection");
                        movieAdapter.setFavoriteMovies(null);
                        loadFavoriteMovies();
                    }
                }
            }
        });

        if (option != null && !option.equals(STRING_FAVORITES)) {
            loadMovieData();
        }
    }

    /** Save the sort option if for example the user changes orientation
     *
     * @param savedInstanceState the Bundle object that is saved in the event that
     * MainActivity is destroyed unexpectedly
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        // Save the current selected option
        savedInstanceState.putString(STATE_OPTION, option);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_most_popular:
                option = STRING_POPULAR;
                movieAdapter.setMovieData(null);
                loadMovieData();
                return true;
            case R.id.action_sort_by_highest_rated:
                option = STRING_HIGHEST_RATED;
                movieAdapter.setMovieData(null);
                loadMovieData();
                return true;
            case R.id.action_display_favorites:
                option = STRING_FAVORITES;
                movieAdapter.setFavoriteMovies(null);
                // load favorites collection
                loadFavoriteMovies();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(Movie movie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(EXTRA_MOVIE, movie);
        startActivity(intentToStartDetailActivity);
    }

    /**
     * This method will get the user's preferred sorting option, and then tell the AsyncTask
     * background method to get the movie data in the background.
     */
    private void loadMovieData() {
        showMovieDataView();

        new FetchMoviesTask().execute(option);
    }

    private void loadFavoriteMovies()
    {
        showMovieDataView();

        // if there are movies in the favorites collection, show them
        if (allFavoriteMovies != null && allFavoriteMovies.size() > 0) {
            movieAdapter.setFavoriteMovies(allFavoriteMovies);
        }
        else
        {
            showNoFavoritesMessage();
        }
    }

    /**
     * This method will make the View for the movie data visible and
     * hide the error message.
     */
    private void showMovieDataView() {
        // First, make sure the error is invisible
        errorMessageDisplay.setVisibility(View.INVISIBLE);
        // Then, make sure the movie data is visible
        recyclerView.setVisibility(View.VISIBLE);

        // Also make sure the no favorites message is gone
        noFavoritesMessageDisplay.setVisibility(View.GONE);
    }

    /**
     * This method will make the error message visible and hide the movie
     * View.
     */
    private void showErrorMessage() {
        // First, hide the currently visible data
        recyclerView.setVisibility(View.INVISIBLE);
        // Then, show the error
        errorMessageDisplay.setVisibility(View.VISIBLE);

        // Also make sure the no favorites message is gone
        noFavoritesMessageDisplay.setVisibility(View.GONE);
    }

    /**
     * This method will make the empty favorites message visible and hide the movie
     * View and error message.
     */
    private void showNoFavoritesMessage() {
        // first, hide the currently visible data
        recyclerView.setVisibility(View.INVISIBLE);
        // hide the error message because it's unrelated
        errorMessageDisplay.setVisibility(View.GONE);
        // then, show the no favorites message
        noFavoritesMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String option = params[0];
            URL movieRequestUrl = NetworkUtils.buildUrl(option);

            try {

                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                List<Movie> simpleJsonMovieData = MovieJsonUtils
                        .getSimpleMovieStringsFromJson(MainActivity.this, jsonMovieResponse);

                return simpleJsonMovieData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movieData) {
            loadingIndicator.setVisibility(View.INVISIBLE);
            if (movieData != null) {
                showMovieDataView();
                movieAdapter.setMovieData(movieData);
            } else {
                showErrorMessage();
            }
        }
    }

}
