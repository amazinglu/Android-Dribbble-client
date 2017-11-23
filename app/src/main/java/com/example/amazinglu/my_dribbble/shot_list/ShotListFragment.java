package com.example.amazinglu.my_dribbble.shot_list;

import android.app.Fragment;
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
import com.example.amazinglu.my_dribbble.base.SpaceItemdecoration;
import com.example.amazinglu.my_dribbble.login.DribbbleFunc;
import com.example.amazinglu.my_dribbble.model.Shot;
import com.example.amazinglu.my_dribbble.model.User;

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
        /**
         * set the listener of refresh layout
         * */
        // set the refresh to fail when first load the shot
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AsyncTaskCompat.executeParallel(new LoadShotTask(true));
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SpaceItemdecoration(
                getResources().getDimensionPixelSize(R.dimen.spacing_medium)));

        /**
         * load more data at a thread
         * */
        adapter = new ShotListAdapter(new ArrayList<Shot>(), new ShotListAdapter.LoadMoreListener() {
            @Override
            public void onLoadMore() {
//                AsyncTaskCompat.executeParallel(new LoadShotTask(adapter.getDataCount() / COUNT_PER_PAGE + 1));
                AsyncTaskCompat.executeParallel(new LoadShotTask(false));
            }
        });
        recyclerView.setAdapter(adapter);

        // enable the refresh after the first loading of the fragment
        swipeRefreshLayout.setEnabled(true);
    }

    /**
     * use AsyncTask, OkHttp and JSON to get the shot back from Dribbble API
     * */
    private class LoadShotTask extends AsyncTask<Void, Void, List<Shot>> {

        int page;
        boolean refresh;

        public LoadShotTask(int page) {
            this.page = page;
        }

        public LoadShotTask(boolean refresh) {
            this.refresh = refresh;
        }

        @Override
        protected List<Shot> doInBackground(Void... voids) {
            int page = refresh ? 1 : adapter.getDataCount() / COUNT_PER_PAGE + 1;
            switch (listType) {
                case LIST_TYPE_POPULAR:
                    try {
                        return DribbbleFunc.getShots(page);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                case LIST_TYPE_LIKED:
                    try {
                        return DribbbleFunc.getLikedShots(page);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                default:
                    try {
                        return DribbbleFunc.getShots(page);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
            }
//            try {
//                // use OkHttp to go get request and return response
//                return refresh ? DribbbleFunc.getShots(1) : DribbbleFunc.getShots(page);
//            } catch (IOException e) {
//                e.printStackTrace();
//                return null;
//            }
        }

        @Override
        protected void onPostExecute(List<Shot> shots) {
            if (shots != null) {
                // pass the shots to adapter
                if (refresh) {
                    adapter.setData(shots);
                    swipeRefreshLayout.setRefreshing(false);
                    refresh = false;
                } else {
                    adapter.append(shots);
                }
            } else {
                Snackbar.make(getView(), "Error!", Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
