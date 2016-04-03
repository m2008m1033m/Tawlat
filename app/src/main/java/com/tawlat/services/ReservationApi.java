package com.tawlat.services;

import com.loopj.android.http.RequestParams;
import com.tawlat.core.ApiListeners;
import com.tawlat.models.Model;
import com.tawlat.models.Reservation;
import com.tawlat.models.ReservationResult;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ReservationApi {

    public static void upcoming(String userId, ApiListeners.OnItemsArrayLoadedListener listener) {
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("status", "Upcoming");
        Stub.get("GetUserReservations", listener, new Stub.ModelParser() {
            @Override
            ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>();
                for (int i = 0; i < len; i++)
                    items.add(new Reservation(jsonArray.getJSONObject(i)));
                return items;
            }
        }, params, "reservation_upcoming");
    }

    public static void canceled(String userId, ApiListeners.OnItemsArrayLoadedListener listener) {
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("status", "Cancelled");
        Stub.get("GetUserReservations", listener, new Stub.ModelParser() {
            @Override
            ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>();
                for (int i = 0; i < len; i++)
                    items.add(new Reservation(jsonArray.getJSONObject(i)));
                return items;
            }
        }, params, "reservation_canceled");
    }

    public static void passed(String userId, ApiListeners.OnItemsArrayLoadedListener listener) {
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("status", "Passed");
        Stub.get("GetUserReservations", listener, new Stub.ModelParser() {
            @Override
            ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>();
                for (int i = 0; i < len; i++)
                    items.add(new Reservation(jsonArray.getJSONObject(i)));
                return items;
            }
        }, params, "reservation_passed");
    }

    public static void noshow(String userId, ApiListeners.OnItemsArrayLoadedListener listener) {
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("status", "NoShow");
        Stub.get("GetUserReservations", listener, new Stub.ModelParser() {
            @Override
            ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>();
                for (int i = 0; i < len; i++)
                    items.add(new Reservation(jsonArray.getJSONObject(i)));
                return items;
            }
        }, params, "reservation_noshow");
    }

    public static void update(String reservationId, String firstName, String lastName, String reservationCode, Date checkInDateTime, int people, String contactNumber, String specialRequests, int totalPoints, ApiListeners.OnActionExecutedListener listener) {
        RequestParams params = new RequestParams();
        params.put("VenueReservationId", reservationId);
        params.put("firstName", firstName);
        params.put("lastName", lastName);
        params.put("ReservationCode", reservationCode);
        params.put("CheckInDateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(checkInDateTime));
        params.put("People", String.valueOf(people));
        params.put("ContactMobile", contactNumber);
        params.put("SpecialRequests", specialRequests);
        params.put("TotalPoints", String.valueOf(totalPoints));

        Stub.postJSON("UpdateUserReservation", listener, new Stub.ModelParser(), params, "reservation_update");
    }

    public static void create(String userId, String venueId, String contactName, String contactEmail, String contactMobile, int people, Date checkInDateTime, int totalPoints, String specialRequests, String type, int redeemedAmount, int points, String userVoucherId, String userVoucherNumber, String offerId, ApiListeners.OnItemLoadedListener listener) {
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("venueId", venueId);
        params.put("contactName", contactName);
        params.put("contactEmail", contactEmail);
        params.put("contactMobile", contactMobile);
        params.put("people", String.valueOf(people));
        params.put("checkInDateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(checkInDateTime));
        params.put("totalPoints", String.valueOf(totalPoints));
        params.put("specialRequests", specialRequests);
        params.put("type", type);
        params.put("redeemedAmount", String.valueOf(redeemedAmount));
        params.put("points", String.valueOf(points));
        params.put("userVoucherId", userVoucherId);
        params.put("userVoucherNumber", userVoucherNumber);
        params.put("offerId", offerId == null ? "0" : offerId);

        Stub.postJSON("ConfirmReservation", listener, new Stub.ModelParser() {
            @Override
            Model parseItem(String response) throws JSONException {
                return new ReservationResult(new JSONArray(response).getJSONObject(0));
            }
        }, params, "reservation_create");
    }

    public static void cancel(String venueReservationId, ApiListeners.OnActionExecutedListener listener) {
        Stub.postJSON("CancelReservation", listener, new Stub.ModelParser(), new RequestParams("venueReservationId", venueReservationId), "reservation_cancel");
    }
}
