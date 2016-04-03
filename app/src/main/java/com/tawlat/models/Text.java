package com.tawlat.models;

/**
 * Created by mohammed on 3/21/16.
 */
public class Text extends Model {

    private String mText;

    public Text(String text) {
        mText = text;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    @Override
    public void copyFrom(Model model) {
        if (!(model instanceof Text)) return;
        setText(((Text) model).getText());
    }
}
