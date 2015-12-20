package com.example.mshukla.cinepop;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mshukla.cinepop.Adapter.MoviesAdapter;
import com.example.mshukla.cinepop.Api.RestClient;
import com.example.mshukla.cinepop.Model.Movie;

import com.example.mshukla.cinepop.Model.MovieResults;
import com.example.mshukla.cinepop.Util.Constants;
import com.example.mshukla.cinepop.Util.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements MoviesAdapter.ViewHolder.ClickListener{

    @Bind(R.id.movie_grid_view)
    RecyclerView mMoviesGrid;
    MoviesAdapter moviesAdapter;
    private List<Movie> movies;
    private int actualWidth;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        mMoviesGrid.setHasFixedSize(true);
        int screenWidth = Util.getScreenWidth(getContext());
        int spanCount = screenWidth / Constants.DESIRED_MOVIE_POSTER_WIDTH;
        actualWidth = screenWidth / spanCount;
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(),spanCount);
        mMoviesGrid.setLayoutManager(mLayoutManager);
        movies = new ArrayList<>();
        moviesAdapter = new MoviesAdapter(getContext(), movies, actualWidth, this );
        mMoviesGrid.setAdapter(moviesAdapter);
        loadMovies();
        return view;
    }

    private void loadMovies() {
        Call<MovieResults> movieResultsCall = RestClient.getApiService().getMovieResults(BuildConfig.API_KEY,Constants.SORT_BY_POPULARITY);
        movieResultsCall.enqueue(new Callback<MovieResults>() {
            @Override
            public void onResponse(Response<MovieResults> response, Retrofit retrofit) {
                movies.addAll(response.body().getResults());
                moviesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    @Override
    public void moviePosterClick(View itemView) {
//        Intent intent = new Intent(getContext(),MovieDetails.class);
//        startActivity(intent);
    }
}
