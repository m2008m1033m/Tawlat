package com.tawlat.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tawlat.R;
import com.tawlat.TawlatApplication;
import com.tawlat.models.Review;
import com.tawlat.utils.MiscUtils;
import com.tawlat.utils.TawlatUtils;

import java.util.ArrayList;

public class ReviewsAdapter extends RefreshAdapter {

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImage;
        private TextView mName;
        private TextView mRate;
        private TextView mDate;
        private TextView mText;
        private View mReplyContainer;
        private TextView mReplyText;
        private TextView mVenueName;
        private TextView mReplyDate;

        public ViewHolder(View itemView) {
            super(itemView);


            mImage = (ImageView) itemView.findViewById(R.id.user_photo);
            mName = (TextView) itemView.findViewById(R.id.user_name);
            mRate = (TextView) itemView.findViewById(R.id.rating);
            mDate = (TextView) itemView.findViewById(R.id.date);
            mText = (TextView) itemView.findViewById(R.id.review);
            mReplyContainer = itemView.findViewById(R.id.reply_container);
            mReplyText = (TextView) itemView.findViewById(R.id.reply);
            mVenueName = (TextView) itemView.findViewById(R.id.venue_name);
            mReplyDate = (TextView) itemView.findViewById(R.id.reply_date);

        }
    }

    private ArrayList<Review> mItems = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        Review item = mItems.get(position);

        ((ViewHolder) holder).mName.setText(item.getUserName());
        ((ViewHolder) holder).mDate.setText(MiscUtils.getDurationFormatted(item.getCreatedDate()));
        float avg = (item.getAmbienceFeedBack() + item.getServiceFeedBack() + item.getCleanlinessFeedBack() + item.getQualityFeedBack()) / 4.0f;
        ((ViewHolder) holder).mRate.setText(TawlatUtils.convertRatingToStars((int) avg));
        ((ViewHolder) holder).mText.setText(item.getReviewNotes());

        Picasso.with(TawlatApplication.getContext())
                .load(item.getUserImage())
                .placeholder(R.drawable.no_avatar)
                .into(((ViewHolder) holder).mImage);

        //load the reply if any:
        if (item.getManagerComents() != null && !item.getManagerComents().isEmpty()) {
            ((ViewHolder) holder).mReplyContainer.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).mReplyText.setText(item.getManagerComents());
            ((ViewHolder) holder).mVenueName.setText(item.getManagerName());
            //((ViewHolder) holder).mReplyDate.setText(MiscUtils.getDurationFormatted(item.getManageCommentDate()));
        } else {
            ((ViewHolder) holder).mReplyContainer.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public ArrayList<Review> getItems() {
        return mItems;
    }
}
