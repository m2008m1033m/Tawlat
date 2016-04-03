package com.tawlat.models;

import com.tawlat.utils.MiscUtils;

import org.json.JSONObject;

/**
 * Created by mohammed on 3/21/16.
 */
public class Rate extends Model {

    private int mTotalPublishedReviews;
    private int mServiceFeedBack;
    private int mQualityFeedBack;
    private int mCleanlinessFeedBack;
    private int mAmbienceFeedBack;
    private int mUsersCountWhoMadeReviews;

    public Rate(JSONObject jsonObject) {
        setTotalPublishedReviews(MiscUtils.getInt(MiscUtils.getJSONObject(MiscUtils.getJSONArray(jsonObject, "Table"), 0), "TotalPublishedReviews", 0));
        setServiceFeedBack(MiscUtils.getInt(MiscUtils.getJSONObject(MiscUtils.getJSONArray(jsonObject, "Table1"), 0), "ServiceFeedBack", 0));
        setQualityFeedBack(MiscUtils.getInt(MiscUtils.getJSONObject(MiscUtils.getJSONArray(jsonObject, "Table2"), 0), "QualityFeedBack", 0));
        setCleanlinessFeedBack(MiscUtils.getInt(MiscUtils.getJSONObject(MiscUtils.getJSONArray(jsonObject, "Table3"), 0), "CleanlinessFeedBack", 0));
        setAmbienceFeedBack(MiscUtils.getInt(MiscUtils.getJSONObject(MiscUtils.getJSONArray(jsonObject, "Table4"), 0), "AmbienceFeedBack", 0));
        setUsersCountWhoMadeReviews(MiscUtils.getInt(MiscUtils.getJSONObject(MiscUtils.getJSONArray(jsonObject, "Table5"), 0), "UsersCountWhoMadeReviews", 0));
    }

    @Override
    public void copyFrom(Model model) {
        if (!(model instanceof Rate)) return;
        setTotalPublishedReviews(((Rate) model).getTotalPublishedReviews());
        setServiceFeedBack(((Rate) model).getServiceFeedBack());
        setQualityFeedBack(((Rate) model).getQualityFeedBack());
        setCleanlinessFeedBack(((Rate) model).getCleanlinessFeedBack());
        setAmbienceFeedBack(((Rate) model).getAmbienceFeedBack());
        setUsersCountWhoMadeReviews(((Rate) model).getUsersCountWhoMadeReviews());
    }

    public int getTotalPublishedReviews() {
        return mTotalPublishedReviews;
    }

    public void setTotalPublishedReviews(int totalPublishedReviews) {
        mTotalPublishedReviews = totalPublishedReviews;
    }

    public int getServiceFeedBack() {
        return mServiceFeedBack;
    }

    public void setServiceFeedBack(int serviceFeedBack) {
        mServiceFeedBack = serviceFeedBack;
    }

    public int getQualityFeedBack() {
        return mQualityFeedBack;
    }

    public void setQualityFeedBack(int qualityFeedBack) {
        mQualityFeedBack = qualityFeedBack;
    }

    public int getCleanlinessFeedBack() {
        return mCleanlinessFeedBack;
    }

    public void setCleanlinessFeedBack(int cleanlinessFeedBack) {
        mCleanlinessFeedBack = cleanlinessFeedBack;
    }

    public int getAmbienceFeedBack() {
        return mAmbienceFeedBack;
    }

    public void setAmbienceFeedBack(int ambienceFeedBack) {
        mAmbienceFeedBack = ambienceFeedBack;
    }

    public int getUsersCountWhoMadeReviews() {
        return mUsersCountWhoMadeReviews;
    }

    public void setUsersCountWhoMadeReviews(int usersCountWhoMadeReviews) {
        mUsersCountWhoMadeReviews = usersCountWhoMadeReviews;
    }
}
