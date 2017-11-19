package me.izabbit.popularmovies.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {
    private static final String MOVIEDB_URL = "https://api.themoviedb.org/3/movie/";

    // Parameters to build the URL
    private static final String PARAMETER_API_KEY = "api_key";

    /**
     * Builds the URL used to query The Movie Database API.
     *
     * @param sort Sort movies by most popular or top rated
     * @param apiKey The Movie DB API key
     * @return The URL to use to query the GitHub.
     */
    public static URL buildUrl(String sort, String apiKey) {
        Uri builtUri = Uri.parse(MOVIEDB_URL + sort).buildUpon()
                .appendQueryParameter(PARAMETER_API_KEY, apiKey)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

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
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
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
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Checks if Internet access is available
     * Source: https://stackoverflow.com/a/27312494
     * @return Boolean indicating whether or not there is Internet access.
     */
    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }
}
