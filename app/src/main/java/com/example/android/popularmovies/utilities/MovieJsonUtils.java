/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.popularmovies.utilities;

import android.util.Log;

import com.example.android.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions to handle MovieDB JSON data.
 */
public final class MovieJsonUtils {

    private final static String TAG = MovieJsonUtils.class.getSimpleName();

    /**
     * This method parses JSON from a web response and returns an ArrayList of Movies
     * describing the movie from the list of movie results.
     *
     * @param movieJsonStr JSON response from server
     *
     * @return ArrayList of Movies describing movie data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static List<Movie> getSimpleMovieStringsFromJson(String movieJsonStr)
            throws JSONException {

        JSONObject movieJson = new JSONObject(movieJsonStr);

        JSONArray movieResultsArray = movieJson.optJSONArray("results");

        final int NUM_MOVIE_RESULTS = movieResultsArray.length();

        ArrayList<Movie> parsedMovieData = new ArrayList<Movie>();

        JSONObject movieResultObject;

        Movie movie;
        int id;
        String title;
        String posterPath;
        String overview;
        int userRating;
        String releaseDate;
        String backdropPath;

        // URL to get video
        //URL videoURL;

        for (int i = 0; i < NUM_MOVIE_RESULTS; i++)
        {
            movieResultObject = movieResultsArray.optJSONObject(i);
            id = movieResultObject.optInt("id");
            title = movieResultObject.optString("title");
            posterPath = movieResultObject.optString("poster_path");
            overview = movieResultObject.optString("overview");
            userRating = movieResultObject.optInt("vote_average");
            releaseDate = movieResultObject.optString("release_date");
            backdropPath = movieResultObject.optString("backdrop_path");

            movie = new Movie(id, title, posterPath, overview, userRating, releaseDate, backdropPath);

            parsedMovieData.add(movie);
        }

        return parsedMovieData;
    }

    /**
     * This method parses JSON from a specified movie and returns an ArrayList of Strings
     * which are the YouTube links to each video.
     *
     * @param jsonVideoResponse The parsed json response from the specified movie
     *
     * @return list which has the complete YouTube URLs of the movie's videos
     *
     */
    public static List<String> getSimpleVideoStringsFromJson(String jsonVideoResponse) throws JSONException {

        final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";

        // Only include videos that are trailers. Other option is "Featurette"
        final String STRING_TRAILER = "Trailer";

        // list of parsed video URLs that will be returned
        ArrayList<String> parsedVideoStrings = new ArrayList<String>();

        // parse the json string
        JSONObject videoJson = new JSONObject(jsonVideoResponse);

        JSONArray videoResultsArray = videoJson.optJSONArray("results");

        // json object of the entire video response that has info like video  key, name, and type
        JSONObject videoResultObject;
        // string that tells whether each video is a trailer or short featurette
        String videoType;
        // key that is needed to complete the YouTube URL
        String videoKey;

        if (videoResultsArray != null) {
            final int NUM_VIDEO_RESULTS = videoResultsArray.length();

            for (int i = 0; i < NUM_VIDEO_RESULTS; i++) {
                videoResultObject = videoResultsArray.optJSONObject(i);

                videoType = videoResultObject.optString("type");

                videoKey = videoResultObject.optString("key");

                // remove if statement to add all videos to list
                if (videoType.equals(STRING_TRAILER)) {
                    parsedVideoStrings.add(BASE_YOUTUBE_URL + videoKey);
                }
            }
        }

        return parsedVideoStrings;
    }

    /**
     * This method parses JSON from a specified movie and returns an ArrayList of Strings
     * which are viewers' reviews of that movie.
     *
     * @param jsonReviewsResponse The parsed json response from the specified movie
     *
     * @return list which has the reviews of the movie
     *
     */
    public static List<String> getSimpleReviewStringsFromJson(String jsonReviewsResponse) throws JSONException {

        // list of parsed video URLs that will be returned
        ArrayList<String> parsedReviewStrings = new ArrayList<String>();

        //Log.d(TAG, "reviews json string is " + jsonReviewsResponse);

        // parse the json string
        JSONObject reviewsJson = new JSONObject(jsonReviewsResponse);

        JSONArray reviewResultsArray = reviewsJson.optJSONArray("results");

        //Log.d(TAG, "reviews results array is " + reviewResultsArray);

        // json object of the entire review that has info like author, content, and URL
        JSONObject reviewResultObject;
        // author of the review
        String reviewAuthor;
        // content of the review
        String reviewContent;

        if (reviewResultsArray != null) {
            final int NUM_VIDEO_RESULTS = reviewResultsArray.length();

            for (int i = 0; i < NUM_VIDEO_RESULTS; i++) {
                reviewResultObject = reviewResultsArray.optJSONObject(i);

                reviewAuthor = reviewResultObject.optString("author");

                reviewContent = reviewResultObject.optString("content");

                // include both the author and content of the review
                parsedReviewStrings.add("Author: " + reviewAuthor +
                        "\n\n" + reviewContent);
            }
        }

        return parsedReviewStrings;
    }
}