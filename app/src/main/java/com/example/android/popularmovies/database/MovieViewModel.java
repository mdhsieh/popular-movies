package com.example.android.popularmovies.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class MovieViewModel extends AndroidViewModel {

    private MovieDao movieDao;
    private MovieRoomDatabase database;

    public MovieViewModel(@NonNull Application application) {
        super(application);

        database = MovieRoomDatabase.getInstance(application);
        movieDao = database.movieDao();
    }

    public void insertMovie(FavoriteMovie movie)
    {
        new InsertAsyncTask(movieDao).execute(movie);
    }

    private static class InsertAsyncTask extends AsyncTask<FavoriteMovie, Void, Void>
    {
        MovieDao movieDao;

        public InsertAsyncTask(MovieDao movieDao)
        {
            this.movieDao = movieDao;
        }

        @Override
        protected Void doInBackground(FavoriteMovie... favoriteMovies) {
            movieDao.insertMovie(favoriteMovies[0]);
            return null;
        }
    }
}
