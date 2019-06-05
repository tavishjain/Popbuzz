package io.github.tavishjain.popbuzz;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import io.github.tavishjain.popbuzz.Adapters.ReviewsAdapter;
import io.github.tavishjain.popbuzz.Adapters.TrailerAdapter;
import io.github.tavishjain.popbuzz.Database.MoviesDatabase;
import io.github.tavishjain.popbuzz.ModelClasses.MoviesResult;
import io.github.tavishjain.popbuzz.ModelClasses.Reviews;
import io.github.tavishjain.popbuzz.ModelClasses.ReviewsResult;
import io.github.tavishjain.popbuzz.ModelClasses.TrailerResult;
import io.github.tavishjain.popbuzz.ModelClasses.Trailers;
import io.github.tavishjain.popbuzz.Networking.ApiInterface;
import io.github.tavishjain.popbuzz.Networking.RetrofitClient;
import io.github.tavishjain.popbuzz.ViewModel.MainViewModel;
import io.github.tavishjain.popbuzz.ViewModel.ViewModelFactory;

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
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DetailActivity extends AppCompatActivity {


    private final static String TRAILER_BASE_URL = "http://www.youtube.com/watch?v=";
    private Context mContext;
    private MoviesResult moviesResultObject;
    private static List<TrailerResult> trailerResultList;
    private static List<ReviewsResult> reviewsResultList;
    private static List<MoviesResult> moviesInDatabaseList;
    private static int movieId;
    private static int position;
    private MoviesDatabase moviesDatabase;
    private MainViewModel mainViewModel;
    private ApiInterface apiInterface;

    public @BindView(R.id.view_pager) ViewPager viewPager;
    public @BindView(R.id.rv_reviews) RecyclerView recyclerViewReviews;
    public @BindView(R.id.iv_background) ImageView ivBackground;
    public @BindView(R.id.iv_detail_main_image) ImageView ivDetailMainImage;
    public @BindView(R.id.tv_movie_title_detail) TextView movieTitle;
    public @BindView(R.id.tv_date_released_detail) TextView dateReleased;
    public @BindView(R.id.tv_rating_detail) TextView rating;
    public @BindView(R.id.tv_overview_detail) TextView overview;
    public @BindView(R.id.coordinator_layout) CoordinatorLayout coordinatorLayout;
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
            showSnackbar("Error !!!");
            finish();
        }

        apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);

        makeApiRequest();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                makeApiRequestForBackImage();
            }
        }, 2000);

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
                boolean favMovie = moviesDatabase.moviesDao().loadMovieById(Integer.toString(movieId));
                if(!favMovie){
                    //this movie is not in favourites
                    showSnackbar("Movie added to favourites !!!");

                    moviesDatabase.moviesDao().insertMovie(moviesResultObject);
                    moviesResultObject.setFavourite(true);
                    if(mainViewModel.getMoviesResults().getValue() != null) {
                        mainViewModel.getMoviesResults().getValue().setFavourite(true);
                    }
                    moviesDatabase.moviesDao().updateMovie(moviesResultObject);
                    setAppropriateFabImage();
                }else {
                    //this movie is in favourites
                    showSnackbar("Movie deleted from favourites !!!");

                    moviesDatabase.moviesDao().deleteMovies(moviesResultObject);
                    moviesResultObject.setFavourite(false);
                    if(mainViewModel.getMoviesResults().getValue() != null) {
                        mainViewModel.getMoviesResults().getValue().setFavourite(false);
                    }
                    moviesDatabase.moviesDao().updateMovie(moviesResultObject);
                    setAppropriateFabImage();
                }
            }
        });

    }

    public void showSnackbar(String message){
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message , Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackbar.setActionTextColor(Color.parseColor("#B0BEC5"));
        snackBarView.setBackgroundColor(Color.parseColor("#202125"));
        snackbar.show();
    }

    @OnClick(R.id.fab_share_detail)
    public void shareBtnClick(){
        if(trailerResultList != null){
            trailerResultList.clear();
        }

        showSnackbar("Loading sharing options for you !!!");
        apiInterface.getMovieTrailers(Integer.toString(moviesResultObject.getId()), MainActivity.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<Trailers>() {

                    @Override
                    public void onError(Throwable e) {
                        showSnackbar("Failed to load Trailers");
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Trailers trailersModel) {

                        trailerResultList.addAll(Objects.requireNonNull(trailersModel).getResults());
                        if(trailerResultList.size() == 0){
                            showSnackbar("No Trailers available !!!");
                        } else {
                            sendIntent(trailerResultList.get(0).getKey());
                        }
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

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putInt("trailerBarVisibilityStatus", trailersBar.getVisibility());
        outState.putInt("reviewBarVisibilityStatus", reviewsBar.getVisibility());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        trailersBar.setVisibility(savedInstanceState.getInt("trailerBarVisibilityStatus"));
        reviewsBar.setVisibility(savedInstanceState.getInt("reviewBarVisibilityStatus"));
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void loadTrailers(){
        if(trailerResultList != null){
            trailerResultList.clear();
        }
        apiInterface.getMovieTrailers(Integer.toString(moviesResultObject.getId()), MainActivity.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<Trailers>() {

                    @Override
                    public void onError(Throwable e) {
                        showSnackbar("Failed to load Trailers");
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Trailers trailersModel) {

                        trailerResultList = trailersModel.getResults();

                        trailerResultList.addAll(Objects.requireNonNull(trailersModel).getResults());
                        trailersBar.setVisibility(View.GONE);

                        if (trailerResultList.size() == 0 || trailerResultList == null) {
                            viewPager.setVisibility(View.GONE);
                            noTrailerTv.setVisibility(View.VISIBLE);
                        } else {
                            TrailerAdapter adapter = new TrailerAdapter(DetailActivity.this ,trailerResultList);
                            noTrailerTv.setVisibility(View.GONE);
                            viewPager.setVisibility(View.VISIBLE);
                            viewPager.setPageMargin(20);
                            viewPager.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void loadReviews(){

        apiInterface.getMovieReviews(Integer.toString(moviesResultObject.getId()), MainActivity.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<Reviews>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Reviews reviewModel) {

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
                        recyclerViewReviews.setAdapter(reviewsAdapter);
                        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(DetailActivity.this));

                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DetailActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void setAppropriateFabImage(){
       boolean favMovie = moviesDatabase.moviesDao().loadMovieById(Integer.toString(movieId));
       if(!favMovie){
           floatingActionButton.setImageResource(R.drawable.ic_unfav);
       }else{
           floatingActionButton.setImageResource(R.drawable.ic_fav);
       }
    }

    private void makeApiRequestForBackImage(){
        apiInterface.getMovieDetailsById(Integer.toString(movieId), MainActivity.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<MoviesResult>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {

                        showSnackbar("Connect to the Internet !!!");

                        Picasso.get()
                                .load("http://image.tmdb.org/t/p/w500/" + moviesInDatabaseList.get(position).getBackdropPath())
                                .error(R.drawable.ic_error)
                                .into(ivBackground);

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onNext(MoviesResult moviesResultObject) {
                        ivBackground.setAlpha((float)1.0);

                        Target target = new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                Animation fadeIn = AnimationUtils.loadAnimation(DetailActivity.this, R.anim.fade_in);
                                ivBackground.startAnimation(fadeIn);

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
                                .load("http://image.tmdb.org/t/p/w500/" + moviesResultObject.getBackdropPath())
                                .error(R.drawable.ic_error)
                                .into(target);
                    }
                });
    }

    private void makeApiRequest(){
        apiInterface.getMovieDetailsById(Integer.toString(movieId), MainActivity.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<MoviesResult>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {

                        showSnackbar("Connect to the Internet !!!");

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

                    @Override
                    public void onNext(MoviesResult moviesResultObject1) {

                        moviesResultObject = moviesResultObject1;

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

                        Picasso.get()
                                .load("http://image.tmdb.org/t/p/w500/" + moviesResultObject.getPosterPath())
                                //    .placeholder(R.drawable.loading)
                                .error(R.drawable.ic_error)
                                .into(ivDetailMainImage);

                        setAppropriateFabImage();

                        loadReviews();
                        loadTrailers();

                    }
                });
    }
}
