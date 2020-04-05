package com.example.android.popularmovies.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "movie_table")
public class FavoriteMovie {

    private final String BASE_URL = "http://image.tmdb.org/t/p/";
    private static String POSTER_SIZE = "w185";
    private static String BACKDROP_SIZE = "w342";

    @PrimaryKey
    private int id;

    private String title;
    @ColumnInfo(name = "poster_url")
    private String posterURL;
    private String synopsis;
    @ColumnInfo(name = "user_rating")
    private int userRating;
    @ColumnInfo(name = "release_date")
    private String releaseDate;
    @ColumnInfo(name = "backdrop_url")
    private String backdropURL;

    public FavoriteMovie(int id, String title, String posterURL, String synopsis, int userRating, String releaseDate, String backdropURL) {
        this.id = id;
        this.title = title;
        this.posterURL = posterURL;
        this.synopsis = synopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.backdropURL = backdropURL;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public int getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getBackdropURL() {
        return backdropURL;
    }


    // We only want the last part of the poster URL when constructing a new Movie object
    // from a FavoriteMovie object.
    public String getPartialPosterURL()
    {
        String partialPosterURL = posterURL;
        partialPosterURL = partialPosterURL.replace(BASE_URL, "");
        partialPosterURL = partialPosterURL.replace(POSTER_SIZE, "");
        return partialPosterURL;
    }

    // We only want the last part of the backdrop URL when constructing a new Movie object
    // from a FavoriteMovie object.
    public String getPartialBackdropURL() {
        String partialBackdropURL = backdropURL;
        partialBackdropURL = partialBackdropURL.replace(BASE_URL, "");
        partialBackdropURL = partialBackdropURL.replace(BACKDROP_SIZE, "");
        return partialBackdropURL;
    }

    // required for private variables in Room entity
    public String getBASE_URL() { return BASE_URL; }
    public String getBACKDROP_SIZE() {
        return BACKDROP_SIZE;
    }
    public String getPOSTER_SIZE() { return POSTER_SIZE; }
    public void setBASE_URL(String BASE_URL) { }
    public static void setBACKDROP_SIZE(String BACKDROP_SIZE) {
        FavoriteMovie.BACKDROP_SIZE = BACKDROP_SIZE;
    }
    public static void setPOSTER_SIZE(String POSTER_SIZE) {
        FavoriteMovie.POSTER_SIZE = POSTER_SIZE;
    }

}

