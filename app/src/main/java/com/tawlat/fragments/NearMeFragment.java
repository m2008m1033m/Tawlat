package com.tawlat.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tawlat.LoginActivity;
import com.tawlat.R;
import com.tawlat.ReservationActivity;
import com.tawlat.VenueActivity;
import com.tawlat.adapters.NearMeAdapter;
import com.tawlat.core.ApiListeners;
import com.tawlat.core.Communicator;
import com.tawlat.core.User;
import com.tawlat.models.Model;
import com.tawlat.models.venues.NearMeVenue;
import com.tawlat.services.Result;
import com.tawlat.services.VenueApi;
import com.tawlat.utils.Notifications;

import java.util.ArrayList;
import java.util.List;

public class NearMeFragment extends Fragment {

    public interface OnActionListener {
        void onLocationPermissionRejected();

        void onLocationDisabled();
    }

    private final static int REQUEST_PERMISSIONS = 0;
    private static final int LOGIN_REQUEST_CODE = 2;

    private View mView;
    private FloatingActionButton mFAB;
    private ProgressBar mProgressBar;
    private ViewPager mViewPager;
    private TextView mNoVenuesMessage;
    private GoogleMap mGoogleMap;

    private NearMeAdapter mAdapter;
    private OnActionListener mOnActionListener;

    private LatLng mCurrentLocation;
    private Marker mSelectedMarker;

    private String mPendingVenueToReserve = null;

    public NearMeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.near_by_fragment, container, false);
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPermissionAndLocation();
    }

    @Override
    public void onDestroy() {
        Communicator.getInstance().cancelByTag("venue_nearby");
        super.onDestroy();
    }

    @Override
    public void onPause() {
        Communicator.getInstance().cancelByTag("venue_nearby");
        super.onPause();
    }

    public void setOnActionListener(OnActionListener listener) {
        mOnActionListener = listener;
    }

    private void init() {
        initReferences();
        initEvents();
    }

    private void initReferences() {
        mFAB = ((FloatingActionButton) mView.findViewById(R.id.fab));
        mProgressBar = ((ProgressBar) mView.findViewById(R.id.progress));
        mViewPager = ((ViewPager) mView.findViewById(R.id.viewpager));
        mNoVenuesMessage = ((TextView) mView.findViewById(R.id.no_venues_message));
        SupportMapFragment supportMapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                setupMap();
            }
        });
    }

    private void initEvents() {
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAdapter == null || mViewPager == null) return;
                int position = mViewPager.getCurrentItem();
                NearMeVenue venue = mAdapter.getItems().get(position);
                goToReservationActivity(venue.getId());
            }
        });
    }

    private void setupMap() {
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (location != null) {
                    mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    //mCurrentLocation = new LatLng(25.191587, 55.275398);
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, 15));
                    mGoogleMap.setOnMyLocationChangeListener(null);
                    setupViewPager();
                }
            }
        });
    }

    private void setupViewPager() {
        mAdapter = new NearMeAdapter(new NearMeAdapter.OnActionListener() {
            @Override
            public void onItemClicked(int position) {
                NearMeVenue venue = mAdapter.getItems().get(position);
                String venueId = venue.getId();
                Intent i = new Intent(getActivity(), VenueActivity.class);
                i.putExtra(VenueActivity.VENUE_ID, venueId);
                startActivity(i);
            }
        });

        Communicator.getInstance().cancelByTag("venue_nearby");
        VenueApi.nearby(mCurrentLocation.latitude, mCurrentLocation.longitude, new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                mProgressBar.setVisibility(View.GONE);
                if (result.isSucceeded() && items != null) {
                    for (Model item : items)
                        mAdapter.getItems().add(((NearMeVenue) item));

                    mAdapter.notifyDataSetChanged();
                    mViewPager.setVisibility(View.VISIBLE);
                    addMarkers();
                } else {
                    mNoVenuesMessage.setVisibility(View.VISIBLE);
                }
            }
        });

        mViewPager.setAdapter(mAdapter);
    }

    private void addMarkers() {
        final ArrayList<NearMeVenue> items = mAdapter.getItems();
        final ArrayList<Marker> markers = new ArrayList<>();

        int len = items.size();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_pin_grey));
        markers.clear();
        for (int i = 0; i < len; i++) {
            NearMeVenue item = items.get(i);
            markerOptions.position(new LatLng(item.getLatitude(), item.getLongitude()));
            markerOptions.title(String.valueOf(i));
            markers.add(mGoogleMap.addMarker(markerOptions));
            if (i == 0)
                setSelected(markers.get(0), mAdapter.getItems().get(0).isDirectory());
        }

        /**
         * go to the closest item in
         * the view pager
         */
        mViewPager.setCurrentItem(0);


        //1- the view pager to the marker
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                boolean isDirectory = mAdapter.getItems().get(position).isDirectory();
                setSelected(markers.get(position), isDirectory);

                // hide the booking fab if it is directory
                if (isDirectory)
                    mFAB.setVisibility(View.GONE);
                else
                    mFAB.setVisibility(View.VISIBLE);

                NearMeVenue nearMeVenue = items.get(position);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(nearMeVenue.getLatitude(), nearMeVenue.getLongitude()), 15));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //2- the marker to the view pager:
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //setSelected(marker);
                int index = Integer.parseInt(marker.getTitle());
                mViewPager.setCurrentItem(index);
                return true;
            }
        });

        //3- show the fab
        mFAB.setVisibility(View.VISIBLE);
    }

    private void setSelected(Marker marker, boolean isDirectory) {
        if (mSelectedMarker != null)
            mSelectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_pin_grey));
        marker.setIcon(isDirectory ? BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_pin_green) : BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_pin));
        mSelectedMarker = marker;
    }

    private void goToReservationActivity(String venueId) {
        /**
         * first check if the user is logged in:
         */
        if (!User.getInstance().isLoggedIn()) {
            mPendingVenueToReserve = venueId;
            startActivityForResult(new Intent(getActivity(), LoginActivity.class), LOGIN_REQUEST_CODE);
        } else {
            Intent i = new Intent(getActivity(), ReservationActivity.class);
            i.putExtra(ReservationActivity.VENUE_ID, venueId);
            startActivity(i);
        }
    }

    private boolean checkLocationService() {
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled;
        boolean network_enabled;
        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps_enabled || network_enabled;
    }

    private void checkPermissionAndLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            List<String> permissionsNeeded = new ArrayList<>();
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                permissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (permissionsNeeded.size() > 0)
                requestPermissions(permissionsNeeded.toArray(new String[permissionsNeeded.size()]), REQUEST_PERMISSIONS);
        } else {
            checkIfLocationIsEnabled();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length == 2) {
                boolean finish = false;
                for (int i = 0; i < 2; i++)
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        finish = true;
                        break;
                    }

                if (finish) {
                    if (mOnActionListener != null)
                        mOnActionListener.onLocationPermissionRejected();
                } else {
                    checkIfLocationIsEnabled();
                }

            } else {
                if (mOnActionListener != null)
                    mOnActionListener.onLocationPermissionRejected();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE && mPendingVenueToReserve != null && data != null && data.getBooleanExtra(LoginActivity.SUCCESS, false)) {
            goToReservationActivity(mPendingVenueToReserve);
            mPendingVenueToReserve = null;
        }
    }

    private void checkIfLocationIsEnabled() {
        if (!checkLocationService()) {
            if (!checkLocationService())
                Notifications.showYesNoDialog(getActivity(),
                        getActivity().getString(R.string.action_required),
                        getActivity().getString(R.string.please_enable_location_from_your_settings),
                        getActivity().getString(R.string.open_location_settings),
                        getActivity().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                getActivity().startActivity(myIntent);

                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (mOnActionListener != null && !NearMeFragment.this.isDetached())
                                    mOnActionListener.onLocationDisabled();
                            }
                        });
        } else {
            init();
        }
    }
}
