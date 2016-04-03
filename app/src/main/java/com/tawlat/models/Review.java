package com.tawlat.models;

import com.tawlat.core.Communicator;
import com.tawlat.utils.MiscUtils;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Review extends Model {

    private String mUserId;
    private String mVenueId;
    private String mManagerId;
    private int mServiceFeedBack;
    private int QualityFeedBack;
    private int AmbienceFeedBack;
    private int CleanlinessFeedBack;
    private String mReviewNotes;
    private boolean mIsApprovedForPublish;
    private boolean mIsAvailableForManager;
    private String mManagerComents;
    private boolean mIsManagerCommentApproved;
    private Date mCreatedDate;
    private Date mModifiedDate;
    private String mRecordStatus;
    private String mManagerName;
    private String mManagerImage;
    private String mUserName;
    private String mUserImage;
    private Date mManageCommentDate;
    private String mUserTimeAgo;
    private String mManagerTimeAgo;


    public Review(JSONObject jsonObject) {
        setId(MiscUtils.getString(jsonObject, "UserReviewId", ""));
        setUserId(MiscUtils.getString(jsonObject, "UserId", ""));
        setVenueId(MiscUtils.getString(jsonObject, "VenueId", ""));
        setManagerId(MiscUtils.getString(jsonObject, "ManagerId", ""));
        setServiceFeedBack(MiscUtils.getInt(jsonObject, "ServiceFeedBack", 0));
        setQualityFeedBack(MiscUtils.getInt(jsonObject, "QualityFeedBack", 0));
        setAmbienceFeedBack(MiscUtils.getInt(jsonObject, "AmbienceFeedBack", 0));
        setCleanlinessFeedBack(MiscUtils.getInt(jsonObject, "CleanlinessFeedBack", 0));
        setReviewNotes(MiscUtils.getString(jsonObject, "ReviewNotes", ""));
        setIsApprovedForPublish(MiscUtils.getBoolean(jsonObject, "IsApprovedForPublish", false));
        setIsAvailableForManager(MiscUtils.getBoolean(jsonObject, "IsAvailableForManager", false));
        setManagerComents(MiscUtils.getString(jsonObject, "ManagerComents", ""));
        setIsManagerCommentApproved(MiscUtils.getBoolean(jsonObject, "IsManagerCommentApproved", false));
        setCreatedDate(MiscUtils.getString(jsonObject, "CreatedDate", ""));
        setModifiedDate(MiscUtils.getString(jsonObject, "ModifiedDate", ""));
        setRecordStatus(MiscUtils.getString(jsonObject, "RecordStatus", ""));
        setManagerName(MiscUtils.getString(jsonObject, "ManagerName", ""));
        setManagerImage(MiscUtils.getString(jsonObject, "ManagerImage", ""));
        setUserName(MiscUtils.getString(jsonObject, "UserName", ""));
        setUserImage(MiscUtils.getString(jsonObject, "UserImage", ""));
        setManageCommentDate(MiscUtils.getString(jsonObject, "ManageCommentDate", ""));
        setUserTimeAgo(MiscUtils.getString(jsonObject, "UserTimeAgo", ""));
        setManagerTimeAgo(MiscUtils.getString(jsonObject, "ManagerTimeAgo", ""));
    }


    @Override
    public void copyFrom(Model model) {
        try {
            throw new Exception("Method has been implemented.");
        } catch (Exception e) {
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

    public String getManagerId() {
        return mManagerId;
    }

    public void setManagerId(String managerId) {
        mManagerId = managerId;
    }

    public int getServiceFeedBack() {
        return mServiceFeedBack;
    }

    public void setServiceFeedBack(int serviceFeedBack) {
        mServiceFeedBack = serviceFeedBack;
    }

    public int getQualityFeedBack() {
        return QualityFeedBack;
    }

    public void setQualityFeedBack(int qualityFeedBack) {
        QualityFeedBack = qualityFeedBack;
    }

    public int getAmbienceFeedBack() {
        return AmbienceFeedBack;
    }

    public void setAmbienceFeedBack(int ambienceFeedBack) {
        AmbienceFeedBack = ambienceFeedBack;
    }

    public int getCleanlinessFeedBack() {
        return CleanlinessFeedBack;
    }

    public void setCleanlinessFeedBack(int cleanlinessFeedBack) {
        CleanlinessFeedBack = cleanlinessFeedBack;
    }

    public String getReviewNotes() {
        return mReviewNotes;
    }

    public void setReviewNotes(String reviewNotes) {
        mReviewNotes = reviewNotes;
    }

    public boolean isApprovedForPublish() {
        return mIsApprovedForPublish;
    }

    public void setIsApprovedForPublish(boolean isApprovedForPublish) {
        mIsApprovedForPublish = isApprovedForPublish;
    }

    public boolean isAvailableForManager() {
        return mIsAvailableForManager;
    }

    public void setIsAvailableForManager(boolean isAvailableForManager) {
        mIsAvailableForManager = isAvailableForManager;
    }

    public String getManagerComents() {
        return mManagerComents;
    }

    public void setManagerComents(String managerComents) {
        mManagerComents = managerComents;
    }

    public boolean isManagerCommentApproved() {
        return mIsManagerCommentApproved;
    }

    public void setIsManagerCommentApproved(boolean isManagerCommentApproved) {
        mIsManagerCommentApproved = isManagerCommentApproved;
    }

    public Date getCreatedDate() {
        return mCreatedDate;
    }

    public void setCreatedDate(Date createdDate) {
        mCreatedDate = createdDate;
    }

    public String getCreatedDate(String format) {
        if (mCreatedDate == null) return "";
        return new SimpleDateFormat(format, Locale.US).format(mCreatedDate);
    }

    public void setCreatedDate(String createdDate) {
        createdDate = createdDate.replace('T', ' ');
        try {
            mCreatedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(createdDate);
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

    public String getManagerName() {
        return mManagerName;
    }

    public void setManagerName(String managerName) {
        mManagerName = managerName;
    }

    public String getManagerImage() {
        return mManagerImage;
    }

    public void setManagerImage(String managerImage) {
        mManagerImage = managerImage;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getUserImage() {
        return Communicator.BASE_URL + "Uploads/Profile/" + mUserImage;
    }

    public void setUserImage(String userImage) {
        mUserImage = userImage;
    }

    public Date getManageCommentDate() {
        return mManageCommentDate;
    }

    public void setManageCommentDate(Date manageCommentDate) {
        mManageCommentDate = manageCommentDate;
    }

    public String getManageCommentDate(String format) {
        if (mManageCommentDate == null) return "";
        return new SimpleDateFormat(format, Locale.US).format(mManageCommentDate);
    }

    public void setManageCommentDate(String manageCommentDate) {
        manageCommentDate = manageCommentDate.replace('T', ' ');
        try {
            mManageCommentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(manageCommentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getUserTimeAgo() {
        return mUserTimeAgo;
    }

    public void setUserTimeAgo(String userTimeAgo) {
        mUserTimeAgo = userTimeAgo;
    }

    public String getManagerTimeAgo() {
        return mManagerTimeAgo;
    }

    public void setManagerTimeAgo(String managerTimeAgo) {
        mManagerTimeAgo = managerTimeAgo;
    }
}
