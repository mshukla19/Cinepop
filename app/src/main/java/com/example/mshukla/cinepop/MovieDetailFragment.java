package com.example.mshukla.cinepop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mshukla.cinepop.Api.RestClient;
import com.example.mshukla.cinepop.Model.Movie;
import com.example.mshukla.cinepop.Model.ReviewResults;
import com.example.mshukla.cinepop.Model.Trailer;
import com.example.mshukla.cinepop.Model.TrailerResults;
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
public class MovieDetailFragment extends Fragment implements View.OnClickListener{

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

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

    @Bind(R.id.trailers_container)
    HorizontalScrollView mTrailersContainer;
    @Bind(R.id.trailers)
    ViewGroup mTrailersLayout;
    @Bind(R.id.trailers_header)
    TextView mTrailersHeader;

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
            addTrailers(movie);
            addReviews(movie);
        }

        return view;
    }
    private void addTrailers(Movie movie) {
        final List<Trailer> trailers = new ArrayList<>();
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        Call<TrailerResults> trailerResultsCall = RestClient.getApiService().
                getTrailersResults(movie.getId(), BuildConfig.API_KEY);
        trailerResultsCall.enqueue(new Callback<TrailerResults>() {
            @Override
            public void onResponse(Response<TrailerResults> response, Retrofit retrofit) {
                trailers.addAll(response.body().getResults());
                if(trailers.size() > 0) {
                    mTrailersContainer.setVisibility(View.VISIBLE);
                    mTrailersHeader.setVisibility(View.VISIBLE);
                    for (Trailer trailer : trailers) {
                        String thumbnailUrl = Trailer.getThumbnailUrl(trailer);
                        String videoUrl = Trailer.getVideoUrl(trailer);
                        ViewGroup thumbnailImage = (ViewGroup) inflater.inflate(R.layout.trailer,mTrailersContainer, false);
                        ImageView thumbnail = (ImageView) thumbnailImage.findViewById(R.id.trailer_thumb);
                        Glide.with(getContext())
                                .load(thumbnailUrl)
                                .crossFade()
                                .into(thumbnail);
                        thumbnail.setTag(videoUrl);
                        thumbnail.setOnClickListener(MovieDetailFragment.this);
                        mTrailersLayout.addView(thumbnailImage);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
    private void addReviews(Movie movie) {
        Call<ReviewResults> reviewResultsCall = RestClient.getApiService().
                getReviewsResults(movie.getId(), BuildConfig.API_KEY);
        reviewResultsCall.enqueue(new Callback<ReviewResults>() {
            @Override
            public void onResponse(Response<ReviewResults> response, Retrofit retrofit) {
                Log.d(LOG_TAG, response.body().getResults().toString());
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.trailer_thumb: String videoUrl = (String) view.getTag();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                startActivity(intent);
        }
    }
}
