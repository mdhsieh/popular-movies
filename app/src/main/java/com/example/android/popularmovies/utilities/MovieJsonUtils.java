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

import android.content.Context;

import com.example.android.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions to handle MovieDB JSON data.
 */
public final class MovieJsonUtils {

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
    public static List<Movie> getSimpleMovieStringsFromJson(Context context, String movieJsonStr)
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
}