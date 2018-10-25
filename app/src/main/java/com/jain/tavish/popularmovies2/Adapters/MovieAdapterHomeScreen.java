package com.jain.tavish.popularmovies2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jain.tavish.popularmovies2.Database.MoviesDatabase;
import com.jain.tavish.popularmovies2.DetailActivity;
import com.jain.tavish.popularmovies2.MainActivity;
import com.jain.tavish.popularmovies2.ModelClasses.MoviesResult;
import com.jain.tavish.popularmovies2.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdapterHomeScreen extends RecyclerView.Adapter<MovieAdapterHomeScreen.myViewHolder> {
    private final List<MoviesResult> mResultList;
    private final Context mContext;

    public @BindView(R.id.tv_fav_zero) TextView tv_fav_zero;

    public static class myViewHolder extends RecyclerView.ViewHolder  {
        public @BindView(R.id.row_item_iv) ImageView mImageView;
        public @BindView(R.id.tv_main_screen_movie_name) TextView movieName;
        public @BindView(R.id.tv_main_screen_star_rating) TextView rating;
        public myViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public MovieAdapterHomeScreen(Context context, List<MoviesResult> resultList) {
        mResultList = resultList;
        mContext = context;
    }

    @NonNull
    @Override
    public MovieAdapterHomeScreen.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movies_row_item, parent, false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final myViewHolder holder, int position) {
        MoviesDatabase moviesDatabase = MoviesDatabase.getInstance(mContext);

        final int[] vibrant = new int[1];

        if(MainActivity.nav_item_selected == MainActivity.INT_POPULAR_MOVIES ||
                MainActivity.nav_item_selected == MainActivity.INT_TOP_RATED_MOVIES) {
            Picasso.get()
                    .load("http://image.tmdb.org/t/p/w780/" + mResultList.get(position).getBackdropPath())
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            if (bitmap != null) {
                                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                    public void onGenerated(Palette p) {
                                        vibrant[0] = p.getDominantColor(Color.WHITE);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {  }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {   }
                    });

        }else if(MainActivity.nav_item_selected == MainActivity.INT_FAVOURITES){
            if(moviesDatabase.moviesDao().getAllMovies().size() != 0) {
               Picasso.get()
                        .load("http://image.tmdb.org/t/p/w780/" +
                                moviesDatabase.moviesDao().getAllMovies().get(position).getBackdropPath())
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                if (bitmap != null) {
                                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                        public void onGenerated(Palette p) {
                                            vibrant[0] = p.getDominantColor(Color.WHITE);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) { }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {   }
                        });
            }
        }

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(mContext, DetailActivity.class);
                myIntent.putExtra("movie_id" , mResultList.get(holder.getAdapterPosition()).getId());
                myIntent.putExtra("nav_item_selected" , MainActivity.nav_item_selected);
                myIntent.putExtra("back_color", vibrant[0]);

                Pair<View, String> p1 = Pair.create((View)holder.mImageView, ViewCompat.getTransitionName(holder.mImageView));

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)mContext, p1);

                mContext.startActivity(myIntent, options.toBundle());

            }
        });

        if(MainActivity.nav_item_selected == MainActivity.INT_POPULAR_MOVIES ||
                MainActivity.nav_item_selected == MainActivity.INT_TOP_RATED_MOVIES) {
            Picasso.get()
                    .load("http://image.tmdb.org/t/p/w780/" + mResultList.get(position).getBackdropPath())
                    //     .placeholder(R.drawable.loading)
                    .error(R.drawable.ic_error)
                    .into(holder.mImageView);
            holder.movieName.setText(mResultList.get(position).getTitle());
            holder.rating.setText(mResultList.get(position).getRating().toString());
        }else if(MainActivity.nav_item_selected == MainActivity.INT_FAVOURITES){
            if(moviesDatabase.moviesDao().getAllMovies().size() != 0) {
                Picasso.get()
                        .load("http://image.tmdb.org/t/p/w780/" +
                                moviesDatabase.moviesDao().getAllMovies().get(position).getBackdropPath())
                        //        .placeholder(R.drawable.loading)
                        .error(R.drawable.ic_error)
                        .into(holder.mImageView);
                holder.movieName.setText(moviesDatabase.moviesDao().getAllMovies().get(position).getTitle());
                holder.rating.setText(moviesDatabase.moviesDao().getAllMovies().get(position).getRating().toString());
            }else if(moviesDatabase.moviesDao().getAllMovies().size() == 0){
                tv_fav_zero.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public int getItemCount() {
        return mResultList.size();
    }
}