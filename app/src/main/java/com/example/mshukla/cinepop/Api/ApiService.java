package com.example.mshukla.cinepop.Api;

import com.example.mshukla.cinepop.Model.MovieResults;
import com.example.mshukla.cinepop.Model.ReviewResults;
import com.example.mshukla.cinepop.Model.TrailerResults;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by manas on 12/17/15.
 */
public interface ApiService {

        @GET("discover/movie")
        Call<MovieResults> getMovieResults(@Query("api_key") String apiKey, @Query("sort_by") String sortBy);

        @GET("/3/movie/{id}/videos")
        Call<TrailerResults> getTrailersResults(@Path("id") long movieId, @Query("api_key") String apiKey);

        @GET("/3/movie/{id}/reviews")
        Call<ReviewResults> getReviewsResults(@Path("id") long movieId, @Query("api_key") String apiKey);

}
