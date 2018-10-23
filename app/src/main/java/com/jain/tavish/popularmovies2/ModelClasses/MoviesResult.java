package com.jain.tavish.popularmovies2.ModelClasses;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "movies")
public class MoviesResult {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    @Expose
    private Integer id;

    @ColumnInfo(name = "rating")
    @SerializedName("vote_average")
    @Expose
    private Double rating;

    @ColumnInfo(name = "movie_title")
    @SerializedName("title")
    @Expose
    private String title;

    @ColumnInfo(name = "poster_path")
    @SerializedName("poster_path")
    @Expose
    private String posterPath;

    @ColumnInfo(name = "backdrop_path")
    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;

    @ColumnInfo(name = "description")
    @SerializedName("overview")
    @Expose
    private String overview;

    @ColumnInfo(name = "release_date")
    @SerializedName("release_date")
    @Expose
    private String releaseDate;

    @ColumnInfo(name = "favourites")
    private boolean favourite;

    public MoviesResult(int id, String title, String overview, String posterPath, Double rating, String releaseDate, String backdropPath, boolean favourite) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.backdropPath = backdropPath;
        this.favourite = favourite;
    }

    public Integer getId() {return id; }

    public Double getRating() {
        return rating;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() { return releaseDate; }

    public boolean getFavourite(){ return favourite; }

    public void setFavourite(boolean favourites){ this.favourite = favourites;}

}