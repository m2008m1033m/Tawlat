package com.tawlat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tawlat.core.ApiListeners;
import com.tawlat.core.User;
import com.tawlat.fragments.ReservationSection1Fragment;
import com.tawlat.fragments.ReservationSection2Fragment;
import com.tawlat.fragments.ReservationSection3Fragment;
import com.tawlat.models.Country;
import com.tawlat.models.Location;
import com.tawlat.models.Model;
import com.tawlat.models.ReservationResult;
import com.tawlat.models.UserModel;
import com.tawlat.models.Voucher;
import com.tawlat.models.venues.Venue;
import com.tawlat.services.ReservationApi;
import com.tawlat.services.Result;
import com.tawlat.services.SupplementaryApi;
import com.tawlat.services.UserApi;
import com.tawlat.services.VenueApi;
import com.tawlat.utils.Notifications;
import com.tawlat.utils.TawlatUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ReservationActivity extends AppCompatActivity {

    public static final String VENUE_ID = "venue_id";
    public static final String OFFER_ID = "offerId";

    private TextView mVenueName;
    private TextView mLocation;
    private TextView mSectionName;
    private TextView mSection1;
    private TextView mSection2;
    private TextView mSection3;
    private Button mProceedButton;

    private ReservationSection1Fragment mReservationSection1Fragment;
    private ReservationSection2Fragment mReservationSection2Fragment;
    private ReservationSection3Fragment mReservationSection3Fragment;

    private AlertDialog mProgressDialog;

    private Venue mVenue;
    private String mOfferId;
    private ArrayList<Location> mLocations = new ArrayList<>();
    private ArrayList<Country> mCountries = new ArrayList<>();
    private ArrayList<Voucher> mVouchers = new ArrayList<>();
    private UserModel mUser;

    private int mCurrentSection = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String venueId = getIntent().getStringExtra(VENUE_ID);
        if (venueId == null || !User.getInstance().isLoggedIn()) {
            finish();
            return;
        }

        // getting the offer id:
        mOfferId = getIntent().getStringExtra(OFFER_ID);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeAsUpIndicator(R.mipmap.back);
        }
        setTitle(R.string.booking_details);

        setContentView(R.layout.reservation_activity);
        loadVenue(venueId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        initReferences();
        fillFields();
        initEvents();
    }

    private void initReferences() {
        mVenueName = ((TextView) findViewById(R.id.venue_name));
        mLocation = ((TextView) findViewById(R.id.location));
        mSectionName = ((TextView) findViewById(R.id.section_name));
        mSection1 = ((TextView) findViewById(R.id.section_1));
        mSection2 = ((TextView) findViewById(R.id.section_2));
        mSection3 = ((TextView) findViewById(R.id.section_3));
        mProceedButton = ((Button) findViewById(R.id.proceed_button));
    }

    private void fillFields() {
        mVenueName.setText(mVenue.getName());
        mLocation.setText(TawlatUtils.getLocationName(mLocations, mVenue.getLocation()));

        loadSection1();
    }

    private void loadSection1() {
        if (mReservationSection1Fragment == null) {
            mReservationSection1Fragment = new ReservationSection1Fragment() {
                @Override
                public void onCreate(@Nullable Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    mReservationSection1Fragment.setVenue(mVenue);
                }
            };
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container, mReservationSection1Fragment).commitAllowingStateLoss();
    }

    private void loadSection2() {
        if (mReservationSection2Fragment == null) {
            mReservationSection2Fragment = new ReservationSection2Fragment() {
                @Override
                public void onCreate(@Nullable Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    mReservationSection2Fragment.setUser(mUser);
                    mReservationSection2Fragment.setCountries(mCountries);
                    mReservationSection2Fragment.setVouchers(mVouchers);
                }
            };
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container, mReservationSection2Fragment).commitAllowingStateLoss();
    }

    private void loadSection3() {
        if (mProgressDialog != null) mProgressDialog.dismiss();
        mProgressDialog = Notifications.showLoadingDialog(this, getString(R.string.loading));
        ReservationApi.create(User.getInstance().getUser().getId(), mVenue.getId(),
                mReservationSection2Fragment.getFirstName() + " " + mReservationSection2Fragment.getLastName(),
                mUser.getEmail(),
                mReservationSection2Fragment.getMobileCountry() + "-" + mReservationSection2Fragment.getMobileNumber(),
                mReservationSection1Fragment.getPeopleNumber(),
                mReservationSection1Fragment.getSelectedDateTime(),
                mReservationSection1Fragment.getTotalPoints(),
                mReservationSection2Fragment.getSpecialRequests(),
                mReservationSection2Fragment.getType(),
                mReservationSection2Fragment.getAmountToRedeem(),
                mReservationSection2Fragment.getPointsToRedeem(),
                mReservationSection2Fragment.getVoucherId(),
                mReservationSection2Fragment.getVoucherNumber(),
                mOfferId,
                new ApiListeners.OnItemLoadedListener() {
                    @Override
                    public void onLoaded(Result result, @Nullable final Model item) {
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        if (result.isSucceeded() && item != null) {


                            mCurrentSection++;
                            /**
                             * change the circle ui
                             */
                            mSection2.setBackgroundResource(R.drawable.circle_outline);
                            mSection3.setBackgroundResource(R.drawable.circle);
                            mSection2.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.black));
                            mSection3.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.white));

                            /**
                             * change title
                             */
                            mSectionName.setText(R.string.booking_summary);

                            /**
                             * change button text
                             */
                            mProceedButton.setText(R.string.share);


                            if (mReservationSection3Fragment == null) {
                                mReservationSection3Fragment = new ReservationSection3Fragment() {
                                    @Override
                                    public void onCreate(@Nullable Bundle savedInstanceState) {
                                        super.onCreate(savedInstanceState);
                                        mReservationSection3Fragment.setVenue(mVenue);
                                        mReservationSection3Fragment.setDateTime(mReservationSection1Fragment.getSelectedDateTime());
                                        mReservationSection3Fragment.setDiscountAmount(mReservationSection2Fragment.getAmountToRedeem());
                                        mReservationSection3Fragment.setPeopleNumber(mReservationSection1Fragment.getPeopleNumber());
                                        mReservationSection3Fragment.setReservationResult(((ReservationResult) item));
                                        mReservationSection3Fragment.setTotalPoints(mReservationSection1Fragment.getTotalPoints());
                                    }
                                };
                            }
                            mCurrentSection++;
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, mReservationSection3Fragment).commitAllowingStateLoss();

                        } else
                            Notifications.showSnackBar(ReservationActivity.this, result.getMessages().get(0));
                    }
                }
        );
    }

    private void initEvents() {
        mProceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentSection == 0) {
                    String errors = mReservationSection1Fragment.getErrors();
                    if (errors != null) {
                        Notifications.showSnackBar(ReservationActivity.this, errors);
                    } else {
                        mCurrentSection++;
                        loadSection2();

                        /**
                         * change the circle ui
                         */
                        mSection1.setBackgroundResource(R.drawable.circle_outline);
                        mSection2.setBackgroundResource(R.drawable.circle);
                        mSection1.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.black));
                        mSection2.setTextColor(ContextCompat.getColor(ReservationActivity.this, R.color.white));

                        /**
                         * change title
                         */
                        mSectionName.setText(R.string.booking_details);
                    }
                } else if (mCurrentSection == 1) {
                    String error = mReservationSection2Fragment.getErrors();
                    if (error != null) {
                        Notifications.showSnackBar(ReservationActivity.this, error);
                    } else {
                        loadSection3();
                    }
                } else {
                    /**
                     * do the sharing
                     */
                    Intent sharingIntent = new Intent(
                            android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String title = getString(R.string.share_reservation_title, mVenue.getName());
                    String text = getString(R.string.share_reservation_text, mVenue.getName(), ((mReservationSection1Fragment.getPeopleNumber() == 1) ? getString(R.string.one_person) : getString(R.string.d_people, mReservationSection1Fragment.getPeopleNumber())), new SimpleDateFormat("dd/MM/yyyy @ hh:mm a", Locale.US).format(mReservationSection1Fragment.getSelectedDateTime()), mReservationSection1Fragment.getTotalPoints());
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
                    startActivity(Intent.createChooser(sharingIntent, "Share With"));
                }
            }
        });
    }

    /**
     * Loading methods
     */
    private void loadVenue(final String venueId) {
        if (mProgressDialog != null) mProgressDialog.dismiss();
        mProgressDialog = Notifications.showLoadingDialog(this, getString(R.string.loading));
        VenueApi.get(venueId, new ApiListeners.OnItemLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable Model item) {
                if (result.isSucceeded() && item != null) {
                    mVenue = ((Venue) item);
                    loadLocations();
                } else {
                    if (mProgressDialog != null) mProgressDialog.dismiss();
                    mProgressDialog = null;
                    Notifications.showYesNoDialog(ReservationActivity.this, getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loadVenue(venueId);
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

    private void loadLocations() {
        if (mProgressDialog == null)
            mProgressDialog = Notifications.showLoadingDialog(this, getString(R.string.loading));
        SupplementaryApi.locations(TawlatUtils.getCurrentCityId(), new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (result.isSucceeded() && items != null) {
                    for (Model item : items) {
                        mLocations.add(((Location) item));
                    }
                    loadCountries();
                } else {
                    if (mProgressDialog != null) mProgressDialog.dismiss();
                    mProgressDialog = null;
                    Notifications.showYesNoDialog(ReservationActivity.this, getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loadLocations();
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

    private void loadCountries() {
        if (mProgressDialog == null)
            mProgressDialog = Notifications.showLoadingDialog(this, getString(R.string.loading));
        SupplementaryApi.countries(new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (result.isSucceeded() && items != null) {
                    for (Model item : items) {
                        mCountries.add(((Country) item));
                    }
                    loadVouchers();
                } else {
                    if (mProgressDialog != null) mProgressDialog.dismiss();
                    mProgressDialog = null;
                    Notifications.showYesNoDialog(ReservationActivity.this, getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
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

    // should be called after the loadVenue()
    private void loadVouchers() {
        if (mProgressDialog == null)
            mProgressDialog = Notifications.showLoadingDialog(this, getString(R.string.loading));
        UserApi.pendingVouchers(User.getInstance().getUser().getId(), new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (result.isSucceeded() && items != null) {
                    for (Model item : items) {
                        Voucher voucher = ((Voucher) item);
                        if (!voucher.getRedeemableAt().contains(mVenue.getId())) continue;
                        mVouchers.add(((Voucher) item));
                    }
                    loadUser();
                } else {
                    if (mProgressDialog != null) mProgressDialog.dismiss();
                    mProgressDialog = null;
                    Notifications.showYesNoDialog(ReservationActivity.this, getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loadVouchers();
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

    private void loadUser() {
        if (mProgressDialog == null)
            mProgressDialog = Notifications.showLoadingDialog(this, getString(R.string.loading));
        UserApi.get(User.getInstance().getUser().getId(), new ApiListeners.OnItemLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable Model item) {
                if (mProgressDialog != null) mProgressDialog.dismiss();
                mProgressDialog = null;
                if (result.isSucceeded() && item != null) {
                    mUser = ((UserModel) item);
                    init();
                } else {
                    Notifications.showYesNoDialog(ReservationActivity.this, getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loadUser();
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

}
