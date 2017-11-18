package com.example.amazinglu.my_dribbble.shot_detail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.amazinglu.my_dribbble.R;
import com.example.amazinglu.my_dribbble.model.Shot;
import com.example.amazinglu.my_dribbble.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by AmazingLu on 11/17/17.
 */

public class ShotFragment extends Fragment {

    @BindView(R.id.recycle_view) RecyclerView recyclerView;

    public static ShotFragment newInstance(@NonNull Bundle args) {
        ShotFragment shotFragment = new ShotFragment();
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ShotAdapter(fakeData()));
    }

    private Shot fakeData() {
        Shot shot = new Shot();
        shot.title = "shot title";
        shot.description = "the description of the shot";
        shot.likes_count = 23;
        shot.buckets_count = 32;
        shot.views_count = 44;
        shot.user = new User();
        shot.user.name = "user name";
        return shot;
    }
}
