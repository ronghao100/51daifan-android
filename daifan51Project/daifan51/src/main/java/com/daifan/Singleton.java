package com.daifan;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import com.daifan.domain.User;
import com.daifan.service.ImageLoader;
import com.daifan.service.PostService;
import com.daifan.service.StatusService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Singleton {


    public static final String DAIFAN_TAG = "51daifan";
    public static final String REST_API = "http://51daifan.sinaapp.com/api";

    private ImageLoader imageLoader;
    /**
     * Cached for booked user uid=>name mapping.
     */
    private Map<Integer, String> uidNames = new HashMap<Integer, String>();
    private User currUser;
    private PostService postService;
    private StatusService statusService;

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public String getCurrUid() {
        return currUser != null? currUser.getId() : null;
    }

    public void addCommentUidNames(HashMap<Integer, String> commentIdNames) {
        this.uidNames.putAll(commentIdNames);
    }

    public User getCurrUser() {
        return currUser;
    }

    public void setCurrUser(User currUser) {
        this.currUser = currUser;
    }

    public String getUNameById(String bookedUid) {
        int uid = 0;
        try {
            uid = Integer.parseInt(bookedUid);
        } catch (NumberFormatException e) {
            return bookedUid;
        }
        String s = this.uidNames.get(uid);
        return s != null? s : "";
    }

    public void addCommentUidNames(User currU) {
        this.uidNames.put(Integer.parseInt(currU.getId()), currU.getName());
    }

    public PostService getPostService() {
        return postService;
    }

    public void setPostService(PostService postService) {
        this.postService = postService;
    }

    static class SingleHolder {
        private static final Singleton single = new Singleton();
    }

    public StatusService getStatusService() {
        return statusService;
    }

    private Singleton() {
        this.postService = new PostService();
        this.statusService=new StatusService();
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

    public static boolean isIntentAvailable(Context context, final Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
