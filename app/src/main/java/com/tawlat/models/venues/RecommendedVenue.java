package com.tawlat.models.venues;

import android.util.Pair;

import com.tawlat.core.Communicator;
import com.tawlat.models.Model;
import com.tawlat.utils.MiscUtils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mohammed on 3/21/16.
 */
public class RecommendedVenue extends Model {

    private int mMaxPoints;
    private String mName;
    private String mVenueName;
    private String mImage;
    private String mLocation;
    private String mLocationName;
    private ArrayList<String> mVenueCuisines = new ArrayList<>();
    private ArrayList<Pair<String, String>> mVenueMenus = new ArrayList<>();
    private String mVenueLogo;
    private ArrayList<String> mOpeningHours = new ArrayList<>();
    private int mPublishedReviewsCount;
    private float mGetAvgReviews;

    public RecommendedVenue(JSONObject jsonObject) {
        setId(MiscUtils.getString(jsonObject, "VenueId", ""));
        setName(MiscUtils.getString(jsonObject, "Name", ""));
        setVenueName(MiscUtils.getString(jsonObject, "VenueName", ""));
        setImage(MiscUtils.getString(jsonObject, "Image", ""));
        setLocation(MiscUtils.getString(jsonObject, "Location", ""));
        setLocationName(MiscUtils.getString(jsonObject, "LocationName", ""));
        setVenueCuisinesFromString(MiscUtils.getString(jsonObject, "VenueCuisines", ""));
        setVenueMenuesFromString(MiscUtils.getString(jsonObject, "VenueMenues", ""));
        setVenueLogo(MiscUtils.getString(jsonObject, "VenueLogo", ""));
        setOpeningHoursFromString(MiscUtils.getString(jsonObject, "VenueOpeningHours", ""));
        setPublishedReviewsCount(MiscUtils.getInt(jsonObject, "PublishedReviewsCount", 0));
        setGetAvgReviews(MiscUtils.getFloat(jsonObject, "GetAvgReviews", 0.0f));
    }

    @Override
    public void copyFrom(Model model) {
        if (!(model instanceof RecommendedVenue)) return;
        setId(model.getId());
        setName(((RecommendedVenue) model).getName());
        setVenueName(((RecommendedVenue) model).getVenueName());
        setImage(((RecommendedVenue) model).getImage());
        setLocation(((RecommendedVenue) model).getLocation());
        setLocationName(((RecommendedVenue) model).getLocationName());
        setVenueCuisinesFromString(((RecommendedVenue) model).getVenueCuisinesAsString());
        setVenueMenuesFromString(((RecommendedVenue) model).getVenueMenusAsString());
        setVenueLogo(((RecommendedVenue) model).getVenueLogo());
        setOpeningHoursFromString(((RecommendedVenue) model).getOpeningHoursAsString());
        setPublishedReviewsCount(((RecommendedVenue) model).getPublishedReviewsCount());
        setGetAvgReviews(((RecommendedVenue) model).getGetAvgReviews());
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

    public String getVenueName() {
        return mVenueName;
    }

    public void setVenueName(String venueName) {
        mVenueName = venueName;
    }

    public String getImage() {
        return Communicator.BASE_URL + "Uploads/Advertisements/" + mImage;
    }

    public void setImage(String image) {
        mImage = image;
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

    public ArrayList<Pair<String, String>> getVenueMenus() {
        return mVenueMenus;
    }

    public String getVenueMenusAsString() {
        String result = "";
        for (Pair pair :
                mVenueMenus) {
            result += pair.first + ", ";
        }
        result = result.substring(0, result.length() - 2);
        return result;
    }

    public void setVenueMenuesFromString(String menus) {
        String bits[] = menus.split(",");
        mVenueMenus.clear();
        for (String bit :
                bits) {
            if (bit.trim().isEmpty()) continue;
            String[] kv = bit.split(" ");
            if (kv.length > 2) {
                for (int i = 1; i < kv.length - 1; i++)
                    kv[0] += kv[i];
                kv[1] = kv[kv.length - 1];
                String[] b = kv[1].split("%");
                if (b.length == 2)
                    kv[1] = b[1];
            }
            mVenueMenus.add(new Pair<>(kv[0], Communicator.BASE_URL + "Uploads/menu/" + kv[1]));
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
