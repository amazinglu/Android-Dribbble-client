package com.example.amazinglu.my_dribbble.bucket_list;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.amazinglu.my_dribbble.R;
import com.example.amazinglu.my_dribbble.base.BaseViewHolder;
import com.example.amazinglu.my_dribbble.base.InfiniteAdapter;
import com.example.amazinglu.my_dribbble.model.Bucket;
import com.example.amazinglu.my_dribbble.model.Shot;
import com.example.amazinglu.my_dribbble.shot_list.ShotListFragment;
import com.example.amazinglu.my_dribbble.utils.ModelUtils;
import com.google.gson.reflect.TypeToken;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class BuckListAdapter extends InfiniteAdapter<Bucket> {

    private boolean isChoosingMode;
    private BucketListFragment bucketListFragment;

    public BuckListAdapter(@NonNull List<Bucket> buckets,
                           @NonNull BucketListFragment bucketListFragment,
                           boolean isChoosingMode,
                           @NonNull LoadMoreListener loadMoreListener) {
        super(buckets, bucketListFragment.getContext(), loadMoreListener);
        this.isChoosingMode = isChoosingMode;
        this.bucketListFragment = bucketListFragment;
    }

    @Override
    protected BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_bucket, parent, false);
        return new BucketViewHolder(view);
    }


        /**
     * note the warning for "final int position", it's for recycler view drag and drop
     * after drag and drop onBindViewHolder will not be call again with the new position,
     * that's why you should not assume this position is always fixed.
     * in our case, we do not supp]ort drag and drop in bucket list because Dribbble API
     * doesn't support reordering buckets, so using "final int position" is fine
     * */
    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Bucket bucket = getData().get(position);

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
                    bucketListFragment.startActivityForResult(intent,
                            BucketListFragment.REQ_CODE_DELETE_BUCKET);
                }
            });
        }
    }

    public ArrayList<String> getSelectBucketIds() {
        ArrayList<String> selectBucketIds = new ArrayList<>();
        for (Bucket bucket : getData()) {
            if (bucket.isChoosing) {
                selectBucketIds.add(bucket.id);
            }
        }
        return selectBucketIds;
    }

    public void updateBucketList(String bucketId) {
        for (Bucket bucket : getData()) {
            if (TextUtils.equals(bucketId, bucket.id)) {
                getData().remove(bucket);
                break;
            }
        }
        notifyDataSetChanged();
    }
}
