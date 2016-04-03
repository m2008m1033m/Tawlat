package com.tawlat.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tawlat.R;
import com.tawlat.models.ReservationResult;
import com.tawlat.models.venues.Venue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReservationSection3Fragment extends Fragment {

    private View mView;
    private TextView mPoints;
    private TextView mCode;
    private TextView mDate;
    private TextView mTime;
    private TextView mPeople;
    private TextView mDiscount;
    private TextView mPhone;
    private TextView mAddress;


    /**
     * to be set on onCreate()
     */
    private int mTotalPoints;
    private ReservationResult mReservationResult;
    private Date mDateTime;
    private int mPeopleNumber;
    private int mDiscountAmount;
    private Venue mVenue;


    public ReservationSection3Fragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.reservation_section_3, container, false);
        init();
        return mView;
    }

    private void init() {
        initReferences();
        fillFields();
        initEvents();
    }

    private void initReferences() {
        mPoints = ((TextView) mView.findViewById(R.id.points));
        mCode = ((TextView) mView.findViewById(R.id.code));
        mDate = ((TextView) mView.findViewById(R.id.date));
        mTime = ((TextView) mView.findViewById(R.id.time));
        mPeople = ((TextView) mView.findViewById(R.id.people));
        mDiscount = ((TextView) mView.findViewById(R.id.discount));
        mPhone = ((TextView) mView.findViewById(R.id.phone));
        mAddress = ((TextView) mView.findViewById(R.id.address));
    }

    private void fillFields() {
        mPoints.setText(getString(R.string.plus_d_pts, mTotalPoints));
        mCode.setText(mReservationResult.getReservationCode());
        mDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(mDateTime));
        mTime.setText(new SimpleDateFormat("hh:mm a", Locale.US).format(mDateTime));
        mPeople.setText(String.valueOf(mPeopleNumber));
        mDiscount.setText(mDiscountAmount == 0 ? getString(R.string.n_a) : getString(R.string.d_aed, mDiscountAmount));
        mPhone.setText(mVenue.getPhone());
        mAddress.setText(mVenue.getAddress());
    }

    private void initEvents() {
        mPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + mVenue.getPhone()));
                startActivity(i);
            }
        });


        mAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?f=d&daddr=" + mVenue.getLatitude() + "," + mVenue.getLongitude() + "&dirflg=d&layer=t"));
                startActivity(intent);
            }
        });
    }

    public void setTotalPoints(int totalPoints) {
        mTotalPoints = totalPoints;
    }

    public void setReservationResult(ReservationResult reservationResult) {
        mReservationResult = reservationResult;
    }

    public void setDateTime(Date dateTime) {
        mDateTime = dateTime;
    }

    public void setPeopleNumber(int peopleNumber) {
        mPeopleNumber = peopleNumber;
    }

    public void setDiscountAmount(int discountAmount) {
        mDiscountAmount = discountAmount;
    }

    public void setVenue(Venue venue) {
        mVenue = venue;
    }
}
