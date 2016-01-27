package com.example.mshukla.cinepop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.mshukla.cinepop.Adapter.MoviesAdapter;
import com.example.mshukla.cinepop.Api.RestClient;
import com.example.mshukla.cinepop.Model.Movie;
import com.example.mshukla.cinepop.Model.MovieResults;
import com.example.mshukla.cinepop.Util.Constants;

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
public class MainActivityFragment extends Fragment {

    @Bind(R.id.movie_grid_view)
    RecyclerView mMoviesGrid;
    MoviesAdapter moviesAdapter;
    private List<Movie> movies;
    private int actualWidth;
    private String mSortCriteria;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        mMoviesGrid.setHasFixedSize(true);
        setHasOptionsMenu(true);

        // dont calculate width before layout is laid out otherwise it returns 0
        // follow this for more details http://stackoverflow.com/questions/3591784/getwidth-and-getheight-of-view-returns-0

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("NewApi")
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                // Ensure you call it only once :
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                int gridWidth = mMoviesGrid.getWidth();
                 int spanCount = gridWidth / Constants.DESIRED_MOVIE_POSTER_WIDTH;
                actualWidth = gridWidth / spanCount;
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), spanCount);
                mMoviesGrid.setLayoutManager(mLayoutManager);
                movies = new ArrayList<>();

                moviesAdapter = new MoviesAdapter(getContext(), movies, actualWidth,
                        (MoviesAdapter.ViewHolder.ClickListener) getActivity());

                mMoviesGrid.setAdapter(moviesAdapter);
                loadMovies();
            }
        });

        return view;
    }

    private void loadMovies() {
        setSortCriteria();
        Call<MovieResults> movieResultsCall = RestClient.getApiService().
                getMovieResults(BuildConfig.API_KEY, mSortCriteria);
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

    public String setSortCriteria() {
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String sortCriteria = preferences.getString(Constants.SORT_CRITERIA,Constants.SORT_BY_POPULARITY);
        if(mSortCriteria != sortCriteria)
            mSortCriteria = sortCriteria;
        return mSortCriteria;
    }
}
