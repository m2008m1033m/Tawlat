package com.tawlat.services;

import com.loopj.android.http.RequestParams;
import com.tawlat.core.ApiListeners;
import com.tawlat.models.City;
import com.tawlat.models.Country;
import com.tawlat.models.Cuisine;
import com.tawlat.models.GoodFor;
import com.tawlat.models.Location;
import com.tawlat.models.Model;
import com.tawlat.models.Text;
import com.tawlat.models.search.KeywordSearchResult;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by mohammed on 3/21/16.
 */
public class SupplementaryApi {

    public static void countries(ApiListeners.OnItemsArrayLoadedListener listener) {

        Stub.get("GetAllCountries", listener, new Stub.ModelParser() {
            @Override
            ArrayList<Model> extractArray(String response) throws JSONException {
                ArrayList<Model> items = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                for (int i = 0; i < len; i++)
                    items.add(new Country(jsonArray.getJSONObject(i)));
                return items;
            }
        }, new RequestParams(), "supplementary_countries");
    }

    public static void cities(String countryId, ApiListeners.OnItemsArrayLoadedListener listener) {
        Stub.get("GetCitiesByCountryId", listener, new Stub.ModelParser() {
            @Override
            ArrayList<Model> extractArray(String response) throws JSONException {
                ArrayList<Model> items = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                for (int i = 0; i < len; i++)
                    items.add(new City(jsonArray.getJSONObject(i)));
                return items;
            }
        }, new RequestParams("countryId", countryId), "supplementary_cities");
    }

    public static void countryCode(String countryId, ApiListeners.OnItemLoadedListener listener) {
        Stub.get("GetCountryCodeByCountryId", listener, new Stub.ModelParser() {
            @Override
            Model parseItem(String response) throws JSONException {
                return new Text(response);
            }
        }, new RequestParams("countryId", countryId), "supplementary_country_code");
    }

    public static void locations(String cityId, ApiListeners.OnItemsArrayLoadedListener listener) {
        Stub.get("GetAllLocations", listener, new Stub.ModelParser() {
            @Override
            ArrayList<Model> extractArray(String response) throws JSONException {
                ArrayList<Model> items = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                for (int i = 0; i < len; i++)
                    items.add(new Location(jsonArray.getJSONObject(i)));
                return items;
            }
        }, new RequestParams("cityId", cityId), "supplementary_locations");
    }

    public static void goodFor(String cityId, ApiListeners.OnItemsArrayLoadedListener listener) {
        Stub.get("GetAllGoodFor", listener, new Stub.ModelParser() {
            @Override
            ArrayList<Model> extractArray(String response) throws JSONException {
                ArrayList<Model> items = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                for (int i = 0; i < len; i++)
                    items.add(new GoodFor(jsonArray.getJSONObject(i)));
                return items;
            }
        }, new RequestParams("cityId", cityId), "supplementary_good_for");
    }

    public static void cuisines(String cityId, ApiListeners.OnItemsArrayLoadedListener listener) {
        Stub.get("GetAllCuisines", listener, new Stub.ModelParser() {
            @Override
            ArrayList<Model> extractArray(String response) throws JSONException {
                ArrayList<Model> items = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                for (int i = 0; i < len; i++)
                    items.add(new Cuisine(jsonArray.getJSONObject(i)));
                return items;
            }
        }, new RequestParams("cityId", cityId), "supplementary_cuisines");
    }

    public static void keywordSearch(final String keyword, String cityId, ApiListeners.OnItemsArrayLoadedListener listener) {
        RequestParams params = new RequestParams();
        params.put("param", keyword);
        params.put("cityId", cityId);
        Stub.get("GetMatchingSearch", listener, new Stub.ModelParser() {
            @Override
            ArrayList<Model> extractArray(String response) throws JSONException {
                ArrayList<Model> items = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(response);
                int len = jsonArray.length();
                for (int i = 0; i < len; i++)
                    items.add(new KeywordSearchResult(jsonArray.getJSONObject(i), keyword));
                return items;
            }
        }, params, "supplementary_keyword_search");
    }
}
