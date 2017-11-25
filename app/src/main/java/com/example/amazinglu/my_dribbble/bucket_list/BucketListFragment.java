package com.example.amazinglu.my_dribbble.bucket_list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.example.amazinglu.my_dribbble.auth_request.DribbbleFunc;
import com.example.amazinglu.my_dribbble.base.DribbbleException;
import com.example.amazinglu.my_dribbble.base.DribbbleTask;
import com.example.amazinglu.my_dribbble.base.SpaceItemdecoration;
import com.example.amazinglu.my_dribbble.model.Bucket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BucketListFragment extends Fragment {

    public static final int REQ_CODE_NEW_BUCKET = 100;
    public static final int REQ_CODE_DELETE_BUCKET = 200;
    public static final String KEY_CHOOSING_MODE = "choose_mode";
    public static final String KEY_CHOSEN_BUCKET_IDS = "chosen_bucket_ids";

    @BindView(R.id.bucket_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.fab) FloatingActionButton floatingActionButton;
    @BindView(R.id.bucket_refresh) SwipeRefreshLayout swipeRefreshLayout;

    private BuckListAdapter adapter;
    private boolean isChoosingMode;
    private List<String> chosenBucketIds;

    public static BucketListFragment newInstance() {
        return new BucketListFragment();
    }

    public static Fragment newInstance(boolean isChoosingMode,
                                       @NonNull ArrayList<String> chosenBucketIds) {
        Bundle args = new Bundle();
        args.putBoolean(KEY_CHOOSING_MODE, isChoosingMode);
        args.putStringArrayList(KEY_CHOSEN_BUCKET_IDS, chosenBucketIds);

        BucketListFragment fragment = new BucketListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // show the option menus
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bucket_recycler_view, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        isChoosingMode = getArguments().getBoolean(KEY_CHOOSING_MODE);
        /**
         * get the bucket id that was chosen for this shot
         * */
        if (isChoosingMode) {
            chosenBucketIds = getArguments().getStringArrayList(KEY_CHOSEN_BUCKET_IDS);
            if (chosenBucketIds == null) {
                chosenBucketIds = new ArrayList<>();
            }
        }

        swipeRefreshLayout.setEnabled(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SpaceItemdecoration(
                getResources().getDimensionPixelSize(R.dimen.spacing_medium)));
        /**
         * initialize the adapter
         * */
        adapter = new BuckListAdapter(new ArrayList<Bucket>(), getContext(), isChoosingMode,
                new BuckListAdapter.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                AsyncTaskCompat.executeParallel(new LoadBucketTask(false));
            }
        });
        recyclerView.setAdapter(adapter);

        /**
         * click the add bucket button
         * */
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * go to the dialog fragment
                 * */
                NewBucketDialogFragment dialogFragment = NewBucketDialogFragment.newInstance();
                dialogFragment.setTargetFragment(BucketListFragment.this, REQ_CODE_NEW_BUCKET);
                dialogFragment.show(getFragmentManager(), NewBucketDialogFragment.TAG);
            }
        });

        /**
         * click the refresh
         * */
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AsyncTaskCompat.executeParallel(new LoadBucketTask(true));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * back from dialog
         * update the new bucket to Dribbble API
         * */
        if (requestCode == REQ_CODE_NEW_BUCKET && resultCode == Activity.RESULT_OK) {
            String bucketName = data.getStringExtra(NewBucketDialogFragment.KEY_BUCKET_NAME);
            String bucketDescription = data.getStringExtra(NewBucketDialogFragment.KEY_BUCKET_DESCRIPTION);
            if (!TextUtils.isEmpty(bucketName)) {
                AsyncTaskCompat.executeParallel(new NewBucketTask(bucketName, bucketDescription));
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        /**
         * add the save button in the toolbar when in choosing mode
         * */
        if (isChoosingMode) {
            inflater.inflate(R.menu.bucket_list_choosing_mode_menus, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /**
         * click the save button
         * */
        if (item.getItemId() == R.id.save) {
            ArrayList<String> chosenBucketIds = adapter.getSelectBucketIds();

            // pass the updated chosenBucketIds to somewhere
            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra(KEY_CHOSEN_BUCKET_IDS, chosenBucketIds);
            getActivity().setResult(Activity.RESULT_OK, resultIntent);
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class LoadBucketTask extends DribbbleTask<Void, Void, List<Bucket>> {

        private int page;
        private boolean refresh;

        public LoadBucketTask(boolean refresh) {
            this.refresh = refresh;
        }

        @Override
        protected List<Bucket> doJob(Void... params) throws DribbbleException, IOException {
            page = refresh ? 1 : adapter.getDataCount() / DribbbleFunc.COUNT_PER_PAGE + 1;
            return DribbbleFunc.getUserBucket(page);
        }

        @Override
        protected void onSuccess(List<Bucket> buckets) {
            /**
             * no matter refresh or load more data
             * we need to check the showloading
             * */
            adapter.setShowLoading(buckets.size() == DribbbleFunc.COUNT_PER_PAGE);

            /**
             * in choosing mode, set the bucket.isChoosing
             * */
            if (isChoosingMode) {
                for (Bucket bucket : buckets) {
                    if (chosenBucketIds.contains(bucket.id)) {
                        bucket.isChoosing = true;
                    }
                }
            }

            if (refresh) {
                adapter.setData(buckets);
                swipeRefreshLayout.setRefreshing(false);
                refresh = false;
            } else {
                // no refresh in choosing mode
                if (!isChoosingMode) {
                    swipeRefreshLayout.setEnabled(true);
                }
                adapter.append(buckets);
            }
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    private class NewBucketTask extends DribbbleTask<Void, Void, Bucket> {

        private String bucketName;
        private String bucketDescription;

        public NewBucketTask(String bucketName, String bucketDescription) {
            this.bucketName = bucketName;
            this.bucketDescription = bucketDescription;
        }

        @Override
        protected Bucket doJob(Void... params) throws DribbbleException, IOException {
            return DribbbleFunc.newBucket(bucketName, bucketDescription);
        }

        @Override
        protected void onSuccess(Bucket bucket) {
            adapter.prepend(Collections.singletonList(bucket));
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }


}
