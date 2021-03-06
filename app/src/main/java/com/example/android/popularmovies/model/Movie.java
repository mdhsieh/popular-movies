package com.example.android.popularmovies.model;

/* from https://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents?page=1&tab=votes#tab-top
 */

import android.os.Parcel;
import android.os.Parcelable;

// implement Parcelable to pass Movie to detail screen using Intents
public class Movie implements Parcelable {

    private final String BASE_URL = "http://image.tmdb.org/t/p/";
    /*
        To build an image URL to display the poster and backdrop,
        you will need a 'size', which will be one of the following: "w92",
        "w154", "w185", "w342", "w500", "w780", or "original". For most phones
        the recommended size to use is "w185".
     */
    private static String POSTER_SIZE = "w185";
    private static String BACKDROP_SIZE = "w342";

    private int id;
    private String title;
    private String posterURL;
    private String synopsis;
    private int userRating;
    private String releaseDate;
    private String backdropURL;

    public Movie(int id, String title, String posterURL, String synopsis, int userRating, String releaseDate, String backdropURL) {
        this.id = id;
        this.title = title;
        this.posterURL = BASE_URL + POSTER_SIZE + posterURL;
        this.synopsis = synopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.backdropURL = BASE_URL + BACKDROP_SIZE + backdropURL;
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

    /* Change the poster and backdrop size if the app is running on a tablet. */
    public static void setBACKDROP_SIZE(String BACKDROP_SIZE) {
        Movie.BACKDROP_SIZE = BACKDROP_SIZE;
    }
    public static void setPOSTER_SIZE(String POSTER_SIZE) {
        Movie.POSTER_SIZE = POSTER_SIZE;
    }

    /* In the case you have more than one field to retrieve from a given Parcel,
    you must do this in the same order you put them in (that is, in a FIFO approach). */

    @Override
    public int describeContents() {
        return 0;
    }

    // write object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(posterURL);
        dest.writeString(synopsis);
        dest.writeInt(userRating);
        dest.writeString(releaseDate);
        dest.writeString(backdropURL);
    }

    // This is used to regenerate the object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    // constructor that takes a Parcel and gives you an object populated with its values
    private Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        posterURL = in.readString();
        synopsis = in.readString();
        userRating = in.readInt();
        releaseDate = in.readString();
        backdropURL = in.readString();
    }
}
