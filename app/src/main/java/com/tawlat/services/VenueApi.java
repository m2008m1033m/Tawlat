package com.tawlat.services;

import com.loopj.android.http.RequestParams;
import com.tawlat.core.ApiListeners;
import com.tawlat.models.Menu;
import com.tawlat.models.Model;
import com.tawlat.models.Rate;
import com.tawlat.models.Review;
import com.tawlat.models.Text;
import com.tawlat.models.TimeSlot;
import com.tawlat.models.search.AdvancedSearchResult;
import com.tawlat.models.venues.NearMeVenue;
import com.tawlat.models.venues.OfferEventVenue;
import com.tawlat.models.venues.RecommendedVenue;
import com.tawlat.models.venues.TopPointVenue;
import com.tawlat.models.venues.Venue;
import com.tawlat.models.venues.VenueSimple;
import com.tawlat.utils.MiscUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class VenueApi {

    public static void topPoints(String cityId, ApiListeners.OnItemsArrayLoadedListener listener) {
        Stub.get("GetTopPoints", listener, new Stub.ModelParser() {
            @Override
            ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>();
                for (int i = 0; i < len; i++) {
                    items.add(new TopPointVenue(jsonArray.getJSONObject(i)));
                }

                Collections.sort(items, new Comparator<Model>() {
                    @Override
                    public int compare(Model m1, Model m2) {
                        int result = ((TopPointVenue) m2).getMaxPoints() - ((TopPointVenue) m1).getMaxPoints();
                        if (result == 0) result = new Random().nextBoolean() ? 1 : -1;
                        return result;
                    }
                });

                if (items.size() > 12) {
                    items.subList(12, items.size()).clear();
                }

                return items;
            }
        }, new RequestParams("cityId", cityId), "venue_top_points");
    }

    public static void recommended(String cityId, ApiListeners.OnItemsArrayLoadedListener listener) {
        Stub.get("GetRecommendedByTawlat", listener, new Stub.ModelParser() {
            @Override
            ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>();
                for (int i = 0; i < len; i++) {
                    items.add(new RecommendedVenue(jsonArray.getJSONObject(i)));
                }

                Collections.shuffle(items);
                if (items.size() > 12) {
                    items.subList(12, items.size()).clear();
                }

                return items;
            }
        }, new RequestParams("cityId", cityId), "venue_recommended");
    }

    public static void offersAndEvents(String cityId, ApiListeners.OnItemsArrayLoadedListener listener) {
        Stub.get("GetTawlatOffersAndEvents", listener, new Stub.ModelParser() {
            @Override
            ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>();
                for (int i = 0; i < len; i++) {
                    items.add(new OfferEventVenue(jsonArray.getJSONObject(i)));
                }

                Collections.shuffle(items);
                if (items.size() > 12) {
                    items.subList(12, items.size()).clear();
                }

                return items;
            }
        }, new RequestParams("cityId", cityId), "venue_offers_and_events");
    }

    public static void availabilitySearch(boolean isAllLocations, boolean isAllCuisines, String dow, String tod, String lid, String cid, int people, Date date, String cityId, ApiListeners.OnItemsArrayLoadedListener listener) {
        RequestParams params = new RequestParams();
        params.put("isAllLocations", isAllLocations ? "true" : "false");
        params.put("isAllCuisines", isAllCuisines ? "true" : "false");
        params.put("dow", dow);
        params.put("tod", tod);
        params.put("lid", lid);
        params.put("cid", cid);
        params.put("people", String.valueOf(people));
        params.put("date", new SimpleDateFormat("MM-dd-yyyy", Locale.US).format(date));
        params.put("cityId", cityId);

        Stub.get("SearchByAvailability", listener, new Stub.ModelParser() {
            @Override
            ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>();
                for (int i = 0; i < len; i++) {
                    items.add(new AdvancedSearchResult(jsonArray.getJSONObject(i)));
                }

                Collections.sort(items, new Comparator<Model>() {
                    @Override
                    public int compare(Model m1, Model m2) {
                        int result = ((AdvancedSearchResult) m2).getPoints() - ((AdvancedSearchResult) m1).getPoints();
                        if (result == 0) result = new Random().nextBoolean() ? 1 : -1;
                        return result;
                    }
                });

                return items;
            }
        }, params, "venue_availability_search");
    }

    public static void nearby(double latitude, double longitude, ApiListeners.OnItemsArrayLoadedListener listener) {
        RequestParams params = new RequestParams();
        params.put("latitude", latitude);
        params.put("longitude", longitude);

        Stub.get("GetNearMeVenues", listener, new Stub.ModelParser() {
            @Override
            ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>();
                for (int i = 0; i < len; i++) {
                    items.add(new NearMeVenue(jsonArray.getJSONObject(i)));
                }

                Collections.sort(items, new Comparator<Model>() {
                    @Override
                    public int compare(Model m1, Model m2) {
                        double result = ((NearMeVenue) m1).getDistance() - ((NearMeVenue) m2).getDistance();
                        if (result > 0)
                            return 1;
                        else if (result < 0)
                            return -1;
                        else
                            return 0;
                    }
                });

                return items;
            }
        }, params, "venue_nearby");
    }

    public static void get(String venueId, ApiListeners.OnItemLoadedListener listener) {
        RequestParams params = new RequestParams("venueId", venueId);
        params.put("dayOfWeek", "");
        Stub.get("GetVenueDetails", listener, new Stub.ModelParser() {
            @Override
            Model parseItem(String response) throws JSONException {
                return new Venue(new JSONObject(response));
            }
        }, params, "venue_get");
    }

    public static void timeSlots(String venueId, Date checkInDate, ApiListeners.OnItemsArrayLoadedListener listener) {
        RequestParams params = new RequestParams();
        params.put("venueId", venueId);
        params.put("checkInDate", new SimpleDateFormat("MM-dd-yyyy", Locale.US).format(checkInDate));
        params.put("dayOfWeek", MiscUtils.getDayOfWeek(checkInDate));
        Stub.get("GetUpdatedTimeSlots", listener, new Stub.ModelParser() {
            @Override
            ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>();
                for (int i = 0; i < len; i++)
                    items.add(new TimeSlot(jsonArray.getJSONObject(i)));
                return items;
            }
        }, params, "venue_time_slots");
    }

    public static void reviews(String venueId, ApiListeners.OnItemsArrayLoadedListener listener) {
        Stub.get("GetPublishedReviews", listener, new Stub.ModelParser() {
            @Override
            ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>();
                for (int i = 0; i < len; i++)
                    items.add(new Review(jsonArray.getJSONObject(i)));
                return items;
            }
        }, new RequestParams("venueId", venueId), "venue_reviews");
    }

    public static void getRate(String venueId, ApiListeners.OnItemLoadedListener listener) {
        Stub.get("GetTotalReviewsTotalRating", listener, new Stub.ModelParser() {
            @Override
            Model parseItem(String response) throws JSONException {
                return new Rate(new JSONObject(response));
            }
        }, new RequestParams("venueId", venueId), "venue_get_rate");
    }

    public static void menus(String venueId, ApiListeners.OnItemsArrayLoadedListener listener) {
        Stub.get("GetVenueMenues", listener, new Stub.ModelParser() {
            @Override
            ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>();
                for (int i = 0; i < len; i++)
                    items.add(new Menu(jsonArray.getJSONObject(i)));
                return items;
            }
        }, new RequestParams("venueId", venueId), "venue_menus");
    }

    public static void advancedSearch(String[] locationIds, String[] cuisineIds, String[] goodForIds, String[] prices, Date timeOfDay, String cityId, boolean isDirectoryIncluded, String venueId, String dayOfWeek, ApiListeners.OnItemsArrayLoadedListener listener) {
        RequestParams params = new RequestParams();
        params.put("LocationIds", MiscUtils.concatArray(locationIds, ","));
        params.put("CuisineIds", MiscUtils.concatArray(cuisineIds, ","));
        params.put("GoodForIds", MiscUtils.concatArray(goodForIds, ","));
        params.put("Prices", MiscUtils.concatArray(prices, ","));
        params.put("timeOfDay", new SimpleDateFormat("HH:mm:ss", Locale.US).format(timeOfDay));
        params.put("cityId", cityId);
        params.put("isDirectoryIncluded", isDirectoryIncluded ? "true" : "false");
        params.put("venueId", venueId);
        params.put("dayOfWeek", dayOfWeek);

        Stub.get("GetAdvanceSearch", listener, new Stub.ModelParser() {
            @Override
            ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>();
                for (int i = 0; i < len; i++)
                    items.add(new AdvancedSearchResult(jsonArray.getJSONObject(i)));

                Collections.sort(items, new Comparator<Model>() {
                    @Override
                    public int compare(Model m1, Model m2) {
                        int result = ((AdvancedSearchResult) m2).getPoints() - ((AdvancedSearchResult) m1).getPoints();
                        if (result == 0) result = new Random().nextBoolean() ? 1 : -1;
                        return result;
                    }
                });

                return items;
            }
        }, params, "venue_advance_search");
    }

    public static void get(String[] venueIds, ApiListeners.OnItemsArrayLoadedListener listener) {
        Stub.get("GetVenuesList", listener, new Stub.ModelParser() {
            @Override
            ArrayList<Model> extractArray(String response) throws JSONException {
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                ArrayList<Model> items = new ArrayList<>();
                for (int i = 0; i < len; i++)
                    items.add(new VenueSimple(jsonArray.getJSONObject(i)));

                Collections.sort(items, new Comparator<Model>() {
                    @Override
                    public int compare(Model t1, Model t2) {
                        return ((VenueSimple) t1).getName().compareTo(((VenueSimple) t2).getName());
                    }
                });

                return items;
            }
        }, new RequestParams("venueIds", MiscUtils.concatArray(venueIds, ",")), "venue_get");
    }

    public static void postReview(String venueId, int service, int quality, int cleanliness, int ambiance, String userId, String review, ApiListeners.OnItemLoadedListener listener) {
        RequestParams params = new RequestParams();
        params.put("VenueId", venueId);
        params.put("Service", String.valueOf(service));
        params.put("Quality", String.valueOf(quality));
        params.put("Cleanliness", String.valueOf(cleanliness));
        params.put("Ambience", String.valueOf(ambiance));
        params.put("UserID", userId);
        params.put("Reviews", review);

        Stub.postJSON("PostReviews", listener, new Stub.ModelParser() {
            @Override
            Model parseItem(String response) throws JSONException {
                return new Text(response.substring(1, response.length() - 1));
            }
        }, params, "venue_post_review");
    }
}
