package com.tawlat.models;

/**
 * Created by mohammed on 3/21/16.
 */
public class Number extends Model {

    private double mNumber;

    public Number(String number) {
        try {
            mNumber = Double.parseDouble(number);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            mNumber = 0;
        }
    }

    @Override
    public void copyFrom(Model model) {
        if (!(model instanceof Number)) return;
        setNumber(((Number) model).getNumber());
    }

    public double getNumber() {
        return mNumber;
    }

    public void setNumber(double number) {
        mNumber = number;
    }
}
