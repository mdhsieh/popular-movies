package com.example.android.popularmovies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.popularmovies.model.Movie;

public class DetailActivity extends AppCompatActivity {

    private TextView movieDisplay;
    private Movie movie;

    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        movieDisplay = (TextView) findViewById(R.id.tv_display_title);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra("Movie"))
        {
            movie = intentThatStartedThisActivity.getParcelableExtra("Movie");

            title = movie.getTitle();
            movieDisplay.setText(title);
        }
    }
}
