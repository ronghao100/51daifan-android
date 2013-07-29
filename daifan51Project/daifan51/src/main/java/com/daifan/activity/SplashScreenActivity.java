package com.daifan.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.daifan.DaifanApplication;
import com.daifan.R;
import com.daifan.Singleton;
import com.daifan.activity.lib.PullToRefreshListView;
import com.daifan.domain.Post;

import java.util.ArrayList;

/**
 * Created by ronghao on 13-7-28.
 * load logo when startup
 */
public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new GetDataTask().execute();
    }

    private class GetDataTask extends AsyncTask<Void, Void, ArrayList<Post>> {

        @Override
        protected ArrayList<Post> doInBackground(Void... params) {
            // Simulates a background job.
            return Singleton.getInstance().getPostService().getPosts();
        }

        @Override
        protected void onPostExecute(ArrayList<Post> posts) {
            DaifanApplication daifanApplication = DaifanApplication.getDaifanApplication();
            daifanApplication.postList=posts;

            Intent i = new Intent(SplashScreenActivity.this, PostListActivity.class);
            i.putExtra("splash", true);
            startActivity(i);

            finish();
        }
    }
}
