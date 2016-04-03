package com.tawlat.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.tawlat.R;
import com.tawlat.adapters.TimePointsAdapter;
import com.tawlat.customViews.CustomGridLayoutManager;
import com.tawlat.models.Model;
import com.tawlat.models.TimeSlot;
import com.tawlat.utils.Notifications;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimePointDialog extends DialogFragment {

    public interface OnActionListener {
        void onItemSelected(Date time);
    }

    private RecyclerView mRecyclerView;
    private TimePointsAdapter mAdapter;
    private Dialog mDialog;
    private TextView mNoTimeSlotsMessage;
    private OnActionListener mOnActionListener;
    private int mNumberOfPeople = 2;


    public TimePointDialog() {
        mAdapter = new TimePointsAdapter(new TimePointsAdapter.OnActionListener() {
            @Override
            public void onSelected(int position) {
                if (mOnActionListener != null)
                    mOnActionListener.onItemSelected(mAdapter.getItems().get(position).getStartTime());
                dismiss();
            }

            @Override
            public void onUnavailableSelected(int position) {
                Notifications.showSnackBar(getActivity(), getContext().getString(R.string.no_seats_avaiable_at_the_selected_time));
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDialog = super.onCreateDialog(savedInstanceState);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.time_point_dialog);

        initRecyclerView();
        mNoTimeSlotsMessage = (TextView) mDialog.findViewById(R.id.no_slots_message);

        if (mAdapter.getItemCount() == 0) {
            showNoTimeSlotsMessage(true);
        } else {
            showNoTimeSlotsMessage(false);
        }

        return mDialog;
    }

    public void setOnActionListener(OnActionListener onActionListener) {
        mOnActionListener = onActionListener;
    }

    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) mDialog.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new CustomGridLayoutManager(mDialog.getContext(), 3));
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setNumberOfPeople(int value) {
        mNumberOfPeople = value;
        mAdapter.setNumberOfPeople(value);
        mAdapter.notifyDataSetChanged();
    }

    public void showNoTimeSlotsMessage(boolean show) {
        mNoTimeSlotsMessage.setVisibility((show) ? View.VISIBLE : View.GONE);
        mRecyclerView.setVisibility((show) ? View.GONE : View.VISIBLE);
    }

    public void setItems(Date date, ArrayList<Model> items) {
        mAdapter.getItems().clear();

        // stores the current time plus 30 minutes
        Calendar currentTime = Calendar.getInstance();
        currentTime.add(Calendar.MINUTE, 30);

        // this will be used later in the loop to hold the date + time of the slot (we need the date as well for comparison)
        Calendar timeSlotDate = Calendar.getInstance();

        // wraps the selected date in a calender object to access year, month, day (used to set the date of the time slot)
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.setTime(date);


        for (Model item : items) {
            // 1- set the time as the time for the slot
            timeSlotDate.setTime(((TimeSlot) item).getStartTime());

            // 2- set the date of this slot based on the selected date (this is for comparison)
            timeSlotDate.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH));

            if (currentTime.after(timeSlotDate))
                continue;
            mAdapter.getItems().add(((TimeSlot) item));
        }
    }

    public Date setSelectedTime(Date time) {
        if (mAdapter.getItemCount() <= 0) return null;

        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            if (mAdapter.getItems().get(i).getStartTime("HH:mm").equals(new SimpleDateFormat("HH:mm", Locale.US).format(time))) {
                setSelectedTime(i);
                return mAdapter.getItems().get(i).getStartTime();
            }
        }

        /**
         * in case the time not found select the first one
         */
        setSelectedTime(0);
        return mAdapter.getItems().get(0).getStartTime();
    }

    private void setSelectedTime(int position) {
        mAdapter.setSelectedItem(position);
        mAdapter.notifyDataSetChanged();
    }

    public int getPoints() {
        if (mAdapter.getSelectedItem() == -1)
            return 0;
        else
            return mAdapter.getItems().get(mAdapter.getSelectedItem()).getPoints();
    }

    public int getErrors() {
        int selectedItem = mAdapter.getSelectedItem();
        if (selectedItem == -1)
            return R.string.no_time_selected;

        if (mAdapter.getItems().get(selectedItem).getSeatQuota() < mNumberOfPeople)
            return R.string.no_seats_avaiable_at_the_selected_time;

        return -1;
    }
}
