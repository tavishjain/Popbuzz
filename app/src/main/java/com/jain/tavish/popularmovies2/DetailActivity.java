package com.jain.tavish.popularmovies2;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jain.tavish.popularmovies2.Adapters.ReviewsAdapter;
import com.jain.tavish.popularmovies2.Adapters.TrailerAdapter;
import com.jain.tavish.popularmovies2.Database.MoviesDatabase;
import com.jain.tavish.popularmovies2.ModelClasses.MoviesResult;
import com.jain.tavish.popularmovies2.ModelClasses.Reviews;
import com.jain.tavish.popularmovies2.ModelClasses.ReviewsResult;
import com.jain.tavish.popularmovies2.ModelClasses.TrailerResult;
import com.jain.tavish.popularmovies2.ModelClasses.Trailers;
import com.jain.tavish.popularmovies2.Networking.ApiInterface;
import com.jain.tavish.popularmovies2.Networking.RetrofitClient;
import com.jain.tavish.popularmovies2.ViewModel.MainViewModel;
import com.jain.tavish.popularmovies2.ViewModel.ViewModelFactory;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {


    private final static String TRAILER_BASE_URL = "http://www.youtube.com/watch?v=";
    private Context mContext;
    private static Reviews reviewModel;
    private MoviesResult moviesResultObject;
    private static Trailers trailersModel;
    private static List<TrailerResult> trailerResultList;
    private static List<ReviewsResult> reviewsResultList;
    private static List<MoviesResult> moviesInDatabaseList;
    private static int movieId;
    private static int position;
    private MoviesDatabase moviesDatabase;
    private MainViewModel mainViewModel;

    public @BindView(R.id.view_pager) ViewPager viewPager;
    public @BindView(R.id.rv_reviews) RecyclerView recyclerViewReviews;
    public @BindView(R.id.iv_background) ImageView ivBackground;
    public @BindView(R.id.iv_detail_main_image) ImageView ivDetailMainImage;
    public @BindView(R.id.tv_movie_title_detail) TextView movieTitle;
    public @BindView(R.id.tv_date_released_detail) TextView dateReleased;
    public @BindView(R.id.tv_rating_detail) TextView rating;
    public @BindView(R.id.tv_overview_detail) TextView overview;
    public @BindView(R.id.frame_layout) FrameLayout frameLayout;
    public @BindView(R.id.fab_share_detail) FloatingActionButton shareFAB;
    public @BindView(R.id.fab_fav_detail) FloatingActionButton floatingActionButton;
    public @BindView(R.id.pb_reviews) ProgressBar reviewsBar;
    public @BindView(R.id.pb_trailers) ProgressBar trailersBar;
    public @BindView(R.id.tv_no_reviews_available) TextView noReviewTv;
    public @BindView(R.id.tv_no_trailers_available) TextView noTrailerTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        position = -1;
        ButterKnife.bind(this);
        moviesDatabase = MoviesDatabase.getInstance(DetailActivity.this);

        viewPager.setVisibility(View.GONE);
        noReviewTv.setVisibility(View.GONE);
        noTrailerTv.setVisibility(View.GONE);

        mContext = getApplicationContext();

        mainViewModel = ViewModelProviders.of(this , new ViewModelFactory(moviesDatabase , Integer.toString(movieId))).get(MainViewModel.class);

        reviewsResultList = new ArrayList<>();
        trailerResultList = new ArrayList<>();
        moviesInDatabaseList = moviesDatabase.moviesDao().getAllMovies();

        movieId = getIntent().getIntExtra("movie_id" , -1);

        int color = getIntent().getIntExtra("back_color", 0 );
        ivBackground.setBackgroundColor(color);

        if(movieId == -1 || MainActivity.nav_item_selected == -1){
            Snackbar snackbar = Snackbar.make(frameLayout , "Error !!!!" , Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackbar.setActionTextColor(Color.parseColor("#B0BEC5"));
            snackBarView.setBackgroundColor(Color.parseColor("#202125"));
            snackbar.show();
            finish();
        }

        makeApiRequest();

        Observer observer = new Observer<MoviesResult>() {
            @Override
            public void onChanged(@Nullable MoviesResult moviesResult) {
                moviesResultObject = moviesResult;
                mainViewModel.getMoviesResults().removeObserver(this);
            }
        };

        mainViewModel.getMoviesResults().observe(DetailActivity.this, observer);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moviesInDatabaseList = moviesDatabase.moviesDao().getAllMovies();
                int i = 0;
                do{
                    if(moviesInDatabaseList.size() == 0){
                        Snackbar snackbar = Snackbar.make(frameLayout , "Movie added to Favourites" , Snackbar.LENGTH_SHORT);
                        View snackBarView = snackbar.getView();
                        snackbar.setActionTextColor(Color.parseColor("#B0BEC5"));
                        snackBarView.setBackgroundColor(Color.parseColor("#202125"));
                        snackbar.show();
                        moviesDatabase.moviesDao().insertMovie(moviesResultObject);
                        moviesResultObject.setFavourite(true);
                        if(mainViewModel.getMoviesResults().getValue() != null) {
                            mainViewModel.getMoviesResults().getValue().setFavourite(true);
                        }
                        moviesDatabase.moviesDao().updateMovie(moviesResultObject);
                        setAppropriateFabImage();
                        break;
                    }

                    if(Objects.equals(moviesResultObject.getId(), moviesInDatabaseList.get(i).getId())){
                        Snackbar snackbar = Snackbar.make(frameLayout , "Movie deleted from Favourites" , Snackbar.LENGTH_SHORT);
                        View snackBarView = snackbar.getView();
                        snackbar.setActionTextColor(Color.parseColor("#B0BEC5"));
                        snackBarView.setBackgroundColor(Color.parseColor("#202125"));
                        snackbar.show();
                        moviesDatabase.moviesDao().deleteMovies(moviesResultObject);
                        moviesResultObject.setFavourite(false);
                        if(mainViewModel.getMoviesResults().getValue() != null) {
                            mainViewModel.getMoviesResults().getValue().setFavourite(false);
                        }
                        moviesDatabase.moviesDao().updateMovie(moviesResultObject);
                        setAppropriateFabImage();
                        break;
                    }

                    if(i == (moviesInDatabaseList.size() - 1)){
                        Snackbar snackbar = Snackbar.make(frameLayout , "Movie added to Favourites" , Snackbar.LENGTH_SHORT);
                        View snackBarView = snackbar.getView();
                        snackbar.setActionTextColor(Color.parseColor("#B0BEC5"));
                        snackBarView.setBackgroundColor(Color.parseColor("#202125"));
                        snackbar.show();
                        moviesDatabase.moviesDao().insertMovie(moviesResultObject);
                        moviesResultObject.setFavourite(true);
                        if(mainViewModel.getMoviesResults().getValue() != null) {
                            mainViewModel.getMoviesResults().getValue().setFavourite(true);
                        }
                        moviesDatabase.moviesDao().updateMovie(moviesResultObject);
                        setAppropriateFabImage();
                        break;
                    }
                    i++;
                }while (i < moviesInDatabaseList.size());
            }
        });

    }

    @OnClick(R.id.fab_share_detail)
    public void shareBtnClick(){
        if(trailerResultList != null){
            trailerResultList.clear();
        }
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<Trailers> call = apiInterface.getMovieTrailers(Integer.toString(moviesResultObject.getId()), MainActivity.API_KEY);
        call.enqueue(new Callback<Trailers>() {
            @Override
            public void onResponse(Call<Trailers> call, Response<Trailers> response) {
                trailersModel = response.body();
                trailerResultList.addAll(Objects.requireNonNull(trailersModel).getResults());
                if(trailerResultList.size() == 0){
                    Snackbar snackbar = Snackbar.make(frameLayout , "No Trailers available !!!" , Snackbar.LENGTH_SHORT);
                    View snackBarView = snackbar.getView();
                    snackbar.setActionTextColor(Color.parseColor("#B0BEC5"));
                    snackBarView.setBackgroundColor(Color.parseColor("#202125"));
                    snackbar.show();
                } else {
                    sendIntent(trailerResultList.get(0).getKey());
                }
            }

            @Override
            public void onFailure(Call<Trailers> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(frameLayout , "Connect to the Internet !!!" , Snackbar.LENGTH_SHORT);
                View snackBarView = snackbar.getView();
                snackbar.setActionTextColor(Color.parseColor("#B0BEC5"));
                snackBarView.setBackgroundColor(Color.parseColor("#202125"));
                snackbar.show();
            }
        });
    }

    private void sendIntent(String trailerKey){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out the trailer of this great movie :\n" + TRAILER_BASE_URL + trailerKey);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void loadTrailers(){
        if(trailerResultList != null){
            trailerResultList.clear();
        }
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<Trailers> call = apiInterface.getMovieTrailers(Integer.toString(moviesResultObject.getId()), MainActivity.API_KEY);
        call.enqueue(new Callback<Trailers>() {
            @Override
            public void onResponse(Call<Trailers> call, Response<Trailers> response) {
                trailersModel = response.body();

                trailerResultList.addAll(Objects.requireNonNull(trailersModel).getResults());
                viewPager.setPageMargin(20);
                trailersBar.setVisibility(View.GONE);

                if (trailerResultList.size() == 0) {
                    viewPager.setVisibility(View.GONE);
                    noTrailerTv.setVisibility(View.VISIBLE);
                } else {
                    TrailerAdapter adapter = new TrailerAdapter(DetailActivity.this ,trailerResultList);
                    viewPager.setAdapter(adapter);
                    viewPager.setVisibility(View.VISIBLE);
                    noTrailerTv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Trailers> call, Throwable t) {

            }
        });
    }

    private void loadReviews(){
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<Reviews> call = apiInterface.getMovieReviews(Integer.toString(moviesResultObject.getId()), MainActivity.API_KEY);
        call.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                reviewModel = response.body();
                if (reviewsResultList != null) {
                    reviewsResultList.clear();
                }

                reviewsResultList.addAll(Objects.requireNonNull(reviewModel).getResults());

                List<String> nameList, reviewList;
                String stringOriginal, stringUpper;

                reviewsBar.setVisibility(View.GONE);
                nameList = new ArrayList<>();
                reviewList = new ArrayList<>();

                recyclerViewReviews.setNestedScrollingEnabled(false);

                if(reviewModel.getTotalResults() == 0){
                    nameList.add(null);
                    viewPager.setVisibility(View.GONE);
                    noReviewTv.setVisibility(View.VISIBLE);
                    reviewList.add(null);
                }else{

                    noReviewTv.setVisibility(View.GONE);
                    viewPager.setVisibility(View.VISIBLE);

                    for (int i = 0 ; i < reviewModel.getTotalResults() ; i++) {
                        try {
                            if (reviewsResultList.get(i) != null) {
                                stringOriginal = reviewsResultList.get(i).getAuthor();
                                stringUpper = stringOriginal.substring(0, 1).toUpperCase() + stringOriginal.substring(1);
                                nameList.add(stringUpper);

                                stringOriginal = reviewsResultList.get(i).getContent();
                                stringUpper = stringOriginal.substring(0, 1).toUpperCase() + stringOriginal.substring(1);
                                reviewList.add(stringUpper);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

                ReviewsAdapter reviewsAdapter = new ReviewsAdapter(DetailActivity.this, nameList, reviewList);
                recyclerViewReviews.setHasFixedSize(true);
                recyclerViewReviews.setLayoutManager(new LinearLayoutManager(DetailActivity.this));
                recyclerViewReviews.setAdapter(reviewsAdapter);
            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {
                Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DetailActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void setAppropriateFabImage(){
        for (int i = 0; i < moviesDatabase.moviesDao().getAllMovies().size(); i++) {
            if(Objects.equals(moviesResultObject.getId(), moviesDatabase.moviesDao().getAllMovies().get(i).getId())){
                position = i;
                break;
            }else{
                position = -1;
            }
        }

        if(position >=0){
            floatingActionButton.setImageResource(R.drawable.ic_fav);
        }else{
            floatingActionButton.setImageResource(R.drawable.ic_unfav);
        }
    }


    private void makeApiRequest(){
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<MoviesResult> callResult = apiInterface.getMovieDetailsById
                (Integer.toString(movieId) , MainActivity.API_KEY);

        callResult.enqueue(new Callback<MoviesResult>() {
            @Override
            public void onResponse(Call<MoviesResult> call, Response<MoviesResult> response) {
                moviesResultObject = response.body();

                ivBackground.setAlpha((float)1.0);

                for (int i = 0; i < moviesInDatabaseList.size(); i++) {
                    if (Objects.equals(moviesResultObject.getId(), moviesInDatabaseList.get(i).getId())){
                        position = i;
                        break;
                    }
                }

                DecimalFormat precision = new DecimalFormat("0.0");
                rating.setText(precision.format(moviesResultObject.getRating()));

                String publishedAt = moviesResultObject.getReleaseDate();

                try {
                    SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
                    Date newDate;
                    newDate = spf.parse(publishedAt);
                    spf = new SimpleDateFormat("dd MMM yyyy");
                    publishedAt = spf.format(newDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                dateReleased.setText(publishedAt);

                movieTitle.setText(moviesResultObject.getTitle());

                overview.setText(moviesResultObject.getOverview());

                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        ivBackground.setImageBitmap(BlurImage.fastblur(bitmap, 1f, 80));
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };

                ivBackground.setTag(target);
                Picasso.get()
                        .load("http://image.tmdb.org/t/p/w780/" + moviesResultObject.getBackdropPath())
                        .error(R.drawable.ic_error)
                        //       .placeholder(R.drawable.loading)
                        .into(target);


                Picasso.get()
                        .load("http://image.tmdb.org/t/p/w500/" + moviesResultObject.getPosterPath())
                        //    .placeholder(R.drawable.loading)
                        .error(R.drawable.ic_error)
                        .into(ivDetailMainImage);

                setAppropriateFabImage();

                loadReviews();
                loadTrailers();
            }

            @Override
            public void onFailure(Call<MoviesResult> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(frameLayout , "Connect to the Internet !!!" , Snackbar.LENGTH_SHORT);
                View snackBarView = snackbar.getView();
                snackbar.setActionTextColor(Color.parseColor("#B0BEC5"));
                snackBarView.setBackgroundColor(Color.parseColor("#202125"));
                snackbar.show();
                DecimalFormat precision = new DecimalFormat("0.0");
                rating.setText("Rating : " + precision.format(moviesInDatabaseList.get(position).getRating()) + " / 10");
                dateReleased.setText(moviesInDatabaseList.get(position).getReleaseDate());
                movieTitle.setText(moviesInDatabaseList.get(position).getTitle());
                overview.setText(moviesInDatabaseList.get(position).getOverview());
                Picasso.get()
                        .load("http://image.tmdb.org/t/p/w780/" + moviesInDatabaseList.get(position).getBackdropPath())
                        //          .placeholder(R.drawable.loading)
                        .error(R.drawable.ic_error)
                        .into(ivBackground);
                Picasso.get()
                        .load("http://image.tmdb.org/t/p/w500/" + moviesInDatabaseList.get(position).getPosterPath())
                        //       .placeholder(R.drawable.loading)
                        .error(R.drawable.ic_error)
                        .into(ivDetailMainImage);
            }
        });
    }
}
