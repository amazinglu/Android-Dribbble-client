package com.example.amazinglu.my_dribbble.shot_list;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.amazinglu.my_dribbble.R;
import com.example.amazinglu.my_dribbble.base.SpaceItemdecoration;
import com.example.amazinglu.my_dribbble.model.Shot;
import com.example.amazinglu.my_dribbble.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShotListFragment extends android.support.v4.app.Fragment {

    @BindView(R.id.recycle_view) RecyclerView recyclerView;

    private static final int COUNT_PER_PAGE = 20;

    private ShotListAdapter adapter;

    public static ShotListFragment newInstance() {
        return new ShotListFragment();
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SpaceItemdecoration(
                getResources().getDimensionPixelSize(R.dimen.spacing_medium)));

        /**
         * load more data at a thread
         * */
        final android.os.Handler handler = new android.os.Handler();
        adapter = new ShotListAdapter(fakeData(0), new ShotListAdapter.LoadMoreListener() {
            @Override
            public void onLoadMore() {
//                Toast.makeText(getContext(), "load more called", Toast.LENGTH_LONG).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    List<Shot> moreData = fakeData(adapter.getDataCount() / COUNT_PER_PAGE);
                                    // get the data
                                    adapter.append(moreData);
                                    // set the show loading
                                    adapter.setShowLoading(moreData.size() >= COUNT_PER_PAGE);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    /**
     * assumes that there are only 3 pages of data
     * and each page has 20 shot list
     * */
    private List<Shot> fakeData(int page) {
        List<Shot> shotList = new ArrayList<>();
        Random random = new Random();

        int count = (page > 2) ? 10 : COUNT_PER_PAGE;
        for (int i = 0; i < count; ++i) {
            Shot shot = new Shot();
            shot.title = "shot" + i;
            shot.views_count = random.nextInt(10000);
            shot.likes_count = random.nextInt(200);
            shot.buckets_count = random.nextInt(50);
            shot.description = makeDescription();

            shot.user = new User();
            shot.user.name = shot.title + " author";

            shotList.add(shot);
        }
        return shotList;
    }

    private static final String[] words = {
            "bottle", "bowl", "brick", "building", "bunny", "cake", "car", "cat", "cup",
            "desk", "dog", "duck", "elephant", "engineer", "fork", "glass", "griffon", "hat", "key",
            "knife", "lawyer", "llama", "manual", "meat", "monitor", "mouse", "tangerine", "paper",
            "pear", "pen", "pencil", "phone", "physicist", "planet", "potato", "road", "salad",
            "shoe", "slipper", "soup", "spoon", "star", "steak", "table", "terminal", "treehouse",
            "truck", "watermelon", "window"
    };

    private static String makeDescription() {
        return TextUtils.join(" ", words);
    }
}
