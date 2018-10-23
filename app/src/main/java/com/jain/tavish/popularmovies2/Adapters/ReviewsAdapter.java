package com.jain.tavish.popularmovies2.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.jain.tavish.popularmovies2.R;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.myViewHolder> {
    private final List<String> reviewerNameList;
    private final List<String> reviewList;

    public @BindView(R.id.tv_fav_zero) TextView tv_fav_zero;

    public static class myViewHolder extends RecyclerView.ViewHolder  {
        public @BindView(R.id.iv_initial_letter_reviews_item) ImageView imageView;
        public @BindView(R.id.tv_reviewer_name_item) TextView reviewerName;
        public @BindView(R.id.tv_reviewer_review_item) TextView reviewReview;
        public myViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public ReviewsAdapter(Context context, List<String> mReviewerNameList, List<String> mReviewList) {
        reviewerNameList = mReviewerNameList;
        Context mContext = context;
        reviewList = mReviewList;
    }

    @NonNull
    @Override
    public ReviewsAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews_row_item, parent, false);
        return new myViewHolder(v);
    }

    private int getColor(){
        Random rand = new Random();

        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);

        return Color.rgb(r,g,b);
    }

    @Override
    public void onBindViewHolder(@NonNull final myViewHolder holder, int position) {

        if (reviewerNameList.size() > 0) {
            if (reviewerNameList.get(position) != null) {
                TextDrawable drawable = TextDrawable.builder()
                        .beginConfig()
                        .withBorder(5)
                        .endConfig()
                        .buildRound(String.valueOf(reviewerNameList.get(position).charAt(0)), getColor());

                holder.imageView.setImageDrawable(drawable);
            }

            holder.reviewerName.setText(reviewerNameList.get(position));
            holder.reviewReview.setText(reviewList.get(position));
        }

    }
    @Override
    public int getItemCount() {
        return reviewList.size();
    }
}