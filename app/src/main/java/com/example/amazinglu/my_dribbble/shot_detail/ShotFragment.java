package com.example.amazinglu.my_dribbble.shot_detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.amazinglu.my_dribbble.R;
import com.example.amazinglu.my_dribbble.base.DribbbleException;
import com.example.amazinglu.my_dribbble.base.DribbbleTask;
import com.example.amazinglu.my_dribbble.auth_request.DribbbleFunc;
import com.example.amazinglu.my_dribbble.bucket_list.BucketListFragment;
import com.example.amazinglu.my_dribbble.model.Bucket;
import com.example.amazinglu.my_dribbble.model.Shot;
import com.example.amazinglu.my_dribbble.utils.ModelUtils;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShotFragment extends Fragment {

    @BindView(R.id.recycle_view) RecyclerView recyclerView;
    @BindView(R.id.recycler_view_refresh_container) SwipeRefreshLayout swipeRefreshLayout;

    public static final String KEY_SHOT = "shot";
    public static final int REQ_CODE_BUCKET = 100;

    private boolean isLiking;
    private Shot shot;
    private ShotAdapter adapter;

    public static ShotFragment newInstance(@NonNull Bundle args) {
        ShotFragment shotFragment = new ShotFragment();
        /**
         * get the intent extra content from ShotActivity
         * */
        shotFragment.setArguments(args);
        return shotFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        swipeRefreshLayout.setEnabled(false);

        shot = ModelUtils.toObject(getArguments().getString(KEY_SHOT),
                            new TypeToken<Shot>(){});
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ShotAdapter(shot, this);
        recyclerView.setAdapter(adapter);

        /**
         * get all the bucket id of current shot
         * */
        AsyncTaskCompat.executeParallel(new LoadCollectedBucketIdsTask());

        /**
         * check the like status of a shot
         * */
        isLiking = true;
        AsyncTaskCompat.executeParallel(new CheckLikeTask());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_BUCKET && resultCode == Activity.RESULT_OK) {
            List<String> chosenBUcketIds = data.getStringArrayListExtra(BucketListFragment.KEY_CHOSEN_BUCKET_IDS);
            List<String> addBucketIds = new ArrayList<>();
            List<String> removeBucketIds = new ArrayList<>();
            List<String> collectedBucketIds = adapter.getReadOnlyCollectedBucketIds();

            // get the add bucket ids
            for (String chosenBucketId : chosenBUcketIds) {
                if (!collectedBucketIds.contains(chosenBucketId)) {
                    addBucketIds.add(chosenBucketId);
                }
            }

            // get the remove bucket ids
            for (String collectedBucketId : collectedBucketIds) {
                if (!chosenBUcketIds.contains(collectedBucketId)) {
                    removeBucketIds.add(collectedBucketId);
                }
            }

            AsyncTaskCompat.executeParallel(new UpdateBucketTask(addBucketIds, removeBucketIds));
        }
    }

    /**
     * set the result intent to update the like and bucket count for shot list
     * */
    private void setResult() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(KEY_SHOT, ModelUtils.toString(shot, new TypeToken<Shot>(){}));
        getActivity().setResult(Activity.RESULT_OK, resultIntent);
    }

    /**
     * what to do when click the like button
     * */
    public void like(@NonNull String shotId, boolean like) {
        if (!isLiking) {
            isLiking = true;
            AsyncTaskCompat.executeParallel(new LikeTask(shotId, like));
        }
    }

    /**
     * update the info of like and unlike via HTTP request
     * */
    private class LikeTask extends DribbbleTask<Void, Void, Void> {

        private String id;
        private boolean like;

        public LikeTask(String shotId, boolean like) {
            this.id = shotId;
            this.like = like;
        }

        @Override
        protected Void doJob(Void... params) throws DribbbleException, IOException {
            if (like) {
                DribbbleFunc.likeShot(id);
            } else {
                DribbbleFunc.unlikeShot(id);
            }
            return null;
        }

        @Override
        protected void onSuccess(Void aVoid) {
            isLiking = false;
            shot.liked = like;
            shot.likes_count += like ? 1 : -1;
            recyclerView.getAdapter().notifyDataSetChanged();

            setResult();
        }

        @Override
        protected void onFailed(DribbbleException e) {
            isLiking = false;
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * get the like status of a shot via HTTP get request
     * */
    private class CheckLikeTask extends DribbbleTask<Void, Void, Boolean> {
        @Override
        protected Boolean doJob(Void... params) throws DribbbleException, IOException {
            return DribbbleFunc.isLikingShot(shot.id);
        }

        @Override
        protected void onSuccess(Boolean res) {
            isLiking = false;
            shot.liked = res;
            recyclerView.getAdapter().notifyDataSetChanged();
        }

        @Override
        protected void onFailed(DribbbleException e) {
            isLiking = false;
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * get the user's bucket and the bucket includes the current shot
     * */
    private class LoadCollectedBucketIdsTask extends DribbbleTask<Void, Void, List<String>> {

        @Override
        protected List<String> doJob(Void... params) throws DribbbleException, IOException {
            List<Bucket> shotBuckets = DribbbleFunc.getShotBuckets(shot.id);
            List<Bucket> userBuckets = DribbbleFunc.getUserBucket();

            /**
             * get the interception of shotBuckets and userBuckets
             * */
            Set<String> userBucketIds = new HashSet<>();
            for (Bucket bucket : userBuckets) {
                userBucketIds.add(bucket.id);
            }
            List<String> collectedBucketIds = new ArrayList<>();
            for (Bucket bucket : shotBuckets) {
                if (userBucketIds.contains(bucket.id)) {
                    collectedBucketIds.add(bucket.id);
                }
            }

            return collectedBucketIds;
        }

        @Override
        protected void onSuccess(List<String> collectedBucketIds) {
            adapter.updateCollectedBucketIds(collectedBucketIds);
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * update the bucket list for a given shot
     * */
    private class UpdateBucketTask extends DribbbleTask<Void, Void, Void> {

        private List<String> added;
        private List<String> removed;

        public UpdateBucketTask(@NonNull List<String> addBucketIds,
                                @NonNull List<String> removeBucketIds) {
            this.added = addBucketIds;
            this.removed = removeBucketIds;
        }

        @Override
        protected Void doJob(Void... params) throws DribbbleException, IOException {
            for (String addId : added) {
                DribbbleFunc.addBucketShot(addId, shot.id);
            }
            for (String removeId : removed) {
                DribbbleFunc.removeBucketShot(removeId, shot.id);
            }
            return null;
        }

        @Override
        protected void onSuccess(Void aVoid) {
            adapter.updateCollectedBucketIds(added, removed);
            setResult();
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

}
