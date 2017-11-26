package com.example.amazinglu.my_dribbble.base;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by AmazingLu on 11/26/17.
 */

public class ShotListSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public ShotListSpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = space;
    }
}
