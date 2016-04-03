package com.tawlat.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.tawlat.R;
import com.tawlat.core.ApiListeners;
import com.tawlat.dialogs.TimePointDialog;
import com.tawlat.models.Model;
import com.tawlat.models.venues.Venue;
import com.tawlat.services.Result;
import com.tawlat.services.VenueApi;
import com.tawlat.utils.Notifications;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReservationSection1Fragment extends Fragment {

    private View mView;
    private TextView mDate;
    private TextView mTime;
    private Button mAddPeople;
    private Button mRemovePeople;
    private Spinner mPeopleSpinner;
    private TextView mEarnings;

    private TimePointDialog mTimePointDialog;

    private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.US);
    private Venue mVenue; //should be filled by the creator in the onCreate();

    private int mPeopleNumber = 2;
    private int mTotalPoints = 0;
    private Date mSelectedTime;
    private Date mSelectedDate = Calendar.getInstance().getTime();


    public ReservationSection1Fragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.reservation_section_1, container, false);
        init();
        return mView;
    }

    public int getTotalPoints() {
        return mTotalPoints;
    }

    public Date getSelectedDateTime() {
        Calendar c = Calendar.getInstance();
        c.setTime(mSelectedTime);

        Calendar d = Calendar.getInstance();
        d.setTime(mSelectedDate);

        c.set(d.get(Calendar.YEAR), d.get(Calendar.MONTH), d.get(Calendar.DAY_OF_MONTH));

        return c.getTime();
    }

    public int getPeopleNumber() {
        return mPeopleNumber;
    }

    public String getErrors() {
        if (mSelectedDate == null)
            return getString(R.string.no_date_is_selected);
        if (mSelectedTime == null)
            return getString(R.string.no_time_selected);

        int error = mTimePointDialog.getErrors();
        if (error == -1)
            return null;
        return getString(error);
    }

    public void setVenue(Venue venue) {
        mVenue = venue;
    }

    public void setPeopleNumber(int peopleNumber) {
        mPeopleSpinner.setSelection(peopleNumber - 1);
    }

    private void init() {
        initReferences();
        fillFields();
        initEvents();
    }

    private void initReferences() {
        mDate = ((TextView) mView.findViewById(R.id.date));
        mTime = ((TextView) mView.findViewById(R.id.time));
        mEarnings = ((TextView) mView.findViewById(R.id.earnings));
        mAddPeople = ((Button) mView.findViewById(R.id.add));
        mRemovePeople = ((Button) mView.findViewById(R.id.subtract));
        mPeopleSpinner = ((Spinner) mView.findViewById(R.id.people));
    }

    private void fillFields() {
        /**
         * create the time dialog
         */
        mTimePointDialog = new TimePointDialog();
        dateUpdated(mSelectedDate);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.people_number_options));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPeopleSpinner.setAdapter(adapter);
        mPeopleSpinner.setSelection(1); // 2 people
        mEarnings.setText("+0");


    }

    private void initEvents() {
        /**
         * event for the selection of the date
         */
        mDate.setOnClickListener(new View.OnClickListener() {

            private DatePickerDialog mDatePickerDialog;

            @Override
            public void onClick(View view) {
                if (mDatePickerDialog == null) {
                    final Calendar c = Calendar.getInstance();
                    c.setTime(mSelectedDate);
                    mDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                            c.set(Calendar.YEAR, year);
                            c.set(Calendar.MONTH, monthOfYear);
                            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            dateUpdated(c.getTime());
                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

                    Calendar maxDate = Calendar.getInstance();
                    maxDate.add(Calendar.MONTH, 2);
                    mDatePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis() - 1000);
                    mDatePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

                }
                mDatePickerDialog.show();
            }
        });

        /**
         * event for the selection of time
         */
        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimePointDialog.show(getChildFragmentManager(), "time_point_dialog");
            }
        });

        /**
         * event for the add people
         */
        mAddPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPeopleNumber >= 20) return;
                mPeopleNumber++;
                mPeopleSpinner.setSelection(mPeopleNumber - 1); // spinner is zero-based and people number is not
            }
        });

        /**
         * event for the remove people
         */
        mRemovePeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPeopleNumber <= 1) return;
                mPeopleNumber--;
                mPeopleSpinner.setSelection(mPeopleNumber - 1); // spinner is zero-based and people number is not
            }
        });

        /**
         * event for people spinner
         */
        mPeopleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                peopleUpdated(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mTimePointDialog.setOnActionListener(new TimePointDialog.OnActionListener() {
            @Override
            public void onItemSelected(Date time) {
                timeUpdated(time);
            }
        });
    }

    private void loadTimes(final Date date) {
        if (mVenue == null) return;
        final AlertDialog progressDialog = Notifications.showLoadingDialog(getActivity(), getString(R.string.loading));
        VenueApi.timeSlots(mVenue.getId(), date, new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                progressDialog.dismiss();
                if (result.isSucceeded() && items != null) {
                    if (mTimePointDialog != null)
                        mTimePointDialog.setItems(date, items);
                    try {
                        timeUpdated(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse("1970-01-01 19:00:00"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    Notifications.showYesNoDialog(getActivity(), getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loadTimes(date);
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getActivity().finish();
                        }
                    });
                }
            }
        });
    }

    private void dateUpdated(Date date) {
        mSelectedDate = date;
        mDate.setText(mSimpleDateFormat.format(date));
        loadTimes(date);
    }

    private void timeUpdated(Date time) {
        if (mTimePointDialog == null) return;
        mSelectedTime = mTimePointDialog.setSelectedTime(time);
        if (mSelectedTime != null) {
            mTime.setText(new SimpleDateFormat("hh:mm a", Locale.US).format(mSelectedTime));
        } else {
            mTime.setText("N/A");
        }
        updatePoints();
    }

    private void peopleUpdated(int peopleNumber) {
        mTimePointDialog.setNumberOfPeople(peopleNumber);
        mPeopleNumber = peopleNumber;
        updatePoints();
    }

    private void updatePoints() {
        mTotalPoints = mTimePointDialog.getPoints();
        if (mTotalPoints != 0) mTotalPoints += ((mPeopleNumber - 1) * 50);
        mEarnings.setText(getString(R.string.plus_d, mTotalPoints));
    }

    public void setSelectedDate(Date selectedDate) {
        mSelectedDate = selectedDate;
    }
}
