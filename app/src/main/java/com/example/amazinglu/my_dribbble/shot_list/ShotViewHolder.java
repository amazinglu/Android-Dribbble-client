package com.example.amazinglu.my_dribbble.shot_list;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amazinglu.my_dribbble.R;
import com.example.amazinglu.my_dribbble.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;

/**
 * Created by AmazingLu on 11/9/17.
 */

public class ShotViewHolder extends BaseViewHolder {

    @BindView(R.id.shot_like_count) public TextView likeCount;
    @BindView(R.id.shot_bucket_count) public TextView bucketCount;
    @BindView(R.id.shot_view_count) public TextView viewCount;
    @BindView(R.id.shot_image) public SimpleDraweeView image;
    @BindView(R.id.shot_clickable_cover) View cover;

    public ShotViewHolder(View itemView) {
        super(itemView);
    }
}
