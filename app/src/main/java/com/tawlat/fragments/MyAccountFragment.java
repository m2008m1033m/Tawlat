package com.tawlat.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tawlat.EditProfileActivity;
import com.tawlat.R;
import com.tawlat.core.ApiListeners;
import com.tawlat.core.User;
import com.tawlat.models.Model;
import com.tawlat.models.UserModel;
import com.tawlat.services.Result;
import com.tawlat.services.UserApi;
import com.tawlat.utils.Notifications;

public class MyAccountFragment extends Fragment {

    private final static int EDIT_PROFILE_RESULT_CODE = 0;

    public interface OnActionListener {
        void onCheckedOutReservationClicked();
    }

    private View mView;
    private ImageView mPhoto;
    private TextView mBalance;
    private TextView mPoints;
    private TextView mReservations;
    private Button mEditButton;
    private CardView mCheckedOutReservationsContainer;

    private OnActionListener mOnActionListener;

    public MyAccountFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.my_account_fragment, container, false);
        init();
        return mView;
    }

    private void init() {
        initReferences();
        fillFields();
        initEvents();
    }

    private void initReferences() {
        mPhoto = ((ImageView) mView.findViewById(R.id.user_photo));
        mBalance = ((TextView) mView.findViewById(R.id.balance));
        mPoints = ((TextView) mView.findViewById(R.id.points));
        mReservations = ((TextView) mView.findViewById(R.id.reservations));
        mEditButton = ((Button) mView.findViewById(R.id.edit_button));
        mCheckedOutReservationsContainer = ((CardView) mView.findViewById(R.id.upcoming_bookings_container));
    }

    private void fillFields() {
        final AlertDialog progressDialog = Notifications.showLoadingDialog(getActivity(), getString(R.string.loading));
        UserApi.get(User.getInstance().getUser().getId(), new ApiListeners.OnItemLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable Model item) {
                progressDialog.dismiss();
                if (result.isSucceeded() && item != null) {

                    UserModel user = ((UserModel) item);

                    if (user.getImage().isEmpty())
                        Picasso.with(getActivity())
                                .load(R.drawable.no_avatar)
                                .into(mPhoto);
                    else
                        Picasso.with(getActivity())
                                .load(user.getImage())
                                .placeholder(R.drawable.no_avatar)
                                .into(mPhoto);

                    mBalance.setText(user.getTotalPoint()/20 + " AED");
                    mPoints.setText(String.valueOf(user.getTotalPoint()));
                    mReservations.setText(String.valueOf(user.getTotalCheckedOutReservations()));
                } else {
                    Notifications.showSnackBar(getActivity(), result.getMessages().get(0));
                }
            }
        });
    }

    private void initEvents() {
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), EditProfileActivity.class), EDIT_PROFILE_RESULT_CODE);
            }
        });

        mCheckedOutReservationsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnActionListener != null)
                    mOnActionListener.onCheckedOutReservationClicked();
            }
        });
    }

    public void setOnActionListener(OnActionListener onActionListener) {
        mOnActionListener = onActionListener;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == EDIT_PROFILE_RESULT_CODE && data != null) {
            boolean isUpdated = data.getBooleanExtra(EditProfileActivity.EDIT_PROFILE_RESULT, false);
            if (isUpdated)
                fillFields();
        }
    }
}
