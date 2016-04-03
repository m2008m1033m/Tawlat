package com.tawlat.models.search;

import com.tawlat.core.Communicator;
import com.tawlat.models.Model;
import com.tawlat.utils.MiscUtils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mohammed on 3/21/16.
 */
public class AdvancedSearchResult extends Model {

    private String mTableName;
    private double mLongitude;
    private double mLatitude;
    private String mCuisineId;
    private String mVenueId;
    private String mName;
    private String mDescription;
    private String mCoverImage;
    private String mLocation;
    private String mVenueLocation;
    private ArrayList<String> mVenueCuisineNames = new ArrayList<>();
    private ArrayList<String> mVenueGoodForNames = new ArrayList<>();
    private ArrayList<String> mOpeningHours = new ArrayList<>();
    private int mPoints;
    private String mVenueLogo;
    private int mPublishedReviewsCount;
    private float mGetAvgReviews;
    private String mPriceRange;
    private boolean mIsDirectory;


    public AdvancedSearchResult(JSONObject jsonObject) {
        setTableName(MiscUtils.getString(jsonObject, "TableName", ""));
        setLongitude(MiscUtils.getDouble(jsonObject, "Longitude", 0.0));
        setLatitude(MiscUtils.getDouble(jsonObject, "Latitude", 0.0));
        setCuisineId(MiscUtils.getString(jsonObject, "CuisineId", ""));
        setVenueId(MiscUtils.getString(jsonObject, "VenueId", ""));
        setName(MiscUtils.getString(jsonObject, "Name", ""));
        setDescription(MiscUtils.getString(jsonObject, "Description", ""));
        setCoverImage(MiscUtils.getString(jsonObject, "CoverImage", ""));
        setLocation(MiscUtils.getString(jsonObject, "Location", ""));
        setVenueLocation(MiscUtils.getString(jsonObject, "VenueLocation", ""));
        setVenueCuisinesNamesFromString(MiscUtils.getString(jsonObject, "VenueCuisineNames", ""));
        setVenueGoodForNamesFromString(MiscUtils.getString(jsonObject, "VenueGoodForNames", ""));
        setOpeningHoursFromString(MiscUtils.getString(jsonObject, "OpeningHours", ""));
        setPoints(MiscUtils.getInt(jsonObject, "Points", 0));
        setVenueLogo(MiscUtils.getString(jsonObject, "VenueLogo", ""));
        setPublishedReviewsCount(MiscUtils.getInt(jsonObject, "PublishedReviewsCount", 0));
        setGetAvgReviews(MiscUtils.getFloat(jsonObject, "GetAvgReviews", 0.0f));
        setPriceRange(MiscUtils.getString(jsonObject, "PriceRange", ""));
        setIsDirectory(MiscUtils.getBoolean(jsonObject, "IsDirectory", false));
    }

    @Override
    public void copyFrom(Model model) {
        try {
            throw new Exception("Method is not implemented");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTableName() {
        return mTableName;
    }

    public void setTableName(String tableName) {
        mTableName = tableName;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public String getCuisineId() {
        return mCuisineId;
    }

    public void setCuisineId(String cuisineId) {
        mCuisineId = cuisineId;
    }

    public String getVenueId() {
        return mVenueId;
    }

    public void setVenueId(String venueId) {
        mVenueId = venueId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
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

    public String getVenueLocation() {
        return mVenueLocation;
    }

    public void setVenueLocation(String venueLocation) {
        mVenueLocation = venueLocation;
    }

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int points) {
        mPoints = points;
    }

    public String getVenueLogo() {
        return Communicator.BASE_URL + "Uploads/LogoImages/" + mVenueLogo;
    }

    public void setVenueLogo(String venueLogo) {
        mVenueLogo = venueLogo;
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

    public boolean isDirectory() {
        return mIsDirectory;
    }

    public void setIsDirectory(boolean isDirectory) {
        mIsDirectory = isDirectory;
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
