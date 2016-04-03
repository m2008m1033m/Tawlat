package com.tawlat.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tawlat.R;
import com.tawlat.TawlatApplication;
import com.tawlat.models.Voucher;

import java.util.ArrayList;

public class VouchersAdapter extends RefreshAdapter {

    public interface OnActionListener {
        void onItemClicked(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CardView mCardView;
        private TextView mVoucherNumber;
        private TextView mRedemption;
        private TextView mExpiry;
        private TextView mValue;
        private TextView mVoucherType;
        private LinearLayout mHeader;

        private String mVenuesToRedeemAt;

        ViewHolder(View itemView) {
            super(itemView);

            mCardView = (CardView) itemView.findViewById(R.id.card_view);
            mVoucherNumber = (TextView) itemView.findViewById(R.id.number);
            mRedemption = (TextView) itemView.findViewById(R.id.redemption);
            mExpiry = (TextView) itemView.findViewById(R.id.expiry);
            mValue = (TextView) itemView.findViewById(R.id.value);
            mVoucherType = (TextView) itemView.findViewById(R.id.type);
            mHeader = (LinearLayout) itemView.findViewById(R.id.header);
        }
    }

    private ArrayList<Voucher> mItems = new ArrayList<>();
    private Voucher.Status mStatus;
    private OnActionListener mOnActionlistener;

    public VouchersAdapter(Voucher.Status status, OnActionListener onActionlistener) {
        mStatus = status;
        mOnActionlistener = onActionlistener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.voucher_item, parent, false);
        final ViewHolder vh = new ViewHolder(v);

        if (mStatus == Voucher.Status.PENDING) {
            vh.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnActionlistener != null) {
                        mOnActionlistener.onItemClicked(vh.getAdapterPosition());
                    }
                }
            });
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Voucher item = mItems.get(position);

        ((ViewHolder) holder).mVoucherNumber.setText(item.getVoucherCode());
        ((ViewHolder) holder).mValue.setText(String.valueOf(item.getAmount()));


        ((ViewHolder) holder).mVenuesToRedeemAt = item.getRedeemableAtAsString();

        //change the titles:
        ((ViewHolder) holder).mVoucherType.setText(((item.getType() == Voucher.Type.GIFT_VOUCHER) ?
                TawlatApplication.getContext().getString(R.string.gift_voucher_paranthesis) :
                TawlatApplication.getContext().getString(R.string.points_voucher_paranthesis)
        ));

        ((ViewHolder) holder).mRedemption.setText((mStatus == Voucher.Status.PENDING) ?
                        TawlatApplication.getContext().getString(R.string.redeemable_at_certain_venues) :
                        TawlatApplication.getContext().getString(R.string.redeemed_at_s, item.getVenueName())
        );


        ((ViewHolder) holder).mExpiry.setText(
                mStatus == Voucher.Status.PENDING ?
                        TawlatApplication.getContext().getString(R.string.expires_at_s, item.getExpiryDate("dd/MM/yyyy")) :
                        TawlatApplication.getContext().getString(R.string.redeemed_on_s, item.getExpiryDate("dd/MM/yyyy"))

        );


        /**
         * setting the color of the value
         */
        ((ViewHolder) holder).mValue.setTextColor(ContextCompat.getColor(TawlatApplication.getContext(),

                (mStatus == Voucher.Status.PENDING) ?
                        R.color.orange_dark :
                        R.color.grey_dark

        ));

        /**
         * setting the color of the texts
         */
        ((ViewHolder) holder).mRedemption.setTextColor(ContextCompat.getColor(TawlatApplication.getContext(),

                (mStatus == Voucher.Status.PENDING) ?
                        R.color.black :
                        R.color.black_light

        ));
        ((ViewHolder) holder).mExpiry.setTextColor(ContextCompat.getColor(TawlatApplication.getContext(),

                (mStatus == Voucher.Status.PENDING) ?
                        R.color.black :
                        R.color.black_light

        ));

        /**
         * setting the color of the header
         */
        ((ViewHolder) holder).mHeader.setBackgroundColor(ContextCompat.getColor(TawlatApplication.getContext(),

                (mStatus == Voucher.Status.PENDING) ?
                        R.color.black_light :
                        R.color.grey_dark

        ));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public ArrayList<Voucher> getItems() {
        return mItems;
    }
}
