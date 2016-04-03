package com.tawlat.models;

import com.tawlat.utils.MiscUtils;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class TimeSlot extends Model {

    private String mDayOfWeek;
    private Date mEndTime;
    private Date mStartTime;
    private int mSeatQuota;
    private int mPoints;
    private String mRecordStatus;

    public TimeSlot(JSONObject jsonObject) {

        setDayOfWeek(MiscUtils.getString(jsonObject, "DayOfWeek", ""));
        setEndTime(MiscUtils.getString(jsonObject, "EndTime", ""));
        setStartTime(MiscUtils.getString(jsonObject, "StartTime", ""));
        setSeatQuota(MiscUtils.getInt(jsonObject, "SeatQuota", 0));
        setPoints(MiscUtils.getInt(jsonObject, "Points", 0));
        setRecordStatus(MiscUtils.getString(jsonObject, "RecordStatus", ""));

    }

    @Override
    public void copyFrom(Model model) {
        if (!(model instanceof TimeSlot)) return;
        setDayOfWeek(((TimeSlot) model).getDayOfWeek());
        setEndTime(((TimeSlot) model).getEndTime());
        setStartTime(((TimeSlot) model).getStartTime());
        setSeatQuota(((TimeSlot) model).getSeatQuota());
        setPoints(((TimeSlot) model).getPoints());
        setRecordStatus(((TimeSlot) model).getRecordStatus());
    }

    public String getDayOfWeek() {
        return mDayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        mDayOfWeek = dayOfWeek;
    }

    public Date getEndTime() {
        return mEndTime;
    }

    public void setEndTime(Date endTime) {
        mEndTime = endTime;
    }

    public String getEndTime(String format) {
        if (mEndTime == null) return "";
        return new SimpleDateFormat(format, Locale.US).format(mEndTime);
    }

    public void setEndTime(String endTime) {
        endTime = "1970-01-01 " + endTime;
        try {
            mEndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Date getStartTime() {
        return mStartTime;
    }

    public void setStartTime(Date startTime) {
        mStartTime = startTime;
    }

    public String getStartTime(String format) {
        if (mStartTime == null) return "";
        return new SimpleDateFormat(format, Locale.US).format(mStartTime);
    }

    public void setStartTime(String startTime) {
        startTime = "1970-01-01 " + startTime;
        try {
            mStartTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public int getSeatQuota() {
        return mSeatQuota;
    }

    public void setSeatQuota(int seatQuota) {
        mSeatQuota = seatQuota;
    }

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int points) {
        mPoints = points;
    }

    public String getRecordStatus() {
        return mRecordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        mRecordStatus = recordStatus;
    }

}
