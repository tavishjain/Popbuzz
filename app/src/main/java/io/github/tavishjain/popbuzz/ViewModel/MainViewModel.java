package io.github.tavishjain.popbuzz.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import io.github.tavishjain.popbuzz.Database.MoviesDatabase;
import io.github.tavishjain.popbuzz.ModelClasses.MoviesResult;

public class MainViewModel extends ViewModel {

    private final LiveData<MoviesResult> moviesEntity;

    public MainViewModel(@NonNull MoviesDatabase movieDatabase , String id) {
        moviesEntity = movieDatabase.moviesDao().getMoviesLiveData(id);
    }

    public LiveData<MoviesResult> getMoviesResults() {
        return moviesEntity;
    }

}