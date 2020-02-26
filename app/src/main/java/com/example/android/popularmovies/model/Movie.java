package com.example.android.popularmovies.model;

/* from https://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents?page=1&tab=votes#tab-top
 */

import android.os.Parcel;
import android.os.Parcelable;

// implement Parcelable to pass Movie to detail screen using Intents
public class Movie implements Parcelable {

    private int id;
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

    /* in the case you have more than one field to retrieve from a given Parcel,
    you must do this in the same order you put them in (that is, in a FIFO approach)*/

    @Override
    public int describeContents() {
        return 0;
    }

    // write object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(posterImage);
        dest.writeString(synopsis);
        dest.writeInt(userRating);
        dest.writeString(releaseDate);
    }

    // this is used to regenerate the object. All Parcelables must have a CREATOR that implements these two methods
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
        posterImage = in.readString();
        synopsis = in.readString();
        userRating = in.readInt();
        releaseDate = in.readString();
    }
}