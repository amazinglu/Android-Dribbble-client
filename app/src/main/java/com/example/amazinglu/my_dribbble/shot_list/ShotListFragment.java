package com.example.amazinglu.my_dribbble.shot_list;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.amazinglu.my_dribbble.R;
import com.example.amazinglu.my_dribbble.base.DribbbleException;
import com.example.amazinglu.my_dribbble.base.DribbbleTask;
import com.example.amazinglu.my_dribbble.base.SpaceItemdecoration;
import com.example.amazinglu.my_dribbble.login.DribbbleFunc;
import com.example.amazinglu.my_dribbble.model.Shot;
import com.example.amazinglu.my_dribbble.model.User;
import com.example.amazinglu.my_dribbble.shot_detail.ShotFragment;
import com.example.amazinglu.my_dribbble.utils.ModelUtils;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShotListFragment extends android.support.v4.app.Fragment {

    public static final int REQ_CODE_SHOT = 100;
    public static final String KEY_LIST_TYPE = "listType";

    public static final int LIST_TYPE_POPULAR = 1;
    public static final int LIST_TYPE_LIKED = 2;

    @BindView(R.id.recycle_view) RecyclerView recyclerView;
    @BindView(R.id.recycler_view_refresh_container) SwipeRefreshLayout swipeRefreshLayout;

    private static final int COUNT_PER_PAGE = 12;

    private ShotListAdapter adapter;
    private int listType;

    public static ShotListFragment newInstance(int listType) {
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, listType);

        ShotListFragment shotListFragment = new ShotListFragment();
        shotListFragment.setArguments(args);
        return shotListFragment;
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
        // get the listType
        listType = getArguments().getInt(KEY_LIST_TYPE);

        // disable the refresh when first load the fragment
        swipeRefreshLayout.setEnabled(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SpaceItemdecoration(
                getResources().getDimensionPixelSize(R.dimen.spacing_medium)));

        /**
         * refresh the data
         * */
        // set the refresh to fail when first load the shot
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AsyncTaskCompat.executeParallel(new LoadShotTask(true));
            }
        });

        /**
         * load more data
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
    }

    /**
     * use AsyncTask, OkHttp and JSON to get the shot back from Dribbble API
     * */

    private class LoadShotTask extends DribbbleTask<Void, Void, List<Shot>> {

        private boolean refresh;
        private int page;

        public LoadShotTask(boolean refresh) {
            this.refresh = refresh;
        }

        @Override
        protected List<Shot> doJob(Void... params) throws DribbbleException, IOException {
            page = refresh ? 1 : adapter.getDataCount() / COUNT_PER_PAGE + 1;
            switch (listType) {
                case LIST_TYPE_POPULAR:
                    return DribbbleFunc.getShots(page);
                case LIST_TYPE_LIKED:
                    return DribbbleFunc.getLikedShots(page);
                default:
                    return DribbbleFunc.getShots(page);
            }
        }

        @Override
        protected void onSuccess(List<Shot> shots) {
            if (refresh) {
                adapter.setData(shots);
                swipeRefreshLayout.setRefreshing(false);
                refresh = false;
            } else {
                swipeRefreshLayout.setEnabled(true);
                // set the show loading
                adapter.setShowLoading(shots.size() == COUNT_PER_PAGE);
                adapter.append(shots);
            }
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }
}
