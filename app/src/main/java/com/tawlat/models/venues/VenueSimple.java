package com.tawlat.models.venues;

import com.tawlat.core.Communicator;
import com.tawlat.models.Model;
import com.tawlat.utils.MiscUtils;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class VenueSimple extends Model {

    private ArrayList<String> mVenueCuisines = new ArrayList<>();
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
    private String mLocationName;

    public VenueSimple(JSONObject jsonObject) {

        /**
         * basic info
         */
        setId(MiscUtils.getString(jsonObject, "VenueId", ""));
        setVenueType(MiscUtils.getString(jsonObject, "VenueType", ""));
        setName(MiscUtils.getString(jsonObject, "Name", ""));
        setEmail(MiscUtils.getString(jsonObject, "Email", ""));
        setPhone(MiscUtils.getString(jsonObject, "Phone", ""));
        setAddress(MiscUtils.getString(jsonObject, "Address", ""));
        setLocation(MiscUtils.getString(jsonObject, "Location", ""));
        setCity(MiscUtils.getString(jsonObject, "City", ""));
        setCountry(MiscUtils.getString(jsonObject, "Country", ""));
        setLatitude(MiscUtils.getDouble(jsonObject, "Latitude", 0.0));
        setLongitude(MiscUtils.getDouble(jsonObject, "Longitude", 0.0));
        setAcceptCreditCard(MiscUtils.getBoolean(jsonObject, "AcceptCreditCard", false));
        setCreditCards(MiscUtils.getString(jsonObject, "CreditCards", ""));
        setParkingAvailable(MiscUtils.getBoolean(jsonObject, "ParkingAvailable", false));
        setParkingDetails(MiscUtils.getString(jsonObject, "ParkingDetails", ""));
        setPriceRange(MiscUtils.getString(jsonObject, "PriceRange", ""));
        setWebsite(MiscUtils.getString(jsonObject, "Website", ""));
        setFacebook(MiscUtils.getString(jsonObject, "Facebook", ""));
        setTwitter(MiscUtils.getString(jsonObject, "Twitter", ""));
        setInstagram(MiscUtils.getString(jsonObject, "Instagram", ""));
        setAdvertisementIFrame(MiscUtils.getString(jsonObject, "AdvertisementIFrame", ""));
        setCoverImage(MiscUtils.getString(jsonObject, "CoverImage", ""));
        setDescription(MiscUtils.getString(jsonObject, "Description", ""));
        setTerms(MiscUtils.getString(jsonObject, "Terms", ""));
        setAddedDate(MiscUtils.getString(jsonObject, "AddedDate", ""));
        setModifiedDate(MiscUtils.getString(jsonObject, "ModifiedDate", ""));
        setRecordStatus(MiscUtils.getString(jsonObject, "RecordStatus", ""));
        setVenueStatus(MiscUtils.getString(jsonObject, "VenueStatus", ""));
        setGetDirections(MiscUtils.getString(jsonObject, "GetDirections", ""));
        setMetaKeywords(MiscUtils.getString(jsonObject, "MetaKeywords", ""));
        setMetaDescription(MiscUtils.getString(jsonObject, "MetaDescription", ""));
        setPageTitle(MiscUtils.getString(jsonObject, "PageTitle", ""));
        setAutomaticCheckout(MiscUtils.getBoolean(jsonObject, "AutomaticCheckout", false));
        setBackgroundImage(MiscUtils.getString(jsonObject, "BackgroundImage", ""));
        setVenueLogo(MiscUtils.getString(jsonObject, "VenueLogo", ""));
        setIsDirectory(MiscUtils.getBoolean(jsonObject, "IsDirectory", false));
        setBookingFee(MiscUtils.getFloat(jsonObject, "BookingFee", 0.0f));
        setVenueCuisines(MiscUtils.getString(jsonObject, "VenueCuisines", ""));
        setLocationName(MiscUtils.getString(jsonObject, "LocationName", ""));

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

    public ArrayList<String> getCreditCards() {
        return mCreditCards;
    }

    public void setCreditCards(String creditCards) {
        mCreditCards.clear();
        String bits[] = creditCards.split(",");
        for (String bit : bits) {
            if (bit.trim().isEmpty()) continue;
            mCreditCards.add(bit.trim());
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
            mMetaKeywords.add(bit.trim());
        }
    }

    public ArrayList<String> getVenueCuisines() {
        return mVenueCuisines;
    }


    public void setVenueCuisines(String venueCuisines) {
        mVenueCuisines.clear();
        String bits[] = venueCuisines.split(",");
        for (String bit : bits) {
            if (bit.trim().isEmpty()) continue;
            mVenueCuisines.add(bit.trim());
        }
    }

    public String getLocationName() {
        return mLocationName;
    }

    public void setLocationName(String locationName) {
        mLocationName = locationName;
    }
}
