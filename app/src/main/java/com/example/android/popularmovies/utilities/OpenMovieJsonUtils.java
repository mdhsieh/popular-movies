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

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions to handle MovieDB JSON data.
 */
public final class OpenMovieJsonUtils {

    private static final String TAG = OpenMovieJsonUtils.class.getSimpleName();

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the movie from the list of movie results.
     *
     * @param movieJsonStr JSON response from server
     *
     * @return Array of Strings describing weather data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static List<String> getSimpleMovieStringsFromJson(Context context, String movieJsonStr)
            throws JSONException {

        JSONObject movieJson = new JSONObject(movieJsonStr);

        JSONArray movieResultsArray = movieJson.optJSONArray("results");

        final int NUM_MOVIE_RESULTS = movieResultsArray.length();
        Log.d(TAG, "Number of movies: " + NUM_MOVIE_RESULTS);

        // String[] parsedMovieData = new String[NUM_MOVIE_RESULTS];
        ArrayList<String> parsedMovieData = new ArrayList<String>();

        JSONObject movieResultObject;
        String title;
        for (int i = 0; i < NUM_MOVIE_RESULTS; i++)
        {
            movieResultObject = movieResultsArray.optJSONObject(i);
            title = movieResultObject.optString("title");

            // add title to returned movie String
            parsedMovieData.add(title);
        }

        return parsedMovieData;
    }
}