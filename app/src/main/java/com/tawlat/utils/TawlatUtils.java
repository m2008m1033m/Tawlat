package com.tawlat.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tawlat.R;
import com.tawlat.TawlatApplication;
import com.tawlat.core.ApiListeners;
import com.tawlat.models.Location;
import com.tawlat.models.Model;
import com.tawlat.models.Text;
import com.tawlat.services.Result;
import com.tawlat.services.UserApi;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by mohammed on 3/22/16.
 */
public class TawlatUtils {

    public static String getCurrentCityId() {
        SharedPreferences sharedPreferences = TawlatApplication.getContext().getSharedPreferences("com.tawlat", Context.MODE_PRIVATE);
        return sharedPreferences.getString("currentCityId", "1");
    }

    public static void setCurrentCityId(String currentCityId) {
        SharedPreferences sharedPreferences = TawlatApplication.getContext().getSharedPreferences("com.tawlat", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currentCityId", currentCityId);
        editor.apply();
    }

    public static String convertRatingToStars(int rating) {
        String rateStr = "";
        int i = 0;
        for (; i < rating; i++) {
            rateStr += "★";
        }
        for (; i < 5; i++) {
            rateStr += "☆";
        }
        return rateStr;
    }

    public static int getIdx(String id, String[] arr) {
        if (arr == null || arr.length == 0) return 0;
        for (int i = 0; i < arr.length; i++) {
            if (id.equals(arr[i]))
                return i;
        }
        return 0;
    }

    /**
     * validates email
     */
    public static boolean validateEmail(String val) {
        Pattern regex =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        return regex.matcher(val).find();
    }


    public static void setGrayScale(ImageView iv, boolean apply) {
        if (apply) {
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0);
            ColorMatrixColorFilter cmcf = new ColorMatrixColorFilter(cm);
            iv.setColorFilter(cmcf);
        } else {
            iv.setColorFilter(null);
        }

    }

    public static String getLocationName(ArrayList<Location> locations, String locationId) {
        for (Location location : locations)
            if (location.getId().equals(locationId))
                return location.getName();
        return "N/A";
    }

    public static int convertDPtoPixel(int dp, Resources r) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                r.getDisplayMetrics());
    }

    public static int compareTimes(Date d1, Date d2) {

        int t1 = (int) (d1.getTime() % (24 * 60 * 60 * 1000L));
        int t2 = (int) (d2.getTime() % (24 * 60 * 60 * 1000L));

        return t1 - t2;

    }

    public static boolean validatePhoneNumber(String val) {
        val = val.replace(" ", "");

        int numberLength = val.length();

        if (numberLength != 9 && numberLength != 10) return false;

        if (numberLength == 10 && val.charAt(0) != '0') return false;

        int startingPoint = (numberLength == 9) ? 0 : 1;

        String key = val.substring(startingPoint, startingPoint + 2);

        if (!key.equals("50") && !key.equals("55") && !key.equals("56") && !key.equals("52"))
            return false;

        return true;
    }

    public static AlertDialog showForgotPassword(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View v = LayoutInflater.from(activity).inflate(R.layout.forgot_password_dialog, null);
        final TextView email = (TextView) v.findViewById(R.id.fp_email);
        builder
                .setView(v)
                .setTitle(R.string.forgot_password)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /**
                         * check the validity of the email
                         */
                        String emailStr = email.getText().toString().trim();

                        /**
                         * continue if it is valid
                         */
                        final AlertDialog progressDialog = Notifications.showLoadingDialog(activity, activity.getString(R.string.loading));

                        UserApi.forgotPassword(emailStr, new ApiListeners.OnItemLoadedListener() {
                            @Override
                            public void onLoaded(Result result, @Nullable Model item) {
                                progressDialog.dismiss();

                                if (result.isSucceeded() && item != null) {
                                    Notifications.showSnackBar(activity, ((Text) item).getText());
                                } else {
                                    Notifications.showSnackBar(activity, result.getMessages().get(0));
                                }
                            }
                        });

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        final AlertDialog forgotPassword = builder.create();
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TawlatUtils.validateEmail(s.toString()))
                    forgotPassword.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                else
                    forgotPassword.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        });

        forgotPassword.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                forgotPassword.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                email.setText("");
            }
        });
        forgotPassword.show();
        return forgotPassword;

    }

}
