package com.tawlat.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tawlat.R;
import com.tawlat.TawlatApplication;
import com.tawlat.models.Reservation;
import com.tawlat.utils.TawlatUtils;

import java.util.ArrayList;


public class ReservationsAdapter extends RefreshAdapter {

    public interface OnActionListener {
        void onItemClicked(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mName;
        private TextView mPoints;
        private TextView mDate;
        private TextView mTime;
        private TextView mPeople;
        private TextView mLocation;
        private ImageView mLogo;
        private View mLogoOverLay;
        private FrameLayout mContainer;

        ViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.venue_name);
            mPoints = (TextView) itemView.findViewById(R.id.points);
            mDate = (TextView) itemView.findViewById(R.id.date);
            mTime = (TextView) itemView.findViewById(R.id.time);
            mPeople = (TextView) itemView.findViewById(R.id.people);
            mLocation = (TextView) itemView.findViewById(R.id.location);
            mLogoOverLay = itemView.findViewById(R.id.image_overlay);
            mContainer = (FrameLayout) itemView.findViewById(R.id.container);
            mLogo = (ImageView) itemView.findViewById(R.id.logo);
        }
    }

    private ArrayList<Reservation> mItems = new ArrayList<>();
    private OnActionListener mOnActionListener;
    private Reservation.Status mStatus;

    public ReservationsAdapter(OnActionListener onActionListener, Reservation.Status status) {
        mOnActionListener = onActionListener;
        mStatus = status;
    }

    @Override
    public ArrayList<Reservation> getItems() {
        return mItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_item, parent, false);
        final ViewHolder vh = new ViewHolder(v);

        /**
         * if the pressed mItem is an
         * upcoming one, listen for clicks
         * and act upon it
         * actions would be either removing
         * the reservation or alter it
         */
        if (mStatus == Reservation.Status.PENDING) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnActionListener != null) {
                        mOnActionListener.onItemClicked(vh.getAdapterPosition());
                    }
                }
            });
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        final Reservation item = mItems.get(position);

        ((ViewHolder) holder).mName.setText(item.getVenueName());
        ((ViewHolder) holder).mPoints.setText(item.getPoints() + " pts");
        ((ViewHolder) holder).mDate.setText(item.getCheckInDateTime("dd/MM/yyyy"));
        ((ViewHolder) holder).mTime.setText(item.getCheckInDateTime("hh:mm a"));
        ((ViewHolder) holder).mPeople.setText(String.valueOf(item.getPeople()));

        ((ViewHolder) holder).mLocation.setText(item.getLocationName());


        if (item.getVenueLogo().isEmpty())
            Picasso.with(TawlatApplication.getContext())
                    .load(R.drawable.venue_no_avatar)
                    .into(((ViewHolder) holder).mLogo);
        else
            Picasso.with(TawlatApplication.getContext())
                    .load(item.getVenueLogo())
                    .placeholder(R.drawable.venue_no_avatar)
                    .into(((ViewHolder) holder).mLogo);


        switch (mStatus) {
            case PENDING:
            case PASSED:
                ((ViewHolder) holder).mLogoOverLay.setBackgroundResource(R.drawable.circle);
                ((ViewHolder) holder).mPoints.setTextColor(TawlatApplication.getContext().getResources().getColor(R.color.orange_dark));
                TawlatUtils.setGrayScale(((ViewHolder) holder).mLogo, false);
                ((ViewHolder) holder).mContainer.setAlpha(1.0f);

                break;

            case CANCELED:
            case NO_SHOW:
                ((ViewHolder) holder).mLogoOverLay.setBackgroundResource(R.drawable.circle_white);
                ((ViewHolder) holder).mPoints.setTextColor(TawlatApplication.getContext().getResources().getColor(R.color.grey_dark));
                TawlatUtils.setGrayScale(((ViewHolder) holder).mLogo, true);
                ((ViewHolder) holder).mContainer.setAlpha(0.7f);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

}
