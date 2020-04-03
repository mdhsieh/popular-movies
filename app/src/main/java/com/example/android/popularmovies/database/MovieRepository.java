package com.example.android.popularmovies.database;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

class MovieRepository {

    private final static String TAG = MovieRepository.class.getSimpleName();

    private MovieDao movieDao;
    private LiveData<List<FavoriteMovie>> allMovies;

    private static LiveData<FavoriteMovie> movie;

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
//                if (movie != null && movie.getValue() != null) {
//                    Log.d(TAG, "movie in repository is " + movie.getValue().getTitle() + ", id is " + id);
//                }
            }
        });
        return movie;
    }

    /*void loadMovieById(final int id) {
       new loadMovieByIdTask(movieDao).execute(Integer.valueOf(id));
    }

    private static class loadMovieByIdTask extends AsyncTask<Integer, Void, LiveData<FavoriteMovie>>
    {
        private MovieDao movieDao;

        private loadMovieByIdTask(MovieDao movieDao)
        {
            this.movieDao = movieDao;
        }

        @Override
        protected LiveData<FavoriteMovie> doInBackground(Integer... integers) {
            int id = integers[0].intValue();
            movie = movieDao.loadMovieById(id);
            return movie;
        }

        @Override
        protected void onPostExecute(LiveData<FavoriteMovie> favoriteMovieLiveData) {
            super.onPostExecute(favoriteMovieLiveData);
            Log.d(TAG, "finished loading movie from id");
            if (favoriteMovieLiveData != null)
            {
                Log.d(TAG, "movie live data is not null");
                if (favoriteMovieLiveData.getValue() != null)
                {
                    Log.d(TAG, "movie is not null!");
                }
            }
        }
    }

    LiveData<FavoriteMovie> getMovieById(final int id) {
        loadMovieById(id);
        return movie;
    }*/

    /*void loadMovieById(final int id) {
        MovieRoomDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                movie = movieDao.loadMovieById(id);
            }
        });
    }

    LiveData<FavoriteMovie> getMovieById(final int id) {
        loadMovieById(id);
        return movie;
    }*/
}
