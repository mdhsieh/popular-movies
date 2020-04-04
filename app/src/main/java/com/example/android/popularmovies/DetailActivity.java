package com.example.android.popularmovies;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.database.FavoriteMovie;
import com.example.android.popularmovies.database.MovieViewModel;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.MovieJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private MovieViewModel viewModel;

    // star button that user can press to mark movie as a favorite
    private ImageView favoritesImageView;
    // boolean to mark as favorite or not favorite
    private boolean isMarkedAsFavorite = false;

    private ImageView backdropImageDisplay;
    private TextView titleDisplay;
    private TextView synopsisDisplay;
    private TextView userRatingDisplay;
    private TextView releaseDateDisplay;

    private Button firstVideoButton;
    private Button secondVideoButton;
    private Button thirdVideoButton;

    private Movie movie;

    // Movie key when retrieving Intent
    private static final String EXTRA_MOVIE = "Movie";

    private String backdropURL;
    private int id;
    private String title;
    private String synopsis;
    private int userRating;
    private String releaseDate;

    // ArrayList to hold all the YouTube URLs of this Movie
    // We will only display maximum 3 videos
    private static List<String> videoUrls = new ArrayList<>();

    // the position of the videos in the ArrayList
    private int FIRST_VIDEO_INDEX = 0;
    private int SECOND_VIDEO_INDEX = 1;
    private int THIRD_VIDEO_INDEX = 2;

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

        firstVideoButton = findViewById(R.id.btn_video_1);
        secondVideoButton = findViewById(R.id.btn_video_2);
        thirdVideoButton = findViewById(R.id.btn_video_3);

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

                /* Check if this movie already exists in the favorite movies database.
                   Mark the favorites button if it does, otherwise leave un-marked. */
                viewModel.getAllMovies().observe(this, new Observer<List<FavoriteMovie>>() {
                    @Override
                    public void onChanged(@Nullable final List<FavoriteMovie> favoriteMovies) {
                        if (favoriteMovies != null) {
                            for (int i = 0; i < favoriteMovies.size(); i++) {

                                if (favoriteMovies.get(i).getId() == id)
                                {
                                    isMarkedAsFavorite = true;
                                    favoritesImageView.setImageResource(R.drawable.ic_star_yellow_24dp);
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

                        if (!isMarkedAsFavorite)
                        {
                            // mark as favorite and insert into favorites database
                            favoritesImageView.setImageResource(R.drawable.ic_star_yellow_24dp);
                            isMarkedAsFavorite = true;

                            viewModel.addMovie(favoriteMovie);
                            //Toast.makeText(getApplicationContext(),"Added movie to favorites.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            // un-mark as favorite and delete from favorites database
                            favoritesImageView.setImageResource(R.drawable.ic_star_border_yellow_24dp);
                            isMarkedAsFavorite = false;

                            viewModel.deleteMovie(favoriteMovie);
                        }
                    }
                });
            }
            else
            {
                Log.e(TAG, "Movie is null");
            }

            /*
                Get the video URLs from this movie in a background task
             */
            new FetchVideosFromMovieTask().execute(id);

            /*
                Click on video button 1. Play the corresponding video if it exists.
             */
            firstVideoButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (videoUrls != null && videoUrls.size() >= 1) {
                        String videoUrl = videoUrls.get(FIRST_VIDEO_INDEX);
                        playVideo(videoUrl);
                    }
                }
            });

            /*
                Click on video button 2. Play the corresponding video if it exists.
             */
            secondVideoButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (videoUrls != null && videoUrls.size() >= 2) {
                        String videoUrl = videoUrls.get(SECOND_VIDEO_INDEX);
                        playVideo(videoUrl);
                    }
                }
            });

            /*
                Click on video button 3. Play the corresponding video if it exists.
             */
            thirdVideoButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (videoUrls != null && videoUrls.size() >= 3) {
                        String videoUrl = videoUrls.get(THIRD_VIDEO_INDEX);
                        playVideo(videoUrl);
                    }
                }
            });
        }
    }

    private void playVideo(String urlText) {
        Uri videoUri = Uri.parse(urlText);
        Intent playVideoIntent = new Intent(Intent.ACTION_VIEW);
        playVideoIntent.setData(videoUri);
        if (playVideoIntent.resolveActivity(getPackageManager()) != null) {
            Log.d(TAG, "starting video using URL " + urlText);
            startActivity(playVideoIntent);
        }
    }

    static class FetchVideosFromMovieTask extends AsyncTask<Integer, Void, List<String>>
    {
        @Override
        protected List<String> doInBackground(Integer... integers) {
            if (integers.length == 0) {
                return null;
            }

            int id = integers[0];

            // URL to get the videos
            URL videoUrl = NetworkUtils.buildVideoUrl(id);

            try {

                String jsonVideoResponse = NetworkUtils
                        .getResponseFromHttpUrl(videoUrl);

                List<String> simpleJsonVideoStrings = MovieJsonUtils
                        .getSimpleVideoStringsFromJson(jsonVideoResponse);

                return simpleJsonVideoStrings;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);

            DetailActivity.videoUrls = strings;

            for (int i = 0; i < videoUrls.size(); i++)
            {
                Log.d(TAG, "video url " + videoUrls.get(i));
            }
        }
    }
}
