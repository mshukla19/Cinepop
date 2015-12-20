package com.example.mshukla.cinepop;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mshukla.cinepop.Model.Movie;
import com.example.mshukla.cinepop.Util.Constants;

import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MovieDetails extends AppCompatActivity {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        movie = getIntent().getParcelableExtra(Constants.MOVIE_BUNDLE_KEY);

        Glide.with(this).load(Constants.IMAGE_URL + movie.getPosterPath())
                .placeholder(R.drawable.movie_placeholder)
                .crossFade()
                .into(mMoviePoster);
        title.setText(movie.getTitle());
        releaseDate.setText(movie.getReleaseDate());
        voteAverage.setText(movie.getVoteAverage().toString());
        overview.setText(movie.getOverview());

    }
}
