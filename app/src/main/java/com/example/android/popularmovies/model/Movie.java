package com.example.android.popularmovies.model;

public class Movie {

    private int id;
    // private String originalTitle;
    private String title;
    private String posterImage;
    private String synopsis;
    private int userRating;
    private String releaseDate;

    public Movie() {
    }

    public Movie(int id, String title, String posterImage, String synopsis, int userRating, String releaseDate) {
        this.id = id;
        this.title = title;
        this.posterImage = posterImage;
        this.synopsis = synopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterImage() {
        return posterImage;
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
}
