package io.github.tavishjain.popbuzz.Networking;

import io.github.tavishjain.popbuzz.ModelClasses.Movies;
import io.github.tavishjain.popbuzz.ModelClasses.MoviesResult;
import io.github.tavishjain.popbuzz.ModelClasses.Reviews;
import io.github.tavishjain.popbuzz.ModelClasses.Trailers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import io.reactivex.Observable;

public interface ApiInterface {

    @GET("movie/{id}")
    Observable<MoviesResult> getMovieDetailsById(@Path(value = "id", encoded = true) String id , @Query("api_key")String api_key);

    @GET("movie/popular")
    Observable<Movies> getPopularMovies(@Query("api_key")String api_key);

    @GET("movie/top_rated")
    Observable<Movies> getTopRatedMovies(@Query("api_key")String api_key);

    @GET("movie/{id}/reviews")
    Observable<Reviews> getMovieReviews(@Path(value = "id", encoded = true) String id , @Query("api_key")String api_key);

    @GET("movie/{id}/videos")
    Observable<Trailers> getMovieTrailers(@Path(value = "id", encoded = true) String id , @Query("api_key")String api_key);
}