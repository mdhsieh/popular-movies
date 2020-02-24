package com.example.android.popularmovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.OpenMovieJsonUtils;

import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;

    private TextView errorMessageDisplay;
    private ProgressBar loadingIndicator;

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
                Log.d("TLS", "Error while setting TLSv1.2");
            }
        }

        // data to populate the RecyclerView with
        ArrayList<String> movieNames = new ArrayList<>();
        movieNames.add("Horse");
        movieNames.add("Cow");
        movieNames.add("Camel");
        movieNames.add("Sheep");
        movieNames.add("Goat");

        recyclerView = findViewById(R.id.rv_movies);

        errorMessageDisplay = findViewById(R.id.tv_error_message_display);

        loadingIndicator = findViewById(R.id.pb_loading_indicator);

        /* LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false); */

        // 2 column grid layout
        GridLayoutManager layoutManager =
                new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);

        movieAdapter = new MovieAdapter(movieNames, this);
        recyclerView.setAdapter(movieAdapter);

        loadMovieData();
    }

    @Override
    public void onItemClick(String movie, int position) {
        // Toast.makeText(this, "You clicked " + movie + " on row number " + position, Toast.LENGTH_SHORT).show();
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, movie);
        startActivity(intentToStartDetailActivity);
    }

    /**
     * This method will get the user's preferred sorting option, and then tell some
     * background method to get the movie data in the background.
     */
    private void loadMovieData() {
        showMovieDataView();

        String option = "popular";
        new FetchMoviesTask().execute(option);

        //String location = SunshinePreferences.getPreferredWeatherLocation(this);
        //new FetchWeatherTask().execute(location);
    }

    /**
     * This method will make the View for the movie data visible and
     * hide the error message.
     */
    private void showMovieDataView() {
        // First, make sure the error is invisible
        errorMessageDisplay.setVisibility(View.INVISIBLE);
        // Then, make sure the weather data is visible
        // mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the movie
     * View.
     */
    private void showErrorMessage() {
        // First, hide the currently visible data
        // mRecyclerView.setVisibility(View.INVISIBLE);
        // Then, show the error
        errorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String option = params[0];
            URL movieRequestUrl = NetworkUtils.buildUrl(option);

            try {

                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                String[] simpleJsonMovieData = OpenMovieJsonUtils
                        .getSimpleMovieStringsFromJson(MainActivity.this, jsonMovieResponse);

                Log.d(TAG, "json response: " + jsonMovieResponse);

                for (int i = 0; i < simpleJsonMovieData.length; i++) {
                    Log.d(TAG, "parsed movie title " + i + ": " + simpleJsonMovieData[i]);
                }

                return simpleJsonMovieData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] movieData) {
            loadingIndicator.setVisibility(View.INVISIBLE);
            if (movieData != null) {
                showMovieDataView();
                Toast.makeText(getApplicationContext(), "Finished execution", Toast.LENGTH_LONG).show();
                // mForecastAdapter.setWeatherData(weatherData);
            } else {
                showErrorMessage();
            }
        }
    }

}
