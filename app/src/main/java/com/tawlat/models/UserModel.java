package com.tawlat.models;

import com.tawlat.core.Communicator;
import com.tawlat.utils.MiscUtils;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserModel extends Model implements Serializable {
    private String mAboutMe;
    private String mImage;
    private Date mAddedDate;
    private String mCurrentCity;
    private Date mDateOfBirth;
    private String mEmail;
    private String mFacebookId;
    private String mFirstName;
    private String mGender;
    private boolean mIsVerified;
    private String mLastName;
    private String mMobileCountry;
    private String mMobileNumber;
    private String mNationality;
    private String mPassword;
    private String mSecondaryEmail;
    private String mMobileVerificationCode;
    private int mTotalPoint;
    private int mTotalCheckedOutReservations;

    public UserModel() {
    }

    public UserModel(JSONObject jsonObject) {

        setId(MiscUtils.getString(jsonObject, "UserId", ""));
        setAboutMe(MiscUtils.getString(jsonObject, "AboutMe", ""));
        setImage(MiscUtils.getString(jsonObject, "Image", ""));
        setAddedDateFromString(MiscUtils.getString(jsonObject, "AddedDate", "1970-01-01T00:00:00"));
        setCurrentCity(MiscUtils.getString(jsonObject, "CurrentCity", "1"));
        setDateOfBirthFromString(MiscUtils.getString(jsonObject, "DOB", "01/01/1970"));
        setEmail(MiscUtils.getString(jsonObject, "Email", ""));
        setFacebookId(MiscUtils.getString(jsonObject, "FacebookId", ""));
        setFirstName(MiscUtils.getString(jsonObject, "FirstName", ""));
        setGender(MiscUtils.getString(jsonObject, "Gender", ""));
        setIsVerified(MiscUtils.getBoolean(jsonObject, "IsVerified", false));
        setLastName(MiscUtils.getString(jsonObject, "LastName", ""));
        setMobileCountry(MiscUtils.getString(jsonObject, "MobileCountry", ""));
        setMobileNumber(MiscUtils.getString(jsonObject, "MobileNumber", ""));
        setNationality(MiscUtils.getString(jsonObject, "Nationality", ""));
        setPassword(MiscUtils.getString(jsonObject, "Password", ""));
        setSecondaryEmail(MiscUtils.getString(jsonObject, "SecondaryEmail", ""));
        setTotalPoints(MiscUtils.getInt(jsonObject, "TotalPoints", 0));
        setMobileVerificationCode(MiscUtils.getString(jsonObject, "MobileVerificationCode", ""));
        setTotalCheckedOutReservations(MiscUtils.getInt(jsonObject, "TotalCheckedOutReservations", 0));

    }

    @Override
    public void copyFrom(Model model) {
        if (!(model instanceof UserModel)) return;
        setId(model.getId());
        setAboutMe(((UserModel) model).getAboutMe());
        setImage(((UserModel) model).getImage());
        setAddedDate(((UserModel) model).getAddedDate());
        setCurrentCity(((UserModel) model).getCurrentCity());
        setDateOfBirth(((UserModel) model).getDateOfBirth());
        setEmail(((UserModel) model).getEmail());
        setFacebookId(((UserModel) model).getFacebookId());
        setFirstName(((UserModel) model).getFirstName());
        setGender(((UserModel) model).getGender());
        setIsVerified(((UserModel) model).isVerified());
        setLastName(((UserModel) model).getLastName());
        setMobileCountry(((UserModel) model).getMobileCountry());
        setMobileNumber(((UserModel) model).getMobileNumber());
        setNationality(((UserModel) model).getNationality());
        setPassword(((UserModel) model).getPassword());
        setSecondaryEmail(((UserModel) model).getSecondaryEmail());
        setTotalPoints(((UserModel) model).getTotalPoint());
        setTotalCheckedOutReservations(((UserModel) model).getTotalCheckedOutReservations());
        setMobileVerificationCode(((UserModel) model).getMobileVerificationCode());
    }

    public String getAboutMe() {
        return mAboutMe;
    }

    public void setAboutMe(String aboutMe) {
        mAboutMe = aboutMe;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = Communicator.BASE_URL + "Uploads/Profile/" + image;
    }

    public Date getAddedDate() {
        return mAddedDate;
    }

    public void setAddedDate(Date addedDate) {
        mAddedDate = addedDate;
    }

    public String getAddedDateAsString(String format) {
        if (mAddedDate == null) return "";
        return new SimpleDateFormat(format, Locale.US).format(mAddedDate);
    }

    public void setAddedDateFromString(String addedDate) {
        try {
            addedDate = addedDate.replace('T', ' ');
            mAddedDate = new SimpleDateFormat("yyyy-MM-27 HH:mm:ss", Locale.US).parse(addedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentCity() {
        return mCurrentCity;
    }

    public void setCurrentCity(String currentCity) {
        mCurrentCity = currentCity;
    }

    public Date getDateOfBirth() {
        return mDateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        mDateOfBirth = dateOfBirth;
    }

    public String getDateOfBirthAsString(String format) {
        if (mDateOfBirth == null) return "";
        return new SimpleDateFormat(format, Locale.US).format(mDateOfBirth);
    }

    public void setDateOfBirthFromString(String dateOfBirth) {
        try {
            mDateOfBirth = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(dateOfBirth);
        } catch (ParseException e1) {
            e1.printStackTrace();
            try {
                mDateOfBirth = new SimpleDateFormat("dd-MMM-yyyy", Locale.US).parse(dateOfBirth);
            } catch (ParseException e2) {
                e2.printStackTrace();
                try {
                    mDateOfBirth = new SimpleDateFormat("dd-MM-yyyy", Locale.US).parse(dateOfBirth);
                } catch (ParseException e3) {
                    e3.printStackTrace();
                }
            }
        }
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getFacebookId() {
        return mFacebookId;
    }

    public void setFacebookId(String facebookId) {
        mFacebookId = facebookId;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getGender() {
        return mGender;
    }

    public void setGender(String gender) {
        mGender = gender;
    }

    public boolean isVerified() {
        return mIsVerified;
    }

    public void setIsVerified(boolean isVerfied) {
        mIsVerified = isVerfied;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getMobileCountry() {
        return mMobileCountry;
    }

    public void setMobileCountry(String mobileCountry) {
        mMobileCountry = mobileCountry;
    }

    public String getMobileNumber() {
        return mMobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        String[] bits = mobileNumber.split("-");
        if (bits.length == 2)
            mobileNumber = bits[1];
        mMobileNumber = mobileNumber;
    }

    public String getNationality() {
        return mNationality;
    }

    public void setNationality(String nationality) {
        mNationality = nationality;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getSecondaryEmail() {
        return mSecondaryEmail;
    }

    public void setSecondaryEmail(String secondaryEmail) {
        mSecondaryEmail = secondaryEmail;
    }

    public int getTotalPoint() {
        return mTotalPoint;
    }

    public void setTotalPoints(int totalPoint) {
        mTotalPoint = totalPoint;
    }

    public int getTotalCheckedOutReservations() {
        return mTotalCheckedOutReservations;
    }

    public void setTotalCheckedOutReservations(int totalCheckedOutReservations) {
        mTotalCheckedOutReservations = totalCheckedOutReservations;
    }

    public String getMobileVerificationCode() {
        return mMobileVerificationCode;
    }

    public void setMobileVerificationCode(String mobileVerificationCode) {
        mMobileVerificationCode = mobileVerificationCode;
    }

    public void setTotalPoint(int totalPoint) {
        mTotalPoint = totalPoint;
    }
}
