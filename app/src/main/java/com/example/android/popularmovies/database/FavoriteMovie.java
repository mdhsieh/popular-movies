package com.example.android.popularmovies.database;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "movie_table")
public class FavoriteMovie { //implements Parcelable {

    private final String BASE_URL = "http://image.tmdb.org/t/p/";
    private final String POSTER_SIZE = "w185";
    private final String BACKDROP_SIZE = "w342";

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
        this.posterURL = posterURL; // BASE_URL + POSTER_SIZE + posterURL;
        this.synopsis = synopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.backdropURL = backdropURL;  // BASE_URL + BACKDROP_SIZE + backdropURL;
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


    // we only want the last part of the poster URL when constructing a new Movie object
    // from a FavoriteMovie object
    public String getPartialPosterURL()
    {
        String partialPosterURL = posterURL;
        partialPosterURL = partialPosterURL.replace(BASE_URL, "");
        partialPosterURL = partialPosterURL.replace(POSTER_SIZE, "");
        return partialPosterURL;
    }

    // we only want the last part of the backdrop URL when constructing a new Movie object
    // from a FavoriteMovie object
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
    public void setBASE_URL(String BASE_URL) {}
    public void setBACKDROP_SIZE(String BACKDROP_SIZE) {}
    public void setPOSTER_SIZE(String POSTER_SIZE) {}

    /* in the case you have more than one field to retrieve from a given Parcel,
    you must do this in the same order you put them in (that is, in a FIFO approach)*/

    /*
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

    // this is used to regenerate the object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<FavoriteMovie> CREATOR = new Parcelable.Creator<FavoriteMovie>() {
        public FavoriteMovie createFromParcel(Parcel in) {
            return new FavoriteMovie(in);
        }

        public FavoriteMovie[] newArray(int size) {
            return new FavoriteMovie[size];
        }
    };

    // constructor that takes a Parcel and gives you an object populated with its values
    private FavoriteMovie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        posterURL = in.readString();
        synopsis = in.readString();
        userRating = in.readInt();
        releaseDate = in.readString();
        backdropURL = in.readString();
    }
    */
}

