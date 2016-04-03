package com.tawlat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.tawlat.core.ApiListeners;
import com.tawlat.core.User;
import com.tawlat.fragments.ReservationSection1Fragment;
import com.tawlat.fragments.ReservationSection2Fragment;
import com.tawlat.models.Country;
import com.tawlat.models.Model;
import com.tawlat.models.Reservation;
import com.tawlat.models.UserModel;
import com.tawlat.models.venues.Venue;
import com.tawlat.services.ReservationApi;
import com.tawlat.services.Result;
import com.tawlat.services.SupplementaryApi;
import com.tawlat.services.UserApi;
import com.tawlat.utils.Notifications;

import java.util.ArrayList;

public class EditReservationActivity extends AppCompatActivity {
    public static final String RESERVATION = "reservation";
    public static final String RESERVATION_RESULT = "reservation_code";
    public static final int RESERVATION_CODE = 1;


    private ReservationSection1Fragment mReservationSection1Fragment;
    private ReservationSection2Fragment mReservationSection2Fragment;

    private AlertDialog mLoadingDialog;

    private Reservation mReservation;
    private UserModel mUser;
    private ArrayList<Country> mCountries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_reservation_activity);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeAsUpIndicator(R.mipmap.back);
        }
        setTitle(R.string.edit_reservation);

        loadCountries();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupUpdateButton() {
        findViewById(R.id.update_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String error;

                error = mReservationSection1Fragment.getErrors();
                if (error != null) {
                    Notifications.showSnackBar(EditReservationActivity.this, error);
                    return;
                }

                error = mReservationSection2Fragment.getErrors();
                if (error != null) {
                    Notifications.showSnackBar(EditReservationActivity.this, error);
                    return;
                }

                // update the reservation
                final AlertDialog progressDialog = Notifications.showLoadingDialog(EditReservationActivity.this, getString(R.string.loading));
                ReservationApi.update(
                        mReservation.getId(),
                        mReservationSection2Fragment.getFirstName(),
                        mReservationSection2Fragment.getLastName(),
                        mReservation.getReservationCode(),
                        mReservationSection1Fragment.getSelectedDateTime(),
                        mReservationSection1Fragment.getPeopleNumber(),
                        mReservationSection2Fragment.getMobileCountry() + "-" + mReservationSection2Fragment.getMobileNumber(),
                        mReservationSection2Fragment.getSpecialRequests(),
                        mReservationSection1Fragment.getTotalPoints(),
                        new ApiListeners.OnActionExecutedListener() {
                            @Override
                            public void onExecuted(Result result) {
                                progressDialog.dismiss();
                                if (result.isSucceeded()) {
                                    mReservation.setCheckInDateTime(mReservationSection1Fragment.getSelectedDateTime());
                                    mReservation.setPeople(mReservationSection1Fragment.getPeopleNumber());
                                    mReservation.setPoints(mReservationSection1Fragment.getTotalPoints());

                                    Bundle b = new Bundle();
                                    b.putSerializable(RESERVATION, mReservation);
                                    Intent intent = new Intent();
                                    intent.putExtras(b);
                                    intent.putExtra(RESERVATION_RESULT, true);
                                    setResult(RESERVATION_CODE, intent);

                                    finish();
                                } else
                                    Notifications.showSnackBar(EditReservationActivity.this, result.getMessages().get(0));
                            }
                        }
                );
            }
        });
    }

    private void loadCountries() {
        if (mLoadingDialog == null)
            mLoadingDialog = Notifications.showLoadingDialog(this, getString(R.string.loading));
        SupplementaryApi.countries(new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (result.isSucceeded() && items != null) {

                    for (Model item : items)
                        mCountries.add(((Country) item));

                    loadUserAndReservation();
                } else {
                    mLoadingDialog.dismiss();
                    mLoadingDialog = null;

                    Notifications.showYesNoDialog(EditReservationActivity.this, getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loadCountries();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                }
            }
        });
    }

    private void loadUserAndReservation() {
        if (mLoadingDialog == null)
            mLoadingDialog = Notifications.showLoadingDialog(this, getString(R.string.loading));
        UserApi.get(User.getInstance().getUser().getId(), new ApiListeners.OnItemLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable Model item) {
                mLoadingDialog.dismiss();
                mLoadingDialog = null;

                if (result.isSucceeded() && item != null) {
                    mUser = ((UserModel) item);
                    Bundle b = getIntent().getExtras();
                    mReservation = ((Reservation) b.getSerializable(RESERVATION));
                    if (mReservation == null) {
                        finish();
                        return;
                    }

                    setupFragments();
                    setupUpdateButton();

                } else {
                    Notifications.showYesNoDialog(EditReservationActivity.this, getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loadUserAndReservation();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                }
            }
        });
    }

    private void setupFragments() {
        /**
         * section 1
         */
        mReservationSection1Fragment = new ReservationSection1Fragment() {
            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                Venue venue = new Venue();
                venue.setId(mReservation.getVenueId());
                venue.setName(mReservation.getVenueName());
                mReservationSection1Fragment.setVenue(venue);
                mReservationSection1Fragment.setSelectedDate(mReservation.getCheckInDateTime());
            }

            @Override
            public void onResume() {
                super.onResume();
                mReservationSection1Fragment.setPeopleNumber(mReservation.getPeople());
            }
        };
        getSupportFragmentManager().beginTransaction().replace(R.id.section1, mReservationSection1Fragment).commitAllowingStateLoss();

        /**
         * section 2
         */
        mReservationSection2Fragment = new ReservationSection2Fragment() {
            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                mReservationSection2Fragment.setCountries(mCountries);
                mReservationSection2Fragment.setMode(Mode.PARTIAL);
                mReservationSection2Fragment.setUser(mUser);
            }
        };
        getSupportFragmentManager().beginTransaction().replace(R.id.section2, mReservationSection2Fragment).commitAllowingStateLoss();
    }
}
