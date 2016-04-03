package com.tawlat.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tawlat.R;
import com.tawlat.models.search.KeywordSearchResult;

import java.util.ArrayList;

/**
 * Created by mohammed on 3/22/16.
 */
public class KeywordSearchItemsAdapter extends RecyclerView.Adapter {

    public interface OnActionListener {
        void onItemClicked(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitle;
        private ImageView mType;
        private int mPosition;

        public ViewHolder(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.title);
            mType = (ImageView) itemView.findViewById(R.id.type);
        }
    }

    private ArrayList<KeywordSearchResult> mItems = new ArrayList<>();
    private OnActionListener mOnActionListener;

    public KeywordSearchItemsAdapter(OnActionListener onActionListener) {
        mOnActionListener = onActionListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.keyword_search_item, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnActionListener.onItemClicked(vh.mPosition);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        KeywordSearchResult item = mItems.get(position);

        if (item.getType() == KeywordSearchResult.Type.VENUE && item.isPartner())
            ((ViewHolder) holder).mType.setVisibility(View.VISIBLE);
        else
            ((ViewHolder) holder).mType.setVisibility(View.GONE);

        ((ViewHolder) holder).mTitle.setText(Html.fromHtml(item.getName() + " (" + item.getTypeName() + ")"), TextView.BufferType.SPANNABLE);
        ((ViewHolder) holder).mPosition = position;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public ArrayList<KeywordSearchResult> getItems() {
        return mItems;
    }
}
