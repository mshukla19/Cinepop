package com.example.mshukla.cinepop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.mshukla.cinepop.Util.Constants;

public class MovieDetailActivity extends AppCompatActivity {

    private String MOVIE_DETAIL_FRAG_TAG = "detailFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState == null) {

            Bundle arguments = new Bundle();
            arguments.putParcelable(Constants.MOVIE_BUNDLE_KEY,
                    getIntent().getExtras().getParcelable(Constants.MOVIE_BUNDLE_KEY));

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }

    }

}
