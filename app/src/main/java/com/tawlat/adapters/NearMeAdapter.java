package com.tawlat.adapters;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tawlat.R;
import com.tawlat.TawlatApplication;
import com.tawlat.models.venues.NearMeVenue;
import com.tawlat.utils.TawlatUtils;

import java.util.ArrayList;

public class NearMeAdapter extends PagerAdapter {

    public interface OnActionListener {
        void onItemClicked(int position);
    }

    private ArrayList<NearMeVenue> mItems = new ArrayList<>();
    private OnActionListener mOnActionListener;


    public NearMeAdapter(OnActionListener listener) {
        mOnActionListener = listener;
    }


    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.near_by_item, container, false);
        final NearMeVenue item = mItems.get(position);


        String rating = TawlatUtils.convertRatingToStars(((int) item.getGetAvgReviews())) + " (" + item.getPublishedReviewsCount() + ")";

        ((TextView) view.findViewById(R.id.venue_name)).setText(item.getName());
        ((TextView) view.findViewById(R.id.location)).setText(item.getLocation());
        ((TextView) view.findViewById(R.id.rating)).setText(rating);
        ((TextView) view.findViewById(R.id.distance)).setText(item.getDistanceFormatted());

        if (item.getVenueLogo().isEmpty())
            Picasso.with(TawlatApplication.getContext())
                    .load(R.drawable.venue_no_avatar)
                    .into(((ImageView) view.findViewById(R.id.logo)));
        else
            Picasso.with(TawlatApplication.getContext())
                    .load(item.getVenueLogo())
                    .placeholder(R.drawable.venue_no_avatar)
                    .into(((ImageView) view.findViewById(R.id.logo)));

        view.findViewById(R.id.wrapper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnActionListener.onItemClicked(position);
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    public ArrayList<NearMeVenue> getItems() {
        return mItems;
    }
}