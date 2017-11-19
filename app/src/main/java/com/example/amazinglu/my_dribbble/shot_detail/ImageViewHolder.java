package com.example.amazinglu.my_dribbble.shot_detail;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;

public class ImageViewHolder extends RecyclerView.ViewHolder {

    /**
     * 这里不用bind view的原因是layout本身就是一个image view
     * */
    SimpleDraweeView image;

    public ImageViewHolder(View itemView) {
        super(itemView);
        image = (SimpleDraweeView) itemView;
    }
}
