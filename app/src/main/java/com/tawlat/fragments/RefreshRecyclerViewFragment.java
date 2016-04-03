package com.tawlat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tawlat.R;
import com.tawlat.adapters.RefreshAdapter;
import com.tawlat.core.ApiListeners;
import com.tawlat.models.Model;
import com.tawlat.services.Result;

import java.util.ArrayList;

/**
 * Created by mohammed on 3/4/16.
 */
public class RefreshRecyclerViewFragment extends Fragment {

    public interface ServiceWrapper {
        void executeService();
    }

    public interface OnActionListener {
        void onLoaded();

        void onStartLoading();
    }

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FrameLayout mPullToRefresh;
    private LinearLayout mEmptyContainer;
    private TextView mTitle;
    private TextView mSubTitle;
    private ImageView mPhoto;

    private RefreshAdapter mAdapter;
    private Runnable mRefreshRunnable;
    private ApiListeners.OnItemsArrayLoadedListener mOnItemsArrayLoadedListener;
    private ServiceWrapper mServiceWrapper;
    private OnActionListener mOnActionListener;

    private String mFirstItemId;
    private String mLastItemId;
    private boolean mIsRefreshing = false;
    private boolean mIsLazyLoading = false;
    private boolean mKeepLoading = true;
    private boolean mIsLoading = true;
    private String mSinceId;
    private String mMaxId;
    private boolean mCanBeEmpty = false;

    public RefreshRecyclerViewFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.refresh_recyclerview_fragment, container, false);

        mRecyclerView = ((RecyclerView) view.findViewById(R.id.recycler_view));
        mSwipeRefreshLayout = ((SwipeRefreshLayout) view.findViewById(R.id.refresh_layout));
        mPullToRefresh = ((FrameLayout) view.findViewById(R.id.pull_to_refresh));
        mEmptyContainer = ((LinearLayout) view.findViewById(R.id.empty_container));
        mTitle = ((TextView) view.findViewById(R.id.title));
        mSubTitle = ((TextView) view.findViewById(R.id.sub_title));
        mPhoto = ((ImageView) view.findViewById(R.id.photo));

        init();
        return view;
    }

    public void setAdapter(RefreshAdapter adapter, ServiceWrapper serviceWrapper, int photoId, int titleId, int subTitleId) {
        mCanBeEmpty = true;
        if (photoId != -1) mPhoto.setImageResource(photoId);
        if (titleId != -1) mTitle.setText(titleId);
        if (subTitleId != -1) mSubTitle.setText(subTitleId);
        setAdapter(adapter, serviceWrapper);
    }

    public void setAdapter(RefreshAdapter adapter, ServiceWrapper serviceWrapper) {
        mAdapter = adapter;
        mServiceWrapper = serviceWrapper;

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRecyclerView.setAdapter(mAdapter);

        /**
         * init an event listener to know
         * if the recycler view is at the bottom.
         * if so, load new comments
         */
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy <= 0 || !mKeepLoading || mIsLoading || !mIsLazyLoading) return;
                LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                int numberOfVisibleItems = layoutManager.getChildCount();
                int numberOfTotalItems = layoutManager.getItemCount();
                int orderOfFirstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                if (numberOfVisibleItems + orderOfFirstVisibleItem >= numberOfTotalItems)
                    refreshItems(null, mLastItemId);
            }
        });

    }

    public void setIsLazyLoading(boolean isLazyLoading) {
        if (isLazyLoading == mIsLazyLoading) return;
        mIsLazyLoading = isLazyLoading;
        mFirstItemId = null;
        mLastItemId = null;
        mSinceId = null;
        mMaxId = null;
    }

    public String getFirstItemId() {
        return mFirstItemId;
    }

    public String getLastItemId() {
        return mLastItemId;
    }

    public String getSinceId() {
        return mSinceId;
    }

    public String getMaxId() {
        return mMaxId;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void refreshItems(@Nullable final String sinceId, @Nullable String maxId) {
        if (mOnActionListener != null)
            mOnActionListener.onStartLoading();
        setRefreshing(true);
        mSinceId = sinceId;
        mMaxId = maxId;
        mIsLoading = true;
        mServiceWrapper.executeService();
    }

    public ApiListeners.OnItemsArrayLoadedListener getAppender() {
        return mOnItemsArrayLoadedListener;
    }

    private void init() {
        initAppender();
        /**
         * initialize the runnable that will
         * refresh the RefreshLayout
         */
        mRefreshRunnable = new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
            }
        };

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems(mFirstItemId, null);
            }
        });
    }

    private void initAppender() {
        mOnItemsArrayLoadedListener = new ApiListeners.OnItemsArrayLoadedListener() {
            @Override
            public void onLoaded(Result result, ArrayList<Model> items) {
                if (result.isSucceeded() && items != null) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mPullToRefresh.setVisibility(View.GONE);

                    /**
                     * if we set the refreshing to false
                     * just refresh the list without max and min
                     */
                    if (!mIsLazyLoading) {
                        mAdapter.getItems().clear();
                        int len = items.size();
                        for (int i = 0; i < len; i++)
                            mAdapter.getItems().add(items.get(i));
                        /**
                         * notify
                         */
                        mAdapter.notifyDataSetChanged();
                        mIsLoading = false;

                        if (mCanBeEmpty && mAdapter.getItems().size() == 0) {
                            setEmpty(true);
                        } else
                            setEmpty(false);

                        setRefreshing(false);
                        if (mOnActionListener != null)
                            mOnActionListener.onLoaded();

                        return;
                    }

                    int len = items.size();
                    /**
                     * if we are requesting newer
                     * comments, append them to the top
                     */
                    if (mSinceId != null) {
                        for (int i = len - 1; i >= 0; i--)
                            mAdapter.getItems().add(0, items.get(i));

                        /**
                         * update the value of the first element
                         * if there are values
                         */
                        if (len != 0)
                            mFirstItemId = items.get(0).getId();
                    }

                    /**
                     * anything else just append to
                     * the end
                     */
                    else {
                        for (int i = 0; i < len; i++)
                            mAdapter.getItems().add(items.get(i));

                        /**
                         * update the last comment id
                         */
                        if (len > 0) mLastItemId = items.get(len - 1).getId();

                        /**
                         * for the first time, update the first comment id
                         */
                        if (len > 0 && mFirstItemId == null)
                            mFirstItemId = items.get(0).getId();

                        /**
                         * id nothing returned, don't load any more
                         */
                        if (items.size() == 0)
                            mKeepLoading = false;
                    }

                    /**
                     * notify
                     */
                    mAdapter.notifyDataSetChanged();
                    mIsLoading = false;
                    mSinceId = null;
                    mMaxId = null;

                } else {
                    /**
                     * check if it is a cancelled request
                     */
                    if ((result.getCode() == null || !result.getCode().equals("0x02")) && !mCanBeEmpty) {
                        //Notifications.showSnackBar(RefreshRecyclerViewFragment.this.getActivity(), result.getMessages().get(0));
                        mRecyclerView.setVisibility(View.GONE);
                        mPullToRefresh.setVisibility(View.VISIBLE);
                    }
                }

                if (mCanBeEmpty && mAdapter.getItems().size() == 0) {
                    setEmpty(true);
                }

                setRefreshing(false);
                if (mOnActionListener != null)
                    mOnActionListener.onLoaded();
            }
        };
    }

    public void setEmpty(boolean empty) {
        if (empty) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyContainer.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyContainer.setVisibility(View.GONE);
        }
    }

    public void setRefreshing(boolean refresh) {
        mIsRefreshing = refresh;
        mSwipeRefreshLayout.post(mRefreshRunnable);
    }

    public void checkIfEmpty() {
        if (!mCanBeEmpty) return;
        if (mAdapter.getItems().size() == 0)
            setEmpty(true);
    }

    public void setOnActionListener(OnActionListener onActionListener) {
        mOnActionListener = onActionListener;
    }
}
