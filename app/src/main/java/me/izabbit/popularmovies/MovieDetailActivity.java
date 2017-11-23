package me.izabbit.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {
    @BindView(R.id.tv_title) TextView mMovieTitle;
    @BindView(R.id.iv_backdrop) ImageView mBackdrop;
    @BindView(R.id.iv_poster) ImageView mPoster;
    @BindView(R.id.tv_release_date) TextView mReleaseDate;
    @BindView(R.id.tv_rating) TextView mRating;
    @BindView(R.id.tv_synopsis) TextView mSynopsis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("movie")) {
                Movie movie = intentThatStartedThisActivity.getParcelableExtra("movie");
                mMovieTitle.setText(movie.getTitle());
                loadImage("w500/" + movie.getBackdrop(), mBackdrop);
                loadImage("w185/" + movie.getPoster(), mPoster);
                mReleaseDate.setText(movie.getReleaseDate());
                mRating.setText(String.format(getString(R.string.vote_average_total), movie.getVoteAverage()));
                mSynopsis.setText(movie.getSynopsis());
            }
        }
    }

    /**
     *  Loads the image into the image view. To use with the poster and backdrop images.
     * @param image Image URL without path
     * @param imageView imageView from layout
     */
    private void loadImage(String image, ImageView imageView) {
        String baseUrl =  "http://image.tmdb.org/t/p/";
        Glide.with(MovieDetailActivity.this)
                .load(baseUrl + image)
                .apply(new RequestOptions().fitCenter())
                .apply(new RequestOptions().override(Target.SIZE_ORIGINAL))
                .into(imageView);
    }
}
