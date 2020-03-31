package com.example.android.popularmovies.database;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class MovieViewModel extends AndroidViewModel {

    private MovieRepository repository;
    private LiveData<List<FavoriteMovie>> allMovies;


    public MovieViewModel(@NonNull Application application) {
        super(application);

        repository = new MovieRepository(application);
        allMovies = repository.getAllMovies();
    }

    LiveData<List<FavoriteMovie>> getAllMovies() { return allMovies; }

    public void insert(FavoriteMovie movie) {
        repository.insert(movie);
    }
}
