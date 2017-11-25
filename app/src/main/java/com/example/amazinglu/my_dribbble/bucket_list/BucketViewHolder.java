package com.example.amazinglu.my_dribbble.bucket_list;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amazinglu.my_dribbble.R;
import com.example.amazinglu.my_dribbble.base.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by AmazingLu on 11/10/17.
 */

public class BucketViewHolder extends BaseViewHolder {

    @BindView(R.id.bucket_name) TextView bucketName;
    @BindView(R.id.bucket_shot_count) TextView bucketShotCount;
    @BindView(R.id.bucket_shot_chosen) ImageView bucketChosen;
    @BindView(R.id.bucket_cover) View bucketCover;

    public BucketViewHolder(View itemView) {
        super(itemView);
    }
}
