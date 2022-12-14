package com.example.dayout.helpers.view;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import static com.example.dayout.api.ApiClient.BASE_URL;

public class ImageViewer {

    public static  String IMAGE_BASE_URL = BASE_URL.substring(0, BASE_URL.length() - 1);

    public static void downloadImageAndCached(Context context, ImageView imv, int placeHolder, String url) {
        Glide.with(context)
                .load(url)
                .placeholder(placeHolder)
                .into(imv);
    }

    public static void downloadImage(Context context, ImageView imv, int placeHolder, String url) {
        Glide.with(context)
                .load(url)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(placeHolder)
                .into(imv);
    }

    public static void downloadCircleImage(Context context, ImageView imv, int placeHolder, String url) {
        Glide.with(context)
                .load(url)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .circleCrop()
                .placeholder(placeHolder)
                .into(imv);
    }
}
