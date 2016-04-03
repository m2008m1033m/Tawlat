package com.tawlat.services;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import com.loopj.android.http.RequestParams;
import com.tawlat.R;
import com.tawlat.TawlatApplication;
import com.tawlat.core.ApiListeners;
import com.tawlat.models.FacebookUser;
import com.tawlat.models.Model;
import com.tawlat.models.Number;
import com.tawlat.models.Text;
import com.tawlat.models.UserModel;
import com.tawlat.models.Voucher;
import com.tawlat.models.venues.FavoriteVenue;
import com.tawlat.utils.MiscUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UserApi {

    public static void login(String email, String password, ApiListeners.OnItemLoadedListener listener) {
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", password);

        Stub.get("Login", listener, new Stub.ModelParser() {
            @Override
            Model parseItem(String response) throws JSONException {
                JSONObject jsonObject = new JSONObject(response).getJSONArray("Table").getJSONObject(0);
                return new UserModel(jsonObject);
            }
        }, params, "user_login");
    }

    public static void favorites(String userId, ApiListeners.OnItemsArrayLoadedListener listener) {
        Stub.get("GetUserFavouriteVenues", listener, new Stub.ModelParser() {
            @Override
            ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>();
                for (int i = 0; i < len; i++) {
                    items.add(new FavoriteVenue(jsonArray.getJSONObject(i)));
                }
                return items;
            }
        }, new RequestParams("userId", userId), "user_favorites");
    }

    public static void forgotPassword(String email, ApiListeners.OnItemLoadedListener listener) {
        Stub.get("ForgetPassword", listener, new Stub.ModelParser() {
            @Override
            Model parseItem(String response) throws JSONException {
                Log.d("forgot_password", response);
                switch (response) {
                    case "Code:PasswordSent":
                        return new Text(TawlatApplication.getContext().getString(R.string.reset_link_has_been_sent_to_your_email));
                    case "\"Code:NoResult\"":
                        return new Text(TawlatApplication.getContext().getString(R.string.there_is_no_account_associated_with_this_email));
                    default:
                        return new Text(TawlatApplication.getContext().getString(R.string.unknown_error_has_occurred));
                }
            }
        }, new RequestParams("email", email), "user_forgot_password");
    }

    public static void get(String userId, ApiListeners.OnItemLoadedListener listener) {
        Stub.get("GetUserProfile", listener, new Stub.ModelParser() {
            @Override
            Model parseItem(String response) throws JSONException {
                return new UserModel(new JSONArray(response).getJSONObject(0));
            }
        }, new RequestParams("userId", userId), "user_get");
    }

    public static void pendingVouchers(String userId, ApiListeners.OnItemsArrayLoadedListener listener) {
        Stub.get("UserVouchersPending", listener, new Stub.ModelParser() {
            @Override
            ArrayList<Model> extractArray(String response) throws JSONException {
                if (response.equals("\"Code:NoResult\"")) return new ArrayList<>();
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>();
                for (int i = 0; i < len; i++)
                    items.add(new Voucher(jsonArray.getJSONObject(i), Voucher.Status.PENDING));
                return items;
            }
        }, new RequestParams("userId", userId), "user_pending_voucher");
    }

    public static void historyVouchers(String userId, ApiListeners.OnItemsArrayLoadedListener listener) {
        Stub.get("UserVouchersHistory", listener, new Stub.ModelParser() {
            @Override
            ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>();
                for (int i = 0; i < len; i++)
                    items.add(new Voucher(jsonArray.getJSONObject(i), Voucher.Status.REDEEMED));
                return items;
            }
        }, new RequestParams("userId", userId), "user_history_voucher");
    }

    public static void points(String userId, ApiListeners.OnItemLoadedListener listener) {
        Stub.get("GetUserPoints", listener, new Stub.ModelParser() {
            @Override
            Model parseItem(String response) throws JSONException {
                return new Number(response);
            }
        }, new RequestParams("userId", userId), "user_points");
    }

    public static void update(String userId, String firstName, String lastName, String email, String about, String gender, String currentCity, String nationality, String mobileCountry, String mobileNumber, String image, Date dob, ApiListeners.OnItemLoadedListener listener) {
        /**
         * prepare the image
         */
        String image_str = "";
        if (image != null && !image.isEmpty()) {
            Bitmap bitmap = MiscUtils.decodeFile(image, 75);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
                byte[] byte_arr = stream.toByteArray();
                image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);
            }
        }

        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("firstName", firstName);
        params.put("lastName", lastName);
        params.put("email", email);
        params.put("password", "");
        params.put("about", about);
        params.put("gender", gender);
        params.put("currentCity", currentCity);
        params.put("nationality", nationality);
        params.put("mobileCountry", mobileCountry);
        params.put("mobileNumber", mobileNumber);
        params.put("image", "");
        params.put("dob", dob != null ? new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(dob) : "");
        params.put("Image", image_str);

        Stub.postJSON("UpdateProfile", listener, new Stub.ModelParser() {
            @Override
            Model parseItem(String response) throws JSONException {
                return new UserModel(new JSONObject(response).getJSONArray("Table").getJSONObject(0));
            }
        }, params, "user_update");
    }

    public static void register(String firstName, String lastName, String email, String password, String about, String gender, String currentCity, String nationality, String mobileCountry, String mobileNumber, Date dob, ApiListeners.OnItemLoadedListener listener) {
        RequestParams params = new RequestParams();
        params.put("firstName", firstName);
        params.put("lastName", lastName);
        params.put("email", email);
        params.put("password", password);
        params.put("about", about);
        params.put("gender", gender);
        params.put("currentCity", currentCity);
        params.put("nationality", nationality);
        params.put("mobileCountry", mobileCountry);
        params.put("mobileNumber", mobileNumber);
        params.put("image", "");
        params.put("dob", dob != null ? new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(dob) : "");

        Stub.postJSON("Register", listener, new Stub.ModelParser() {
            @Override
            Model parseItem(String response) throws JSONException {
                return new UserModel(new JSONObject(response).getJSONArray("Table").getJSONObject(0));
            }
        }, params, "user_register");
    }

    public static void facebookLogin(String accessToken, final ApiListeners.OnItemLoadedListener listener) {
        /**
         * first we need to get the user info:
         */
        RequestParams params = new RequestParams();
        params.put("fields", "id,gender,email,birthday,name");
        params.put("access_token", accessToken);
        Stub.get("https://graph.facebook.com/me", new ApiListeners.OnItemLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable Model item) {
                if (result.isSucceeded() && item != null) {
                    FacebookUser facebookUser = ((FacebookUser) item);
                    RequestParams params = new RequestParams();
                    params.put("facebookId", facebookUser.getId());
                    params.put("firstName", facebookUser.getFirstName());
                    params.put("lastName", facebookUser.getLastName());
                    params.put("email", facebookUser.getEmail());
                    params.put("gender", facebookUser.getGender());
                    params.put("dob", facebookUser.getDateOfBirth() == null ? "" : new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(facebookUser.getDateOfBirth()));
                    Stub.postJSON("RegisterFacebook", listener, new Stub.ModelParser() {
                        @Override
                        Model parseItem(String response) throws JSONException {
                            return new UserModel(new JSONObject(response));
                        }
                    }, params, "user_facebook_register");

                } else {
                    result.setIsSucceeded(false);
                    listener.onLoaded(result, null);
                }

            }
        }, new Stub.ModelParser() {
            @Override
            Model parseItem(String response) throws JSONException {
                return new FacebookUser(new JSONObject(response));
            }
        }, params, "user_facebook_info");
    }

    public static void favorite(String userId, String venueId, ApiListeners.OnActionExecutedListener listener) {
        RequestParams params = new RequestParams("userId", userId);
        params.put("venueId", venueId);
        Stub.postJSON("MakeVenueFavourite", listener, new Stub.ModelParser(), params, "user_favorite");
    }

    public static void unfavorite(String userId, String venueId, ApiListeners.OnActionExecutedListener listener) {
        RequestParams params = new RequestParams("userId", userId);
        params.put("venueId", venueId);
        Stub.postJSON("DeleteUserFavouriteVenue", listener, new Stub.ModelParser(), params, "user_unfavorite");
    }

    public static void verify(String userId, String verificationCode, ApiListeners.OnItemLoadedListener listener) {
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("VerificationCode", verificationCode);
        Stub.postJSON("UserAccountVerification", listener, new Stub.ModelParser() {
            @Override
            Model parseItem(String response) throws JSONException {
                if (response.contains("Code:In Valid Verification Code")) return null;
                return new UserModel(new JSONObject(response).getJSONArray("Table").getJSONObject(0));
            }
        }, params, "user_verify");
    }

    public static void changePassword(String userId, String newPassword, ApiListeners.OnItemLoadedListener listener) {
        RequestParams params = new RequestParams("userId", userId);
        params.put("newpassword", newPassword);
        Stub.postJSON("ChangePassword", listener, new Stub.ModelParser() {
            @Override
            Model parseItem(String response) throws JSONException {
                return new Text(response.split(",")[1]);
            }
        }, params, "user_change_password");
    }
}
