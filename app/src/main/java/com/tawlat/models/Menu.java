package com.tawlat.models;

import com.tawlat.utils.MiscUtils;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Menu extends Model {

    private String mVenueId;
    private String mTitle;
    private String mAttachment;
    private Date mAddedDate;
    private Date mModifiedDate;
    private String mRecordStatus;


    public Menu(JSONObject jsonObject) {
        setId(MiscUtils.getString(jsonObject, "PdfMenueId", ""));
        setVenueId(MiscUtils.getString(jsonObject, "VenueId", ""));
        setTitle(MiscUtils.getString(jsonObject, "Title", ""));
        setAttachment(MiscUtils.getString(jsonObject, "Attachment", ""));
        setAddedDate(MiscUtils.getString(jsonObject, "AddedDate", ""));
        setModifiedDate(MiscUtils.getString(jsonObject, "ModifiedDate", ""));
        setRecordStatus(MiscUtils.getString(jsonObject, "RecordStatus", ""));
    }

    @Override
    public void copyFrom(Model model) {
        if (!(model instanceof Menu)) return;
        setId(model.getId());
        setVenueId(((Menu) model).getVenueId());
        setTitle(((Menu) model).getTitle());
        setAttachment(((Menu) model).getAttachment());
        setAddedDate(((Menu) model).getAddedDate());
        setModifiedDate(((Menu) model).getModifiedDate());
        setRecordStatus(((Menu) model).getRecordStatus());
    }

    public String getVenueId() {
        return mVenueId;
    }

    public void setVenueId(String venueId) {
        mVenueId = venueId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getAttachment() {
        return mAttachment;
    }

    public void setAttachment(String attachment) {
        mAttachment = attachment;
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
        if (mModifiedDate == null) return "";
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
