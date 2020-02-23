package com.example.android.popularmovies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private TextView movieDisplay;
    private String movieName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        movieDisplay = (TextView) findViewById(R.id.tv_display_movie_name);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT))
        {
            movieName = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
            movieDisplay.setText(movieName);
        }
    }
}
