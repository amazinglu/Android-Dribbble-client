package com.example.amazinglu.my_dribbble.shot_detail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.amazinglu.my_dribbble.R;
import com.example.amazinglu.my_dribbble.model.Shot;
import com.example.amazinglu.my_dribbble.model.User;
import com.example.amazinglu.my_dribbble.utils.ModelUtils;
import com.google.gson.reflect.TypeToken;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShotFragment extends Fragment {

    @BindView(R.id.recycle_view) RecyclerView recyclerView;
    @BindView(R.id.recycler_view_refresh_container) SwipeRefreshLayout swipeRefreshLayout;

    public static final String KEY_SHOT = "shot";

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

        Shot shot = ModelUtils.toObject(getArguments().getString(KEY_SHOT),
                            new TypeToken<Shot>(){});
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ShotAdapter(shot));
    }
}
