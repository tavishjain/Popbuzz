package com.jain.tavish.popularmovies2.Networking;

import com.jain.tavish.popularmovies2.ModelClasses.Movies;
import com.jain.tavish.popularmovies2.ModelClasses.MoviesResult;
import com.jain.tavish.popularmovies2.ModelClasses.Reviews;
import com.jain.tavish.popularmovies2.ModelClasses.Trailers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("movie/{id}")
    Call<MoviesResult> getMovieDetailsById(@Path(value = "id", encoded = true) String id , @Query("api_key")String api_key);

    @GET("movie/popular")
    Call<Movies> getPopularMovies(@Query("api_key")String api_key);

    @GET("movie/top_rated")
    Call<Movies> getTopRatedMovies(@Query("api_key")String api_key);

    @GET("movie/{id}/reviews")
    Call<Reviews> getMovieReviews(@Path(value = "id", encoded = true) String id , @Query("api_key")String api_key);

    @GET("movie/{id}/videos")
    Call<Trailers> getMovieTrailers(@Path(value = "id", encoded = true) String id , @Query("api_key")String api_key);
}