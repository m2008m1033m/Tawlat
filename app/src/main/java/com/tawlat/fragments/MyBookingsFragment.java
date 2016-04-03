package com.tawlat.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tawlat.EditReservationActivity;
import com.tawlat.R;
import com.tawlat.adapters.ReservationsAdapter;
import com.tawlat.core.ApiListeners;
import com.tawlat.core.Communicator;
import com.tawlat.core.User;
import com.tawlat.models.Reservation;
import com.tawlat.services.ReservationApi;
import com.tawlat.services.Result;
import com.tawlat.utils.Notifications;


public class MyBookingsFragment extends Fragment {

    private static final int EDIT_CODE = 1;

    private View mView;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private RefreshRecyclerViewFragment mPendingFragment;
    private RefreshRecyclerViewFragment mCancelledFragment;
    private RefreshRecyclerViewFragment mHistoryFragment;
    private RefreshRecyclerViewFragment mNoShowFragment;

    private ReservationsAdapter mPendingReservationsAdapter;
    private ReservationsAdapter mCancelledReservationsAdapter;

    private int mEdittedReservation;

    public MyBookingsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.my_bookings_fragment, container, false);

        init();

        return mView;
    }

    @Override
    public void onPause() {
        Communicator.getInstance().cancelByTag("reservation_upcoming");
        Communicator.getInstance().cancelByTag("reservation_canceled");
        Communicator.getInstance().cancelByTag("reservation_passed");
        Communicator.getInstance().cancelByTag("reservation_noshow");
        super.onPause();
    }

    private void init() {
        initReferences();
        setupViewPager();
    }

    private void initReferences() {
        mTabLayout = ((TabLayout) mView.findViewById(R.id.tabs));
        mViewPager = ((ViewPager) mView.findViewById(R.id.viewpager));
    }

    private void setupViewPager() {
        setupFragments();

        mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return mPendingFragment;
                    case 1:
                        return mCancelledFragment;
                    case 2:
                        return mHistoryFragment;
                    case 3:
                        return mNoShowFragment;
                }

                return null;
            }

            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "Upcoming";
                    case 1:
                        return "Cancelled";
                    case 2:
                        return "History";
                    case 3:
                        return "No Show";
                }
                return "";
            }
        });

        mViewPager.setOffscreenPageLimit(4);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void setupFragments() {
        setupPendingFragment();
        setupCancelledFragment();
        setupHistoryFragment();
        setupNoShowFragment();
    }

    private void setupPendingFragment() {
        mPendingReservationsAdapter = new ReservationsAdapter(new ReservationsAdapter.OnActionListener() {
            @Override
            public void onItemClicked(final int position) {
                final Reservation reservation = mPendingReservationsAdapter.getItems().get(position);
                Notifications.showListDialog(getActivity(), reservation.getVenueName(), new String[]{getActivity().getString(R.string.edit_reservation), getActivity().getString(R.string.cancel_reservation)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        switch (i) {
                            case 0:
                                mEdittedReservation = position;
                                Intent intent = new Intent(getActivity(), EditReservationActivity.class);
                                Bundle b = new Bundle();
                                b.putSerializable(EditReservationActivity.RESERVATION, reservation);
                                intent.putExtras(b);
                                startActivityForResult(intent, EDIT_CODE);
                                break;
                            case 1:
                                ReservationApi.cancel(reservation.getId(), new ApiListeners.OnActionExecutedListener() {
                                    @Override
                                    public void onExecuted(Result result) {
                                        if (result.isSucceeded()) {
                                            Reservation cancelledReservation = mPendingReservationsAdapter.getItems().remove(position);
                                            mPendingReservationsAdapter.notifyItemRemoved(position);
                                            mPendingFragment.checkIfEmpty();
                                            mCancelledReservationsAdapter.getItems().add(0, cancelledReservation);
                                            mCancelledReservationsAdapter.notifyDataSetChanged();
                                            Notifications.showSnackBar(getActivity(), getString(R.string.your_booking_has_been_cancelled_successfully));
                                        } else {
                                            Notifications.showSnackBar(getActivity(), result.getMessages().get(0));
                                        }
                                    }
                                });
                                break;
                        }

                    }
                });
            }
        }, Reservation.Status.PENDING);

        mPendingFragment = new RefreshRecyclerViewFragment() {
            @Override
            public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
                super.onViewCreated(view, savedInstanceState);
                mPendingFragment.setAdapter(mPendingReservationsAdapter, new ServiceWrapper() {
                    @Override
                    public void executeService() {
                        ReservationApi.upcoming(User.getInstance().getUser().getId(), mPendingFragment.getAppender());
                    }
                }, R.mipmap.ic_info_outline_grey600_48dp, R.string.you_have_no_upcoming_booking, R.string.you_have_no_upcoming_booking_sub);
                refreshItems(null, null);
            }
        };
    }

    private void setupCancelledFragment() {
        mCancelledReservationsAdapter = new ReservationsAdapter(null, Reservation.Status.CANCELED);
        mCancelledFragment = new RefreshRecyclerViewFragment() {
            @Override
            public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
                super.onViewCreated(view, savedInstanceState);
                mCancelledFragment.setAdapter(mCancelledReservationsAdapter, new ServiceWrapper() {
                    @Override
                    public void executeService() {
                        ReservationApi.canceled(User.getInstance().getUser().getId(), mCancelledFragment.getAppender());
                    }
                }, R.mipmap.ic_info_outline_grey600_48dp, R.string.you_have_no_cancelled_booking, R.string.you_have_no_cancelled_booking_sub);
                mCancelledFragment.refreshItems(null, null);
            }
        };
    }

    private void setupHistoryFragment() {
        final ReservationsAdapter adapter = new ReservationsAdapter(null, Reservation.Status.PASSED);
        mHistoryFragment = new RefreshRecyclerViewFragment() {
            @Override
            public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
                super.onViewCreated(view, savedInstanceState);
                mHistoryFragment.setAdapter(adapter, new ServiceWrapper() {
                    @Override
                    public void executeService() {
                        ReservationApi.passed(User.getInstance().getUser().getId(), mHistoryFragment.getAppender());
                    }
                }, R.mipmap.ic_info_outline_grey600_48dp, R.string.you_have_no_booking_history, R.string.you_have_no_booking_history_sub);
                mHistoryFragment.refreshItems(null, null);
            }
        };
    }

    private void setupNoShowFragment() {
        final ReservationsAdapter adapter = new ReservationsAdapter(null, Reservation.Status.NO_SHOW);
        mNoShowFragment = new RefreshRecyclerViewFragment() {
            @Override
            public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
                super.onViewCreated(view, savedInstanceState);
                mNoShowFragment.setAdapter(adapter, new ServiceWrapper() {
                    @Override
                    public void executeService() {
                        ReservationApi.noshow(User.getInstance().getUser().getId(), mNoShowFragment.getAppender());
                    }
                }, R.mipmap.ic_info_outline_grey600_48dp, R.string.you_have_no_noshow_booking, R.string.you_have_no_noshow_booking_sub);
                mNoShowFragment.refreshItems(null, null);
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == EDIT_CODE && data != null && data.getBooleanExtra(EditReservationActivity.RESERVATION_RESULT, false) && mEdittedReservation != -1) {
            Reservation reservation = ((Reservation) data.getExtras().getSerializable(EditReservationActivity.RESERVATION));
            if (reservation == null) return;
            mPendingReservationsAdapter.getItems().set(mEdittedReservation, reservation);
            mPendingReservationsAdapter.notifyItemChanged(mEdittedReservation);
            mEdittedReservation = -1;
            Notifications.showSnackBar(getActivity(), getActivity().getString(R.string.reservation_updated_successfully));
        }
    }
}
