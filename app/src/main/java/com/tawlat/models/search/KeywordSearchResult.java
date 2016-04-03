package com.tawlat.models.search;

import android.support.v4.content.ContextCompat;

import com.tawlat.R;
import com.tawlat.TawlatApplication;
import com.tawlat.models.Model;
import com.tawlat.utils.MiscUtils;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeywordSearchResult extends Model {

    public enum Type {
        VENUE,
        CUISINE,
        LOCATION,
        DIRECTORY
    }

    private String mName;
    private Type mType;
    private boolean mIsPartner;

    public KeywordSearchResult(JSONObject jsonObject, String keyword) {
        setId(MiscUtils.getString(jsonObject, "Id", ""));
        setName(MiscUtils.getString(jsonObject, "Name", ""));
        setType(MiscUtils.getString(jsonObject, "Type", ""));


        /**
         * color the keyword
         */
        String hexColor = String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(TawlatApplication.getContext(), R.color.colorAccent)));
        String name = getName();

        Pattern p = Pattern.compile("((?i)" + keyword + ")");
        Matcher m = p.matcher(name);
        while (m.find()) {
            String group = m.group();
            name = name.replaceAll(group, "<font color=\"" + hexColor + "\"><b>" + group + "</b></font>");
        }
        setName(name);


    }

    @Override
    public void copyFrom(Model model) {
        if (!(model instanceof KeywordSearchResult)) return;
        setId(model.getId());
        setName(((KeywordSearchResult) model).getName());
        setType(((KeywordSearchResult) model).getType());
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mIsPartner = name.contains("[Tawlat Partner]");
        name = name.replace("[Tawlat Partner]", "");
        name = name.trim();
        mName = name;
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    public void setType(String type) {
        switch (type) {
            case "Venue":
                mType = Type.VENUE;
                break;

            case "Location":
                mType = Type.LOCATION;
                break;

            case "Cuisine":
                mType = Type.CUISINE;
                break;

            case "Directory":
                mType = Type.DIRECTORY;
                break;
        }
    }

    public String getTypeName() {
        switch (mType) {
            case VENUE:
                return TawlatApplication.getContext().getString(R.string.venue);
            case CUISINE:
                return TawlatApplication.getContext().getString(R.string.cuisine);
            case DIRECTORY:
                return TawlatApplication.getContext().getString(R.string.directory);
            case LOCATION:
                return TawlatApplication.getContext().getString(R.string.location);
        }

        return "";
    }

    public boolean isPartner() {
        return mIsPartner;
    }
}
