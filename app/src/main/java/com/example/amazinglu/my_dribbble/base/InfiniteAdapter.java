package com.example.amazinglu.my_dribbble.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.amazinglu.my_dribbble.R;
import com.example.amazinglu.my_dribbble.model.Bucket;
import com.example.amazinglu.my_dribbble.model.Shot;
import com.example.amazinglu.my_dribbble.shot_list.ShotListAdapter;

import java.util.List;

public abstract class InfiniteAdapter<T> extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private List<T> data;
    private boolean showLoading;
    private LoadMoreListener loadMoreListener;
    private Context context;

    public InfiniteAdapter(@NonNull List<T> data,
                           @NonNull Context context,
                           @NonNull LoadMoreListener loadMoreListener) {
        this.data = data;
        this.showLoading = true;
        this.loadMoreListener = loadMoreListener;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item_loading, parent, false);
            // since no view is in this view holder, use the system view holder is fine
            return new RecyclerView.ViewHolder(view) {};
        } else {
            return onCreateItemViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_LOADING) {
            loadMoreListener.onLoadMore();
        } else {
            onBindItemViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return showLoading ? data.size() + 1 : data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (showLoading) {
            return position == data.size() ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    public Context getContext() {
        return context;
    }

    public void setData(List<T> data) {
        this.data.clear();
        this.data = data;
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return data;
    }
    public int getDataCount() {
        return data.size();
    }

    public void append(@NonNull List<T> newData) {
        data.addAll(newData);
        notifyDataSetChanged();
    }

    public void prepend(List<T> newData) {
        // insert all the elements in buckets in the position 0 of data
        this.data.addAll(0, newData);
        notifyDataSetChanged();
    }

    public void setShowLoading(boolean showLoading) {
        this.showLoading = showLoading;
        // if the showLoading changes, no more loading will be exist
        notifyDataSetChanged();
    }

    protected abstract BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType);
    protected abstract void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position);

    public interface LoadMoreListener {
        void onLoadMore();
    }
}
