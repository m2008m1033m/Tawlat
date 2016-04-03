package com.tawlat.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.tawlat.R;
import com.tawlat.TawlatApplication;
import com.tawlat.models.venues.OfferEventVenue;
import com.tawlat.utils.TawlatUtils;

import java.util.ArrayList;

public class OffersAndEventsVenuesAdapter extends RefreshAdapter {

    public interface OnActionListener {
        void onItemClicked(int position);

        void onButtonClicked(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CardView mCardView;
        private ImageView mCoverImage;
        private ImageView mLogo;
        private TextView mVenueName;
        private TextView mLocationName;
        private TextView mOfferEvent;
        private Button mButton;
        private TextView mRating;

        public ViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.card_view);
            mCoverImage = (ImageView) itemView.findViewById(R.id.cover_image);
            mVenueName = (TextView) itemView
                    .findViewById(R.id.venue_name);
            mLocationName = (TextView) itemView.findViewById(R.id.location);
            mOfferEvent = (TextView) itemView.findViewById(R.id.offer_event);
            mButton = (Button) itemView.findViewById(R.id.button);
            mLogo = (ImageView) itemView.findViewById(R.id.logo);
            mRating = (TextView) itemView.findViewById(R.id.rating);

            itemView.findViewById(R.id.points_ribbon).setVisibility(View.GONE);
            mOfferEvent.setVisibility(View.VISIBLE);
            mButton.setVisibility(View.VISIBLE);
            mRating.setVisibility(View.GONE);
            mButton.setText(R.string.details);
        }
    }

    private ArrayList<OfferEventVenue> mItems = new ArrayList<>();

    private OnActionListener mOnActionListener;

    public OffersAndEventsVenuesAdapter(OnActionListener listener) {
        mOnActionListener = listener;
    }

    @Override
    public ArrayList<OfferEventVenue> getItems() {
        return mItems;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.venue_item, parent, false);
        final ViewHolder vh = new ViewHolder(v);

        vh.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnActionListener != null) {
                    mOnActionListener.onItemClicked(vh.getAdapterPosition());
                }
            }
        });

        vh.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnActionListener != null)
                    mOnActionListener.onButtonClicked(vh.getAdapterPosition());
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
        final OfferEventVenue item = this.mItems.get(position);

        Glide.with(TawlatApplication.getContext())
                .load(item.getImage())
                .placeholder(R.drawable.logograyscale)
                .into(((ViewHolder) vh).mCoverImage);

        if (item.getVenueLogo().isEmpty())
            Picasso.with(TawlatApplication.getContext())
                    .load(R.drawable.venue_no_avatar)
                    .into(((ViewHolder) vh).mLogo);
        else
            Picasso.with(TawlatApplication.getContext())
                    .load(item.getVenueLogo())
                    .placeholder(R.drawable.venue_no_avatar)
                    .into(((ViewHolder) vh).mLogo);

        ((ViewHolder) vh).mLocationName.setText(item.getVenueLocation());
        ((ViewHolder) vh).mRating.setText(TawlatUtils.convertRatingToStars(((int) item.getGetAvgReviews())));

        ((ViewHolder) vh).mOfferEvent.setText(item.getName());
        ((ViewHolder) vh).mVenueName.setText(item.getVenueName());

        if (item.getType() == OfferEventVenue.Type.OFFER) {
            ((ViewHolder) vh).mOfferEvent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_restaurant_menu_white_24dp, 0);
        } else {
            ((ViewHolder) vh).mOfferEvent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_event_available_white_24dp, 0);
        }

    }
}
