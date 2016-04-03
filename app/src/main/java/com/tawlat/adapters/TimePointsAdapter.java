package com.tawlat.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tawlat.R;
import com.tawlat.models.TimeSlot;

import java.util.ArrayList;

public class TimePointsAdapter extends RecyclerView.Adapter {

    public interface OnActionListener {
        void onSelected(int position);

        void onUnavailableSelected(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTime;
        private TextView mPoints;
        private ImageView mIcon;
        private TextView mAvailable;
        private TextView mNotAvailable;

        public ViewHolder(View itemView) {
            super(itemView);

            mTime = (TextView) itemView.findViewById(R.id.time);
            mPoints = (TextView) itemView.findViewById(R.id.points);
            mIcon = (ImageView) itemView.findViewById(R.id.icon);
            mAvailable = (TextView) itemView.findViewById(R.id.available);
            mNotAvailable = (TextView) itemView.findViewById(R.id.unavailable);
            mIcon.setVisibility(View.GONE);
        }
    }

    private OnActionListener mOnActionListener;
    private ArrayList<TimeSlot> mItems = new ArrayList<>();
    private int mSelectedItem = -1;
    private int mNumberOfPeople = 2;

    public TimePointsAdapter(OnActionListener onActionListener) {
        mOnActionListener = onActionListener;
    }

    public ArrayList<TimeSlot> getItems() {
        return mItems;
    }

    public void setSelectedItem(int selectedItem) {
        mSelectedItem = selectedItem;
    }

    public int getSelectedItem() {
        if (mItems.size() == 0) return -1;
        return mSelectedItem;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        mNumberOfPeople = numberOfPeople;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_point_item, parent, false);
        final ViewHolder vh = new ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeSlot timeSlot = mItems.get(vh.getAdapterPosition());
                if (mOnActionListener != null)
                    // check if the seats are available
                    if (timeSlot.getSeatQuota() >= mNumberOfPeople) {

                        // if there is another one selected, deselect it first
                        if (mSelectedItem != -1) {
                            notifyItemChanged(mSelectedItem);
                            mSelectedItem = -1;
                        }

                        // set the newly selected item.
                        mSelectedItem = vh.getAdapterPosition();
                        notifyItemChanged(mSelectedItem);
                        mOnActionListener.onSelected(vh.getAdapterPosition());
                    } else {
                        mOnActionListener.onUnavailableSelected(vh.getAdapterPosition());
                    }
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TimeSlot item = mItems.get(position);

        ((ViewHolder) holder).mTime.setText(item.getStartTime("hh:mm a"));
        ((ViewHolder) holder).mPoints.setText((item.getPoints() + 50 * (mNumberOfPeople - 1)) + " pts");

        if (mNumberOfPeople > item.getSeatQuota()) {
            ((ViewHolder) holder).mAvailable.setVisibility(View.GONE);
            ((ViewHolder) holder).mNotAvailable.setVisibility(View.VISIBLE);
        } else {
            ((ViewHolder) holder).mAvailable.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).mNotAvailable.setVisibility(View.GONE);
        }

        if (position == mSelectedItem) {
            ((ViewHolder) holder).mIcon.setVisibility(View.VISIBLE);
        } else {
            ((ViewHolder) holder).mIcon.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


}
