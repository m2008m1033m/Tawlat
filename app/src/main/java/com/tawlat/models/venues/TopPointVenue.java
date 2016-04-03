package com.tawlat.models.venues;

import com.tawlat.core.Communicator;
import com.tawlat.models.Model;
import com.tawlat.utils.MiscUtils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mohammed on 3/21/16.
 */
public class TopPointVenue extends Model {

    private int mMaxPoints;
    private String mName;
    private String mCoverImage;
    private String mLocation;
    private String mLocationName;
    private ArrayList<String> mVenueCuisines = new ArrayList<>();
    private String mVenueLogo;
    private ArrayList<String> mOpeningHours = new ArrayList<>();
    private int mPublishedReviewsCount;
    private float mGetAvgReviews;

    public TopPointVenue(JSONObject jsonObject) {
        setId(MiscUtils.getString(jsonObject, "VenueId", ""));
        setName(MiscUtils.getString(jsonObject, "Name", ""));
        setCoverImage(MiscUtils.getString(jsonObject, "CoverImage", ""));
        setLocation(MiscUtils.getString(jsonObject, "Location", ""));
        setLocationName(MiscUtils.getString(jsonObject, "LocationName", ""));
        setVenueCuisinesFromString(MiscUtils.getString(jsonObject, "VenueCuisines", ""));
        setVenueLogo(MiscUtils.getString(jsonObject, "VenueLogo", ""));
        setOpeningHoursFromString(MiscUtils.getString(jsonObject, "OpeningHours", ""));
        setPublishedReviewsCount(MiscUtils.getInt(jsonObject, "PublishedReviewsCount", 0));
        setMaxPoints(MiscUtils.getInt(jsonObject, "MaxPoints", 0));
        setGetAvgReviews(MiscUtils.getFloat(jsonObject, "GetAvgReviews", 0.0f));
    }

    @Override
    public void copyFrom(Model model) {
        if (!(model instanceof TopPointVenue)) return;
        setId(model.getId());
        setName(((TopPointVenue) model).getName());
        setCoverImage(((TopPointVenue) model).getCoverImage());
        setLocation(((TopPointVenue) model).getLocation());
        setLocationName(((TopPointVenue) model).getLocationName());
        setVenueCuisinesFromString(((TopPointVenue) model).getVenueCuisinesAsString());
        setVenueLogo(((TopPointVenue) model).getVenueLogo());
        setOpeningHoursFromString(((TopPointVenue) model).getOpeningHoursAsString());
        setPublishedReviewsCount(((TopPointVenue) model).getPublishedReviewsCount());
        setMaxPoints(((TopPointVenue) model).getMaxPoints());
        setGetAvgReviews(((TopPointVenue) model).getGetAvgReviews());
    }

    public int getMaxPoints() {
        return mMaxPoints;
    }

    public void setMaxPoints(int maxPoints) {
        mMaxPoints = maxPoints;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getCoverImage() {
        return Communicator.BASE_URL + "Uploads/Gallery/" + mCoverImage;
    }

    public void setCoverImage(String coverImage) {
        mCoverImage = coverImage;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getLocationName() {
        return mLocationName;
    }

    public void setLocationName(String locationName) {
        mLocationName = locationName;
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
}
