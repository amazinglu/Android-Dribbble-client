package com.example.amazinglu.my_dribbble.bucket_list;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.amazinglu.my_dribbble.R;
import com.example.amazinglu.my_dribbble.model.Bucket;
import com.example.amazinglu.my_dribbble.shot_list.ShotListFragment;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

/**
 * Created by AmazingLu on 11/10/17.
 */

public class BuckListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_BUCKET = 1;
    private static final int VIEW_TYPE_LOADING = 2;

    private List<Bucket> data;
    private boolean showLoading;
    private boolean isChoosingMode;
    private LoadMoreListener loadMoreListener;
    private Context context;
    private BucketListFragment bucketListFragment;

    public BuckListAdapter(@NonNull List<Bucket> buckets,
                           @NonNull Context context,
                           boolean isChoosingMode,
                           @NonNull LoadMoreListener loadMoreListener) {
        this.data = buckets;
        this.showLoading = true;
        this.isChoosingMode = isChoosingMode;
        this.loadMoreListener = loadMoreListener;
        this.context = context;
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

    /**
     * note the warning for "final int position", it's for recycler view drag and drop
     * after drag and drop onBindViewHolder will not be call again with the new position,
     * that's why you should not assume this position is always fixed.
     * in our case, we do not support drag and drop in bucket list because Dribbble API
     * doesn't support reordering buckets, so using "final int position" is fine
     * */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_LOADING) {
            // load something
            loadMoreListener.onLoadMore();
        } else {
            final Bucket bucket = data.get(position);

            // 0 -> 0 shot
            // 1 -> 1 shot
            // 2 -> 2 shots
            String bucketShotCountString = MessageFormat.format(
                    holder.itemView.getContext().getResources().getString(R.string.shot_count),
                    bucket.shots_count);

            BucketViewHolder bucketViewHolder = (BucketViewHolder) holder;
            bucketViewHolder.bucketName.setText(bucket.name);
            bucketViewHolder.bucketShotCount.setText(bucketShotCountString);

            /**
             * choosing mode
             * */
            if (isChoosingMode) {
                bucketViewHolder.bucketChosen.setVisibility(View.VISIBLE);
                /**
                 * the view of the choosing image view
                 * */
                bucketViewHolder.bucketChosen.setImageDrawable(bucket.isChoosing
                        ? ContextCompat.getDrawable(getContext(), R.drawable.ic_check_box_black_24dp)
                        : ContextCompat.getDrawable(getContext(), R.drawable.ic_check_box_outline_blank_black_24dp));
                /**
                 * click the bucket
                 * */
                bucketViewHolder.bucketCover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bucket.isChoosing = !bucket.isChoosing;
                        notifyItemChanged(position);
                    }
                });
            } else {
                bucketViewHolder.bucketChosen.setVisibility(View.GONE);
                bucketViewHolder.bucketCover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // send the bucket id to shot list fragment
                        Intent intent = new Intent(getContext(), BucketShotListActivity.class);
                        intent.putExtra(ShotListFragment.KEY_BUCKET_ID, bucket.id);
                        intent.putExtra(BucketShotListActivity.KEY_BUCKET_NAME, bucket.name);
                        getContext().startActivity(intent);
                    }
                });
            }
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

    public ArrayList<String> getSelectBucketIds() {
        ArrayList<String> selectBucketIds = new ArrayList<>();
        for (Bucket bucket : data) {
            if (bucket.isChoosing) {
                selectBucketIds.add(bucket.id);
            }
        }
        return selectBucketIds;
    }

    public Context getContext() {
        return context;
    }

    public interface LoadMoreListener {
        void onLoadMore();
    }
}
