package com.example.amazinglu.my_dribbble.shot_list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.amazinglu.my_dribbble.R;
import com.example.amazinglu.my_dribbble.base.DribbbleException;
import com.example.amazinglu.my_dribbble.base.DribbbleTask;
import com.example.amazinglu.my_dribbble.base.ShotListSpaceItemDecoration;
import com.example.amazinglu.my_dribbble.base.SpaceItemdecoration;
import com.example.amazinglu.my_dribbble.auth_request.DribbbleFunc;
import com.example.amazinglu.my_dribbble.bucket_list.BucketListFragment;
import com.example.amazinglu.my_dribbble.bucket_list.NewBucketDialogFragment;
import com.example.amazinglu.my_dribbble.model.Shot;
import com.example.amazinglu.my_dribbble.shot_detail.ShotFragment;
import com.example.amazinglu.my_dribbble.utils.ModelUtils;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShotListFragment extends android.support.v4.app.Fragment {

    public static final int REQ_CODE_SHOT = 100;
    public static final int REQ_CODE_COMFRIN_DELETE = 101;
    public static final String KEY_LIST_TYPE = "listType";
    public static final String KEY_BUCKET_ID = "bucketId";
    public static final String KEY_TARGET_FRAGMENT = "target_fragment";
    public static final String KEY_DELETE_BUCKET_ID = "delete_bucket_id";

    public static final int LIST_TYPE_POPULAR = 1;
    public static final int LIST_TYPE_LIKED = 2;
    public static final int LIST_TYPE_BUCKET = 3;

    @BindView(R.id.recycle_view) RecyclerView recyclerView;
    @BindView(R.id.recycler_view_refresh_container) SwipeRefreshLayout swipeRefreshLayout;

    private ShotListAdapter adapter;
    private int listType;
    private String bucket_id;
    private BucketListFragment bucketListFragment;

    /**
     * create a new instance of ShotListFragment (popular and like type)
     * */
    private ShotListFragment() {}

    public static ShotListFragment newInstance(int listType) {
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, listType);

        ShotListFragment shotListFragment = new ShotListFragment();
        shotListFragment.setArguments(args);
        return shotListFragment;
    }

    /**
     * create a new instance of ShotListFragment (bucket type)
     * */
    public static Fragment newBucketListInstance(int listType, String bucketId) {
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, listType);
        args.putString(KEY_BUCKET_ID, bucketId);

        ShotListFragment fragment = new ShotListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * if the parent activity already do something to menus
         * this function will tell the OD that this fragment would like to participate
         * in populating the options menu by receiving a call to
         * onCreateOptionsMenu(Menu, MenuInflater)
         * */
        setHasOptionsMenu(true);
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
        /**
         * get the listType
         * */
        listType = getArguments().getInt(KEY_LIST_TYPE);
        if (listType == LIST_TYPE_BUCKET) {
            bucket_id = getArguments().getString(KEY_BUCKET_ID);
        }

        // disable the refresh when first load the fragment
        swipeRefreshLayout.setEnabled(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new ShotListSpaceItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.spacing_medium)));

        /**
         * refresh data listener
         * */
        // set the refresh to fail when first load the shot
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AsyncTaskCompat.executeParallel(new LoadShotTask(true));
            }
        });

        /**
         * set the adapter
         * */
        adapter = new ShotListAdapter(new ArrayList<Shot>(), this,
                new ShotListAdapter.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                AsyncTaskCompat.executeParallel(new LoadShotTask(false));
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * hear back from shotAdapter
         * update the shot count and like count
         * */
        if (requestCode == REQ_CODE_SHOT && resultCode == Activity.RESULT_OK) {
            Shot updatedShot = ModelUtils.toObject(data.getStringExtra(ShotFragment.KEY_SHOT),
                    new TypeToken<Shot>(){});
            for (Shot shot : adapter.getData()) {
                if (TextUtils.equals(shot.id, updatedShot.id)) {
                    shot.likes_count = updatedShot.likes_count;
                    shot.buckets_count = updatedShot.buckets_count;
                    adapter.notifyDataSetChanged();
                    return;
                }
            }
        }
        /**
         * hear back from delete bucket dialog
         * delete the current bucket
         * */
        if (requestCode == REQ_CODE_COMFRIN_DELETE && resultCode == Activity.RESULT_OK) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(KEY_DELETE_BUCKET_ID, bucket_id);
            getActivity().setResult(Activity.RESULT_OK, resultIntent);
            getActivity().finish();
        }
    }

    /**
     * buttons on option menus
     * */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (listType == LIST_TYPE_BUCKET) {
            inflater.inflate(R.menu.shot_list_bucket_mode_menus, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            DeleteBucketDialogFragment dialogFragment = DeleteBucketDialogFragment.newInstance();
            dialogFragment.setTargetFragment(ShotListFragment.this, REQ_CODE_COMFRIN_DELETE);
            dialogFragment.show(getFragmentManager(), DeleteBucketDialogFragment.TAG);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * load shot task
     * */
    private class LoadShotTask extends DribbbleTask<Void, Void, List<Shot>> {

        private boolean refresh;
        private int page;

        public LoadShotTask(boolean refresh) {
            this.refresh = refresh;
        }

        @Override
        protected List<Shot> doJob(Void... params) throws DribbbleException, IOException {
            page = refresh ? 1 : adapter.getDataCount() / DribbbleFunc.COUNT_PER_PAGE + 1;
            switch (listType) {
                case LIST_TYPE_POPULAR:
                    return DribbbleFunc.getShots(page);
                case LIST_TYPE_LIKED:
                    return DribbbleFunc.getLikedShots(page);
                case LIST_TYPE_BUCKET:
                    String bucketId = getArguments().getString(KEY_BUCKET_ID);
                    return DribbbleFunc.getBucketShots(bucketId, page);
                default:
                    return DribbbleFunc.getShots(page);
            }
        }

        @Override
        protected void onSuccess(List<Shot> shots) {
            // set the show loading
            adapter.setShowLoading(shots.size() == DribbbleFunc.COUNT_PER_PAGE);

            if (refresh) {
                adapter.setData(shots);
                swipeRefreshLayout.setRefreshing(false);
                refresh = false;
            } else {
                swipeRefreshLayout.setEnabled(true);
                adapter.append(shots);
            }
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }
}
