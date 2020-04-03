package com.example.android.popularmovies.database;

import android.app.Application;
import android.util.Log;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MovieViewModel extends AndroidViewModel {

    private final static String TAG = MovieViewModel.class.getSimpleName();

    private MovieRepository repository;
    private LiveData<List<FavoriteMovie>> allMovies;

    private LiveData<FavoriteMovie> movie;

    //private final MutableLiveData<FavoriteMovie> selected = new MutableLiveData<FavoriteMovie>();


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
//        if (movie != null && movie.getValue() != null) {
//            Log.d(TAG, "movie in ViewModel is " + movie.getValue().getTitle() + ", id is " + id);
//        }
        return movie;
    }

    /*public void select(FavoriteMovie item) {
        selected.setValue(item);
    }

    public LiveData<FavoriteMovie> getSelected() {
        return selected;
    }*/
}
