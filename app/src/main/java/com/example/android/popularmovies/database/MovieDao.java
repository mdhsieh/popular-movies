package com.example.android.popularmovies.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MovieDao {
    @Insert
    void insertMovie(FavoriteMovie movie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(FavoriteMovie movie);

    @Delete
    void deleteMovie(FavoriteMovie movie);

    @Query("SELECT * FROM movie_table")
    LiveData<List<FavoriteMovie>> loadAllMovies();

    @Query("SELECT * FROM movie_table WHERE id = :id")
    LiveData<FavoriteMovie> loadMovieById(int id);
}
