package com.tawlat.dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.squareup.picasso.Picasso;
import com.tawlat.R;
import com.tawlat.core.ApiListeners;
import com.tawlat.core.User;
import com.tawlat.models.Model;
import com.tawlat.models.Text;
import com.tawlat.models.UserModel;
import com.tawlat.models.venues.Venue;
import com.tawlat.services.Result;
import com.tawlat.services.UserApi;
import com.tawlat.services.VenueApi;
import com.tawlat.utils.BitmapTransform;
import com.tawlat.utils.Notifications;


public class RatingDialog extends DialogFragment {

    private EditText mReview;
    private RatingBar mQuality;
    private RatingBar mService;
    private RatingBar mAmbiance;
    private RatingBar mCleanness;
    private Button mSubmitButton;

    private UserModel mUser;
    private Venue mVenue;

    public void setVenue(Venue venue) {
        mVenue = venue;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog d = super.onCreateDialog(savedInstanceState);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.rating_dialog);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final int size = (int) Math.ceil(Math.sqrt(250 * 250));
        /**
         * load the user photo
         */
        if (mUser == null) {
            UserApi.get(User.getInstance().getUser().getId(), new ApiListeners.OnItemLoadedListener() {
                @Override
                public void onLoaded(Result result, @Nullable Model item) {
                    if (!result.isSucceeded() || item == null) return;
                    mUser = ((UserModel) item);
                    Picasso.with(getActivity())
                            .load(mUser.getImage())
                            .placeholder(R.drawable.no_avatar)
                            .transform(new BitmapTransform(250, 250))
                            .resize(size, size)
                            .into((ImageView) d.findViewById(R.id.user_photo));

                }
            });

        }

        mReview = (EditText) d.findViewById(R.id.rd_review);
        mQuality = (RatingBar) d.findViewById(R.id.quality_rating);
        mService = (RatingBar) d.findViewById(R.id.service_rating);
        mAmbiance = (RatingBar) d.findViewById(R.id.ambience_rating);
        mCleanness = (RatingBar) d.findViewById(R.id.cleanliness_rating);
        mSubmitButton = (Button) d.findViewById(R.id.submit_button);

        mReview.setText("");
        mQuality.setRating(0.0f);
        mService.setRating(0.0f);
        mAmbiance.setRating(0.0f);
        mCleanness.setRating(0.0f);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSubmitButton.setEnabled(false);
                VenueApi.postReview(mVenue.getId(),
                        ((int) mService.getRating()),
                        ((int) mQuality.getRating()),
                        ((int) mCleanness.getRating()),
                        ((int) mAmbiance.getRating()),
                        User.getInstance().getUser().getId(),
                        mReview.getText().toString(),
                        new ApiListeners.OnItemLoadedListener() {
                            @Override
                            public void onLoaded(Result result, @Nullable Model item) {
                                mSubmitButton.setEnabled(true);
                                dismiss();
                                if (result.isSucceeded() && item != null)
                                    Notifications.showSnackBar(getActivity(), ((Text) item).getText());
                                else
                                    Notifications.showSnackBar(getActivity(), result.getMessages().get(0));
                            }
                        });
            }
        });

        return d;
    }
}


