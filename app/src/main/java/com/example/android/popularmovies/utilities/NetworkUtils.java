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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * These utilities will be used to communicate with the weather servers.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    /* In order to request popular and highest rated movies you will want to request data from
     the /movie/popular and /movie/top_rated endpoints */

    // example: "http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]"

    private static final String STRING_POPULAR = "popular";

    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";

    private static final String OPTION_POPULAR_PARAM = "/popular";

    private static final String OPTION_RATED_PARAM = "/top_rated";

    final static String QUESTION_PARAM = "?";

    /*
        In order to fetch a movie's videos and reviews you will want to request data from
        the /movie/{id}/videos and /movie/{id}/reviews endpoints, where {id}
        is a particular movie's ID.
     */

    // example: http://api.themoviedb.org/3/movie/<movie_id>/videos?api_key=<api_key>

    // To get the Youtube URL get the movie's key and use https://www.youtube.com/watch?v=<key>

    private final static String VIDEO_PARAM = "/videos";

    private final static String API_KEY_PARAM = "api_key=" + ApiKey.getApiKey();

    /**
     * Builds the URL used to talk to MovieDB using an option.
     *
     * @param optionQuery The option that will be queried for.
     * @return The URL to use to query MovieDB.
     */
    public static URL buildUrl(String optionQuery) {

        URL url = null;

        String OPTION_PARAM;
        if (optionQuery.equals(STRING_POPULAR))
        {
            OPTION_PARAM = OPTION_POPULAR_PARAM;
        }
        else
        {
            OPTION_PARAM = OPTION_RATED_PARAM;
        }

        try {

            url = new URL(MOVIE_BASE_URL + OPTION_PARAM + QUESTION_PARAM + API_KEY_PARAM);
        } catch (MalformedURLException e) {
            Log.e(TAG, "malformed url exception", e);
            e.printStackTrace();
        }

        Log.v(TAG, "Built URL " + url);

        return url;
    }


    /**
     * Builds the URL used to get the video of a movie
     * specified by the movie's ID.
     *
     * @param id The ID of the movie.
     * @return The URL to use to query MovieDB.
     */
    public static URL buildVideoUrl(int id) {

        URL url = null;

        try {

            url = new URL(MOVIE_BASE_URL + "/" + id + VIDEO_PARAM + QUESTION_PARAM + API_KEY_PARAM);
        } catch (MalformedURLException e) {
            Log.e(TAG, "malformed url exception", e);
            e.printStackTrace();
        }

        Log.v(TAG, "Built video URL " + url);

        return url;
    }


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } catch(Exception exception) {
            Log.e(TAG, "Exception in getResponseFromHttpUrl", exception);
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }
}