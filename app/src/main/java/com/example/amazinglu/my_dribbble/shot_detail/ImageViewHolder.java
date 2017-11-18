package com.example.amazinglu.my_dribbble.shot_detail;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by AmazingLu on 11/17/17.
 */

public class ImageViewHolder extends RecyclerView.ViewHolder {

    ImageView image;

    public ImageViewHolder(View itemView) {
        super(itemView);
        image = (ImageView) itemView;
    }
}
