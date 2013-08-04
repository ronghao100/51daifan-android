package com.daifan.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.daifan.DaifanApplication;
import com.daifan.R;
import com.daifan.Singleton;
import com.daifan.domain.Post;

import java.util.ArrayList;

/**
 * Created by ronghao on 13-7-28.
 * load logo when startup
 */
public class SplashScreenActivity extends Activity {
    /** Max waiting time in the splash view */
    public static final int MAX_WAIT_SECONDS = 5;
    private volatile boolean GONE_TO_POSTLIST = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new GetDataTask().execute();

        postDelay(System.currentTimeMillis(), new Handler());
    }

    private void postDelay(final long start, final Handler ha) {
        ha.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (GONE_TO_POSTLIST)
                    return;

                if (System.currentTimeMillis()-start > MAX_WAIT_SECONDS * 1000)
                    gotoPostList();
                else
                    postDelay(start, ha);
            }
        }, 1000);
    }

    private void gotoPostList() {
        if (GONE_TO_POSTLIST)
            return;

        this.GONE_TO_POSTLIST = true;
        Intent i = new Intent(this, PostListActivity.class);
        i.putExtra("splash", true);
        startActivity(i);

        finish();
    }

    private class GetDataTask extends AsyncTask<Void, Void, ArrayList<Post>> {

        @Override
        protected ArrayList<Post> doInBackground(Void... params) {
            // Simulates a background job.
            return Singleton.getInstance().getPostService().getPosts();
        }

        @Override
        protected void onPostExecute(ArrayList<Post> posts) {
            if (posts != null) {
                DaifanApplication.getDaifanApplication().postList=posts;
                gotoPostList();
            }
        }
    }
}
