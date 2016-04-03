package com.tawlat.models;

import com.tawlat.utils.MiscUtils;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Country extends Model {

    private String mName;
    private String mShortName;
    private String mCallingCode;
    private boolean mIsForTawlatApplication;
    private Date mAddedDate;
    private Date mModifiedDate;
    private String mRecordStatus;

    public Country(JSONObject jsonObject) {
        setId(MiscUtils.getString(jsonObject, "CountryId", ""));
        setName(MiscUtils.getString(jsonObject, "Name", ""));
        setShortName(MiscUtils.getString(jsonObject, "ShortName", ""));
        setCallingCode(MiscUtils.getString(jsonObject, "CallingCode", ""));
        setIsForTawlatApplication(MiscUtils.getBoolean(jsonObject, "IsForTawlatApplication", false));
        setAddedDate(MiscUtils.getString(jsonObject, "AddedDate", "1970-01-01-00:00:00"));
        setModifiedDate(MiscUtils.getString(jsonObject, "ModifiedDate", "1970-01-01-00:00:00"));
        setRecordStatus(MiscUtils.getString(jsonObject, "RecordStatus", ""));
    }

    @Override
    public void copyFrom(Model model) {
        if (!(model instanceof Country)) return;
        setId(model.getId());
        setName(((Country) model).getName());
        setShortName(((Country) model).getShortName());
        setCallingCode(((Country) model).getCallingCode());
        setIsForTawlatApplication(((Country) model).isForTawlatApplication());
        setAddedDate(((Country) model).getAddedDate());
        setModifiedDate(((Country) model).getModifiedDate());
        setRecordStatus(((Country) model).getRecordStatus());
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getShortName() {
        return mShortName;
    }

    public void setShortName(String shortName) {
        mShortName = shortName;
    }

    public String getCallingCode() {
        return mCallingCode;
    }

    public void setCallingCode(String callingCode) {
        mCallingCode = callingCode;
    }

    public boolean isForTawlatApplication() {
        return mIsForTawlatApplication;
    }

    public void setIsForTawlatApplication(boolean isForTawlatApplication) {
        mIsForTawlatApplication = isForTawlatApplication;
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
