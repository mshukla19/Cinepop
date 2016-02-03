package com.example.mshukla.cinepop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mshukla.cinepop.Api.RestClient;
import com.example.mshukla.cinepop.Model.Movie;
import com.example.mshukla.cinepop.Model.Review;
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

    @Bind(R.id.reviews_header)
    TextView mReviewsHeader;
    @Bind(R.id.reviews)
    ViewGroup mReviewsLayout;

    @Bind(R.id.favourite)
    Button mFavouriteButton;

    Toolbar mToolbar;

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, view);
        if(arguments != null) {
            setupToolbar();
            movie = arguments.getParcelable(Constants.MOVIE_BUNDLE_KEY);
            if(mToolbar!=null)
                mToolbar.setTitle(movie.getTitle());
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
            mFavouriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    
                }
            });
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
                        if(trailer.getType().equals(Constants.VIDEO_TYPE_TRAILER) && trailer.getSite().equals(Constants.VIDEO_SITE_YOUTUBE)) {
                            String thumbnailUrl = Trailer.getThumbnailUrl(trailer);
                            String videoUrl = Trailer.getVideoUrl(trailer);
                            ViewGroup thumbnailImage = (ViewGroup) inflater.inflate(R.layout.trailer, mTrailersContainer, false);
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
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
    private void addReviews(Movie movie) {
        final List<Review> reviews = new ArrayList<>();
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        Call<ReviewResults> reviewResultsCall = RestClient.getApiService().
                getReviewsResults(movie.getId(), BuildConfig.API_KEY);
        reviewResultsCall.enqueue(new Callback<ReviewResults>() {
            @Override
            public void onResponse(Response<ReviewResults> response, Retrofit retrofit) {
                reviews.addAll(response.body().getResults());
                if(reviews.size() != 0) {
                    mReviewsHeader.setVisibility(View.VISIBLE);
                    mReviewsLayout.setVisibility(View.VISIBLE);
                    for(Review r : reviews) {
                        ViewGroup review = (ViewGroup) inflater.inflate(R.layout.review_item, mReviewsLayout, false);
                        TextView reviewAuthor = (TextView) review.findViewById(R.id.review_author);
                        TextView reviewContent = (TextView) review.findViewById(R.id.review_content);
                        reviewAuthor.setText(r.getAuthor());
                        reviewContent.setText(r.getContent());
                        mReviewsLayout.addView(review);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void setupToolbar() {
        if (getActivity() instanceof MovieDetailActivity) {
            MovieDetailActivity activity = ((MovieDetailActivity) getActivity());
            mToolbar = activity.getToolbar();
        }
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
