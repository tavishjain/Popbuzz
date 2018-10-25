package com.jain.tavish.popularmovies2;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {

    private static final Long SPLASH_DELAY = 3000L;
    private Handler mDelayHandler = new Handler();
    private TextView mAppNameView;
    private ImageView mAppLogoView;

    private Runnable runnable = new Runnable() {
        @Override public void run() {
            Intent intent = new Intent(SplashScreenActivity.this , MainActivity.class);

            Pair<View, String> p1 = Pair.create((View)mAppNameView, ViewCompat.getTransitionName(mAppNameView));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashScreenActivity.this, p1);
            startActivity(intent, options.toBundle());
         //   finish();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1500);




        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAppLogoView = findViewById(R.id.iv_app_icon);
        mAppNameView = findViewById(R.id.tv_app_name);

        mDelayHandler.postDelayed(runnable, SPLASH_DELAY);

        startAnimation();
    }

    private void startAnimation() {
        mAppNameView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override public void onGlobalLayout() {
                        mAppNameView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        AnimatorSet mAnimatorSet = new AnimatorSet();
                        mAnimatorSet.playTogether(
                                ObjectAnimator.ofFloat(mAppNameView, "alpha", 0, 1, 1, 1),
                                ObjectAnimator.ofFloat(mAppNameView, "scaleX", 0.3f, 1.05f, 0.9f, 1),
                                ObjectAnimator.ofFloat(mAppNameView, "scaleY", 0.3f, 1.05f, 0.9f, 1));
                        mAnimatorSet.setDuration(2000);
                        mAnimatorSet.start();
                    }
                });
        mAppLogoView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override public void onGlobalLayout() {
                        mAppLogoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        AnimatorSet mAnimatorSet = new AnimatorSet();
                        mAnimatorSet.playTogether(
                                ObjectAnimator.ofFloat(mAppLogoView, "alpha", 0, 1, 1, 1),
                                ObjectAnimator.ofFloat(mAppLogoView, "scaleX", 0.3f, 1.05f, 0.9f, 1),
                                ObjectAnimator.ofFloat(mAppLogoView, "scaleY", 0.3f, 1.05f, 0.9f, 1));
                        mAnimatorSet.setDuration(2000);
                        mAnimatorSet.start();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDelayHandler.removeCallbacks(runnable);
    }
}