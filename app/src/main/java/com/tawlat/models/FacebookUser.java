package com.tawlat.models;

import com.tawlat.utils.MiscUtils;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FacebookUser extends Model {

    private String mFirstName;
    private String mLastName;
    private String mGender;
    private String mEmail;
    private Date mDateOfBirth;


    public FacebookUser(JSONObject jsonObject) {

        String firstLastName = MiscUtils.getString(jsonObject, "name", "");
        String firstName = "";
        String lastName = "";
        if (!firstLastName.isEmpty()) {
            String[] bits = firstLastName.split(" ");
            if (bits.length == 2) {
                firstName = bits[0];
                lastName = bits[1];
            }
        }

        setId(MiscUtils.getString(jsonObject, "id", ""));
        setFirstName(firstName);
        setLastName(lastName);
        setGender(MiscUtils.getUTF8String(jsonObject, "gender", ""));
        setEmail(MiscUtils.getUTF8String(jsonObject, "email", ""));
        setDateOfBirth(MiscUtils.getString(jsonObject, "birthday", ""));

    }

    @Override
    public void copyFrom(Model model) {
        if (!(model instanceof FacebookUser)) return;
        setId(model.getId());
        setFirstName(((FacebookUser) model).getFirstName());
        setLastName(((FacebookUser) model).getLastName());
        setGender(((FacebookUser) model).getGender());
        setEmail(((FacebookUser) model).getEmail());
        setDateOfBirth(((FacebookUser) model).getDateOfBirth());
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getGender() {
        return mGender;
    }

    public void setGender(String gender) {
        mGender = gender;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public Date getDateOfBirth() {
        return mDateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        mDateOfBirth = dateOfBirth;
    }

    public String getDateOfBirth(String format) {
        if (mDateOfBirth == null) return "";
        return new SimpleDateFormat(format, Locale.US).format(mDateOfBirth);
    }

    public void setDateOfBirth(String dateOfBirth) {
        try {
            mDateOfBirth = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(dateOfBirth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
