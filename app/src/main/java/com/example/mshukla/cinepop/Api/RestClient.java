package com.example.mshukla.cinepop.Api;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by manas on 12/17/15.
 */
public class RestClient {
    private static final String BASE_URL = "http://api.themoviedb.org/3/";
    private static ApiService apiService;

        static     {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        apiService = retrofit.create(ApiService.class);
    }

    public static ApiService getApiService()
    {
        return apiService;
    }
}
