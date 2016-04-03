package com.tawlat.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tawlat.LoginActivity;
import com.tawlat.R;
import com.tawlat.ReservationActivity;
import com.tawlat.VenueActivity;
import com.tawlat.adapters.OffersAndEventsVenuesAdapter;
import com.tawlat.adapters.RecommendedVenuesAdapter;
import com.tawlat.adapters.TopPointsVenuesAdapter;
import com.tawlat.core.User;
import com.tawlat.models.venues.OfferEventVenue;
import com.tawlat.models.venues.RecommendedVenue;
import com.tawlat.models.venues.TopPointVenue;
import com.tawlat.services.VenueApi;
import com.tawlat.utils.Notifications;
import com.tawlat.utils.PDFLoaderReader;
import com.tawlat.utils.TawlatUtils;


public class HomeFragment extends Fragment {

    private static final int REQUEST_PERMISSION = 1;
    private static final int LOGIN_REQUEST_CODE = 2;

    private View mView;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ImageView mOfferEventImage;
    private TextView mOfferEventDescription;

    private TopPointsVenuesAdapter mTopPointsVenuesAdapter;
    private OffersAndEventsVenuesAdapter mOffersAndEventsVenuesAdapter;
    private RecommendedVenuesAdapter mRecommendedVenuesAdapter;

    private RefreshRecyclerViewFragment mTopPointsFragment;
    private RefreshRecyclerViewFragment mOffersEventsFragment;
    private RefreshRecyclerViewFragment mRecommendedFragment;

    private AlertDialog mOfferEventDialog;
    private OfferEventVenue mSelectedOfferEventVenue;

    private String mPendingMenuToRead = null;
    private String mPendingVenueToReserve = null;
    private String mPendingOfferId = null;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.home_fragment, container, false);
        init();
        return mView;
    }

    @Override
    public void onPause() {
        /*Communicator.getInstance().cancelByTag("venue_top_points");
        Communicator.getInstance().cancelByTag("venue_recommended");
        Communicator.getInstance().cancelByTag("venue_offers_and_events");*/
        super.onPause();
    }

    public void refreshAdapters() {
        mTopPointsFragment.refreshItems(null, null);
        mOffersEventsFragment.refreshItems(null, null);
        mRecommendedFragment.refreshItems(null, null);
    }

    private void init() {
        initReferences();
        setupViewPager();
        setupOfferAndEventDialog();
    }

    private void setupOfferAndEventDialog() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.offer_and_event_dialog, null);
        mOfferEventImage = ((ImageView) view.findViewById(R.id.offer_event_details_image));
        mOfferEventDescription = ((TextView) view.findViewById(R.id.offer_event_details_description));
        Button offerEventButton = ((Button) view.findViewById(R.id.offer_event_details_button));
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setView(view);
        mOfferEventDialog = b.create();
        mOfferEventDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        offerEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedOfferEventVenue == null) return;
                goToReservationActivity(mSelectedOfferEventVenue.getId(), mSelectedOfferEventVenue.getVenueOfferId());
            }
        });

        mOfferEventDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mSelectedOfferEventVenue = null;
            }
        });
    }

    private void initReferences() {
        mViewPager = ((ViewPager) mView.findViewById(R.id.viewpager));
        mTabLayout = ((TabLayout) mView.findViewById(R.id.tabs));
    }

    private void setupViewPager() {
        setupFragments();

        mViewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return mTopPointsFragment;
                    case 1:
                        return mOffersEventsFragment;
                    case 2:
                        return mRecommendedFragment;
                }

                return null;
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return getActivity().getString(R.string.top_points);
                    case 1:
                        return getActivity().getString(R.string.offers_and_events);
                    case 2:
                        return getActivity().getString(R.string.recommended);
                }

                return "";
            }
        });

        mViewPager.setOffscreenPageLimit(3);

        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void setupFragments() {
        setupTopPoints();
        setupOffersAndEvents();
        setupRecommended();
    }

    private void setupTopPoints() {
        mTopPointsVenuesAdapter = new TopPointsVenuesAdapter(new TopPointsVenuesAdapter.OnActionListener() {
            @Override
            public void onItemClicked(int position) {
                TopPointVenue venue = mTopPointsVenuesAdapter.getItems().get(position);
                String venueId = venue.getId();
                Intent i = new Intent(getActivity(), VenueActivity.class);
                i.putExtra(VenueActivity.VENUE_ID, venueId);
                startActivity(i);
            }

            @Override
            public void onButtonClicked(int position) {
                TopPointVenue venue = mTopPointsVenuesAdapter.getItems().get(position);
                String venueId = venue.getId();
                goToReservationActivity(venueId, null);
            }
        });

        mTopPointsFragment = new RefreshRecyclerViewFragment() {
            @Override
            public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
                super.onViewCreated(view, savedInstanceState);

                mTopPointsFragment.setIsLazyLoading(false);
                mTopPointsFragment.setAdapter(mTopPointsVenuesAdapter, new ServiceWrapper() {
                    @Override
                    public void executeService() {
                        VenueApi.topPoints(TawlatUtils.getCurrentCityId(), mTopPointsFragment.getAppender());
                    }
                });
                mTopPointsFragment.refreshItems(null, null);
            }
        };
    }

    private void setupOffersAndEvents() {
        mOffersAndEventsVenuesAdapter = new OffersAndEventsVenuesAdapter(new OffersAndEventsVenuesAdapter.OnActionListener() {
            @Override
            public void onItemClicked(int position) {
                OfferEventVenue venue = mOffersAndEventsVenuesAdapter.getItems().get(position);
                String venueId = venue.getId();
                Intent i = new Intent(getActivity(), VenueActivity.class);
                i.putExtra(VenueActivity.VENUE_ID, venueId);
                startActivity(i);
            }

            @Override
            public void onButtonClicked(int position) {
                OfferEventVenue venue = mOffersAndEventsVenuesAdapter.getItems().get(position);
                Glide.with(getActivity())
                        .load(venue.getImage())
                        .placeholder(R.drawable.logograyscale)
                        .into(mOfferEventImage);
                mOfferEventDescription.setText(Html.fromHtml(venue.getDescription()));
                mSelectedOfferEventVenue = venue;
                mOfferEventDialog.show();
            }
        });

        mOffersEventsFragment = new RefreshRecyclerViewFragment() {
            @Override
            public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
                super.onViewCreated(view, savedInstanceState);

                mOffersEventsFragment.setIsLazyLoading(false);
                mOffersEventsFragment.setAdapter(mOffersAndEventsVenuesAdapter, new ServiceWrapper() {
                    @Override
                    public void executeService() {
                        VenueApi.offersAndEvents(TawlatUtils.getCurrentCityId(), mOffersEventsFragment.getAppender());
                    }
                });
                mOffersEventsFragment.refreshItems(null, null);
            }
        };
    }

    private void setupRecommended() {
        mRecommendedVenuesAdapter = new RecommendedVenuesAdapter(new RecommendedVenuesAdapter.OnActionListener() {
            @Override
            public void onItemClicked(int position) {
                RecommendedVenue venue = mRecommendedVenuesAdapter.getItems().get(position);
                String venueId = venue.getId();
                Intent i = new Intent(getActivity(), VenueActivity.class);
                i.putExtra(VenueActivity.VENUE_ID, venueId);
                startActivity(i);
            }

            @Override
            public void onButtonClicked(int position) {
                RecommendedVenue venue = mRecommendedVenuesAdapter.getItems().get(position);
                if (venue.getVenueMenus().size() == 0) {
                    Notifications.showSnackBar(getActivity(), getActivity().getString(R.string.there_are_no_menus_for_this_venue));
                    return;
                }
                final String[] menuNames = new String[venue.getVenueMenus().size()];
                final String[] menuLinks = new String[venue.getVenueMenus().size()];
                int idx = 0;

                for (Pair<String, String> name : venue.getVenueMenus()) {
                    menuNames[idx] = name.first;
                    menuLinks[idx++] = name.second;
                }

                Notifications.showListDialog(getActivity(), getActivity().getString(R.string.venue_menus), menuNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        loadMenu(menuLinks[i]);
                    }
                });
            }
        });

        mRecommendedFragment = new RefreshRecyclerViewFragment() {
            @Override
            public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
                super.onViewCreated(view, savedInstanceState);

                mRecommendedFragment.setIsLazyLoading(false);
                mRecommendedFragment.setAdapter(mRecommendedVenuesAdapter, new ServiceWrapper() {
                    @Override
                    public void executeService() {
                        VenueApi.recommended(TawlatUtils.getCurrentCityId(), mRecommendedFragment.getAppender());
                    }
                });
                mRecommendedFragment.refreshItems(null, null);
            }
        };
    }

    private void goToReservationActivity(String venueId, String offerId) {
        /**
         * first check if the user is logged in:
         */
        if (!User.getInstance().isLoggedIn()) {
            mPendingVenueToReserve = venueId;
            mPendingOfferId = offerId;
            startActivityForResult(new Intent(getActivity(), LoginActivity.class), LOGIN_REQUEST_CODE);
        } else {
            Intent i = new Intent(getActivity(), ReservationActivity.class);
            i.putExtra(ReservationActivity.VENUE_ID, venueId);
            i.putExtra(ReservationActivity.OFFER_ID, offerId);
            startActivity(i);
        }
    }

    private void loadMenu(String menuLink) {
        /**
         * first check if the write storage is granted:
         */
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            mPendingMenuToRead = menuLink;
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            PDFLoaderReader.downloadAndLoadPDF(menuLink, getActivity());
            mPendingMenuToRead = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE && mPendingVenueToReserve != null && data != null && data.getBooleanExtra(LoginActivity.SUCCESS, false)) {
            goToReservationActivity(mPendingVenueToReserve, mPendingOfferId);
            mPendingVenueToReserve = null;
            mPendingOfferId = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && mPendingMenuToRead != null) {
                loadMenu(mPendingMenuToRead);
            } else {
                Notifications.showSnackBar(getActivity(), getString(R.string.you_need_to_grant_the_permission));
            }
        }

    }
}
