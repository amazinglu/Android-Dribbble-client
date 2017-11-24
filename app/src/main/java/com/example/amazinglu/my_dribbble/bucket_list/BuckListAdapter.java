package com.example.amazinglu.my_dribbble.bucket_list;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.amazinglu.my_dribbble.R;
import com.example.amazinglu.my_dribbble.model.Bucket;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AmazingLu on 11/10/17.
 */

public class BuckListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_BUCKET = 1;
    private static final int VIEW_TYPE_LOADING = 2;

    private List<Bucket> data;
    private boolean showLoading;
    private LoadMoreListener loadMoreListener;

    public BuckListAdapter(List<Bucket> data) {
        this.data = data;
    }

    public BuckListAdapter(List<Bucket> buckets, LoadMoreListener loadMoreListener) {
        this.data = buckets;
        this.showLoading = true;
        this.loadMoreListener = loadMoreListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_BUCKET) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_bucket, parent, false);
            return new BucketViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_loading, parent, false);
            return new RecyclerView.ViewHolder(view) {};
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_LOADING) {
            // load something
            loadMoreListener.onLoadMore();
        } else {
            Bucket bucket = data.get(position);

            // 0 -> 0 shot
            // 1 -> 1 shot
            // 2 -> 2 shots
            String bucketShotCountString = MessageFormat.format(
                    holder.itemView.getContext().getResources().getString(R.string.shot_count),
                    bucket.shots_count);

            BucketViewHolder bucketViewHolder = (BucketViewHolder) holder;
            bucketViewHolder.bucketName.setText(bucket.name);
            bucketViewHolder.bucketShotCount.setText(bucketShotCountString);

            // no need for checkbox yet
            bucketViewHolder.bucketChosen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return showLoading ? data.size() + 1 : data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position < data.size() ? VIEW_TYPE_BUCKET : VIEW_TYPE_LOADING;
    }

    public int getDataCount() {
        return data.size();
    }

    public void setShowLoading(boolean showLoading) {
        this.showLoading = showLoading;
        notifyDataSetChanged();
    }

    public void append(@NonNull List<Bucket> buckets) {
        data.addAll(buckets);
        notifyDataSetChanged();
    }

    public void setData(List<Bucket> data) {
        this.data.clear();
        this.data = data;
        notifyDataSetChanged();
    }

    public void prepend(List<Bucket> buckets) {
        // insert all the elements in buckets in the position 0 of data
        this.data.addAll(0, buckets);
        notifyDataSetChanged();
    }

    public interface LoadMoreListener {
        void onLoadMore();
    }
}
