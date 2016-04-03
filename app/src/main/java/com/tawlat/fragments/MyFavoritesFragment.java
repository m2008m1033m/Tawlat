package com.tawlat.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tawlat.R;
import com.tawlat.VenueActivity;
import com.tawlat.adapters.MyFavoritesAdapter;
import com.tawlat.core.ApiListeners;
import com.tawlat.core.User;
import com.tawlat.models.venues.FavoriteVenue;
import com.tawlat.services.Result;
import com.tawlat.services.UserApi;
import com.tawlat.utils.Notifications;

public class MyFavoritesFragment extends RefreshRecyclerViewFragment {

    private MyFavoritesAdapter mAdapter;

    public MyFavoritesFragment() {
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new MyFavoritesAdapter(new MyFavoritesAdapter.OnActionListener() {
            @Override
            public void onItemClicked(final int position) {
                final FavoriteVenue venue = mAdapter.getItems().get(position);
                Notifications.showListDialog(getActivity(), venue.getVenueName(), new String[]{getActivity().getString(R.string.go_to_venue), getActivity().getString(R.string.remove_from_favorites)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            // navigate to the venue page
                            Intent intent = new Intent(getActivity(), VenueActivity.class);
                            intent.putExtra(VenueActivity.VENUE_ID, venue.getId());
                            startActivity(intent);
                        } else {
                            // remove from favorites:
                            UserApi.unfavorite(User.getInstance().getUser().getId(), venue.getId(), new ApiListeners.OnActionExecutedListener() {

                                @Override
                                public void onExecuted(Result result) {
                                    if (!result.isSucceeded()) return;
                                    mAdapter.getItems().remove(position);
                                    mAdapter.notifyItemRemoved(position);
                                    checkIfEmpty();
                                }
                            });

                        }
                    }
                });
            }
        });

        setAdapter(mAdapter, new ServiceWrapper() {
            @Override
            public void executeService() {
                UserApi.favorites(User.getInstance().getUser().getId(), MyFavoritesFragment.this.getAppender());
            }
        }, R.mipmap.ic_info_outline_grey600_48dp, R.string.no_favorites_title, R.string.no_favorite_venues);

        refreshItems(null, null);

    }

}
