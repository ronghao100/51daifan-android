package com.daifan;

import android.content.Context;
import com.daifan.service.ImageLoader;

public class Singleton {


    public static final String DAIFAN_TAG = "51daifan";

    private ImageLoader imageLoader;
    private String currUid;

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public String getCurrUid() {
        return currUid;
    }

    public void setCurrUid(String currUid) {
        this.currUid = currUid;
    }

    static class SingleHolder {
        private static final Singleton single = new Singleton();
    }

    public static Singleton getInstance() {
        return SingleHolder.single;
    }

    private volatile boolean imageLoaderInited = false;

    public void initImageLoader(Context appCtx) {
        if (!imageLoaderInited) {
            synchronized (this) {
                if (this.imageLoaderInited)
                    return;
                imageLoader = new ImageLoader(appCtx);
                this.imageLoaderInited = true;
            }
        }
    }
}
