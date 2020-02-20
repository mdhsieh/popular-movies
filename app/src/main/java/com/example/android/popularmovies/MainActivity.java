package com.example.android.popularmovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // data to populate the RecyclerView with
        ArrayList<String> movieNames = new ArrayList<>();
        movieNames.add("Horse");
        movieNames.add("Cow");
        movieNames.add("Camel");
        movieNames.add("Sheep");
        movieNames.add("Goat");

        recyclerView = findViewById(R.id.rv_movies);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);

        movieAdapter = new MovieAdapter(movieNames);
        recyclerView.setAdapter(movieAdapter);
    }
}
