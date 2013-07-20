package com.daifan;

import android.app.Application;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.BitmapAjaxCallback;

/**
 * Created by ronghao on 13-7-20.
 * load self info
 */
public class DaifanApplication extends Application {

    private static DaifanApplication daifanApplication;
    private final LruCache<String, Bitmap> mImageCache = new LruCache<String, Bitmap>(20);
    private ImageLoader mImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();

        daifanApplication = this;

        RequestQueue queue = Volley.newRequestQueue(this);

        ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache() {
            @Override
            public void putBitmap(String key, Bitmap value) {
                mImageCache.put(key, value);
            }

            @Override
            public Bitmap getBitmap(String key) {
                return mImageCache.get(key);
            }
        };

        mImageLoader = new ImageLoader(queue, imageCache);

        // set the max number of concurrent network connections, default is 4
        AjaxCallback.setNetworkLimit(8);

        // set the max number of icons (image width <= 50) to be cached in
        // memory, default is 20
        BitmapAjaxCallback.setIconCacheLimit(40);

        // set the max number of images (image width > 50) to be cached in
        // memory, default is 20
        BitmapAjaxCallback.setCacheLimit(40);

        // set the max size of an image to be cached in memory, default is 1600
        // pixels (ie. 400x400)
        BitmapAjaxCallback.setPixelLimit(400 * 400);

        // set the max size of the memory cache, default is 1M pixels (4MB)
        BitmapAjaxCallback.setMaxPixelLimit(2000000);
    }

    @Override
    public void onLowMemory() {
        // clear all memory cached images when system is in low memory
        // note that you can configure the max image cache count, see
        // CONFIGURATION
        BitmapAjaxCallback.clearCache();
    }

    public static DaifanApplication getDaifanApplication() {
        return daifanApplication;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
