package com.example.android.popularmovies.database;

import android.app.Application;
import android.util.Log;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class MovieViewModel extends AndroidViewModel {

    private final static String TAG = MovieViewModel.class.getSimpleName();

    private MovieRepository repository;
    private LiveData<List<FavoriteMovie>> allMovies;

    private LiveData<FavoriteMovie> movie;


    public MovieViewModel(@NonNull Application application) {
        super(application);

        repository = new MovieRepository(application);
        allMovies = repository.getAllMovies();
    }

    public LiveData<List<FavoriteMovie>> getAllMovies() {
        return allMovies;
    }

    public void addMovie(FavoriteMovie movie) {
        repository.addMovie(movie);
    }

    public void deleteMovie(FavoriteMovie movie) {
        repository.deleteMovie(movie);
    }

    public LiveData<FavoriteMovie> getMovieById(int id) {
        movie = repository.getMovieById(id);
        return movie;
    }
}
