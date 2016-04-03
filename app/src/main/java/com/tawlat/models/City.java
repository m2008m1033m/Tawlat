package com.tawlat.models;

import com.tawlat.utils.MiscUtils;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class City extends Model {
    private String mName;
    private String mCountryId;
    private Date mAddedDate;
    private Date mModifiedDate;
    private String mRecordStatus;

    public City(JSONObject jsonObject) {
        setId(MiscUtils.getString(jsonObject, "CityId", ""));
        setName(MiscUtils.getString(jsonObject, "Name", ""));
        setCountryId(MiscUtils.getString(jsonObject, "CountryId", ""));
        setAddedDate(MiscUtils.getString(jsonObject, "AddedDate", "1970-01-01-00:00:00"));
        setModifiedDate(MiscUtils.getString(jsonObject, "ModifiedDate", "1970-01-01-00:00:00"));
        setRecordStatus(MiscUtils.getString(jsonObject, "RecordStatus", ""));
    }

    @Override
    public void copyFrom(Model model) {
        if (!(model instanceof City)) return;
        setId(model.getId());
        setName(((City) model).getName());
        setAddedDate(((City) model).getAddedDate());
        setModifiedDate(((City) model).getModifiedDate());
        setRecordStatus(((City) model).getRecordStatus());
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getCountryId() {
        return mCountryId;
    }

    public void setCountryId(String countryId) {
        mCountryId = countryId;
    }

    public Date getAddedDate() {
        return mAddedDate;
    }

    public void setAddedDate(Date addedDate) {
        mAddedDate = addedDate;
    }

    public String getAddedDate(String format) {
        if (mAddedDate == null) return null;
        return new SimpleDateFormat(format, Locale.US).format(mAddedDate);
    }

    public void setAddedDate(String addedDate) {
        addedDate = addedDate.replace('T', ' ');
        try {
            mAddedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(addedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Date getModifiedDate() {
        return mModifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        mModifiedDate = modifiedDate;
    }

    public String getModifiedDate(String format) {
        if (mModifiedDate == null) return null;
        return new SimpleDateFormat(format, Locale.US).format(mModifiedDate);
    }

    public void setModifiedDate(String modifiedDate) {
        modifiedDate = modifiedDate.replace('T', ' ');
        try {
            mModifiedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(modifiedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getRecordStatus() {
        return mRecordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        mRecordStatus = recordStatus;
    }
}
