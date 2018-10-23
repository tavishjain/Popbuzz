package com.jain.tavish.popularmovies2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jain.tavish.popularmovies2.ModelClasses.TrailerResult;
import com.jain.tavish.popularmovies2.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailerAdapter extends PagerAdapter {

    private Context context;
    private List<TrailerResult> trailerResultList;
    private String TRAILER_BASE_URL = "http://www.youtube.com/watch?v=";
    private LayoutInflater mLayoutInflater;

    public TrailerAdapter(Context context, List<TrailerResult> trailerResultList) {
        this.context = context;
        this.trailerResultList = trailerResultList;
        mLayoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return trailerResultList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.trailers_row_item, container, false);

        ImageView thumbnailImage = itemView.findViewById(R.id.iv_trailer_thumb_item);

        String TRAILER_THUMBNAIL_END = "/0.jpg";
        String TRAILER_THUMBNAIL_START = "https://img.youtube.com/vi/";
        Picasso.get()
                .load(TRAILER_THUMBNAIL_START + trailerResultList.get(position).getKey() + TRAILER_THUMBNAIL_END)
                .into(thumbnailImage);

        thumbnailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(TRAILER_BASE_URL + trailerResultList.get(position).getKey()));
                context.startActivity(intent);
            }
        });

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position,@NonNull Object object) {
        container.removeView((FrameLayout) object);
    }
}