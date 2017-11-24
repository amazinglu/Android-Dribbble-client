package com.example.amazinglu.my_dribbble.shot_list;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.amazinglu.my_dribbble.R;
import com.example.amazinglu.my_dribbble.model.Shot;
import com.example.amazinglu.my_dribbble.shot_detail.ShotActivity;
import com.example.amazinglu.my_dribbble.shot_detail.ShotFragment;
import com.example.amazinglu.my_dribbble.utils.ImageUtils;
import com.example.amazinglu.my_dribbble.utils.ModelUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import static java.security.AccessController.getContext;

public class ShotListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_SHOT = 1;
    private static final int VIEW_TYPE_LOADING = 2;

    private List<Shot> data;
    private boolean showLoading;
    private LoadMoreListener loadMoreListener;

    private final ShotListFragment shotListFragment;

    public ShotListAdapter(@NonNull List<Shot> data,
                           @NonNull ShotListFragment shotListFragment,
                           @NonNull LoadMoreListener loadMoreListener) {
        this.data = data;
        this.showLoading = true;
        this.loadMoreListener = loadMoreListener;
        this.shotListFragment = shotListFragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SHOT) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.list_item_shot, parent, false);
            return new ShotViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_loading, parent, false);
            // since no view is in this view holder, use the system view holder is fine
            return new RecyclerView.ViewHolder(view) {};
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_LOADING) {
            loadMoreListener.onLoadMore();
        } else {
            final Shot shot = data.get(position);
            ShotViewHolder shotViewHolder = (ShotViewHolder) holder;
            shotViewHolder.likeCount.setText(String.valueOf(shot.likes_count));
            shotViewHolder.bucketCount.setText(String.valueOf(shot.buckets_count));
            shotViewHolder.viewCount.setText(String.valueOf(shot.views_count));
            // load shot image
            ImageUtils.loadShotImage(shot, shotViewHolder.image);

            // listener for clicking the shot list
            shotViewHolder.cover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Context context = holder.itemView.getContext();
                    Intent intent = new Intent(shotListFragment.getContext(), ShotActivity.class);
                    /**
                     * use JSON to transit object through activities
                     * */
                    intent.putExtra(ShotFragment.KEY_SHOT,
                            ModelUtils.toString(shot, new TypeToken<Shot>(){}));
                    intent.putExtra(ShotActivity.KEY_SHOT_TITLE, shot.title);
                    shotListFragment.startActivityForResult(intent, ShotListFragment.REQ_CODE_SHOT);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return showLoading ? data.size() + 1 : data.size();
    }

    /**
     * set the view type of the adapter
     * */
    @Override
    public int getItemViewType(int position) {
        return position < data.size() ? VIEW_TYPE_SHOT : VIEW_TYPE_LOADING;
    }

    public void setData(List<Shot> data) {
        this.data.clear();
        this.data = data;
        notifyDataSetChanged();
    }

    public List<Shot> getData() {
        return data;
    }


    public interface LoadMoreListener {
        void onLoadMore();
    }

    public void append(@NonNull List<Shot> moreShots) {
        data.addAll(moreShots);
        // call the adapter to update the view
        notifyDataSetChanged();
    }

    public int getDataCount() {
        return data.size();
    }

    public void setShowLoading(boolean showLoading) {
        this.showLoading = showLoading;
        // if the showLoading changes, no more loading will be exist
        notifyDataSetChanged();
    }
}
