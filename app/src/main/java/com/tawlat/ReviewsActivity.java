package com.tawlat;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.tawlat.adapters.ReviewsAdapter;
import com.tawlat.fragments.RefreshRecyclerViewFragment;
import com.tawlat.services.VenueApi;

public class ReviewsActivity extends AppCompatActivity {
    public static final String VENUE_ID = "venue_id";
    public static final String VENUE_NAME = "venue_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String venueId = getIntent().getStringExtra(VENUE_ID);
        String venueName = getIntent().getStringExtra(VENUE_NAME);
        if (venueId == null || venueName == null) {
            finish();
            return;
        }

        setContentView(R.layout.reviews_activity);
        setTitle(getString(R.string.s_reviews, venueName));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back);
        }

        final RefreshRecyclerViewFragment refreshRecyclerViewFragment = ((RefreshRecyclerViewFragment) getSupportFragmentManager().findFragmentById(R.id.refresh_recycler_view_fragment));
        refreshRecyclerViewFragment.setAdapter(new ReviewsAdapter(), new RefreshRecyclerViewFragment.ServiceWrapper() {
            @Override
            public void executeService() {
                VenueApi.reviews(venueId, refreshRecyclerViewFragment.getAppender());
            }
        }, R.mipmap.ic_info_outline_grey600_48dp, R.string.no_reviews, R.string.no_reviews_for_this_venue);

        refreshRecyclerViewFragment.refreshItems(null, null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
