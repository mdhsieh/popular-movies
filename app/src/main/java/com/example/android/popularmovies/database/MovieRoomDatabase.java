package com.example.android.popularmovies.database;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = FavoriteMovie.class, version = 1, exportSchema = false)
public abstract class MovieRoomDatabase extends RoomDatabase {
    public abstract MovieDao movieDao();

    private static final String DATABASE_NAME = "movie_database";

    private static volatile MovieRoomDatabase movieRoomInstance;

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static MovieRoomDatabase getInstance(Context context)
    {
        if (movieRoomInstance == null)
        {
            synchronized(MovieRoomDatabase.class) {
                if (movieRoomInstance == null) {
                    // Creating new database instance
                    movieRoomInstance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            MovieRoomDatabase.class,
                            DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return movieRoomInstance;
    }
}