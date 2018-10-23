package com.jain.tavish.popularmovies2.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.jain.tavish.popularmovies2.Database.MoviesDatabase;
import com.jain.tavish.popularmovies2.ModelClasses.MoviesResult;

public class MainViewModel extends ViewModel {

    private final LiveData<MoviesResult> moviesEntity;

    public MainViewModel(@NonNull MoviesDatabase movieDatabase , String id) {
        moviesEntity = movieDatabase.moviesDao().getMoviesLiveData(id);
    }

    public LiveData<MoviesResult> getMoviesResults() {
        return moviesEntity;
    }

}