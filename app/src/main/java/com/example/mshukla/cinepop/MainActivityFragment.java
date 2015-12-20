package com.example.mshukla.cinepop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
public class MainActivityFragment extends Fragment implements MoviesAdapter.ViewHolder.ClickListener {

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
        setHasOptionsMenu(true);
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
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String mSortCriteria = preferences.getString(Constants.SORT_CRITERIA,Constants.SORT_BY_POPULARITY);
        Call<MovieResults> movieResultsCall = RestClient.getApiService().getMovieResults(BuildConfig.API_KEY, mSortCriteria);
        movieResultsCall.enqueue(new Callback<MovieResults>() {
            @Override
            public void onResponse(Response<MovieResults> response, Retrofit retrofit) {
                movies.clear();
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
        int position = mMoviesGrid.getChildLayoutPosition(itemView);
        if(position == RecyclerView.NO_POSITION)
            return;
        Movie selectedMovie = movies.get(position);
        Intent intent = new Intent(getContext(),MovieDetails.class);
        //http://stackoverflow.com/questions/3323074/android-difference-between-parcelable-and-serializable
        intent.putExtra(Constants.MOVIE_BUNDLE_KEY,selectedMovie);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int id = item.getItemId();
        switch (id) {
            case R.id.sort_popularity: editor.putString(Constants.SORT_CRITERIA,Constants.SORT_BY_POPULARITY);
                break;
            case R.id.sort_rating: editor.putString(Constants.SORT_CRITERIA, Constants.SORT_BY_RATING);
                break;
            default: editor.putString(Constants.SORT_CRITERIA,Constants.SORT_BY_POPULARITY);
                break;
        }
        editor.commit();
        loadMovies();
        return super.onOptionsItemSelected(item);
    }
}
