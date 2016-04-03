package com.tawlat.models.venues;

import com.tawlat.core.Communicator;
import com.tawlat.models.Model;
import com.tawlat.utils.MiscUtils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mohammed on 3/21/16.
 */
public class SearchedVenue extends Model {
    private String mName;
    private String mCoverImage;
    private String mVenueLocation;
    private ArrayList<String> mVenueCuisines = new ArrayList<>();
    private ArrayList<String> mVenueGoodFor = new ArrayList<>();
    private String mVenueLogo;
    private ArrayList<String> mOpeningHours = new ArrayList<>();
    private int mPublishedReviewsCount;
    private float mGetAvgReviews;
    private String mPriceRange;
    private String mDescription;
    private String mLocationId;
    private String mDayOfWeek;
    private int mPoints;
    private String mStartTime;
    private ArrayList<String> mVenueCuisineNames = new ArrayList<>();
    private ArrayList<String> mVenueGoodForNames = new ArrayList<>();
    private double mLatitude;
    private double mLongitude;


    public SearchedVenue(JSONObject jsonObject) {
        setId(MiscUtils.getString(jsonObject, "VenueId", ""));
        setName(MiscUtils.getString(jsonObject, "Name", ""));
        setCoverImage(MiscUtils.getString(jsonObject, "CoverImage", ""));
        setVenueLocation(MiscUtils.getString(jsonObject, "VenueLocation", ""));
        setVenueCuisinesFromString(MiscUtils.getString(jsonObject, "VenueCuisines", ""));
        setVenueLogo(MiscUtils.getString(jsonObject, "VenueLogo", ""));
        setOpeningHoursFromString(MiscUtils.getString(jsonObject, "OpeningHours", ""));
        setPublishedReviewsCount(MiscUtils.getInt(jsonObject, "PublishedReviewsCount", 0));
        setGetAvgReviews(MiscUtils.getFloat(jsonObject, "GetAvgReviews", 0.0f));
        setVenueGoodForFromString(MiscUtils.getString(jsonObject, "VenueGoodFor", ""));
        setPriceRange(MiscUtils.getString(jsonObject, "PriceRange", ""));
        setDescription(MiscUtils.getString(jsonObject, "Description", ""));
        setLocationId(MiscUtils.getString(jsonObject, "LocationId", ""));
        setDayOfWeek(MiscUtils.getString(jsonObject, "DayOfWeek", ""));
        setPoints(MiscUtils.getInt(jsonObject, "Points", 0));
        setStartTime(MiscUtils.getString(jsonObject, "StartTime", ""));
        setVenueCuisinesNamesFromString(MiscUtils.getString(jsonObject, "VenueCuisineNames", ""));
        setVenueGoodForNamesFromString(MiscUtils.getString(jsonObject, "VenueGoodForNames", ""));
        setLatitude(MiscUtils.getDouble(jsonObject, "Latitude", 0.0));
        setLongitude(MiscUtils.getDouble(jsonObject, "Longitude", 0.0));
    }

    @Override
    public void copyFrom(Model model) {
        if (!(model instanceof SearchedVenue)) return;
        setId(model.getId());
        setName(((SearchedVenue) model).getName());
        setCoverImage(((SearchedVenue) model).getCoverImage());
        setVenueLocation(((SearchedVenue) model).getVenueLocation());
        setVenueCuisinesFromString(((SearchedVenue) model).getVenueCuisinesAsString());
        setVenueLogo(((SearchedVenue) model).getVenueLogo());
        setOpeningHoursFromString(((SearchedVenue) model).getOpeningHoursAsString());
        setPublishedReviewsCount(((SearchedVenue) model).getPublishedReviewsCount());
        setGetAvgReviews(((SearchedVenue) model).getGetAvgReviews());
        setVenueGoodForFromString(((SearchedVenue) model).getVenueGoodForAsString());
        setPriceRange(((SearchedVenue) model).getPriceRange());
        setDescription(((SearchedVenue) model).getDescription());
        setLocationId(((SearchedVenue) model).getLocationId());
        setDayOfWeek(((SearchedVenue) model).getDayOfWeek());
        setPoints(((SearchedVenue) model).getPoints());
        setStartTime(((SearchedVenue) model).getStartTime());
        setVenueCuisinesNamesFromString(((SearchedVenue) model).getVenueCuisinesNamesAsString());
        setVenueGoodForNamesFromString(((SearchedVenue) model).getVenueGoodForNamesAsString());
        setLatitude(((SearchedVenue) model).getLatitude());
        setLongitude(((SearchedVenue) model).getLongitude());
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

    public String getPriceRange() {
        return mPriceRange;
    }

    public void setPriceRange(String priceRange) {
        mPriceRange = priceRange;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getLocationId() {
        return mLocationId;
    }

    public void setLocationId(String locationId) {
        mLocationId = locationId;
    }

    public String getDayOfWeek() {
        return mDayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        mDayOfWeek = dayOfWeek;
    }

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int points) {
        mPoints = points;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(String startTime) {
        mStartTime = startTime;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public ArrayList<String> getVenueGoodForNames() {
        return mVenueGoodForNames;
    }

    public String getVenueGoodForNamesAsString() {
        String result = "";
        for (String string :
                mVenueGoodForNames) {
            result += string + ", ";
        }
        result = result.substring(0, result.length() - 2);
        return result;
    }

    public void setVenueGoodForNamesFromString(String venueGoodForNames) {
        String bits[] = venueGoodForNames.split(",");
        mVenueGoodForNames.clear();
        for (String bit :
                bits) {
            if (bit.trim().isEmpty()) continue;
            mVenueGoodForNames.add(bit.trim());
        }
    }

    public ArrayList<String> getVenueCuisineNames() {
        return mVenueCuisineNames;
    }

    public String getVenueCuisinesNamesAsString() {
        String result = "";
        for (String string :
                mVenueCuisineNames) {
            result += string + ", ";
        }
        result = result.substring(0, result.length() - 2);
        return result;
    }

    public void setVenueCuisinesNamesFromString(String venueCusinieNames) {
        String bits[] = venueCusinieNames.split(",");
        mVenueCuisineNames.clear();
        for (String bit :
                bits) {
            if (bit.trim().isEmpty()) continue;
            mVenueCuisineNames.add(bit.trim());
        }
    }
}
