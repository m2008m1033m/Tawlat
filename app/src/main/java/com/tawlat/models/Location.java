package com.tawlat.models;

import com.tawlat.utils.MiscUtils;

import org.json.JSONObject;

/**
 * Created by mohammed on 3/21/16.
 */
public class Location extends Model {

    private String mName;
    private String mCityId;

    public Location(JSONObject jsonObject) {
        setId(MiscUtils.getString(jsonObject, "LocationId", ""));
        setName(MiscUtils.getString(jsonObject, "Name", ""));
        setCityId(MiscUtils.getString(jsonObject, "CityId", ""));
    }

    @Override
    public void copyFrom(Model model) {
        if (!(model instanceof Location)) return;
        setId(model.getId());
        setName(((Location) model).getName());
        setCityId(((Location) model).getCityId());
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getCityId() {
        return mCityId;
    }

    public void setCityId(String cityId) {
        mCityId = cityId;
    }
}
