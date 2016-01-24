package com.example.mshukla.cinepop;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mshukla.cinepop.Model.Movie;
import com.example.mshukla.cinepop.Util.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {

    private Movie movie;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.release_date)
    TextView releaseDate;
    @Bind(R.id.vote_average)
    TextView voteAverage;
    @Bind(R.id.overview)
    TextView overview;
    @Bind(R.id.movie_poster)
    ImageView mMoviePoster;

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, view);
        if(arguments != null) {
            movie = arguments.getParcelable(Constants.MOVIE_BUNDLE_KEY);
            Glide.with(this).load(Constants.IMAGE_URL + movie.getPosterPath())
                    .placeholder(R.drawable.movie_placeholder)
                    .crossFade()
                    .into(mMoviePoster);

            title.setText(movie.getTitle());
            releaseDate.setText(movie.getReleaseDate());
            voteAverage.setText(movie.getVoteAverage().toString());
            overview.setText(movie.getOverview());
        }

        return view;
    }
}
