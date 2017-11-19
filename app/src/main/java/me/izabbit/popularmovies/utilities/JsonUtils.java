package me.izabbit.popularmovies.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.izabbit.popularmovies.Movie;

/**
 * Utility functions to handle Movie DB JSON data.
 */
public final class JsonUtils {
    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing popular movies.
     *
     * @param movieJsonString JSON response from server
     *
     * @return ArrayList of Movie objects
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ArrayList<Movie> getMovieDataFromJson(String movieJsonString)
            throws JSONException {

        if (movieJsonString == null) {
            return null;
        }

        final String RESULTS = "results";
        final String POSTER_PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String ORIGINAL_TITLE = "original_title";
        final String VOTE_AVERAGE = "vote_average";
        final String BACKDROP_PATH = "backdrop_path";

        ArrayList<Movie> movieList = new ArrayList<>();
        JSONObject movieJson = new JSONObject(movieJsonString);
        JSONArray movieArray = movieJson.getJSONArray(RESULTS);

        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movie = movieArray.getJSONObject(i);
            String title = movie.getString(ORIGINAL_TITLE);
            String releaseDate = movie.getString(RELEASE_DATE);
            String[] dateArray = releaseDate.split("-");
            String poster = movie.getString(POSTER_PATH);
            String voteAverage = movie.getString(VOTE_AVERAGE);
            String synopsis = movie.getString(OVERVIEW);
            String backdrop = movie.getString(BACKDROP_PATH);

            Movie newMovie = new Movie(title, dateArray[0], poster, voteAverage, synopsis, backdrop);
            movieList.add(newMovie);
        }
        return movieList;
    }
}