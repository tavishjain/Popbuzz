package io.github.tavishjain.popbuzz.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import io.github.tavishjain.popbuzz.ModelClasses.MoviesResult;

import java.util.List;

@Dao
public interface MoviesDao{

    @Query("SELECT * FROM movies")
    List<MoviesResult> getAllMovies();

    @Query("SELECT * FROM movies WHERE id = :id")
    LiveData<MoviesResult> getMoviesLiveData(String id);

    @Insert
    void insertMovie(MoviesResult... moviesResult);

    @Update
    void updateMovie(MoviesResult moviesResult);

    @Delete
    void deleteMovies(MoviesResult moviesResult);

    @Query("SELECT * FROM movies WHERE id = :movieID")
    boolean loadMovieById(String movieID);

}