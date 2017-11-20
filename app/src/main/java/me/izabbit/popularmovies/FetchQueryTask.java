package me.izabbit.popularmovies;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import me.izabbit.popularmovies.utilities.JsonUtils;
import me.izabbit.popularmovies.utilities.NetworkUtils;

public class FetchQueryTask extends AsyncTask<URL, Void, ArrayList<Movie>> {
    Context context;
    private QueryTaskCompleteListener<ArrayList<Movie>> listener;

    public FetchQueryTask(Context context, QueryTaskCompleteListener<ArrayList<Movie>> listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected ArrayList<Movie> doInBackground(URL... params) {
        URL searchUrl = params[0];
        try {
            String jsonResponse = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            return JsonUtils.getMovieDataFromJson(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> movieData) {
        if (movieData != null) {
            listener.onTaskComplete(movieData);
        }
    }
}
