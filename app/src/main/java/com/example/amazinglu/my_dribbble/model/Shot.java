package com.example.amazinglu.my_dribbble.model;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.Map;

/**
 * Created by AmazingLu on 11/9/17.
 */

public class Shot {

    public static final String IMAGE_NORMAL = "normal";
    public static final String IMAGE_HIDPI = "hidpi";

    public String id;
    public String title;
    public String description;
    public String html_url;

    public int width;
    public int height;
    public Map<String, String> images;
    public boolean animated;

    public int views_count;
    public int likes_count;
    public int buckets_count;

    public Date created_at;

    public boolean liked;
    public boolean bucketed;

    public User user;

    @NonNull
    public String getImageUrl() {
        if (images == null) {
            return null;
        } else if (animated) {
            return images.get(IMAGE_NORMAL);
        } else {
            return images.containsKey(IMAGE_HIDPI)
                    ? images.get(IMAGE_HIDPI)
                    : images.get(IMAGE_NORMAL);
        }
    }
}
