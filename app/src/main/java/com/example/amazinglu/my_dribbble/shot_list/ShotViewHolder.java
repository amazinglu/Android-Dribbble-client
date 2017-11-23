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

    @BindView(R.id.shot_like_count) TextView likeCount;
    @BindView(R.id.shot_bucket_count) TextView bucketCount;
    @BindView(R.id.shot_view_count) TextView viewCount;
    @BindView(R.id.shot_image) SimpleDraweeView image;
    @BindView(R.id.shot_clickable_cover) View cover;

    public ShotViewHolder(View itemView) {
        super(itemView);
    }
}
