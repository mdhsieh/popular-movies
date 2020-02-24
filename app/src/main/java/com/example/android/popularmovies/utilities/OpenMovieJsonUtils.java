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

/**
 * Utility functions to handle OpenWeatherMap JSON data.
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
    public static String[] getSimpleMovieStringsFromJson(Context context, String movieJsonStr)
            throws JSONException {

        /*
        // Weather information. Each day's forecast info is an element of the "list" array
        final String OWM_LIST = "list";

        // All temperatures are children of the "temp" object
        final String OWM_TEMPERATURE = "temp";

        // Max temperature for the day
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";

        final String OWM_WEATHER = "weather";
        final String OWM_DESCRIPTION = "main";

        final String OWM_MESSAGE_CODE = "cod";

        // String array to hold each day's weather String
        String[] parsedWeatherData = null;

        JSONObject forecastJson = new JSONObject(forecastJsonStr);

        // Is there an error?
        if (forecastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    // Location invalid
                    return null;
                default:
                    // Server probably down
                    return null;
            }
        }*/

        JSONObject movieJson = new JSONObject(movieJsonStr);

        JSONArray movieResultsArray = movieJson.optJSONArray("results");

        final int NUM_MOVIE_RESULTS = movieResultsArray.length();
        Log.d(TAG, "Number of movies: " + NUM_MOVIE_RESULTS);

        String[] parsedMovieData = new String[NUM_MOVIE_RESULTS];

        JSONObject movieResultObject;
        String title;
        for (int i = 0; i < NUM_MOVIE_RESULTS; i++)
        {
            movieResultObject = movieResultsArray.optJSONObject(i);
            title = movieResultObject.optString("title");

            // add title to returned movie String
            parsedMovieData[i] = title;
        }

        return parsedMovieData;
    }
}