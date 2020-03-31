package com.example.android.popularmovies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    // star button that user can press to mark movie as a favorite
    private ImageView favoritesImageView;
    // boolean to mark as favorite or not favorite
    private boolean isMarkedAsFavorite = false;

    private ImageView backdropImageDisplay;
    //private TextView idDisplay;
    private TextView titleDisplay;
    private TextView synopsisDisplay;
    private TextView userRatingDisplay;
    private TextView releaseDateDisplay;

    private Movie movie;

    private String posterURL;
    private int id;
    private String title;
    private String synopsis;
    private int userRating;
    private String releaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        favoritesImageView = findViewById(R.id.iv_favorite_button);
        favoritesImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMarkedAsFavorite)
                {
                    Log.v(TAG, "Favorite");
                    // mark as favorite and insert into favorites list
                    favoritesImageView.setImageResource(android.R.drawable.btn_star_big_on);
                    isMarkedAsFavorite = true;
                }
                else
                {
                    Log.v(TAG, "Not favorite");
                    // un-mark as favorite and delete from favorites list
                    favoritesImageView.setImageResource(android.R.drawable.btn_star_big_off);
                    isMarkedAsFavorite = false;
                }
            }
        });

        backdropImageDisplay = findViewById(R.id.image_iv);
        //idDisplay = findViewById(R.id.tv_display_id);
        titleDisplay = findViewById(R.id.tv_display_title);
        synopsisDisplay = findViewById(R.id.tv_display_synopsis);
        userRatingDisplay = findViewById(R.id.tv_display_user_rating);
        releaseDateDisplay = findViewById(R.id.tv_display_release_date);

        // get the Movie from Intent
        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra("Movie"))
        {
            movie = intentThatStartedThisActivity.getParcelableExtra("Movie");

            if (movie != null) {

                // get the movie's data
                posterURL = movie.getBackdropURL();

                id = movie.getId();
                title = movie.getTitle();
                synopsis = movie.getSynopsis();
                userRating = movie.getUserRating();
                releaseDate = movie.getReleaseDate();

                // set the displays accordingly, casting int to String if necessary
                Picasso.get().load(posterURL).into(backdropImageDisplay);

                //idDisplay.setText(String.valueOf(id));
                titleDisplay.setText(title);
                synopsisDisplay.setText(synopsis);
                // display user rating / 10
                userRatingDisplay.setText(String.format(getString(R.string.out_of_10), userRating));
                releaseDateDisplay.setText(releaseDate);
            }
            else
            {
                Log.e(TAG, "Movie is null");
            }
        }
    }
}
