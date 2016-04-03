package com.tawlat.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tawlat.R;
import com.tawlat.TawlatApplication;
import com.tawlat.models.venues.FavoriteVenue;
import com.tawlat.utils.TawlatUtils;

import java.util.ArrayList;

public class MyFavoritesAdapter extends RefreshAdapter {

    public interface OnActionListener {
        void onItemClicked(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImage;
        private TextView mLocation;
        private TextView mName;
        private TextView mCuisines;
        private TextView mPrice;
        private TextView mRating;

        public ViewHolder(View itemView) {
            super(itemView);

            mImage = (ImageView) itemView.findViewById(R.id.photo);
            mLocation = (TextView) itemView.findViewById(R.id.location);
            mName = (TextView) itemView.findViewById(R.id.venue_name);
            mCuisines = (TextView) itemView.findViewById(R.id.cuisine);
            mPrice = (TextView) itemView.findViewById(R.id.price);
            mRating = (TextView) itemView.findViewById(R.id.rating);
        }
    }


    private ArrayList<FavoriteVenue> mItems = new ArrayList<>();
    private OnActionListener mOnActionListener;

    public MyFavoritesAdapter(OnActionListener onActionListener) {
        mOnActionListener = onActionListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_item, parent, false);
        final ViewHolder vh = new ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnActionListener != null) {
                    mOnActionListener.onItemClicked(vh.getAdapterPosition());
                }
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        FavoriteVenue item = mItems.get(position);

        ((ViewHolder) holder).mRating.setText(TawlatUtils.convertRatingToStars((int) item.getGetAvgReviews()));
        ((ViewHolder) holder).mPrice.setText(item.getPriceRange());
        ((ViewHolder) holder).mCuisines.setText(item.getVenueCuisinesAsString());
        ((ViewHolder) holder).mName.setText(item.getVenueName());
        ((ViewHolder) holder).mLocation.setText(item.getVenueLocation());

        Glide.with(TawlatApplication.getContext())
                .load(item.getCoverImage())
                .placeholder(R.drawable.logograyscale)
                .into(((ViewHolder) holder).mImage);

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public ArrayList<FavoriteVenue> getItems() {
        return mItems;
    }

}
