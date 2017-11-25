package com.example.amazinglu.my_dribbble.bucket_list;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.example.amazinglu.my_dribbble.R;
import com.example.amazinglu.my_dribbble.base.SingleFragmentActivity;

import java.util.ArrayList;

public class ChooseBucketActivity extends SingleFragmentActivity {
    @NonNull
    @Override
    protected Fragment newFragment() {
        /**
         * get the chosen bucket from Shot adapter and pass it to BucketListFragment
         * since intent can only pass data to activity
         * we need this activity to reveice the data
         * */
        ArrayList<String> chosenBucketIds = getIntent().getStringArrayListExtra(
                BucketListFragment.KEY_CHOSEN_BUCKET_IDS);
        return BucketListFragment.newInstance(true, chosenBucketIds);
    }

    @NonNull
    @Override
    public String getActivityTitle() {
        return getString(R.string.choose_bucket);
    }
}
