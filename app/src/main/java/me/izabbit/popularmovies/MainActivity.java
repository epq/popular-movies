package me.izabbit.popularmovies;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import me.izabbit.popularmovies.utilities.JsonUtils;
import me.izabbit.popularmovies.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String STATE_SORT = "sort";
    private static final String STATE_MOVIE_LIST = "movieList";
    private static final String INTENT_MOVIE = "movie";

    private GridView mGridView;
    private String mSortOrder = "Most Popular"; // movies list sort order, default is popular
    private MovieAdapter mMovieAdapter;
    private String apiKey = ""; // The Movie Database API Key from secrets.xml

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mGridView = findViewById(R.id.grid_view);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Context context = parent.getContext();
                Class destinationClass = MovieDetailActivity.class;
                Intent intentToStartDetailActivity = new Intent(context, destinationClass);
                Movie movie = (Movie) parent.getAdapter().getItem(position);
                intentToStartDetailActivity.putExtra(INTENT_MOVIE, movie);
                startActivity(intentToStartDetailActivity);
            }
        });

        mMovieAdapter = new MovieAdapter(MainActivity.this);
        apiKey = getString(R.string.api_key);

        // Restore sort order and scroll position of movie list on screen orientation change
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_SORT)) {
                mSortOrder = savedInstanceState.getString(STATE_SORT);
            }
            if (savedInstanceState.containsKey(STATE_MOVIE_LIST)) {
                ArrayList<Movie> movieList =
                        savedInstanceState.getParcelableArrayList(STATE_MOVIE_LIST);
                // Check for movieList == null to prevent app from crashing when there is no
                // Internet connection and device orientation changes
                if (movieList != null && !movieList.isEmpty()) {
                    loadData(movieList);
                }
                else {
                    makeMovieQuery();
                }
            }
        }
        else {
            // Show the popular movies  by default if there was no previous state
            makeMovieQuery();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_SORT, mSortOrder);
        outState.putParcelableArrayList(STATE_MOVIE_LIST, mMovieAdapter.getItems());
        super.onSaveInstanceState(outState);
    }

    /**
     * Create the spinner options for most popular/top rated in the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) item.getActionView();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setSelected(false);
        // Needed to avoid onItemSelected calls during initialization to keep GridView position
        // See: https://stackoverflow.com/questions/13397933/android-spinner-avoid-onitemselected-calls-during-initialization/44715988#44715988
        int spinnerPosition = adapter.getPosition(mSortOrder);
        spinner.setSelection(spinnerPosition, true);
        spinner.setOnItemSelectedListener(this);

        return true;
    }

    /**
     * Determines the selected movies lits sort order and calls QueryTask to fetch list of movies
     * in the background.
     */
    private void makeMovieQuery() {
        if (!NetworkUtils.isOnline()) {
            createAlertDialog();
        }
        else {
            String sort = "";
            if (mSortOrder.equals("Most Popular")) {
                sort = "popular";
            }
            else if (mSortOrder.equals("Top Rated")) {
                sort = "top_rated";
            }
            URL url = NetworkUtils.buildUrl(sort, apiKey);
            new QueryTask().execute(url);
        }
    }

    /**
     * Creates alert dialog to display when there is no Internet connection.
     */
    private void createAlertDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Info");
        alertDialog.setMessage("No Internet connection available. Check your connection and " +
                "try again.");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
        mSortOrder = parent.getItemAtPosition(pos).toString();
        makeMovieQuery();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public class QueryTask extends AsyncTask<URL, Void, ArrayList<Movie>> {
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
                loadData(movieData);
            }
        }
    }

    /**
     * Binds GridView to movies list data
     * @param movieData
     */
    private void loadData(ArrayList<Movie> movieData) {
        mMovieAdapter.setItems(movieData);
        mGridView.setAdapter(mMovieAdapter);
    }

}
