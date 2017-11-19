package me.izabbit.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

class MovieAdapter extends ArrayAdapter<Movie> {
    private final Context mContext;
    private ArrayList<Movie> mMoviesArray;

    public MovieAdapter(Context context) {
        super(context, 0);
        this.mContext = context;
    }

    public void setItems(ArrayList<Movie> mMoviesArray) {
        this.mMoviesArray = mMoviesArray;
    }

    public int getCount() {
        return mMoviesArray.size();
    }

    public ArrayList<Movie> getItems() {
        return mMoviesArray;
    }

    public Movie getItem(int position) {
        return mMoviesArray.get(position);
    }

    // create a new ImageView for each item referenced by the Adapter
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }

        Movie movie = getItem(position);

        if (movie != null) {
            String baseUrl = "http://image.tmdb.org/t/p/w185/";
            Glide.with(this.mContext)
                    .load(baseUrl + movie.getPoster())
                    .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                    .apply(new RequestOptions().override(Target.SIZE_ORIGINAL))
                    .into(imageView);
        }
        else {
            imageView.setImageResource(R.drawable.placeholder);
        }
        return imageView;
    }
}
