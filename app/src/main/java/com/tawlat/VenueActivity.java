package com.tawlat;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.tawlat.core.ApiListeners;
import com.tawlat.core.Broadcasting;
import com.tawlat.core.User;
import com.tawlat.customViews.CustomGridView;
import com.tawlat.dialogs.RatingDialog;
import com.tawlat.models.Location;
import com.tawlat.models.Model;
import com.tawlat.models.Review;
import com.tawlat.models.venues.FavoriteVenue;
import com.tawlat.models.venues.Venue;
import com.tawlat.services.Result;
import com.tawlat.services.SupplementaryApi;
import com.tawlat.services.UserApi;
import com.tawlat.services.VenueApi;
import com.tawlat.utils.MiscUtils;
import com.tawlat.utils.Notifications;
import com.tawlat.utils.PDFLoaderReader;
import com.tawlat.utils.TawlatUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class VenueActivity extends AppCompatActivity {

    public static final String VENUE_ID = "venue_id";
    private static final int LOGIN_REQUEST_CODE = 0;
    private static final int WRITE_PERMISSION_REQUEST_CODE = 1;

    private enum PendingOperation {
        OPEN_REVIEW_DIALOG,
        GO_TO_RESERVATION
    }

    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private LinearLayout mHeaderContainer;
    private TextView mLocation;
    private TextView mRating;
    private TextView mNumberOfRating;
    private ImageView mReviewIcon;
    private ImageView mCoverImage;

    private TextView mOpenToday;
    private TextView mOpenTodayTime;
    private TextView mPrice;
    private TextView mCuisines;
    private ImageView mLogo;

    private CustomGridView mGoodForGridView;
    private TextView mAddress;
    private TextView mPhone;
    private TextView mParkingInfo;

    private SupportMapFragment mSupportMapFragment;
    private FrameLayout mAboutContainer;
    private TextView mAbout;
    private LinearLayout mOpeningHours;
    private TextView mOpeningHours0;
    private TextView mOpeningHours1;
    private TextView mOpeningHours2;
    private TextView mOpeningHours3;
    private TextView mOpeningHours4;
    private TextView mOpeningHours5;
    private TextView mOpeningHours6;

    private FrameLayout mReviewContainer;
    private Button mAddReviewButton;
    private TextView mMoreReviews;
    private ImageView mFacebook;
    private ImageView mTwitter;
    private ImageView mInstagram;
    private ImageView mWebsite;
    private Button mBookButton;

    private FloatingActionsMenu mFABMenu;
    private FloatingActionButton mMenuButton;
    private FloatingActionButton mReviewButton;
    private FloatingActionButton mFavoriteButton;
    private FloatingActionButton mShareButton;

    private RatingDialog mRatingDialog;

    private ArrayList<Location> mLocations = new ArrayList<>();
    private Venue mVenue;
    private PendingOperation mPendingOperation = null;

    private BroadcastReceiver mBroadcastReceiver;

    private String mPendingMenuToRead = null;

    private int mAboutHeight;
    private boolean mIsFavored = false;
    private boolean mIsAboutExpanded = false;
    private boolean mIsOpeningHoursExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String venueId = getIntent().getStringExtra(VENUE_ID);
        if (venueId == null) finish();
        setupBroadcastReceiver();
        setContentView(R.layout.venue_activity);

        /**
         * set the toolbar as the actionbar
         */
        Toolbar toolbar = ((Toolbar) findViewById(R.id.toolbar));
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeAsUpIndicator(R.mipmap.back);
        }

        init(venueId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    private void setupBroadcastReceiver() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case Broadcasting.LOGIN:
                        refreshForLoginLogout();
                        break;
                    case Broadcasting.LOGOUT:
                        refreshForLoginLogout();
                        break;
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Broadcasting.LOGOUT);
        intentFilter.addAction(Broadcasting.LOGIN);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void init(String venueId) {
        initReferences();
        loadVenue(venueId);
    }

    private void initReferences() {
        mCollapsingToolbarLayout = ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar));
        mHeaderContainer = ((LinearLayout) findViewById(R.id.header_container));
        mLocation = ((TextView) findViewById(R.id.location));
        mRating = ((TextView) findViewById(R.id.rating));
        mNumberOfRating = ((TextView) findViewById(R.id.number_of_ratings));
        mCoverImage = ((ImageView) findViewById(R.id.photo));
        mReviewIcon = ((ImageView) findViewById(R.id.review_icon));
        mOpenToday = ((TextView) findViewById(R.id.open_today));
        mOpenTodayTime = ((TextView) findViewById(R.id.open_today_time));
        mPrice = ((TextView) findViewById(R.id.price));
        mCuisines = ((TextView) findViewById(R.id.cuisines));
        mLogo = ((ImageView) findViewById(R.id.logo));
        mGoodForGridView = ((CustomGridView) findViewById(R.id.good_for));
        mAddress = ((TextView) findViewById(R.id.address));
        mPhone = ((TextView) findViewById(R.id.phone));
        mParkingInfo = ((TextView) findViewById(R.id.parking_info));
        mSupportMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        mAboutContainer = ((FrameLayout) findViewById(R.id.about_container));
        mAbout = ((TextView) findViewById(R.id.about));
        mOpeningHours = ((LinearLayout) findViewById(R.id.opening_hours));
        mOpeningHours0 = ((TextView) findViewById(R.id.opening_hours_0));
        mOpeningHours1 = ((TextView) findViewById(R.id.opening_hours_1));
        mOpeningHours2 = ((TextView) findViewById(R.id.opening_hours_2));
        mOpeningHours3 = ((TextView) findViewById(R.id.opening_hours_3));
        mOpeningHours4 = ((TextView) findViewById(R.id.opening_hours_4));
        mOpeningHours5 = ((TextView) findViewById(R.id.opening_hours_5));
        mOpeningHours6 = ((TextView) findViewById(R.id.opening_hours_6));
        mReviewContainer = ((FrameLayout) findViewById(R.id.review_container));
        mAddReviewButton = ((Button) findViewById(R.id.add_review_button));
        mMoreReviews = ((TextView) findViewById(R.id.more_reviews));
        mFacebook = ((ImageView) findViewById(R.id.facebook));
        mTwitter = ((ImageView) findViewById(R.id.twitter));
        mInstagram = ((ImageView) findViewById(R.id.instagram));
        mWebsite = ((ImageView) findViewById(R.id.website));
        mBookButton = ((Button) findViewById(R.id.book_button));
        mFABMenu = ((FloatingActionsMenu) findViewById(R.id.fab));
        mMenuButton = ((FloatingActionButton) findViewById(R.id.menu_fab));
        mReviewButton = ((FloatingActionButton) findViewById(R.id.review_fab));
        mFavoriteButton = ((FloatingActionButton) findViewById(R.id.favorite_fab));
        mShareButton = ((FloatingActionButton) findViewById(R.id.share_fab));

    }


    private void loadVenue(final String venueId) {
        final AlertDialog progressDialog = Notifications.showLoadingDialog(this, getString(R.string.loading));
        VenueApi.get(venueId, new ApiListeners.OnItemLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable Model item) {
                progressDialog.dismiss();
                if (result.isSucceeded() && item != null) {
                    mVenue = ((Venue) item);
                    loadLocations();
                } else {
                    Notifications.showYesNoDialog(VenueActivity.this, getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
        final AlertDialog progressDialog = Notifications.showLoadingDialog(this, getString(R.string.loading));
        SupplementaryApi.locations(mVenue.getCity(), new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                progressDialog.dismiss();
                if (result.isSucceeded() && items != null) {
                    for (Model item : items)
                        mLocations.add(((Location) item));
                    fillFields();
                } else {
                    Notifications.showYesNoDialog(VenueActivity.this, getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
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

    private void fillFields() {
        mBookButton.setVisibility(mVenue.isDirectory() ? View.GONE : View.VISIBLE);

        Glide.with(this)
                .load(mVenue.getCoverImage())
                .placeholder(R.drawable.logograyscale)
                .into(mCoverImage);

        /**
         * filling the header section
         */
        mCollapsingToolbarLayout.setTitle(mVenue.getName());
        mLocation.setText(TawlatUtils.getLocationName(mLocations, mVenue.getLocation()));
        mRating.setText(TawlatUtils.convertRatingToStars((int) mVenue.getGetAvgReviews()));
        mNumberOfRating.setText(getString(R.string.between_parantheses, String.valueOf(mVenue.getPublishedReviewsCount())));

        /**
         * filling the logo section
         */
        String todayDayOfWeekAsString = MiscUtils.getDayOfWeek(Calendar.getInstance().getTime());
        boolean openToday = mVenue.getOpeningHours().keySet().contains(todayDayOfWeekAsString);
        mOpenToday.setText(openToday ? getString(R.string.open_today) : getString(R.string.closed_today));
        if (openToday) {
            mOpenTodayTime.setText(mVenue.getOpeningHours().get(todayDayOfWeekAsString));
        } else {
            mOpenTodayTime.setVisibility(View.GONE);
        }
        mPrice.setText(mVenue.getPriceRange());
        mCuisines.setText(MiscUtils.concatArray(mVenue.getCuisines().values().toArray(new String[mVenue.getCuisines().size()]), ", "));
        Picasso.with(this)
                .load(mVenue.getVenueLogo())
                .placeholder(R.drawable.venue_no_avatar)
                .into(mLogo);

        /**
         * from the logo section to the map
         */
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.good_for_item, mVenue.getGoodFor().values().toArray(new String[mVenue.getGoodFor().size()]));
        mGoodForGridView.setAdapter(adapter);
        mAddress.setText(mVenue.getAddress());
        if (mVenue.getPhone() != null && !mVenue.getPhone().isEmpty()) {
            String hexColor = String.format("#%06X", (0xFFFFFF & ContextCompat.getColor(this, R.color.orange)));
            String phone = getString(R.string.phone_colon);
            phone += " <u><b><font color=" + hexColor + ">" + mVenue.getPhone() + "</font></b></u>";
            mPhone.setText(Html.fromHtml(phone), TextView.BufferType.SPANNABLE);
        } else {
            mPhone.setVisibility(View.GONE);
        }
        mParkingInfo.setText(mVenue.getParkingDetails());


        /**
         * from the map to the reviews
         */
        mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                LatLng latLng = new LatLng(mVenue.getLatitude(), mVenue.getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                map.addMarker(new MarkerOptions().position(latLng));
                map.getUiSettings().setZoomControlsEnabled(false);
            }
        });

        initAboutExpandability();
        mAbout.setText(Html.fromHtml(mVenue.getDescription()));

        String[] openingHours = getOrganizedOpeningHours();
        mOpeningHours0.setText(openingHours[0]);
        mOpeningHours1.setText(openingHours[1]);
        mOpeningHours2.setText(openingHours[2]);
        mOpeningHours3.setText(openingHours[3]);
        mOpeningHours4.setText(openingHours[4]);
        mOpeningHours5.setText(openingHours[5]);
        mOpeningHours6.setText(openingHours[6]);
        initOpeningHoursExpandability();

        /**
         * from reviews to last
         */
        VenueApi.reviews(mVenue.getId(), new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (result.isSucceeded() && items != null && items.size() > 0) {
                    Review review = ((Review) items.get(items.size() - 1));
                    mReviewContainer.removeAllViews();
                    View view = getLayoutInflater().inflate(R.layout.review_item, mReviewContainer, false);
                    ImageView userPhoto = ((ImageView) view.findViewById(R.id.user_photo));
                    TextView userName = ((TextView) view.findViewById(R.id.user_name));
                    TextView rating = ((TextView) view.findViewById(R.id.rating));
                    TextView date = ((TextView) view.findViewById(R.id.date));
                    TextView reviewText = ((TextView) view.findViewById(R.id.review));

                    Picasso.with(VenueActivity.this)
                            .load(review.getUserImage())
                            .placeholder(R.drawable.no_avatar)
                            .into(userPhoto);

                    userName.setText(review.getUserName());
                    float average = (review.getAmbienceFeedBack() + review.getCleanlinessFeedBack() + review.getQualityFeedBack() + review.getServiceFeedBack()) / 4.0f;
                    rating.setText(TawlatUtils.convertRatingToStars((int) average));
                    date.setText(MiscUtils.getDurationFormatted(review.getCreatedDate()));
                    reviewText.setText(review.getReviewNotes());

                    mReviewContainer.addView(view);
                    mMoreReviews.setVisibility(View.VISIBLE);
                }
            }
        });


        if (mVenue.getFacebook() != null && !mVenue.getFacebook().isEmpty())
            mFacebook.setVisibility(View.VISIBLE);
        if (mVenue.getTwitter() != null && !mVenue.getTwitter().isEmpty())
            mTwitter.setVisibility(View.VISIBLE);
        if (mVenue.getInstagram() != null && !mVenue.getInstagram().isEmpty())
            mInstagram.setVisibility(View.VISIBLE);
        if (mVenue.getWebsite() != null && !mVenue.getWebsite().isEmpty())
            mWebsite.setVisibility(View.VISIBLE);

        /**
         * fill the favorite
         */
        fillFavorite();

        refreshForLoginLogout();
        initEvents();

    }

    private void refreshForLoginLogout() {
        boolean isLoggedIn = User.getInstance().isLoggedIn();
        mFavoriteButton.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
        fillFavorite();
    }

    private void initEvents() {
        mHeaderContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mVenue.getImages().size() == 0) return;
                Intent intent = new Intent(VenueActivity.this, GalleryActivity.class);
                intent.putExtra(GalleryActivity.VENUE_NAME, mVenue.getName());
                intent.putStringArrayListExtra(GalleryActivity.PHOTOS, mVenue.getImages());
                startActivity(intent);
            }
        });

        mReviewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReviewDialog();
            }
        });

        mAddReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReviewDialog();
            }
        });

        mMoreReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(VenueActivity.this, ReviewsActivity.class);
                i.putExtra(ReviewsActivity.VENUE_ID, mVenue.getId());
                i.putExtra(ReviewsActivity.VENUE_NAME, mVenue.getName());
                startActivity(i);
            }
        });

        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFABMenu.collapse();
                if (mVenue.getVenueMenues().size() == 0) {
                    Notifications.showSnackBar(VenueActivity.this, VenueActivity.this.getString(R.string.there_are_no_menus_for_this_venue));
                    return;
                }
                final String[] menuNames = new String[mVenue.getVenueMenues().size()];
                final String[] menuLinks = new String[mVenue.getVenueMenues().size()];
                int idx = 0;

                for (Pair<String, String> name : mVenue.getVenueMenues()) {
                    menuNames[idx] = name.first;
                    menuLinks[idx++] = name.second;
                }

                Notifications.showListDialog(VenueActivity.this, VenueActivity.this.getString(R.string.venue_menus), menuNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        loadMenu(menuLinks[i]);
                    }
                });
            }
        });

        mReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFABMenu.collapse();
                showReviewDialog();
            }
        });

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFABMenu.collapse();
                if (!User.getInstance().isLoggedIn()) return;
                if (mIsFavored) {
                    UserApi.unfavorite(User.getInstance().getUser().getId(), mVenue.getId(), new ApiListeners.OnActionExecutedListener() {
                        @Override
                        public void onExecuted(Result result) {
                            if (result.isSucceeded()) {
                                mIsFavored = false;
                                mFavoriteButton.setIcon(R.mipmap.ic_favorite_outline_grey600_18dp);
                            }
                        }
                    });
                } else {
                    UserApi.favorite(User.getInstance().getUser().getId(), mVenue.getId(), new ApiListeners.OnActionExecutedListener() {
                        @Override
                        public void onExecuted(Result result) {
                            if (result.isSucceeded()) {
                                mIsFavored = true;
                                mFavoriteButton.setIcon(R.mipmap.ic_favorite_grey600_18dp);
                            }
                        }
                    });
                }
            }
        });

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFABMenu.collapse();
                Intent sharingIntent = new Intent(
                        android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                final String shareBody = getString(R.string.share_venue_text, mVenue.getName());
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.s_on_tawlat, mVenue.getName()));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_with)));
            }
        });

        mPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Notifications.showAlertDialog(VenueActivity.this, "Price Guidelines", R.layout.price_info);
            }
        });

        mPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + mVenue.getPhone()));
                startActivity(i);
            }
        });

        mFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(mVenue.getFacebook()));
                startActivity(i);
            }
        });

        mTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(mVenue.getTwitter()));
                startActivity(i);
            }
        });

        mInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(mVenue.getInstagram()));
                startActivity(i);
            }
        });

        mWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(mVenue.getWebsite()));
                startActivity(i);
            }
        });

        mBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToReservationActivity();
            }
        });
    }

    private void goToReservationActivity() {
        /**
         * first check if the user is logged in:
         */
        if (!User.getInstance().isLoggedIn()) {
            mPendingOperation = PendingOperation.GO_TO_RESERVATION;
            startActivityForResult(new Intent(this, LoginActivity.class), LOGIN_REQUEST_CODE);
        } else {
            Intent i = new Intent(this, ReservationActivity.class);
            i.putExtra(ReservationActivity.VENUE_ID, mVenue.getId());
            startActivity(i);
        }
    }

    private void showReviewDialog() {
        if (!User.getInstance().isLoggedIn()) {
            startActivityForResult(new Intent(this, LoginActivity.class), LOGIN_REQUEST_CODE);
            mPendingOperation = PendingOperation.OPEN_REVIEW_DIALOG;
        } else {
            if (mRatingDialog == null) {
                mRatingDialog = new RatingDialog();
                mRatingDialog.setVenue(mVenue);
            }
            mRatingDialog.show(getSupportFragmentManager(), "rating_dialog");
        }
    }

    private void fillFavorite() {
        if (!User.getInstance().isLoggedIn()) return;
        UserApi.favorites(User.getInstance().getUser().getId(), new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                if (result.isSucceeded() && items != null) {
                    for (Model item : items) {
                        FavoriteVenue favoriteVenue = ((FavoriteVenue) item);
                        if (favoriteVenue.getId().equals(mVenue.getId())) {
                            mIsFavored = true;
                            mFavoriteButton.setIcon(R.mipmap.ic_favorite_grey600_18dp);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void loadMenu(String menuLink) {
        /**
         * first check if the write storage is granted:
         */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            mPendingMenuToRead = menuLink;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST_CODE);
        } else {
            PDFLoaderReader.downloadAndLoadPDF(menuLink, this);
            mPendingMenuToRead = null;
        }
    }

    private String[] getOrganizedOpeningHours() {
        HashMap<String, String> currentOpeningHours = mVenue.getOpeningHours();
        String[] result = new String[7];
        for (int i = 0; i < 7; i++) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, i);
            String day = MiscUtils.getDayOfWeek(c.getTime());
            String time = currentOpeningHours.containsKey(day) ? currentOpeningHours.get(day) : getString(R.string.closed_paranthesis);
            result[i] = day + " " + time;
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE && data != null) {
            if (data.getBooleanExtra(LoginActivity.SUCCESS, false) && mPendingOperation != null) {
                switch (mPendingOperation) {
                    case GO_TO_RESERVATION:
                        goToReservationActivity();
                        break;
                    case OPEN_REVIEW_DIALOG:
                        showReviewDialog();
                        break;
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && mPendingMenuToRead != null) {
                loadMenu(mPendingMenuToRead);
            } else {
                Notifications.showSnackBar(this, getString(R.string.you_need_to_grant_the_permission));
            }
        }

    }

    private void initAboutExpandability() {
        mAbout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int _60DP = TawlatUtils.convertDPtoPixel(60, getResources());
                mAboutContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                mAboutHeight = mAbout.getMeasuredHeight();// + _60DP;
                mAboutContainer.getLayoutParams().height = _60DP;

                /**
                 * create the listener that will change the height
                 * of the about container
                 */
                final ValueAnimator.AnimatorUpdateListener showingHidingAnimatorListener = new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mAboutContainer.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                        mAboutContainer.requestLayout();
                    }
                };

                /**
                 * the showing animator
                 */
                final ValueAnimator showingAnimator = ValueAnimator.ofInt(TawlatUtils.convertDPtoPixel(30 * 2, getResources()),
                        mAboutHeight);
                showingAnimator.addUpdateListener(showingHidingAnimatorListener);

                /**
                 * the hiding animator
                 */
                final ValueAnimator hidingAnimator = ValueAnimator.ofInt(mAboutHeight,
                        TawlatUtils.convertDPtoPixel(30 * 2, getResources()));
                hidingAnimator.addUpdateListener(showingHidingAnimatorListener);

                mAboutContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mIsAboutExpanded) {
                            mIsAboutExpanded = false;
                            hidingAnimator.start();
                        } else {
                            mIsAboutExpanded = true;
                            showingAnimator.start();
                        }
                    }
                });

                return true;
            }
        });


    }

    private void initOpeningHoursExpandability() {
        final ValueAnimator.AnimatorUpdateListener showingHidingAnimatorListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOpeningHours.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                mOpeningHours.requestLayout();
            }
        };

        int _30DP = TawlatUtils.convertDPtoPixel(30, getResources());
        final ValueAnimator showingAnimator = ValueAnimator.ofInt(_30DP * 2, _30DP * 8);
        showingAnimator.addUpdateListener(showingHidingAnimatorListener);

        final ValueAnimator hidingAnimator = ValueAnimator.ofInt(_30DP * 8, _30DP * 2);
        hidingAnimator.addUpdateListener(showingHidingAnimatorListener);

        mOpeningHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsOpeningHoursExpanded) {
                    mIsOpeningHoursExpanded = false;
                    hidingAnimator.start();
                } else {
                    mIsOpeningHoursExpanded = true;
                    showingAnimator.start();
                }
            }
        });

    }
}