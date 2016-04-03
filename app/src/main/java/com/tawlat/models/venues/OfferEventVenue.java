package com.tawlat.models.venues;

import android.util.Pair;

import com.tawlat.core.Communicator;
import com.tawlat.models.Model;
import com.tawlat.utils.MiscUtils;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OfferEventVenue extends Model {
    public enum Type {
        OFFER,
        EVENT
    }

    private int mMaxPoints;
    private String mName;
    private String mVenueName;
    private String mImage;
    private String mLocation;
    private String mVenueLocation;
    private ArrayList<String> mVenueCuisines = new ArrayList<>();
    private ArrayList<Pair<String, String>> mVenueMenus = new ArrayList<>();
    private String mVenueLogo;
    private ArrayList<String> mOpeningHours = new ArrayList<>();
    private int mPublishedReviewsCount;
    private float mGetAvgReviews;

    private String mVenueOfferId;
    private Type mType;
    private Date mStartDate;
    private Date mEndDate;
    private String mDescription;
    private boolean mIsRepeated;
    private String mRepeatedDay;
    private boolean mActivatedByAdmin;
    private boolean mIsExclusive;


    public OfferEventVenue(JSONObject jsonObject) {
        setId(MiscUtils.getString(jsonObject, "VenueId", ""));
        setName(MiscUtils.getString(jsonObject, "Name", ""));
        setVenueName(MiscUtils.getString(jsonObject, "VenueName", ""));
        setImage(MiscUtils.getString(jsonObject, "Image", ""));
        setLocation(MiscUtils.getString(jsonObject, "Location", ""));
        setVenueLocation(MiscUtils.getString(jsonObject, "VenueLocaion", ""));
        setVenueCuisinesFromString(MiscUtils.getString(jsonObject, "VenueCuisines", ""));
        setVenueMenuesFromString(MiscUtils.getString(jsonObject, "VenueMenues", ""));
        setVenueLogo(MiscUtils.getString(jsonObject, "VenueLogo", ""));
        setOpeningHoursFromString(MiscUtils.getString(jsonObject, "OpeningHours", ""));
        setPublishedReviewsCount(MiscUtils.getInt(jsonObject, "PublishedReviewsCount", 0));
        setGetAvgReviews(MiscUtils.getFloat(jsonObject, "GetAvgReviews", 0.0f));

        setVenueOfferId(MiscUtils.getString(jsonObject, "VenueOfferId", ""));
        setType(MiscUtils.getString(jsonObject, "Type", "").equals("Offer") ? Type.OFFER : Type.EVENT);
        setStartDate(MiscUtils.getString(jsonObject, "StartDate", "1970-01-01T00:00:00"));
        setEndDate(MiscUtils.getString(jsonObject, "EndDate", "1970-01-01T00:00:00"));
        setDescription(MiscUtils.getString(jsonObject, "Description", ""));
        setIsRepeated(MiscUtils.getBoolean(jsonObject, "IsRepeated", false));
        setRepeatedDay(MiscUtils.getString(jsonObject, "RepeatedDay", ""));
        setActivatedByAdmin(MiscUtils.getBoolean(jsonObject, "ActivatedByAdmin", false));
        setIsExclusive(MiscUtils.getBoolean(jsonObject, "IsExclusive", false));
    }

    @Override
    public void copyFrom(Model model) {
        if (!(model instanceof OfferEventVenue)) return;
        setId(model.getId());
        setName(((OfferEventVenue) model).getName());
        setVenueName(((OfferEventVenue) model).getVenueName());
        setImage(((OfferEventVenue) model).getImage());
        setLocation(((OfferEventVenue) model).getLocation());
        setVenueLocation(((OfferEventVenue) model).getVenueLocation());
        setVenueCuisinesFromString(((OfferEventVenue) model).getVenueCuisinesAsString());
        setVenueMenuesFromString(((OfferEventVenue) model).getVenueMenusAsString());
        setVenueLogo(((OfferEventVenue) model).getVenueLogo());
        setOpeningHoursFromString(((OfferEventVenue) model).getOpeningHoursAsString());
        setPublishedReviewsCount(((OfferEventVenue) model).getPublishedReviewsCount());
        setGetAvgReviews(((OfferEventVenue) model).getGetAvgReviews());
        setVenueOfferId(((OfferEventVenue) model).getVenueOfferId());
        setType(((OfferEventVenue) model).getType());
        setStartDate(((OfferEventVenue) model).getStartDate());
        setEndDate(((OfferEventVenue) model).getEndDate());
        setDescription(((OfferEventVenue) model).getDescription());
        setIsRepeated(((OfferEventVenue) model).isRepeated());
        setRepeatedDay(((OfferEventVenue) model).getRepeatedDay());
        setActivatedByAdmin(((OfferEventVenue) model).isActivatedByAdmin());
        setIsExclusive(((OfferEventVenue) model).isExclusive());
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
        return Communicator.BASE_URL + "Uploads/Offers/" + mImage;
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

    public String getVenueOfferId() {
        return mVenueOfferId;
    }

    public void setVenueOfferId(String venueOfferId) {
        mVenueOfferId = venueOfferId;
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Date startDate) {
        mStartDate = startDate;
    }

    public String getStartDate(String format) {
        if (mStartDate == null) return "";
        return new SimpleDateFormat(format, Locale.US).format(mStartDate);
    }

    public void setStartDate(String startDate) {
        try {
            startDate = startDate.replace('T', ' ');
            mStartDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Date getEndDate() {
        return mEndDate;
    }

    public void setEndDate(Date endDate) {
        mEndDate = endDate;
    }

    public String getEndDate(String format) {
        if (mEndDate == null) return "";
        return new SimpleDateFormat(format, Locale.US).format(mEndDate);
    }

    public void setEndDate(String endDate) {
        try {
            endDate = endDate.replace('T', ' ');
            mEndDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public boolean isRepeated() {
        return mIsRepeated;
    }

    public void setIsRepeated(boolean isRepeated) {
        mIsRepeated = isRepeated;
    }

    public String getRepeatedDay() {
        return mRepeatedDay;
    }

    public void setRepeatedDay(String repeatedDay) {
        mRepeatedDay = repeatedDay;
    }

    public boolean isActivatedByAdmin() {
        return mActivatedByAdmin;
    }

    public void setActivatedByAdmin(boolean activatedByAdmin) {
        mActivatedByAdmin = activatedByAdmin;
    }

    public boolean isExclusive() {
        return mIsExclusive;
    }

    public void setIsExclusive(boolean isExclusive) {
        mIsExclusive = isExclusive;
    }
}
