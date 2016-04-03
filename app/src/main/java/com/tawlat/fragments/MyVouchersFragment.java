package com.tawlat.fragments;

import android.app.AlertDialog;
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

import com.tawlat.R;
import com.tawlat.VenueActivity;
import com.tawlat.adapters.VouchersAdapter;
import com.tawlat.core.ApiListeners;
import com.tawlat.core.Communicator;
import com.tawlat.core.User;
import com.tawlat.models.Model;
import com.tawlat.models.Voucher;
import com.tawlat.models.venues.VenueSimple;
import com.tawlat.services.Result;
import com.tawlat.services.UserApi;
import com.tawlat.services.VenueApi;
import com.tawlat.utils.Notifications;

import java.util.ArrayList;


public class MyVouchersFragment extends Fragment {

    private View mView;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private RefreshRecyclerViewFragment mPendingFragment;
    private RefreshRecyclerViewFragment mHistoryFragment;

    private VouchersAdapter mPendingVouchersAdapter;

    public MyVouchersFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.my_vouchers_fragment, container, false);
        init();
        return mView;
    }

    @Override
    public void onPause() {
        Communicator.getInstance().cancelByTag("user_history_voucher");
        Communicator.getInstance().cancelByTag("user_pending_voucher");
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
                if (position == 0)
                    return mPendingFragment;
                else
                    return mHistoryFragment;
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                if (position == 0)
                    return getActivity().getString(R.string.pending_vouchers);
                else
                    return getActivity().getString(R.string.history_vouchers);
            }
        });

        mViewPager.setOffscreenPageLimit(2);

        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void setupFragments() {
        setupPendingFragments();
        setupHistoryFragments();
    }

    private void setupPendingFragments() {
        mPendingVouchersAdapter = new VouchersAdapter(Voucher.Status.PENDING, new VouchersAdapter.OnActionListener() {
            @Override
            public void onItemClicked(int position) {
                Voucher voucher = mPendingVouchersAdapter.getItems().get(position);
                final AlertDialog progressBar = Notifications.showLoadingDialog(getActivity(), getString(R.string.loading));
                VenueApi.get(voucher.getRedeemableAt().toArray(new String[voucher.getRedeemableAt().size()]), new ApiListeners.OnItemsArrayLoadedListener() {
                    @Override
                    public void onLoaded(Result result, @Nullable ArrayList<Model> items) {
                        progressBar.dismiss();
                        if (result.isSucceeded() && items != null) {

                            showVenuesList(items);

                        } else {
                            Notifications.showSnackBar(getActivity(), result.getMessages().get(0));
                        }
                    }
                });
            }
        });

        mPendingFragment = new RefreshRecyclerViewFragment() {
            @Override
            public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
                super.onViewCreated(view, savedInstanceState);
                mPendingFragment.setAdapter(mPendingVouchersAdapter, new ServiceWrapper() {
                    @Override
                    public void executeService() {
                        UserApi.pendingVouchers(User.getInstance().getUser().getId(), mPendingFragment.getAppender());
                    }
                }, R.mipmap.ic_info_outline_grey600_48dp, R.string.no_pending_vouchers_title, R.string.no_pending_vouchers);

                mPendingFragment.refreshItems(null, null);
            }
        };
    }

    private void showVenuesList(ArrayList<Model> items) {
        final String[] venueIds = new String[items.size()];
        final String[] venueNames = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            VenueSimple item = ((VenueSimple) items.get(i));
            venueIds[i] = item.getId();
            venueNames[i] = item.getName();
        }

        Notifications.showListDialog(getActivity(), getActivity().getString(R.string.venues), venueNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // go to venue page
                String venueId = venueIds[i];
                Intent intent = new Intent(getActivity(), VenueActivity.class);
                intent.putExtra(VenueActivity.VENUE_ID, venueId);
                startActivity(intent);
            }
        });
    }

    private void setupHistoryFragments() {
        final VouchersAdapter adapter = new VouchersAdapter(Voucher.Status.REDEEMED, null);

        mHistoryFragment = new RefreshRecyclerViewFragment() {
            @Override
            public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
                super.onViewCreated(view, savedInstanceState);
                mHistoryFragment.setAdapter(adapter, new ServiceWrapper() {
                    @Override
                    public void executeService() {
                        UserApi.historyVouchers(User.getInstance().getUser().getId(), mHistoryFragment.getAppender());
                    }
                }, R.mipmap.ic_info_outline_grey600_48dp, R.string.no_history_vouchers_title, R.string.no_history_vouchers);

                mHistoryFragment.refreshItems(null, null);
            }
        };
    }
}
