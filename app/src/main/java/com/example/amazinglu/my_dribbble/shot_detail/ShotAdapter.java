package com.example.amazinglu.my_dribbble.shot_detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.amazinglu.my_dribbble.R;
import com.example.amazinglu.my_dribbble.bucket_list.BucketListFragment;
import com.example.amazinglu.my_dribbble.bucket_list.ChooseBucketActivity;
import com.example.amazinglu.my_dribbble.model.Shot;
import com.example.amazinglu.my_dribbble.utils.ImageUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class ShotAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_SHOT_IMAGE = 0;
    private static final int VIEW_TYPE_SHOT_INFO = 1;

    private final Shot shot;
    private final ShotFragment shotFragment;

    private ArrayList<String> collectedBUcketIds;

    public ShotAdapter(@NonNull Shot shot, @NonNull ShotFragment shotFragment) {
        this.shot = shot;
        this.shotFragment = shotFragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_SHOT_IMAGE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.shot_item_image, parent, false);
                return new ImageViewHolder(view);
            case VIEW_TYPE_SHOT_INFO:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.shot_item_info, parent, false);
                return new InfoViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int viewType = getItemViewType(position);
        switch (viewType) {
            case VIEW_TYPE_SHOT_IMAGE:
                /**
                 * download the image and put it into the image view using Fresco
                 * show animation
                 * 1. need to include "compile 'com.facebook.fresco:animated-gif:0.13.0'"
                 * 2. check if the download image has the key "hidpi"
                 * */
                ImageUtils.loadShotImage(shot, ((ImageViewHolder) holder).image);
                break;
            case VIEW_TYPE_SHOT_INFO:
                InfoViewHolder shotDetailViewHolder = (InfoViewHolder)holder;
                shotDetailViewHolder.title.setText(shot.title);
                shotDetailViewHolder.authorName.setText(shot.user.name);

                shotDetailViewHolder.description.setText(Html.fromHtml(
                        shot.description == null ? "" : shot.description));
                shotDetailViewHolder.description.setMovementMethod(LinkMovementMethod.getInstance());

                shotDetailViewHolder.likeCount.setText(String.valueOf(shot.likes_count));
                shotDetailViewHolder.bucketCount.setText(String.valueOf(shot.buckets_count));
                shotDetailViewHolder.viewCount.setText(String.valueOf(shot.views_count));

                Drawable bucketDrawble = shot.bucketed
                        ? ContextCompat.getDrawable(shotDetailViewHolder.itemView.getContext(),
                        R.drawable.ic_inbox_dribbble_18dp)
                        : ContextCompat.getDrawable(shotDetailViewHolder.itemView.getContext(),
                        R.drawable.ic_inbox_black_18dp);
                shotDetailViewHolder.bucketButton.setImageDrawable(bucketDrawble);

                ImageUtils.loadUserPicture(holder.itemView.getContext(),
                        shotDetailViewHolder.authorPicture,
                        shot.user.avatar_url);
                /**
                 * click the share button
                 * */
                shotDetailViewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        share(view.getContext());
                    }
                });

                /**
                 * click the share button
                 * */
                shotDetailViewHolder.bucketButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bucket(view.getContext());
                    }
                });

                /**
                 * click the like button
                 * */
                shotDetailViewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shotFragment.like(shot.id, !shot.liked);
                    }
                });

                /**
                 * set the view of the like button
                 * 1. user likes this shot => pink
                 * 1. user not likes this shot => black
                 * */
                Drawable likeDrawable = shot.liked
                        ? ContextCompat.getDrawable(shotFragment.getContext(), R.drawable.ic_favorite_dribbble_18dp)
                        : ContextCompat.getDrawable(shotFragment.getContext(), R.drawable.ic_favorite_black_18dp);
                shotDetailViewHolder.likeButton.setImageDrawable(likeDrawable);

                break;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    /**
     * here define the view type
     * */
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_SHOT_IMAGE;
        } else {
            return VIEW_TYPE_SHOT_INFO;
        }
    }

    /**
     * implicit intent
     * do not 规定 intent 的对象
     * android sytem will find all the activities that its intent filter has the action and type
     * of this intent
     * */
    private void share(Context context) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shot.title + " " + shot.html_url);
        shareIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(shareIntent, "Share this amazing shot!"));
    }

    /**
     * pass the collectedBUcketIds to BucketListFragment
     * */
    private void bucket(Context context) {
        if (collectedBUcketIds != null) {
            Intent intent = new Intent(context, ChooseBucketActivity.class);
            intent.putStringArrayListExtra(BucketListFragment.KEY_CHOSEN_BUCKET_IDS,
                    collectedBUcketIds);
            shotFragment.startActivityForResult(intent, ShotFragment.REQ_CODE_BUCKET);
        }
    }

    public void updateCollectedBucketIds(List<String> collectedBucketIds) {
        if (this.collectedBUcketIds == null) {
            this.collectedBUcketIds = new ArrayList<>();
        }

        this.collectedBUcketIds.clear();
        this.collectedBUcketIds.addAll(collectedBucketIds);

        shot.bucketed = !this.collectedBUcketIds.isEmpty();
        notifyDataSetChanged();
    }

    public void updateCollectedBucketIds(@NonNull List<String> added,
                                         @NonNull List<String> removed) {
        if(collectedBUcketIds == null) {
            collectedBUcketIds = new ArrayList<>();
        }

        collectedBUcketIds.addAll(added);
        collectedBUcketIds.removeAll(removed);

        shot.bucketed = !collectedBUcketIds.isEmpty();
        shot.buckets_count += added.size() - removed.size();
        notifyDataSetChanged();
    }

    public List<String> getReadOnlyCollectedBucketIds() {
        return Collections.unmodifiableList(collectedBUcketIds);
    }
}
