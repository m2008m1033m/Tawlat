package com.tawlat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.tawlat.adapters.SearchResultAdapter;
import com.tawlat.core.Communicator;
import com.tawlat.core.User;
import com.tawlat.fragments.RefreshRecyclerViewFragment;
import com.tawlat.models.search.AdvancedSearchResult;
import com.tawlat.models.serializables.AdvancedSearchRequest;
import com.tawlat.models.serializables.AvailabilitySearchRequest;
import com.tawlat.services.VenueApi;

public class SearchResultActivity extends AppCompatActivity {

    public static final String REQUEST = "request";
    public static final String TYPE = "type"; //0 -> availability, 1 -> advanced search
    private static final int LOGIN_REQUEST_CODE = 0;

    private RefreshRecyclerViewFragment mRefreshRecyclerViewFragment;
    private SearchResultAdapter mAdapter;
    private String mPendingVenueId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result_activity);

        final int type = getIntent().getIntExtra(TYPE, -1);
        if (type == -1) {
            finish();
            return;
        }
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeAsUpIndicator(R.mipmap.back);
        }
        setTitle(getString(R.string.searching));

        setupRefreshRecyclerView(type);
    }

    @Override
    protected void onPause() {
        Communicator.getInstance().cancelByTag("venue_advance_search");
        Communicator.getInstance().cancelByTag("venue_availability_search");
        super.onPause();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE && data != null) {
            if (data.getBooleanExtra(LoginActivity.SUCCESS, false) && mPendingVenueId != null) {
                goToReservationActivity(mPendingVenueId);
            }
        }
    }

    private void setupRefreshRecyclerView(final int type) {
        mRefreshRecyclerViewFragment = ((RefreshRecyclerViewFragment) getSupportFragmentManager().findFragmentById(R.id.refresh_recycler_view_fragment));
        mAdapter = new SearchResultAdapter(new SearchResultAdapter.OnActionListener() {
            @Override
            public void onItemClicked(int position) {
                AdvancedSearchResult advancedSearchResult = mAdapter.getItems().get(position);
                String venueId = advancedSearchResult.getVenueId();
                Intent i = new Intent(SearchResultActivity.this, VenueActivity.class);
                i.putExtra(VenueActivity.VENUE_ID, venueId);
                startActivity(i);
            }

            @Override
            public void onButtonClicked(int position) {
                AdvancedSearchResult advancedSearchResult = mAdapter.getItems().get(position);
                String venueId = advancedSearchResult.getVenueId();
                goToReservationActivity(venueId);
            }
        });
        mRefreshRecyclerViewFragment.setAdapter(mAdapter, new RefreshRecyclerViewFragment.ServiceWrapper() {

            private AvailabilitySearchRequest mAvailabilitySearchResult;
            private AdvancedSearchRequest mAdvancedSearchRequest;

            @Override
            public void executeService() {
                if (type == 0) {
                    if (mAvailabilitySearchResult == null) {
                        mAvailabilitySearchResult = ((AvailabilitySearchRequest) getIntent().getExtras().getSerializable(REQUEST));
                        if (mAvailabilitySearchResult == null) {
                            finish();
                            return;
                        }
                    }

                    VenueApi.availabilitySearch(
                            mAvailabilitySearchResult.isAllLocations,
                            mAvailabilitySearchResult.isAllCuisines,
                            mAvailabilitySearchResult.dow,
                            mAvailabilitySearchResult.tod,
                            mAvailabilitySearchResult.lid,
                            mAvailabilitySearchResult.cid,
                            mAvailabilitySearchResult.people,
                            mAvailabilitySearchResult.date,
                            mAvailabilitySearchResult.cityId,
                            mRefreshRecyclerViewFragment.getAppender()
                    );

                } else {
                    if (mAdvancedSearchRequest == null) {
                        mAdvancedSearchRequest = ((AdvancedSearchRequest) getIntent().getExtras().getSerializable(REQUEST));
                        if (mAdvancedSearchRequest == null) {
                            finish();
                            return;
                        }
                        mAdapter.setNumberOfPeople(mAdvancedSearchRequest.people);
                    }


                    VenueApi.advancedSearch(
                            mAdvancedSearchRequest.locationIds,
                            mAdvancedSearchRequest.cuisineIds,
                            mAdvancedSearchRequest.goodForIds,
                            mAdvancedSearchRequest.prices,
                            mAdvancedSearchRequest.timeOfDay,
                            mAdvancedSearchRequest.cityId,
                            mAdvancedSearchRequest.isDirectoryIncluded,
                            mAdvancedSearchRequest.venueId,
                            mAdvancedSearchRequest.dayOfWeek,
                            mRefreshRecyclerViewFragment.getAppender()
                    );
                }
            }
        }, R.mipmap.ic_info_outline_grey600_48dp, R.string.no_results_found, R.string.please_try_another_search_filters_or_reset_your_search);

        mRefreshRecyclerViewFragment.setOnActionListener(new RefreshRecyclerViewFragment.OnActionListener() {
            @Override
            public void onLoaded() {
                setTitle(getString(R.string.search_results_d, mAdapter.getItems().size()));
            }

            @Override
            public void onStartLoading() {
                setTitle(getString(R.string.searching));
            }
        });
        mRefreshRecyclerViewFragment.refreshItems(null, null);
    }

    private void goToReservationActivity(String venueId) {
        if (!User.getInstance().isLoggedIn()) {
            mPendingVenueId = venueId;
            startActivityForResult(new Intent(this, LoginActivity.class), LOGIN_REQUEST_CODE);
            return;
        }
        Intent i = new Intent(SearchResultActivity.this, ReservationActivity.class);
        i.putExtra(ReservationActivity.VENUE_ID, venueId);
        startActivity(i);
        mPendingVenueId = null;
    }
}
