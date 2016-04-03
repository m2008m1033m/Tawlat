package com.tawlat.models.venues;

import android.support.v4.util.Pair;

import com.tawlat.core.Communicator;
import com.tawlat.models.Model;
import com.tawlat.utils.MiscUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Venue extends Model {

    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<Pair<String, String>> mVenueMenues = new ArrayList<>();
    private HashMap<String, String> mOpeningHours = new HashMap<>();
    private HashMap<String, String> mCuisines = new HashMap<>();
    private HashMap<String, String> mGoodFor = new HashMap<>();
    private ArrayList<String> mCreditCards = new ArrayList<>();
    private ArrayList<String> mMetaKeywords = new ArrayList<>();

    private String mVenueType;
    private String mName;
    private String mEmail;
    private String mPhone;
    private String mAddress;
    private String mLocation;
    private String mCity;
    private String mCountry;
    private double mLatitude;
    private double mLongitude;
    private boolean mAcceptCreditCard;
    private boolean mParkingAvailable;
    private String mParkingDetails;
    private String mPriceRange;
    private String mWebsite;
    private String mFacebook;
    private String mTwitter;
    private String mInstagram;
    private String mAdvertisementIFrame;
    private String mCoverImage;
    private String mDescription;
    private String mTerms;
    private Date mAddedDate;
    private Date mModifiedDate;
    private String mRecordStatus;
    private String mVenueStatus;
    private String mGetDirections;
    private String mMetaDescription;
    private String mPageTitle;
    private boolean mAutomaticCheckout;
    private String mBackgroundImage;
    private String mVenueLogo;
    private boolean mIsDirectory;
    private float mBookingFee;
    private int mPublishedReviewsCount;
    private float mGetAvgReviews;

    public Venue() {
    }

    public Venue(JSONObject jsonObject) {

        /**
         * get the images for the gallery
         */
        JSONArray jsonArray = MiscUtils.getJSONArray(jsonObject, "Table");
        if (jsonArray != null) {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject jObj = MiscUtils.getJSONObject(jsonArray, i);
                if (jObj == null) continue;
                mImages.add(Communicator.BASE_URL + "Uploads/Gallery/" + MiscUtils.getString(jObj, "Image", ""));
            }
        }

        /**
         * basic info
         */
        jsonArray = MiscUtils.getJSONArray(jsonObject, "Table1");
        if (jsonArray != null) {
            JSONObject jObj = MiscUtils.getJSONObject(jsonArray, 0);

            if (jObj != null) {
                setId(MiscUtils.getString(jObj, "VenueId", ""));
                setVenueType(MiscUtils.getString(jObj, "VenueType", ""));
                setName(MiscUtils.getString(jObj, "Name", ""));
                setEmail(MiscUtils.getString(jObj, "Email", ""));
                setPhone(MiscUtils.getString(jObj, "Phone", ""));
                setAddress(MiscUtils.getString(jObj, "Address", ""));
                setLocation(MiscUtils.getString(jObj, "Location", ""));
                setCity(MiscUtils.getString(jObj, "City", ""));
                setCountry(MiscUtils.getString(jObj, "Country", ""));
                setLatitude(MiscUtils.getDouble(jObj, "Latitude", 0.0));
                setLongitude(MiscUtils.getDouble(jObj, "Longitude", 0.0));
                setAcceptCreditCard(MiscUtils.getBoolean(jObj, "AcceptCreditCard", false));
                setCreditCards(MiscUtils.getString(jObj, "CreditCards", ""));
                setParkingAvailable(MiscUtils.getBoolean(jObj, "ParkingAvailable", false));
                setParkingDetails(MiscUtils.getString(jObj, "ParkingDetails", ""));
                setPriceRange(MiscUtils.getString(jObj, "PriceRange", ""));
                setWebsite(MiscUtils.getString(jObj, "Website", ""));
                setFacebook(MiscUtils.getString(jObj, "Facebook", ""));
                setTwitter(MiscUtils.getString(jObj, "Twitter", ""));
                setInstagram(MiscUtils.getString(jObj, "Instagram", ""));
                setAdvertisementIFrame(MiscUtils.getString(jObj, "AdvertisementIFrame", ""));
                setCoverImage(MiscUtils.getString(jObj, "CoverImage", ""));
                setDescription(MiscUtils.getString(jObj, "Description", ""));
                setTerms(MiscUtils.getString(jObj, "Terms", ""));
                setAddedDate(MiscUtils.getString(jObj, "AddedDate", ""));
                setModifiedDate(MiscUtils.getString(jObj, "ModifiedDate", ""));
                setRecordStatus(MiscUtils.getString(jObj, "RecordStatus", ""));
                setVenueStatus(MiscUtils.getString(jObj, "VenueStatus", ""));
                setGetDirections(MiscUtils.getString(jObj, "GetDirections", ""));
                setMetaKeywords(MiscUtils.getString(jObj, "MetaKeywords", ""));
                setMetaDescription(MiscUtils.getString(jObj, "MetaDescription", ""));
                setPageTitle(MiscUtils.getString(jObj, "PageTitle", ""));
                setAutomaticCheckout(MiscUtils.getBoolean(jObj, "AutomaticCheckout", false));
                setBackgroundImage(MiscUtils.getString(jObj, "BackgroundImage", ""));
                setVenueLogo(MiscUtils.getString(jObj, "VenueLogo", ""));
                setIsDirectory(MiscUtils.getBoolean(jObj, "IsDirectory", false));
                setBookingFee(MiscUtils.getFloat(jObj, "BookingFee", 0.0f));
                setPublishedReviewsCount(MiscUtils.getInt(jObj, "PublishedReviewsCount", 0));
                setGetAvgReviews(MiscUtils.getFloat(jObj, "GetAvgReviews", 0.0f));
                setVenueMenues(MiscUtils.getString(jObj, "VenueMenues", ""));
            }
        }

        /**
         * opening hours
         */
        jsonArray = MiscUtils.getJSONArray(jsonObject, "Table2");
        if (jsonArray != null) {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject jObj = MiscUtils.getJSONObject(jsonArray, i);
                if (jObj == null) continue;
                mOpeningHours.put(
                        MiscUtils.getString(jObj, "Day", ""),
                        MiscUtils.getString(jObj, "Time", "")
                );
            }
        }

        /**
         * cuisines
         */
        jsonArray = MiscUtils.getJSONArray(jsonObject, "Table3");
        if (jsonArray != null) {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject jObj = MiscUtils.getJSONObject(jsonArray, i);
                if (jObj == null) continue;
                mCuisines.put(
                        MiscUtils.getString(jObj, "CuisineId", ""),
                        MiscUtils.getString(jObj, "CuisineName", "")
                );
            }
        }

        /**
         * good for
         */
        jsonArray = MiscUtils.getJSONArray(jsonObject, "Table4");
        if (jsonArray != null) {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                JSONObject jObj = MiscUtils.getJSONObject(jsonArray, i);
                if (jObj == null) continue;
                mGoodFor.put(
                        MiscUtils.getString(jObj, "GoodForId", ""),
                        MiscUtils.getString(jObj, "GoodForName", "")
                );
            }
        }
    }

    @Override
    public void copyFrom(Model model) {
        try {
            throw new Exception("Method not implemented");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getVenueType() {
        return mVenueType;
    }

    public void setVenueType(String venueType) {
        mVenueType = venueType;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
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

    public boolean isAcceptCreditCard() {
        return mAcceptCreditCard;
    }

    public void setAcceptCreditCard(boolean acceptCreditCard) {
        mAcceptCreditCard = acceptCreditCard;
    }

    public boolean isParkingAvailable() {
        return mParkingAvailable;
    }

    public void setParkingAvailable(boolean parkingAvailable) {
        mParkingAvailable = parkingAvailable;
    }

    public String getParkingDetails() {
        return mParkingDetails;
    }

    public void setParkingDetails(String parkingDetails) {
        mParkingDetails = parkingDetails;
    }

    public String getPriceRange() {
        return mPriceRange;
    }

    public void setPriceRange(String priceRange) {
        mPriceRange = priceRange;
    }

    public String getWebsite() {
        return mWebsite;
    }

    public void setWebsite(String website) {
        mWebsite = website;
    }

    public String getFacebook() {
        return mFacebook;
    }

    public void setFacebook(String facebook) {
        mFacebook = facebook;
    }

    public String getTwitter() {
        return mTwitter;
    }

    public void setTwitter(String twitter) {
        mTwitter = twitter;
    }

    public String getInstagram() {
        return mInstagram;
    }

    public void setInstagram(String instagram) {
        mInstagram = instagram;
    }

    public String getAdvertisementIFrame() {
        return mAdvertisementIFrame;
    }

    public void setAdvertisementIFrame(String advertisementIFrame) {
        mAdvertisementIFrame = advertisementIFrame;
    }

    public String getCoverImage() {
        return Communicator.BASE_URL + "Uploads/Gallery/" + mCoverImage;
    }

    public void setCoverImage(String coverImage) {
        mCoverImage = coverImage;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getTerms() {
        return mTerms;
    }

    public void setTerms(String terms) {
        mTerms = terms;
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
        try {
            addedDate = addedDate.replace('T', ' ');
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
        try {
            modifiedDate = modifiedDate.replace('T', ' ');
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

    public String getVenueStatus() {
        return mVenueStatus;
    }

    public void setVenueStatus(String venueStatus) {
        mVenueStatus = venueStatus;
    }

    public String getGetDirections() {
        return mGetDirections;
    }

    public void setGetDirections(String getDirections) {
        mGetDirections = getDirections;
    }

    public String getMetaDescription() {
        return mMetaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        mMetaDescription = metaDescription;
    }

    public String getPageTitle() {
        return mPageTitle;
    }

    public void setPageTitle(String pageTitle) {
        mPageTitle = pageTitle;
    }

    public boolean isAutomaticCheckout() {
        return mAutomaticCheckout;
    }

    public void setAutomaticCheckout(boolean automaticCheckout) {
        mAutomaticCheckout = automaticCheckout;
    }

    public String getBackgroundImage() {
        return mBackgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        mBackgroundImage = backgroundImage;
    }

    public String getVenueLogo() {
        return Communicator.BASE_URL + "Uploads/LogoImages/" + mVenueLogo;
    }

    public void setVenueLogo(String venueLogo) {
        mVenueLogo = venueLogo;
    }

    public boolean isDirectory() {
        return mIsDirectory;
    }

    public void setIsDirectory(boolean isDirectory) {
        mIsDirectory = isDirectory;
    }

    public float getBookingFee() {
        return mBookingFee;
    }

    public void setBookingFee(float bookingFee) {
        mBookingFee = bookingFee;
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

    public ArrayList<String> getImages() {
        return mImages;
    }

    public ArrayList<Pair<String, String>> getVenueMenues() {
        return mVenueMenues;
    }

    public void setVenueMenues(String venueMenues) {
        mVenueMenues.clear();
        String bits[] = venueMenues.split(",");
        for (String bit : bits) {
            if (bit.trim().isEmpty()) continue;
            String[] keyValue = bit.trim().split(" ");
            mVenueMenues.add(new Pair<>(keyValue[0], Communicator.BASE_URL + "Uploads/menu/" + keyValue[1]));
        }
    }

    public HashMap<String, String> getOpeningHours() {
        return mOpeningHours;
    }

    public HashMap<String, String> getCuisines() {
        return mCuisines;
    }

    public HashMap<String, String> getGoodFor() {
        return mGoodFor;
    }

    public ArrayList<String> getCreditCards() {
        return mCreditCards;
    }

    public void setCreditCards(String creditCards) {
        mCreditCards.clear();
        String bits[] = creditCards.split(",");
        for (String bit : bits) {
            if (bit.trim().isEmpty()) continue;
            mCreditCards.add(bit);
        }
    }

    public ArrayList<String> getMetaKeywords() {
        return mMetaKeywords;
    }


    public void setMetaKeywords(String creditCards) {
        mMetaKeywords.clear();
        String bits[] = creditCards.split(",");
        for (String bit : bits) {
            if (bit.trim().isEmpty()) continue;
            mMetaKeywords.add(bit);
        }
    }
}
