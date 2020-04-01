package com.example.android.popularmovies.database;

import android.app.Application;
import android.util.Log;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

class MovieRepository {

    private MovieDao movieDao;
    private LiveData<List<FavoriteMovie>> allMovies;

    private LiveData<FavoriteMovie> movie;

    MovieRepository(@NonNull Application application) {
        MovieRoomDatabase database = MovieRoomDatabase.getInstance(application);
        movieDao = database.movieDao();
        allMovies = movieDao.loadAllMovies();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<FavoriteMovie>> getAllMovies() {
        return allMovies;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void addMovie(final FavoriteMovie movie) {
        MovieRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                movieDao.insert(movie);
            }
        });
    }

    void deleteMovie(final FavoriteMovie movie) {
        MovieRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                movieDao.delete(movie);
            }
        });
    }

    LiveData<FavoriteMovie> getMovieById(final int id) {
        MovieRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                movie = movieDao.loadMovieById(id);
                if (movie != null) {
                    Log.d("MovieRepository", "movie in repository is " + movie + ", id is " + id);
                }
            }
        });
        return movie;
    }
}
