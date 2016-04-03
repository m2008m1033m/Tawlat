package com.tawlat.models;

import com.tawlat.core.Communicator;
import com.tawlat.utils.MiscUtils;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Reservation extends Model implements Serializable {

    public enum Status {
        PENDING,
        CANCELED,
        PASSED,
        NO_SHOW,
        PROMO_DINER
    }

    private Date mCheckInDateTime;
    private Date mCheckOutDateTime;
    private String mUserId;
    private String mVenueId;
    private int mPeople;
    private int mPoints;
    private String mVenueName;
    private Status mStatus;
    private String mCoverImage;
    private String mVenueLogo;
    private String mLocationName;
    private String mReservationCode;

    public Reservation(JSONObject jsonObject) {
        setId(MiscUtils.getString(jsonObject, "VenueReservationId", ""));
        setCheckInDateTime(MiscUtils.getString(jsonObject, "CheckInDateTime", ""));
        setCheckOutDateTime(MiscUtils.getString(jsonObject, "CheckOutDateTime", ""));
        setUserId(MiscUtils.getString(jsonObject, "UserId", ""));
        setVenueId(MiscUtils.getString(jsonObject, "VenueId", ""));
        setPeople(MiscUtils.getInt(jsonObject, "People", 1));
        setPoints(MiscUtils.getInt(jsonObject, "TotalPoints", 0));
        setVenueName(MiscUtils.getString(jsonObject, "VenueName", ""));
        setStatus(MiscUtils.getString(jsonObject, "Status", ""));
        setCoverImage(MiscUtils.getString(jsonObject, "CoverImage", ""));
        setVenueLogo(MiscUtils.getString(jsonObject, "VenueLogo", ""));
        setLocationName(MiscUtils.getString(jsonObject, "LocationName", ""));
        setReservationCode(MiscUtils.getString(jsonObject, "ReservationCode", ""));
    }

    @Override
    public void copyFrom(Model model) {
        if (!(model instanceof Reservation)) return;
        setId(model.getId());
        setCheckInDateTime(((Reservation) model).getCheckInDateTime());
        setCheckOutDateTime(((Reservation) model).getCheckOutDateTime());
        setUserId(((Reservation) model).getUserId());
        setVenueId(((Reservation) model).getVenueId());
        setPeople(((Reservation) model).getPeople());
        setPoints(((Reservation) model).getPoints());
        setVenueName(((Reservation) model).getVenueName());
        setStatus(((Reservation) model).getStatus());
        setCoverImage(((Reservation) model).getCoverImage());
        setVenueLogo(((Reservation) model).getVenueLogo());
        setLocationName(((Reservation) model).getLocationName());
        setReservationCode(((Reservation) model).getReservationCode());
    }

    public Date getCheckInDateTime() {
        return mCheckInDateTime;
    }

    public void setCheckInDateTime(Date checkInDateTime) {
        mCheckInDateTime = checkInDateTime;
    }

    public String getCheckInDateTime(String format) {
        if (mCheckInDateTime == null) return "";
        return new SimpleDateFormat(format, Locale.US).format(mCheckInDateTime);
    }

    public void setCheckInDateTime(String checkInDateTime) {
        try {
            mCheckInDateTime = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US).parse(checkInDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Date getCheckOutDateTime() {
        return mCheckOutDateTime;
    }

    public void setCheckOutDateTime(Date checkOutDateTime) {
        mCheckOutDateTime = checkOutDateTime;
    }

    public String getCheckOutDateTime(String format) {
        if (mCheckOutDateTime == null) return "";
        return new SimpleDateFormat(format, Locale.US).format(mCheckOutDateTime);
    }

    public void setCheckOutDateTime(String checkOutDateTime) {
        try {
            mCheckOutDateTime = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US).parse(checkOutDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getVenueId() {
        return mVenueId;
    }

    public void setVenueId(String venueId) {
        mVenueId = venueId;
    }

    public int getPeople() {
        return mPeople;
    }

    public void setPeople(int people) {
        mPeople = people;
    }

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int points) {
        mPoints = points;
    }

    public String getVenueName() {
        return mVenueName;
    }

    public void setVenueName(String venueName) {
        mVenueName = venueName;
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        mStatus = status;
    }

    public void setStatus(String status) {
        switch (status) {
            case "Pending":
                mStatus = Status.PENDING;
                break;
            case "Cancelled":
                mStatus = Status.CANCELED;
                break;
            case "Passed":
                mStatus = Status.PASSED;
                break;
            case "PromoDiner":
                mStatus = Status.PROMO_DINER;
                break;
            case "NoShow":
                mStatus = Status.NO_SHOW;
                break;
        }
    }

    public String getCoverImage() {
        return mCoverImage;
    }

    public void setCoverImage(String coverImage) {
        mCoverImage = coverImage;
    }

    public String getVenueLogo() {
        return Communicator.BASE_URL + "Uploads/LogoImages/" + mVenueLogo;
    }

    public void setVenueLogo(String venueLogo) {
        mVenueLogo = venueLogo;
    }

    public String getLocationName() {
        return mLocationName;
    }

    public void setLocationName(String locationName) {
        mLocationName = locationName;
    }

    public String getReservationCode() {
        return mReservationCode;
    }

    public void setReservationCode(String reservationCode) {
        mReservationCode = reservationCode;
    }
}
