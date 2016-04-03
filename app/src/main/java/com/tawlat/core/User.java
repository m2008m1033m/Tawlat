package com.tawlat.core;

import android.content.Context;
import android.content.SharedPreferences;

import com.tawlat.TawlatApplication;
import com.tawlat.models.UserModel;


/**
 * Created by mohammed on 2/28/16.
 */
public class User {

    private UserModel mUser = new UserModel();
    private boolean mIsLoggedIn;
    private Context mContext;
    private static User mInstance = null;

    public static User getInstance() {
        if (mInstance == null) {
            mInstance = new User(TawlatApplication.getContext());
        }
        return mInstance;
    }

    private User(Context context) {
        mContext = context;
        mIsLoggedIn = false;
        checkLoggedIn();
    }

    public void login(UserModel user) {
        /**
         * set to logged in since the access token is obtained;
         */
        mIsLoggedIn = true;

        mUser = user;


        /**
         * once we get everything without problems
         * we need to store these information in the
         * shared preferences
         */
        fillSharedPrefs();
    }


    public void logout() {
        /**
         * remove shared preferences
         */
        mIsLoggedIn = false;
        fillSharedPrefs();
    }

    public void update() {
        fillSharedPrefs();
    }

    private void checkLoggedIn() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("com.tawlat", Context.MODE_PRIVATE);
        mIsLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (mIsLoggedIn) {
            mUser.setId(sharedPreferences.getString("userId", ""));

        }
    }

    private void fillSharedPrefs() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("com.tawlat", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", mIsLoggedIn ? mUser.getId() : "");
/*        editor.putString("aboutMe", mIsLoggedIn ? mUser.getAboutMe() : "");
        editor.putString("image", mIsLoggedIn ? mUser.getImage() : "");
        editor.putString("currentCity", mIsLoggedIn ? mUser.getCurrentCity() : "");
        editor.putString("email", mIsLoggedIn ? mUser.getEmail() : "");
        editor.putString("facebookId", mIsLoggedIn ? mUser.getFacebookId() : "");
        editor.putString("firstName", mIsLoggedIn ? mUser.getFirstName() : "");
        editor.putString("gender", mIsLoggedIn ? mUser.getGender() : "");
        editor.putString("lastName", mIsLoggedIn ? mUser.getLastName() : "");
        editor.putString("mobileCountry", mIsLoggedIn ? mUser.getMobileCountryId() : "");
        editor.putString("mobileNumber", mIsLoggedIn ? mUser.getMobileNumber() : "");
        editor.putString("nationality", mIsLoggedIn ? mUser.getNationality() : "");
        editor.putString("secondaryEmail", mIsLoggedIn ? mUser.getSecondaryEmail() : "");
        editor.putString("mobileVerificationCode", mIsLoggedIn ? mUser.getMobileVerificationCode() : "");
        editor.putInt("totalPoints", mIsLoggedIn ? mUser.getTotalPoint() : 0);
        editor.putInt("totalCheckedOutReservations", mIsLoggedIn ? mUser.getTotalCheckedOutReservations() : 0);*/
        editor.putBoolean("isLoggedIn", mIsLoggedIn);
        editor.apply();
    }

    public UserModel getUser() {
        return mUser;
    }

    public boolean isLoggedIn() {
        return mIsLoggedIn;
    }
}
