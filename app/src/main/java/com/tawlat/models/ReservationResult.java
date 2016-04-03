package com.tawlat.models;

import com.tawlat.utils.MiscUtils;

import org.json.JSONObject;

public class ReservationResult extends Model {

    private String mData;
    private String mMessage;
    private String mCode;

    public ReservationResult(JSONObject jsonObject) {

        setData(MiscUtils.getString(jsonObject, "Data", ""));
        setMessage(MiscUtils.getString(jsonObject, "Message", ""));
        setCode(MiscUtils.getString(jsonObject, "Code", ""));

    }

    @Override
    public void copyFrom(Model model) {
        try {
            throw new Exception("Method is not implemented.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getData() {
        return mData;
    }

    public void setData(String data) {
        mData = data;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public String getReservationCode() {
        String[] bits = mData.split(",");
        for (int i = bits.length - 1; i >= 0; i++)
            if (bits[i].trim().length() == 8) return bits[i].trim();

        return "";
    }
}
