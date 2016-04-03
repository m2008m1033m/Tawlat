package com.tawlat.models;

import com.tawlat.utils.MiscUtils;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Voucher extends Model {

    public enum Type {
        POINTS_VOUCHER,
        GIFT_VOUCHER
    }

    public enum Status {
        PENDING,
        REDEEMED
    }

    private Date mAddedDate;
    private int mAmount;
    private Date mExpiryDate;
    private Date mGeneratedDateAndTime;
    private boolean mIsRedeemed;
    private int mPoints;
    private String mVoucherCode;
    private Type mType;
    private String mUserId;
    private ArrayList<String> mRedeemableAt = new ArrayList<>();
    private String mTermsAndConditions;
    private String mReservationStatus;
    private String mVenueName;
    private Status mStatus;

    public Voucher(JSONObject jsonObject, Status status) {

        setId(MiscUtils.getString(jsonObject, "Id", ""));
        setAddedDate(MiscUtils.getString(jsonObject, "AddedDate", ""));
        setAmount(MiscUtils.getInt(jsonObject, "Amount", 0));
        setExpiryDate(MiscUtils.getString(jsonObject, "ExpiryDate", ""));
        setGeneratedDateAndTime(MiscUtils.getString(jsonObject, "GeneratedDateAndTime", ""));
        setIsRedeemed(MiscUtils.getBoolean(jsonObject, "IsRedeemed", false));
        setPoints(MiscUtils.getInt(jsonObject, "Points", 0));
        setVoucherCode(MiscUtils.getString(jsonObject, "VoucherCode", ""));
        setType(MiscUtils.getString(jsonObject, "Type", ""));
        setUserId(MiscUtils.getString(jsonObject, "UserId", ""));
        setRedeemableAt(MiscUtils.getString(jsonObject, "RedeemableAt", ""));
        setTermsAndConditions(MiscUtils.getString(jsonObject, "TermsAndConditions", ""));
        setReservationStatus(MiscUtils.getString(jsonObject, "ReservationStatus", ""));
        setVenueName(MiscUtils.getString(jsonObject, "VenueName", ""));
        setStatus(status);
    }


    @Override
    public void copyFrom(Model model) {
        if (!(model instanceof Voucher)) return;
        setId(model.getId());
        setAddedDate(((Voucher) model).getAddedDate());
        setAmount(((Voucher) model).getAmount());
        setExpiryDate(((Voucher) model).getExpiryDate());
        setGeneratedDateAndTime(((Voucher) model).getGeneratedDateAndTime());
        setIsRedeemed(((Voucher) model).isRedeemed());
        setPoints(((Voucher) model).getPoints());
        setVoucherCode(((Voucher) model).getVoucherCode());
        setType(((Voucher) model).getType());
        setUserId(((Voucher) model).getUserId());
        setRedeemableAt(((Voucher) model).getRedeemableAtAsString());
        setTermsAndConditions(((Voucher) model).getTermsAndConditions());
        setReservationStatus(((Voucher) model).getReservationStatus());
        setVenueName(((Voucher) model).getVenueName());
        setStatus(((Voucher) model).getStatus());
    }

    public Date getGeneratedDateAndTime() {
        return mGeneratedDateAndTime;
    }

    public void setGeneratedDateAndTime(Date generatedDateAndTime) {
        mGeneratedDateAndTime = generatedDateAndTime;
    }

    public String getGeneratedDateAndTime(String format) {
        if (mGeneratedDateAndTime == null) return "";
        return new SimpleDateFormat(format, Locale.US).format(mGeneratedDateAndTime);
    }

    public void setGeneratedDateAndTime(String generatedDateAndTime) {
        try {
            generatedDateAndTime = generatedDateAndTime.replace('T', ' ');
            mGeneratedDateAndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(generatedDateAndTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Date getAddedDate() {
        return mAddedDate;
    }

    public void setAddedDate(Date addedDate) {
        mAddedDate = addedDate;
    }

    public String getAddedDate(String format) {
        if (mAddedDate == null) return "";
        return new SimpleDateFormat(format, Locale.US).format(mAddedDate);
    }

    public void setAddedDate(String addedDate) {
        try {
            addedDate = addedDate.replace('T', ' ');
            mAddedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(addedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public int getAmount() {
        return mAmount;
    }

    public void setAmount(int amount) {
        mAmount = amount;
    }

    public Date getExpiryDate() {
        return mExpiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        mExpiryDate = expiryDate;
    }

    public String getExpiryDate(String format) {
        if (mExpiryDate == null) return "";
        return new SimpleDateFormat(format, Locale.US).format(mExpiryDate);
    }

    public void setExpiryDate(String expiryDate) {
        try {
            expiryDate = expiryDate.replace('T', ' ');
            mExpiryDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(expiryDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean isRedeemed() {
        return mIsRedeemed;
    }

    public void setIsRedeemed(boolean isRedeemed) {
        mIsRedeemed = isRedeemed;
    }

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int points) {
        mPoints = points;
    }

    public String getVoucherCode() {
        return mVoucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        mVoucherCode = voucherCode;
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    public void setType(String type) {
        switch (type) {
            case "PointsVoucher":
                mType = Type.POINTS_VOUCHER;
                break;
            case "GiftVoucher":
                mType = Type.GIFT_VOUCHER;
                break;
        }
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getTermsAndConditions() {
        return mTermsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        mTermsAndConditions = termsAndConditions;
    }

    public String getReservationStatus() {
        return mReservationStatus;
    }

    public void setReservationStatus(String reservationStatus) {
        mReservationStatus = reservationStatus;
    }

    public String getVenueName() {
        return mVenueName;
    }

    public void setVenueName(String venueName) {
        mVenueName = venueName;
    }

    public void setRedeemableAt(String redeemableAt) {
        String[] bits = redeemableAt.split(",");
        for (String bit : bits) {
            if (bit.trim().isEmpty()) continue;
            mRedeemableAt.add(bit.trim());
        }
    }

    public String getRedeemableAtAsString() {
        String result = "";
        for (String item : mRedeemableAt)
            result += item + ", ";
        return result.substring(0, result.length() - 2);
    }

    public ArrayList<String> getRedeemableAt() {
        return mRedeemableAt;
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        mStatus = status;
    }


}
