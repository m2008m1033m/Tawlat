package com.tawlat;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tawlat.adapters.KeywordSearchItemsAdapter;
import com.tawlat.core.ApiListeners;
import com.tawlat.core.Broadcasting;
import com.tawlat.core.Communicator;
import com.tawlat.core.User;
import com.tawlat.customViews.CustomLinearLayoutManager;
import com.tawlat.fragments.HomeFragment;
import com.tawlat.fragments.MyAccountFragment;
import com.tawlat.fragments.MyBookingsFragment;
import com.tawlat.fragments.MyFavoritesFragment;
import com.tawlat.fragments.MyVouchersFragment;
import com.tawlat.fragments.NearMeFragment;
import com.tawlat.models.City;
import com.tawlat.models.Model;
import com.tawlat.models.UserModel;
import com.tawlat.models.search.KeywordSearchResult;
import com.tawlat.models.serializables.AvailabilitySearchRequest;
import com.tawlat.services.Result;
import com.tawlat.services.SupplementaryApi;
import com.tawlat.services.UserApi;
import com.tawlat.utils.MiscUtils;
import com.tawlat.utils.Notifications;
import com.tawlat.utils.TawlatUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;


public class HomeActivity extends AppCompatActivity {

    enum Screen {
        HOME,
        NEAR_ME,
        MY_BOOKINGS,
        MY_VOUCHERS,
        MY_ACCOUNT,
        MY_FAVORITES
    }

    private HomeFragment mHomeFragment;
    private NearMeFragment mNearByFragment;
    private MyAccountFragment mMyAccountFragment;
    private MyBookingsFragment mMyBookingsFragment;
    private MyVouchersFragment mMyVouchersFragment;
    private MyFavoritesFragment mMyFavoritesFragment;

    private DrawerLayout mWrapper;
    private NavigationView mNavigationView;
    private LinearLayout mSearchSection;
    private EditText mSearchText;
    private FrameLayout mSearchWrapper;
    private RecyclerView mRecyclerView;
    private ProgressBar mSearchProgress;
    private CardView mAdvancedSearch;
    private CardView mSearchAllVenuesInCityCard;
    private TextView mSearchAllVenuesInCityText;

    private Button mRegisterButton;
    private ImageView mUserPhoto;
    private TextView mUserNameTextView;
    private TextView mCityTextView;
    private TextView mPointsTextView;
    private LinearLayout mLoggedOutContainer;
    private FrameLayout mLoggedInContainer;

    private AlertDialog mProgressDialog;
    private AlertDialog mCityDialog;
    private DialogInterface.OnClickListener mCityChangedListener;
    private Menu mMenu;

    private BroadcastReceiver mBroadcastReceiver;

    private int mCurrentlySelectedCityIndex;
    String[] mCitiesIds;
    String[] mCitiesNames;

    private Screen mCurrentlyDisplaying;
    private boolean mIsSearching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupBroadcastReceiver();
        setContentView(R.layout.home_activity);
        Toolbar toolbar = ((Toolbar) findViewById(R.id.toolbar));
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeAsUpIndicator(R.mipmap.ic_menu_white_24dp);
        }
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_activity_actions, menu);
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mWrapper.openDrawer(GravityCompat.START);
                return true;
            case R.id.location:
                if (mCitiesIds == null || mCitiesNames == null) return true;
                if (mCityChangedListener == null) {
                    mCityChangedListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mCurrentlySelectedCityIndex = i;
                            TawlatUtils.setCurrentCityId(mCitiesIds[i]);
                            Broadcasting.sendCityChanged(HomeActivity.this, mCitiesIds[i]);
                            if (mCityDialog != null) mCityDialog.dismiss();
                        }
                    };
                }
                mCityDialog = Notifications.showListWithRadioButton(this, getString(R.string.choose_city), mCitiesNames, mCurrentlySelectedCityIndex, mCityChangedListener);
                return true;
            case R.id.search:
                showSearch(true);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mProgressDialog != null && mCurrentlyDisplaying == Screen.HOME) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
            Communicator.getInstance().cancelAll();
            finish();
            return;
        } else if (mIsSearching) {
            showSearch(false);
            return;
        } else if (mCurrentlyDisplaying != Screen.HOME) {
            loadHomeFragment();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        showSearch(false);
        super.onPause();
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
                        refreshNavigationView();
                        mWrapper.openDrawer(GravityCompat.START);
                        break;
                    case Broadcasting.LOGOUT:
                        loadHomeFragment();
                        refreshNavigationView();
                        break;
                    case Broadcasting.CITY_CHANGED:
                        Communicator.getInstance().cancelByTag("venue_top_points");
                        Communicator.getInstance().cancelByTag("venue_recommended");
                        Communicator.getInstance().cancelByTag("venue_offers_and_events");
                        mHomeFragment.refreshAdapters();
                        /**
                         * The search all venues in {city}
                         */
                        mSearchAllVenuesInCityText.setText(getString(R.string.search_all_venues_in_city, mCitiesNames[mCurrentlySelectedCityIndex]));

                        /**
                         * update the selected item in the dialog
                         */
                        String cityId = intent.getStringExtra("cityId");
                        if (cityId == null) return;

                        mCurrentlySelectedCityIndex = 0;
                        for (int i = 0; i < mCitiesIds.length; i++)
                            if (mCitiesIds[i].equals(cityId)) {
                                mCurrentlySelectedCityIndex = i;
                                break;
                            }
                        break;
                    case Broadcasting.USER_UPDATED:
                        refreshUser();
                        break;
                }
            }

        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Broadcasting.LOGIN);
        intentFilter.addAction(Broadcasting.LOGOUT);
        intentFilter.addAction(Broadcasting.CITY_CHANGED);
        intentFilter.addAction(Broadcasting.USER_UPDATED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void init() {
        initReferences();
        fillFields();
        setupNavigationView();
        loadHomeFragment();

        initEvents();
    }

    private void initReferences() {
        mWrapper = ((DrawerLayout) findViewById(R.id.wrapper));
        mNavigationView = ((NavigationView) findViewById(R.id.navigation_view));
        mSearchSection = ((LinearLayout) findViewById(R.id.search_section));
        mSearchText = ((EditText) findViewById(R.id.search_text));
        mSearchWrapper = ((FrameLayout) findViewById(R.id.search_wrapper));
        mRecyclerView = ((RecyclerView) findViewById(R.id.recycler_view));
        mSearchProgress = ((ProgressBar) findViewById(R.id.progress));
        mSearchAllVenuesInCityText = ((TextView) findViewById(R.id.search_in_city_text_view));
        mSearchAllVenuesInCityCard = ((CardView) findViewById(R.id.show_all_venues_in_city));
        mAdvancedSearch = ((CardView) findViewById(R.id.card_view));

        mRegisterButton = ((Button) mNavigationView.getHeaderView(0).findViewById(R.id.register));
        mUserPhoto = ((ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.user_photo));
        mUserNameTextView = ((TextView) mNavigationView.getHeaderView(0).findViewById(R.id.user_name));
        mCityTextView = ((TextView) mNavigationView.getHeaderView(0).findViewById(R.id.city));
        mPointsTextView = ((TextView) mNavigationView.getHeaderView(0).findViewById(R.id.points));
        mLoggedOutContainer = ((LinearLayout) mNavigationView.getHeaderView(0).findViewById(R.id.logged_out_container));
        mLoggedInContainer = ((FrameLayout) mNavigationView.getHeaderView(0).findViewById(R.id.logged_in_container));
    }

    private void showSearch(boolean show) {
        if (show == mIsSearching) return;
        mIsSearching = show;
        // get the center for the clipping circle
        int cx = mSearchSection.getRight();
        int cy = (mSearchText.getTop() + mSearchText.getBottom()) / 2;

        // get the final radius for the clipping circle
        int dx = Math.max(cx, mSearchSection.getWidth() - cx);
        int dy = Math.max(cy, mSearchSection.getHeight() - cy);
        float finalRadius = (float) Math.hypot(dx, dy);

        SupportAnimator animator =
                ViewAnimationUtils.createCircularReveal(mSearchSection, cx, cy, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(250);
        if (show) {
            animator.start();
            mSearchWrapper.setVisibility(View.VISIBLE);
        } else {
            animator = animator.reverse();
            animator.setDuration(250);
            animator.addListener(new SupportAnimator.AnimatorListener() {
                @Override
                public void onAnimationStart() {

                }

                @Override
                public void onAnimationEnd() {
                    mSearchWrapper.setVisibility(View.INVISIBLE);
                    mSearchText.setText("");
                    if (mRecyclerView.getAdapter() != null) {
                        ((KeywordSearchItemsAdapter) mRecyclerView.getAdapter()).getItems().clear();
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                }

                @Override
                public void onAnimationCancel() {

                }

                @Override
                public void onAnimationRepeat() {

                }
            });
            animator.start();
        }
        showKeyboard(show, mSearchText);
    }

    private void showActions(boolean show) {
        if (mMenu == null) return;
        mMenu.getItem(0).setVisible(show);
        mMenu.getItem(1).setVisible(show);
    }

    private void fillFields() {
        /**
         * loading the cities:
         */
        loadCities();


        /**
         * setup the search recycler view
         */
        mRecyclerView.setLayoutManager(new CustomLinearLayoutManager(this));
        mRecyclerView.setAdapter(new KeywordSearchItemsAdapter(new KeywordSearchItemsAdapter.OnActionListener() {
            @Override
            public void onItemClicked(int position) {
                KeywordSearchResult result = ((KeywordSearchItemsAdapter) mRecyclerView.getAdapter()).getItems().get(position);
                Intent i;

                AvailabilitySearchRequest availabilitySearchRequest = new AvailabilitySearchRequest();
                availabilitySearchRequest.cid = "0";
                availabilitySearchRequest.cityId = TawlatUtils.getCurrentCityId();
                availabilitySearchRequest.date = Calendar.getInstance().getTime();
                availabilitySearchRequest.dow = MiscUtils.getDayOfWeek(Calendar.getInstance().getTime());
                availabilitySearchRequest.isAllCuisines = true;
                availabilitySearchRequest.isAllLocations = true;
                availabilitySearchRequest.lid = "0";
                availabilitySearchRequest.people = 2;
                availabilitySearchRequest.tod = "19:00:00";

                Bundle b = new Bundle();


                switch (result.getType()) {
                    case VENUE:
                    case DIRECTORY:
                        i = new Intent(HomeActivity.this, VenueActivity.class);
                        i.putExtra(VenueActivity.VENUE_ID, result.getId());
                        startActivity(i);
                        break;
                    case LOCATION:
                        i = new Intent(HomeActivity.this, SearchResultActivity.class);
                        i.putExtra(SearchResultActivity.TYPE, 0);

                        availabilitySearchRequest.isAllLocations = false;
                        availabilitySearchRequest.lid = result.getId();

                        b.putSerializable(SearchResultActivity.REQUEST, availabilitySearchRequest);
                        i.putExtras(b);

                        startActivity(i);
                        break;
                    case CUISINE:
                        i = new Intent(HomeActivity.this, SearchResultActivity.class);
                        i.putExtra(SearchResultActivity.TYPE, 0);

                        availabilitySearchRequest.cid = result.getId();
                        availabilitySearchRequest.isAllCuisines = false;

                        b.putSerializable(SearchResultActivity.REQUEST, availabilitySearchRequest);
                        i.putExtras(b);

                        startActivity(i);
                        break;
                }
            }
        }));

    }

    private void loadCities() {
        mProgressDialog = Notifications.showLoadingDialog(this, getString(R.string.loading));
        SupplementaryApi.cities("1", new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
                if (result.isSucceeded() && items != null) {

                    mCitiesIds = new String[items.size()];
                    mCitiesNames = new String[items.size()];
                    String currentlySelectedCityId = TawlatUtils.getCurrentCityId();

                    int idx = 0;
                    for (Model item : items) {
                        City city = ((City) item);
                        if (city.getId().equals(currentlySelectedCityId)) {
                            mCurrentlySelectedCityIndex = idx;
                        }
                        mCitiesIds[idx] = city.getId();
                        mCitiesNames[idx++] = city.getName();
                    }

                    refreshUser();

                    /**
                     * The search all venues in {city}
                     */
                    mSearchAllVenuesInCityText.setText(getString(R.string.search_all_venues_in_city, mCitiesNames[mCurrentlySelectedCityIndex]));

                } else {
                    Notifications.showYesNoDialog(HomeActivity.this, getString(R.string.error), result.getMessages().get(0), getString(R.string.retry), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loadCities();
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

    private void initEvents() {
        /**
         * event for the search text
         */
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Communicator.getInstance().cancelByTag("supplementary_keyword_search");

                String keyword = mSearchText.getText().toString().trim();
                if (keyword.length() < 2) {
                    if (mRecyclerView.getAdapter() != null) {
                        ((KeywordSearchItemsAdapter) mRecyclerView.getAdapter()).getItems().clear();
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                        mSearchProgress.setVisibility(View.GONE);
                    }
                    return;
                }
                mSearchProgress.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                SupplementaryApi.keywordSearch(keyword, TawlatUtils.getCurrentCityId(), new ApiListeners.OnItemsArrayLoadedListener() {
                    @Override
                    public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                        if (result.getCode() == null || !result.getCode().equals("0x02"))
                            mSearchProgress.setVisibility(View.GONE);

                        if (!result.isSucceeded() || items == null) return;
                        ArrayList<KeywordSearchResult> mAdapterItems = ((KeywordSearchItemsAdapter) mRecyclerView.getAdapter()).getItems();
                        mAdapterItems.clear();
                        for (Model item : items)
                            mAdapterItems.add(((KeywordSearchResult) item));
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        /**
         * event for the search wrapper
         */
        mSearchWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearch(false);
            }
        });

        /**
         * event for the adavanced search
         */
        mAdvancedSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, AdvancedSearchActivity.class));
            }
        });

        /**
         * event for the search all in city
         */
        mSearchAllVenuesInCityCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, SearchResultActivity.class);
                i.putExtra(SearchResultActivity.TYPE, 0);

                AvailabilitySearchRequest availabilitySearchRequest = new AvailabilitySearchRequest();
                availabilitySearchRequest.cid = "0";
                availabilitySearchRequest.cityId = TawlatUtils.getCurrentCityId();
                availabilitySearchRequest.date = Calendar.getInstance().getTime();
                availabilitySearchRequest.dow = MiscUtils.getDayOfWeek(Calendar.getInstance().getTime());
                availabilitySearchRequest.isAllCuisines = true;
                availabilitySearchRequest.isAllLocations = true;
                availabilitySearchRequest.lid = "0";
                availabilitySearchRequest.people = 2;
                availabilitySearchRequest.tod = "19:00:00";

                Bundle b = new Bundle();
                b.putSerializable(SearchResultActivity.REQUEST, availabilitySearchRequest);
                i.putExtras(b);

                startActivity(i);
            }
        });

        /**
         * event for the register button in the nav header
         */
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, RegisterActivity.class));
            }
        });
    }

    private void setupNavigationView() {
        refreshNavigationView();
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                mWrapper.closeDrawer(GravityCompat.START);
                switch (item.getItemId()) {
                    case R.id.home:
                        loadHomeFragment();
                        return true;
                    case R.id.near_me:
                        loadNearByFragment();
                        return true;
                    case R.id.login:
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        return true;
                    case R.id.logout:
                        User.getInstance().logout();
                        Broadcasting.sendLogout(HomeActivity.this);
                        return true;
                    case R.id.my_account:
                        loadMyAccountFragment();
                        return true;
                    case R.id.my_bookings:
                        loadMyBookingsFragment();
                        return true;
                    case R.id.my_vouchers:
                        loadMyVouchersFragments();
                        return true;
                    case R.id.my_favorites:
                        loadMyFavoritesFragment();
                        return true;
                    case R.id.settings:
                        startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                        return true;
                    case R.id.how_it_works:
                        startActivity(new Intent(HomeActivity.this, HowItWorksActivity.class));
                        return true;
                }
                return false;
            }
        });
    }

    private void refreshNavigationView() {
        mNavigationView.getMenu().clear();
        boolean loggedIn = User.getInstance().isLoggedIn();
        mNavigationView.inflateMenu(loggedIn ? R.menu.navigation_view_logged_in : R.menu.navigation_view_normal);
        mLoggedInContainer.setVisibility(loggedIn ? View.VISIBLE : View.GONE);
        mLoggedOutContainer.setVisibility(loggedIn ? View.GONE : View.VISIBLE);
        refreshUser();
    }

    private void refreshUser() {
        if (!User.getInstance().isLoggedIn()) return;
        UserApi.get(User.getInstance().getUser().getId(), new ApiListeners.OnItemLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable Model item) {
                if (result.isSucceeded() && item != null) {
                    UserModel user = ((UserModel) item);
                    String userName = user.getFirstName() + " " + user.getLastName();
                    String points = String.valueOf(user.getTotalPoint()) + " pts";
                    mUserNameTextView.setText(userName);
                    mPointsTextView.setText(points);

                    if (mCitiesNames != null)
                        mCityTextView.setText(mCitiesNames[TawlatUtils.getIdx(user.getCurrentCity(), mCitiesIds)]);

                    Picasso.with(HomeActivity.this)
                            .load(user.getImage())
                            .placeholder(R.drawable.no_avatar)
                            .into(mUserPhoto);

                } else
                    refreshUser();
            }
        });
    }

    private void loadHomeFragment() {
        if (mCurrentlyDisplaying == Screen.HOME) return;
        if (mHomeFragment == null) {
            mHomeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.container, mHomeFragment).commitAllowingStateLoss();
        }

        setTitle("");
        mNavigationView.getMenu().getItem(0).setChecked(true);
        showActions(true);
        mCurrentlyDisplaying = Screen.HOME;
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments == null) return;

        int numberOfLoadedFragments = fragments.size();
        if (numberOfLoadedFragments == 1) return;

        for (int i = numberOfLoadedFragments - 1; i > 0; i--) {
            Fragment fragmentToRemove = fragments.get(i);
            if (fragmentToRemove == null) continue;
            getSupportFragmentManager().beginTransaction().remove(fragmentToRemove).commitAllowingStateLoss();
        }

    }

    private void loadNearByFragment() {
        if (mCurrentlyDisplaying == Screen.NEAR_ME) return;
        if (mNearByFragment == null) {
            mNearByFragment = new NearMeFragment() {
                @Override
                public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
                    mNearByFragment.setOnActionListener(new OnActionListener() {
                        @Override
                        public void onLocationPermissionRejected() {
                            loadHomeFragment();
                            Notifications.showSnackBar(HomeActivity.this, getString(R.string.you_need_to_grant_location_permissions));
                        }

                        @Override
                        public void onLocationDisabled() {
                            Notifications.showSnackBar(HomeActivity.this, getString(R.string.you_need_to_enable_location));
                            loadHomeFragment();
                        }
                    });
                }
            };
        }
        setTitle(R.string.near_me);
        mNavigationView.getMenu().getItem(1).setChecked(true);
        showActions(false);
        mCurrentlyDisplaying = Screen.NEAR_ME;
        loadFragment(mNearByFragment);

    }

    private void loadMyAccountFragment() {
        if (mCurrentlyDisplaying == Screen.MY_ACCOUNT || !User.getInstance().isLoggedIn()) return;
        if (mMyAccountFragment == null) {
            mMyAccountFragment = new MyAccountFragment();
            mMyAccountFragment.setOnActionListener(new MyAccountFragment.OnActionListener() {
                @Override
                public void onCheckedOutReservationClicked() {
                    loadMyBookingsFragment();
                }
            });
        }
        setTitle(R.string.my_account);
        mNavigationView.getMenu().getItem(2).setChecked(true);
        showActions(false);
        mCurrentlyDisplaying = Screen.MY_ACCOUNT;
        loadFragment(mMyAccountFragment);

    }

    private void loadMyBookingsFragment() {
        if (mCurrentlyDisplaying == Screen.MY_BOOKINGS || !User.getInstance().isLoggedIn()) return;
        if (mMyBookingsFragment == null) {
            mMyBookingsFragment = new MyBookingsFragment();
        }
        setTitle(R.string.my_bookings);
        mNavigationView.getMenu().getItem(3).setChecked(true);
        showActions(false);
        mCurrentlyDisplaying = Screen.MY_BOOKINGS;
        loadFragment(mMyBookingsFragment);
    }

    private void loadMyVouchersFragments() {
        if (mCurrentlyDisplaying == Screen.MY_VOUCHERS || !User.getInstance().isLoggedIn()) return;
        if (mMyVouchersFragment == null) {
            mMyVouchersFragment = new MyVouchersFragment();
        }
        setTitle(R.string.my_vouchers);
        mNavigationView.getMenu().getItem(4).setChecked(true);
        showActions(false);
        mCurrentlyDisplaying = Screen.MY_VOUCHERS;
        loadFragment(mMyVouchersFragment);
    }

    private void loadMyFavoritesFragment() {
        if (mCurrentlyDisplaying == Screen.MY_FAVORITES || !User.getInstance().isLoggedIn()) return;
        if (mMyFavoritesFragment == null) {
            mMyFavoritesFragment = new MyFavoritesFragment();
        }
        setTitle(R.string.my_favorites);
        mNavigationView.getMenu().getItem(5).setChecked(true);
        showActions(false);
        mCurrentlyDisplaying = Screen.MY_FAVORITES;
        loadFragment(mMyFavoritesFragment);
    }

    private void loadFragment(Fragment fragment) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();

        Fragment fragmentToRemove = null;
        for (int i = fragments.size() - 1; i > 0; i--) {
            if (fragments.get(i) != null) {
                fragmentToRemove = fragments.get(i);
                break;
            }
        }

        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commitAllowingStateLoss();
        if (fragmentToRemove != null)
            getSupportFragmentManager().beginTransaction().remove(fragmentToRemove).commitAllowingStateLoss();

    }

    private void showKeyboard(boolean show, EditText editText) {

        if (show) {
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
        } else {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }
}
