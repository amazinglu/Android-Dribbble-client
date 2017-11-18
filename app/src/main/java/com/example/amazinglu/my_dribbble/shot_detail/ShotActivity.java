package com.example.amazinglu.my_dribbble.shot_detail;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.example.amazinglu.my_dribbble.base.SingleFragmentActivity;

public class ShotActivity extends SingleFragmentActivity {

    public static final String KEY_SHOT_TITLE = "shot_title";

    @NonNull
    @Override
    protected Fragment newFragment() {
        return ShotFragment.newInstance(getIntent().getExtras());
    }

    @NonNull
    @Override
    public String getActivityTitle() {
        return getIntent().getStringExtra(KEY_SHOT_TITLE);
    }
}
