package com.example.mshukla.cinepop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import info.quantumflux.QuantumFlux;
import info.quantumflux.model.query.Select;
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

    ArrayList<Trailer> trailers;
    ArrayList<Review> reviews;

    boolean mFavourite = false;
    private ShareActionProvider mShareActionProvider;

    Call<TrailerResults> trailerResultsCall;
    Call<ReviewResults> reviewResultsCall;

    public MovieDetailFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.MOVIE_BUNDLE_KEY, movie);
        outState.putParcelableArrayList(Constants.TRAILER_BUNDLE_KEY, trailers);
        outState.putParcelableArrayList(Constants.REVIEW_BUNDLE_KEY, reviews);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle arguments = getArguments();
        if(savedInstanceState!=null) {
            movie = savedInstanceState.getParcelable(Constants.MOVIE_BUNDLE_KEY);
            trailers = savedInstanceState.getParcelableArrayList(Constants.TRAILER_BUNDLE_KEY);
            reviews = savedInstanceState.getParcelableArrayList(Constants.REVIEW_BUNDLE_KEY);
            updateTrailers();
            updateReviews();
        } else if(arguments != null) {
            movie = arguments.getParcelable(Constants.MOVIE_BUNDLE_KEY);
            addTrailers(movie);
            addReviews(movie);
        }
        if(movie!=null) {
            setupToolbar();
            if (mToolbar != null)
                mToolbar.setTitle(movie.getTitle());
            setHasOptionsMenu(true);
            Glide.with(this).load(Constants.IMAGE_URL + movie.getPosterPath())
                    .placeholder(R.drawable.movie_placeholder)
                    .crossFade()
                    .into(mMoviePoster);

            title.setText(movie.getTitle());
            releaseDate.setText(movie.getReleaseDate());
            voteAverage.setText(movie.getVoteAverage().toString());
            overview.setText(movie.getOverview());

            toggleFavouriteButton();
            mFavouriteButton.setVisibility(View.VISIBLE);
            mFavouriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mFavourite) {
                        QuantumFlux.delete(Select.from(Movie.class).whereEquals("id", movie.getId()));
                        mFavourite = false;
                        mFavouriteButton.setText(R.string.mark_favourite);
                    } else {
                        mFavourite = true;
                        mFavouriteButton.setText(R.string.remove_favourite);
                        QuantumFlux.insert(movie);
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, view);

        return view;
    }
    private void toggleFavouriteButton() {
        Movie movieD = Select.from(Movie.class).whereEquals("id",movie.getId()).first();
        if(movieD!=null) {
            mFavouriteButton.setText(R.string.remove_favourite);
            mFavourite = true;
        }

    }

    private void addTrailers(Movie movie) {
        trailerResultsCall = RestClient.getApiService().
                getTrailersResults(movie.getId(), BuildConfig.API_KEY);
        trailerResultsCall.enqueue(new Callback<TrailerResults>() {
            @Override
            public void onResponse(Response<TrailerResults> response, Retrofit retrofit) {
                trailers = new ArrayList<>();
                trailers.addAll(response.body().getResults());
                updateTrailers();
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void updateTrailers() {
        if(trailers!=null && trailers.size() > 0) {
            final LayoutInflater inflater = getActivity().getLayoutInflater();
            mTrailersContainer.setVisibility(View.VISIBLE);
            mTrailersHeader.setVisibility(View.VISIBLE);
            for (Trailer trailer : trailers) {
                if(trailer.getType().equals(Constants.VIDEO_TYPE_TRAILER) && trailer.getSite().equals
                        (Constants.VIDEO_SITE_YOUTUBE)) {
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
            if(mShareActionProvider!=null) {
                mShareActionProvider.setShareIntent(createShareMovieIntent());
            }
        }
    }
    private void addReviews(Movie movie) {
        reviewResultsCall = RestClient.getApiService().
                getReviewsResults(movie.getId(), BuildConfig.API_KEY);
        reviewResultsCall.enqueue(new Callback<ReviewResults>() {
            @Override
            public void onResponse(Response<ReviewResults> response, Retrofit retrofit) {
                reviews = new ArrayList<Review>();
                reviews.addAll(response.body().getResults());
                updateReviews();
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void updateReviews() {
        if(reviews!=null && reviews.size() != 0) {
            final LayoutInflater inflater = getActivity().getLayoutInflater();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_detail,menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (trailers != null && trailers.size() > 0) {
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, Trailer.getVideoUrl(trailers.get(0)));
        return shareIntent;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(trailerResultsCall!=null)
            trailerResultsCall.cancel();
        if(reviewResultsCall!=null)
            reviewResultsCall.cancel();
    }

}
