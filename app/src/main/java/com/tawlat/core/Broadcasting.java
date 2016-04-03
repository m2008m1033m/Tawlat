package com.tawlat.core;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by mohammed on 3/24/16.
 */
public class Broadcasting {

    public static final String LOGIN = "login";
    public static final String LOGOUT = "logout";
    public static final String CITY_CHANGED = "city_changed";
    public static final String USER_UPDATED = "user_updated";

    public static void sendLogin(Activity activity) {
        Intent intent = new Intent(LOGIN);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

    public static void sendLogout(Activity activity) {
        Intent intent = new Intent(LOGOUT);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

    public static void sendCityChanged(Activity activity, String cityId) {
        Intent intent = new Intent(CITY_CHANGED);
        intent.putExtra("cityId", cityId);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

    public static void sendUserUpdated(Activity activity) {
        Intent intent = new Intent(USER_UPDATED);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

}
