package com.example.android.popularmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

    // key when storing or retrieving the selected option
    private static final String STATE_OPTION = "option";

    // load movies by most popular or highest rated
    private String option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Creating MainActivity");

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
            //Log.d(TAG, "saved instance state is " + savedInstanceState);
            // default option is most popular
            option = "popular";
        }
        //Log.d(TAG, "option is " + option);
        loadMovieData();
    }

    /** Save the sort option if for example the user changes orientation
     *
     * @param savedInstanceState the Bundle object that is saved in the event that
     * MainActivity is destroyed unexpectedly
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        //Log.d(TAG, "reached savedInstanceState");
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
                option = "popular";
                //Log.d(TAG, "option selected is " + option);
                movieAdapter.setMovieData(null);
                loadMovieData();
                return true;
            case R.id.action_sort_by_highest_rated:
                option = "highest rated";
                //Log.d(TAG, "option selected is " + option);
                movieAdapter.setMovieData(null);
                loadMovieData();
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
        intentToStartDetailActivity.putExtra("Movie", movie);
        startActivity(intentToStartDetailActivity);
    }

    /**
     * This method will get the user's preferred sorting option, and then tell the AsyncTask
     * background method to get the movie data in the background.
     */
    private void loadMovieData() {
        showMovieDataView();

        //Log.d(TAG, "option in loadMovieData is " + option);
        new FetchMoviesTask().execute(option);
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
            //Log.d(TAG, "option in doInBackground is " + option);
            URL movieRequestUrl = NetworkUtils.buildUrl(option);

            try {

                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                //Log.d(TAG, "json movie response is " + jsonMovieResponse);

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
