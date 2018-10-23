package com.jain.tavish.popularmovies2.ViewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.jain.tavish.popularmovies2.Database.MoviesDatabase;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final MoviesDatabase movieDatabase;
    private final String movieId;

    public ViewModelFactory(MoviesDatabase movieDatabase, String movieId) {
        this.movieDatabase = movieDatabase;
        this.movieId = movieId;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainViewModel(movieDatabase, movieId);
    }
}