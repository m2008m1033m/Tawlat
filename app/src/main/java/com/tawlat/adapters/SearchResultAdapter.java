package com.tawlat.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.tawlat.R;
import com.tawlat.TawlatApplication;
import com.tawlat.models.search.AdvancedSearchResult;
import com.tawlat.utils.TawlatUtils;

import java.util.ArrayList;

public class SearchResultAdapter extends RefreshAdapter {
    public interface OnActionListener {
        void onItemClicked(int position);

        void onButtonClicked(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private TextView mLocation;
        private TextView mPoints;
        private TextView mRate;
        private ImageView mCoverImage;
        private ImageView mLogo;
        private LinearLayout mRibbon;

        private Button mBookButton;

        ViewHolder(View itemView) {
            super(itemView);

            mName = (TextView) itemView.findViewById(R.id.venue_name);
            mLocation = (TextView) itemView.findViewById(R.id.location);
            mPoints = (TextView) itemView.findViewById(R.id.points);
            mRate = (TextView) itemView.findViewById(R.id.rating);
            mCoverImage = (ImageView) itemView.findViewById(R.id.cover_image);
            mLogo = (ImageView) itemView.findViewById(R.id.logo);
            mRibbon = (LinearLayout) itemView.findViewById(R.id.points_ribbon);
            mBookButton = (Button) itemView.findViewById(R.id.button);
        }
    }

    private ArrayList<AdvancedSearchResult> mItems = new ArrayList<>();
    private OnActionListener mOnActionListener;
    private int mNumberOfPeople = 2;

    public SearchResultAdapter(OnActionListener onActionListener) {
        mOnActionListener = onActionListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.venue_item, parent, false);
        final ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnActionListener.onItemClicked(vh.getAdapterPosition());
            }
        });


        vh.mBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnActionListener.onButtonClicked(vh.getAdapterPosition());
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        AdvancedSearchResult item = mItems.get(position);
        ((ViewHolder) holder).mName.setText(item.getName());
        ((ViewHolder) holder).mLocation.setText(item.getVenueLocation());
        ((ViewHolder) holder).mRate.setText(TawlatUtils.convertRatingToStars((int) item.getGetAvgReviews()));
        ((ViewHolder) holder).mPoints.setText("+" + (item.getPoints() + (mNumberOfPeople - 1) * 50));
        if (item.isDirectory()) {
            ((ViewHolder) holder).mBookButton.setVisibility(View.GONE);
            ((ViewHolder) holder).mRibbon.setVisibility(View.GONE);
        } else {
            ((ViewHolder) holder).mBookButton.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).mRibbon.setVisibility(View.VISIBLE);
        }


        Glide.with(TawlatApplication.getContext())
                .load(item.getCoverImage())
                .placeholder(R.drawable.logograyscale)
                .into(((ViewHolder) holder).mCoverImage);

        Picasso.with(TawlatApplication.getContext())
                .load(item.getVenueLogo())
                .placeholder(R.drawable.venue_no_avatar)
                .into(((ViewHolder) holder).mLogo);


    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public ArrayList<AdvancedSearchResult> getItems() {
        return mItems;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        mNumberOfPeople = numberOfPeople;
        notifyDataSetChanged();
    }
}
