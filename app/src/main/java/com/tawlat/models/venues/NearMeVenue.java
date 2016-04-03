package com.tawlat.models.venues;

import com.tawlat.core.Communicator;
import com.tawlat.models.Model;
import com.tawlat.utils.MiscUtils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mohammed on 3/21/16.
 */
public class NearMeVenue extends Model {
    private String mName;
    private String mCoverImage;
    private String mLocation;
    private ArrayList<String> mVenueCuisines = new ArrayList<>();
    private ArrayList<String> mVenueGoodFor = new ArrayList<>();
    private String mVenueLogo;
    private ArrayList<String> mOpeningHours = new ArrayList<>();
    private int mPublishedReviewsCount;
    private float mGetAvgReviews;
    private int mPoints;
    private double mLatitude;
    private double mLongitude;
    private int mTotal;
    private int mRownum;
    private String mAddress;
    private String mBackgroundImage;
    private String mCity;
    private double mDistance;
    private boolean mIsDirectory;


    public NearMeVenue(JSONObject jsonObject) {
        setId(MiscUtils.getString(jsonObject, "VenueId", ""));
        setName(MiscUtils.getString(jsonObject, "Name", ""));
        setCoverImage(MiscUtils.getString(jsonObject, "CoverImage", ""));
        setLocation(MiscUtils.getString(jsonObject, "Location", ""));
        setVenueCuisinesFromString(MiscUtils.getString(jsonObject, "VenueCuisines", ""));
        setVenueLogo(MiscUtils.getString(jsonObject, "VenueLogo", ""));
        setOpeningHoursFromString(MiscUtils.getString(jsonObject, "OpeningHours", ""));
        setPublishedReviewsCount(MiscUtils.getInt(jsonObject, "PublishedReviewsCount", 0));
        setGetAvgReviews(MiscUtils.getFloat(jsonObject, "GetAvgReviews", 0.0f));
        setVenueGoodForFromString(MiscUtils.getString(jsonObject, "VenueGoodFor", ""));
        setPoints(MiscUtils.getInt(jsonObject, "Points", 0));
        setLatitude(MiscUtils.getDouble(jsonObject, "Latitude", 0.0));
        setLongitude(MiscUtils.getDouble(jsonObject, "Longitude", 0.0));
        setTotal(MiscUtils.getInt(jsonObject, "total", 0));
        setRownum(MiscUtils.getInt(jsonObject, "rownum", 0));
        setAddress(MiscUtils.getString(jsonObject, "Address", ""));
        setBackgroundImage(MiscUtils.getString(jsonObject, "BackgroundImage", ""));
        setCity(MiscUtils.getString(jsonObject, "City", ""));
        setDistance(MiscUtils.getDouble(jsonObject, "Distance", 0));
        setDistance(MiscUtils.getDouble(jsonObject, "Distance", 0));
        setIsDirectory(MiscUtils.getBoolean(jsonObject, "IsDirectory", false));
    }

    @Override
    public void copyFrom(Model model) {
        if (!(model instanceof NearMeVenue)) return;
        setId(model.getId());
        setName(((NearMeVenue) model).getName());
        setCoverImage(((NearMeVenue) model).getCoverImage());
        setLocation(((NearMeVenue) model).getLocation());
        setVenueCuisinesFromString(((NearMeVenue) model).getVenueCuisinesAsString());
        setVenueLogo(((NearMeVenue) model).getVenueLogo());
        setOpeningHoursFromString(((NearMeVenue) model).getOpeningHoursAsString());
        setPublishedReviewsCount(((NearMeVenue) model).getPublishedReviewsCount());
        setGetAvgReviews(((NearMeVenue) model).getGetAvgReviews());
        setVenueGoodForFromString(((NearMeVenue) model).getVenueGoodForAsString());
        setPoints(((NearMeVenue) model).getPoints());
        setLatitude(((NearMeVenue) model).getLatitude());
        setLongitude(((NearMeVenue) model).getLongitude());
        setTotal(((NearMeVenue) model).getTotal());
        setRownum(((NearMeVenue) model).getRownum());
        setAddress(((NearMeVenue) model).getAddress());
        setBackgroundImage(((NearMeVenue) model).getBackgroundImage());
        setCity(((NearMeVenue) model).getCity());
        setDistance(((NearMeVenue) model).getDistance());
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

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int points) {
        mPoints = points;
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

    public int getTotal() {
        return mTotal;
    }

    public void setTotal(int total) {
        mTotal = total;
    }

    public int getRownum() {
        return mRownum;
    }

    public void setRownum(int rownum) {
        mRownum = rownum;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getBackgroundImage() {
        return mBackgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        mBackgroundImage = backgroundImage;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public double getDistance() {
        return mDistance;
    }

    public void setDistance(double distance) {
        mDistance = distance;
    }

    public String getDistanceFormatted() {
        double d = mDistance;
        if (d >= 1) {
            return ((int) Math.round(d * 10)) / 10.0f + " KM";
        }

        d *= 1000;
        return ((int) Math.round(d * 10)) / 10.0f + "m";
    }

    public boolean isDirectory() {
        return mIsDirectory;
    }

    public void setIsDirectory(boolean isDirectory) {
        mIsDirectory = isDirectory;
    }
}
