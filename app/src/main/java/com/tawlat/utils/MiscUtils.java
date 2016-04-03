package com.tawlat.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tawlat.R;
import com.tawlat.TawlatApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mohammed on 3/4/16.
 */
public class MiscUtils {

    /**
     * json getters:
     */
    public static String getString(JSONObject jsonObject, String name, String defaultValue) {
        try {
            String string = jsonObject.getString(name).trim();
            return string.equals("null") ? "" : string;
        } catch (JSONException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static String getUTF8String(JSONObject jsonObject, String name, String defaultValue) {
        try {
            String string = new String(jsonObject.getString(name).trim().getBytes(), "UTF-8");
            return string.equals("null") ? "" : string;
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static int getInt(JSONObject jsonObject, String name, int defaultValue) {
        try {
            return Integer.parseInt(jsonObject.getString(name).trim());
        } catch (JSONException e) {
            e.printStackTrace();
            return defaultValue;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static float getFloat(JSONObject jsonObject, String name, float defaultValue) {
        try {
            return Float.parseFloat(jsonObject.getString(name).trim());
        } catch (JSONException e) {
            e.printStackTrace();
            return defaultValue;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static double getDouble(JSONObject jsonObject, String name, double defaultValue) {
        try {
            return Double.parseDouble(jsonObject.getString(name).trim());
        } catch (JSONException e) {
            e.printStackTrace();
            return defaultValue;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static boolean getBoolean(JSONObject jsonObject, String name, boolean defaultValue) {
        try {
            String val = jsonObject.getString(name).trim();
            return val.equals("1") ? true : val.equals("0") ? false : val.equals("true") ? true : false;
        } catch (JSONException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static JSONArray getJSONArray(JSONObject jsonObject, String name) {
        try {
            return jsonObject.getJSONArray(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getJSONObject(JSONObject jsonObject, String name) {
        try {
            return jsonObject.getJSONObject(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getJSONObject(JSONArray jsonArray, int idx) {
        try {
            return jsonArray.getJSONObject(idx);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getDurationFormatted(Date date) {
        if (date == null) return "N/A";
        int currentSeconds = (int) (Calendar.getInstance().getTimeInMillis() / 1000);
        int seconds = (int) (date.getTime() / 1000);

        seconds = currentSeconds - seconds;

        if (seconds < 60)
            return TawlatApplication.getContext().getString(seconds == 1 ? R.string.s_second_ago : R.string.s_seconds_ago, seconds);

        seconds = seconds / 60;
        if (seconds < 60)
            return TawlatApplication.getContext().getString(seconds == 1 ? R.string.s_minute_ago : R.string.s_minutes_ago, seconds);

        seconds = seconds / 60;
        if (seconds < 24)
            return TawlatApplication.getContext().getString(seconds == 1 ? R.string.s_hour_ago : R.string.s_hours_ago, seconds);

        seconds = seconds / 24;
        if (seconds < 30)
            return TawlatApplication.getContext().getString(seconds == 1 ? R.string.s_day_ago : R.string.s_days_ago, seconds);

        seconds = seconds / 30;
        if (seconds < 12)
            return TawlatApplication.getContext().getString(seconds == 1 ? R.string.s_month_ago : R.string.s_months_ago, seconds);

        seconds = seconds / 12;
        return TawlatApplication.getContext().getString(seconds == 1 ? R.string.s_year_ago : R.string.s_years_ago, seconds);

    }

    // Decodes image and scales it to reduce memory consumption
    public static Bitmap decodeFile(String photoPath, int size) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(photoPath), null, o);

            // The new size we want to scale to

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= size ||
                    o.outHeight / scale / 2 >= size) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(photoPath), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String convertStringToHashTag(String str) {
        String[] bits = str.split(" ");
        String finalDescription = "";
        for (String bit : bits) {
            if (bit.trim().isEmpty()) continue;
            if (!bit.startsWith("#"))
                bit = "#" + bit;
            finalDescription += bit + " ";
        }

        return finalDescription;
    }

    public static String getDayOfWeek(Date date) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        switch (c.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                return "Sunday";
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
        }
        return "";
    }

    public static String concatArray(String[] arr, String d) {
        if (arr == null) return "";
        String r = "";
        for (String elem : arr) {
            r += elem.trim() + d;
        }
        return r.substring(0, r.length() - d.length());
    }
}
