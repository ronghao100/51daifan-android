package com.daifan.activity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.androidquery.AQuery;
import com.costum.android.widget.PullAndLoadListView;
import com.daifan.R;
import com.daifan.Singleton;
import com.daifan.activity.adapter.StatusItemAdapter;
import com.daifan.domain.Status;
import com.daifan.service.StatusService;
import com.daifan.util.Preferences;
import com.daifan.util.Util;

import java.util.ArrayList;

public class PersonalCenterActivity extends BaseActivity {

    public static final String TAG = PersonalCenterActivity.class.getSimpleName();

    private PullAndLoadListView mListView = null;

    private Menu mOptionsMenu;

    protected int mSinceId = 0;

    protected int mMaxId = 0;

    private AQuery aq;
    private StatusItemAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        setTitle(Singleton.getInstance().getCurrUser().getName());

        aq = new AQuery(this);
        mListView = ((PullAndLoadListView) findViewById(R.id.msg_list_item));
        // Set a listener to be invoked when the list should be refreshed.
        mListView.setOnRefreshListener(new PullAndLoadListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
                refreshStatus(mMaxId);
            }
        });

        mListView.setOnLoadMoreListener(new PullAndLoadListView.OnLoadMoreListener() {

            public void onLoadMore() {
                loadMoreData(mSinceId);
            }
        });

        mListView.setLastUpdated(getLastSyncTime(Preferences.PREF_LAST_SYNC_TIME));

        mAdapter = new StatusItemAdapter(this, getDaifanApplication().getImageLoader());
        getStatuses();
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void getStatuses() {
        hideErrorIndicator();
        showLoadingIndicator();

        StatusService statusService = Singleton.getInstance().getStatusService();
        ArrayList<Status> statuses = statusService.getStatuses();
        mAdapter.addNewestStatuses(statuses);
        mMaxId = statuses.get(0).id;
        mSinceId = statuses.get(statuses.size() - 1).id;

        hideLoadingIndicator();
        aq.id(R.id.placeholder_error).gone();

        showContents();
        mAdapter.notifyDataSetChanged();
        setLastSyncTime(Util.getNowLocaleTime());
    }

    private void showContents() {
        aq.id(R.id.timelist_list).visible();

        // FIXME: put it here, else will pop up "Tap to Refresh"
        mListView.setAdapter(mAdapter);
    }

    private void refreshStatus(int maxId) {
        StatusService statusService = Singleton.getInstance().getStatusService();
        ArrayList<Status> statuses = statusService.getLatestStatuses(maxId);
        mAdapter.addNewestStatuses(statuses);
        mMaxId = statuses.get(0).id;

        mAdapter.notifyDataSetChanged();
        // Call onRefreshComplete when the list has been refreshed.
        mListView.onRefreshComplete();
        mListView.setLastUpdated(getLastSyncTime(
                Preferences.PREF_LAST_SYNC_TIME));

        setLastSyncTime(Util.getNowLocaleTime());

        if (statuses.size() > 0) {
            displayToast(String.format(getResources().getString(
                    R.string.new_blog_toast), statuses.size()));
        } else {
            displayToast(R.string.no_new_blog_toast);
        }
    }

    private void loadMoreData(int sinceId) {
        StatusService statusService = Singleton.getInstance().getStatusService();
        ArrayList<Status> statuses = statusService.getOldestPosts(sinceId);
        mAdapter.addNewestStatuses(statuses);
        mSinceId = statuses.get(statuses.size() - 1).id;

        mAdapter.notifyDataSetChanged();
        // Call onRefreshComplete when the list has been refreshed.
        mListView.onLoadMoreComplete();

        setLastSyncTime(Util.getNowLocaleTime());
    }

    private void setLastSyncTime(String time) {
        SharedPreferences.Editor editor = Preferences.get(this).edit();
        editor.putString(Preferences.PREF_LAST_SYNC_TIME, time);
        editor.commit();
    }

    private String getLastSyncTime(String pre) {
        SharedPreferences prefs = Preferences.get(this);
        String time = prefs.getString(pre, "");
        return time;
    }

    private void showLoadingIndicator() {
        aq.id(R.id.placeholder_loading).visible();
    }

    private void hideLoadingIndicator() {
        aq.id(R.id.placeholder_loading).gone();
    }

    private void showErrorIndicator() {
        aq.id(R.id.placeholder_error).visible();
    }

    private void hideErrorIndicator() {
        aq.id(R.id.placeholder_error).gone();
    }
}
