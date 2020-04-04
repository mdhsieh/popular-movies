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

import java.lang.ref.WeakReference;
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

    // ArrayList to hold all the YouTube URLs of this Movie.
    // We will only display maximum 3 videos.
    // This is static because we will use it in a static AsyncTask.
    private static List<String> videoUrls = new ArrayList<>();

    // the position of the videos in the ArrayList
    private int FIRST_VIDEO_INDEX = 0;
    private int SECOND_VIDEO_INDEX = 1;
    private int THIRD_VIDEO_INDEX = 2;

    // ArrayList to hold all the reviews of this Movie
    private static List<String> allReviews = new ArrayList<>();

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
            new FetchVideosFromMovieTask(this).execute(id);

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

            /*
                Get the reviews of this movie in a background task
             */
            new FetchReviewsFromMovieTask(this).execute(id);
        }
    }

    // play a video using an Intent given a URL
    private void playVideo(String urlText) {
        Uri videoUri = Uri.parse(urlText);
        Intent playVideoIntent = new Intent(Intent.ACTION_VIEW);
        playVideoIntent.setData(videoUri);
        if (playVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(playVideoIntent);
        }
    }

    // background task to fetch YouTube video URLs
    static class FetchVideosFromMovieTask extends AsyncTask<Integer, Void, List<String>>
    {
        // use a weak reference to DetailActivity to get UI components and still avoid potential memory leak
        // source: https://stackoverflow.com/questions/44309241/warning-this-asynctask-class-should-be-static-or-leaks-might-occur
        private WeakReference<DetailActivity> activityReference;

        // only retain a weak reference to the activity
        FetchVideosFromMovieTask(DetailActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<String> doInBackground(Integer... integers) {
            if (integers.length == 0) {
                return null;
            }

            int id = integers[0];

            // URL to get the videos
            URL videoUrl = NetworkUtils.buildVideosUrl(id);

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

            // get the video URLs
            DetailActivity.videoUrls = strings;

            // get a reference to the activity if it is still there
            DetailActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            // modify the activity's UI
            TextView videosLabel = activity.findViewById(R.id.textView6);

            Button firstVideoButton = activity.findViewById(R.id.btn_video_1);
            Button secondVideoButton = activity.findViewById(R.id.btn_video_2);
            Button thirdVideoButton = activity.findViewById(R.id.btn_video_3);

            // first set videos label to visible
            videosLabel.setVisibility(View.VISIBLE);

            // next set all video buttons to visible
            firstVideoButton.setVisibility(View.VISIBLE);
            secondVideoButton.setVisibility(View.VISIBLE);
            thirdVideoButton.setVisibility(View.VISIBLE);

            if (videoUrls != null) {
                int numVideos = videoUrls.size();

                /* If a movie only has a certain number of videos,
                   don't show the remaining buttons */
                if (numVideos < 1) {
                    // We don't show the videos label if the movie has no videos
                    videosLabel.setVisibility(View.GONE);

                    // there are no videos, so hide all buttons
                    firstVideoButton.setVisibility(View.GONE);
                    secondVideoButton.setVisibility(View.GONE);
                    thirdVideoButton.setVisibility(View.GONE);
                } else if (numVideos < 2) {
                    // there is 1 video, so hide 2nd and 3rd buttons
                    secondVideoButton.setVisibility(View.GONE);
                    thirdVideoButton.setVisibility(View.GONE);
                } else if (numVideos < 3) {
                    // there are 2 videos, so hide 3rd buttons
                    thirdVideoButton.setVisibility(View.GONE);
                }
            }
            else
            {
                Log.e(TAG, "Video URLs list is null.");
            }
        }
    }

    static class FetchReviewsFromMovieTask extends AsyncTask<Integer, Void, List<String>>
    {
        private WeakReference<DetailActivity> activityReference;

        // only retain a weak reference to the activity
        FetchReviewsFromMovieTask(DetailActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<String> doInBackground(Integer... integers) {
            if (integers.length == 0) {
                return null;
            }

            int id = integers[0];

            // URL to get the reviews
            URL reviewsUrl = NetworkUtils.buildReviewsUrl(id);

            try {

                String jsonReviewsResponse = NetworkUtils
                        .getResponseFromHttpUrl(reviewsUrl);

                List<String> simpleJsonReviewStrings = MovieJsonUtils
                        .getSimpleReviewStringsFromJson(jsonReviewsResponse);

                return simpleJsonReviewStrings;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);

            // get the reviews
            allReviews = strings;

            // get a reference to the activity if it is still there
            DetailActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            // modify the activity's UI
            TextView reviewsLabel = activity.findViewById(R.id.textView7);
            // the TextView which will hold all reviews
            TextView reviewsDisplay = activity.findViewById(R.id.tv_display_reviews);

            if (allReviews != null)
            {
                if (allReviews.size() < 1)
                {
                    /* If there are no reviews available,
                     remove the reviews label and hide the TextView */
                    reviewsLabel.setVisibility(View.GONE);
                    reviewsDisplay.setVisibility(View.INVISIBLE);
                }
                else
                {
                    /*
                        Set both the reviews label and reviews themselves visible/
                     */
                    reviewsLabel.setVisibility(View.VISIBLE);
                    reviewsDisplay.setVisibility(View.VISIBLE);

                    String review;

                    for (int i = 0; i < allReviews.size(); i++)
                    {
                        //Log.d(TAG, review);
                        review = allReviews.get(i);
                        reviewsDisplay.append(review);
                        // Separate reviews with a newline.
                        reviewsDisplay.append("\n\n");
                    }
                }
            }
        }
    }
}
