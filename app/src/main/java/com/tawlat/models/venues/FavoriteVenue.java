package com.tawlat.models.venues;

import com.tawlat.core.Communicator;
import com.tawlat.models.Model;
import com.tawlat.utils.MiscUtils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mohammed on 3/21/16.
 */
public class FavoriteVenue extends Model {
    private String mVenueName;
    private String mCoverImage;
    private String mVenueLocation;
    private ArrayList<String> mVenueCuisines = new ArrayList<>();
    private String mVenueLogo;
    private ArrayList<String> mOpeningHours = new ArrayList<>();
    private int mPublishedReviewsCount;
    private float mGetAvgReviews;
    private String mFavoriteId;
    private String mUserId;
    private ArrayList<String> mVenueGoodFor = new ArrayList<>();
    private String mPriceRange;


    public FavoriteVenue(JSONObject jsonObject) {
        setId(MiscUtils.getString(jsonObject, "VenueId", ""));
        setVenueName(MiscUtils.getString(jsonObject, "VenueName", ""));
        setCoverImage(MiscUtils.getString(jsonObject, "CoverImage", ""));
        setVenueLocation(MiscUtils.getString(jsonObject, "VenueLocation", ""));
        setVenueCuisinesFromString(MiscUtils.getString(jsonObject, "VenueCuisines", ""));
        setVenueLogo(MiscUtils.getString(jsonObject, "VenueLogo", ""));
        setOpeningHoursFromString(MiscUtils.getString(jsonObject, "OpeningHours", ""));
        setPublishedReviewsCount(MiscUtils.getInt(jsonObject, "PublishedReviewsCount", 0));
        setGetAvgReviews(MiscUtils.getFloat(jsonObject, "GetAvgReviews", 0.0f));
        setFavoriteId(MiscUtils.getString(jsonObject, "FavoriteId", ""));
        setUserId(MiscUtils.getString(jsonObject, "UserId", ""));
        setVenueGoodForFromString(MiscUtils.getString(jsonObject, "VenueGoodFor", ""));
        setPriceRange(MiscUtils.getString(jsonObject, "PriceRange", ""));
    }

    @Override
    public void copyFrom(Model model) {
        if (!(model instanceof FavoriteVenue)) return;
        setId(model.getId());
        setVenueName(((FavoriteVenue) model).getVenueName());
        setCoverImage(((FavoriteVenue) model).getCoverImage());
        setVenueLocation(((FavoriteVenue) model).getVenueLocation());
        setVenueCuisinesFromString(((FavoriteVenue) model).getVenueCuisinesAsString());
        setVenueLogo(((FavoriteVenue) model).getVenueLogo());
        setOpeningHoursFromString(((FavoriteVenue) model).getOpeningHoursAsString());
        setPublishedReviewsCount(((FavoriteVenue) model).getPublishedReviewsCount());
        setGetAvgReviews(((FavoriteVenue) model).getGetAvgReviews());
        setFavoriteId(((FavoriteVenue) model).getFavoriteId());
        setUserId(((FavoriteVenue) model).getUserId());
        setVenueGoodForFromString(((FavoriteVenue) model).getVenueGoodForAsString());
        setPriceRange(((FavoriteVenue) model).getPriceRange());
    }

    public String getVenueName() {
        return mVenueName;
    }

    public void setVenueName(String venueName) {
        mVenueName = venueName;
    }

    public String getCoverImage() {
        return Communicator.BASE_URL + "Uploads/Gallery/" + mCoverImage;
    }

    public void setCoverImage(String coverImage) {
        mCoverImage = coverImage;
    }

    public String getVenueLocation() {
        return mVenueLocation;
    }

    public void setVenueLocation(String venueLocation) {
        mVenueLocation = venueLocation;
    }

    public ArrayList<String> getVenueCuisines() {
        return mVenueCuisines;
    }

    public String getVenueCuisinesAsString() {
        String result = "";
        for (String string :
                mVenueCuisines) {
            result += string + ", ";
        }

        return result.substring(0, result.length() - 2);
    }

    public void setVenueCuisinesFromString(String venueCuisines) {
        String[] bits = venueCuisines.split(",");
        mVenueCuisines.clear();
        for (String bit :
                bits) {
            if (bit.trim().isEmpty()) continue;
            mVenueCuisines.add(bit.trim());
        }
    }

    public String getVenueLogo() {
        return Communicator.BASE_URL + "Uploads/LogoImages/" + mVenueLogo;
    }

    public void setVenueLogo(String venueLogo) {
        mVenueLogo = venueLogo;
    }

    public ArrayList<String> getOpeningHours() {
        return mOpeningHours;
    }

    public String getOpeningHoursAsString() {
        String result = "";
        for (String string :
                mOpeningHours) {
            result += string + ", ";
        }
        result = result.substring(0, result.length() - 2);
        return result;
    }

    public void setOpeningHoursFromString(String openingHours) {
        String bits[] = openingHours.split(",");
        mOpeningHours.clear();
        for (String bit :
                bits) {
            if (bit.trim().isEmpty()) continue;
            mOpeningHours.add(bit.trim());
        }
    }

    public ArrayList<String> getVenueGoodFor() {
        return mVenueGoodFor;
    }

    public String getVenueGoodForAsString() {
        String result = "";
        for (String string :
                mVenueGoodFor) {
            result += string + ", ";
        }
        result = result.substring(0, result.length() - 2);
        return result;
    }

    public void setVenueGoodForFromString(String openingHours) {
        String bits[] = openingHours.split(",");
        mVenueGoodFor.clear();
        for (String bit :
                bits) {
            if (bit.trim().isEmpty()) continue;
            mVenueGoodFor.add(bit.trim());
        }
    }

    public int getPublishedReviewsCount() {
        return mPublishedReviewsCount;
    }

    public void setPublishedReviewsCount(int publishedReviewsCount) {
        mPublishedReviewsCount = publishedReviewsCount;
    }

    public float getGetAvgReviews() {
        return mGetAvgReviews;
    }

    public void setGetAvgReviews(float getAvgReviews) {
        mGetAvgReviews = getAvgReviews;
    }

    public String getFavoriteId() {
        return mFavoriteId;
    }

    public void setFavoriteId(String favoriteId) {
        mFavoriteId = favoriteId;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getPriceRange() {
        return mPriceRange;
    }

    public void setPriceRange(String priceRange) {
        mPriceRange = priceRange;
    }
}
