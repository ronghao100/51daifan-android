package com.daifan.activity;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.daifan.DaifanApplication;
import com.daifan.R;

/**
 * Created by ronghao on 13-7-20.
 * integrate toast and SherlockFragmentActivity
 */
public class BaseActivity extends SherlockFragmentActivity {
    private static final int TOAST_DURATION = Toast.LENGTH_SHORT;

    protected DaifanApplication mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock_Light);
        super.onCreate(savedInstanceState);

        mApplication = (DaifanApplication) getApplication();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (this instanceof PostListActivity) {
                    return false;
                }

                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * @param resId resource id
     */
    public void displayToast(int resId) {
        Toast.makeText(this, resId, TOAST_DURATION).show();
    }

    /**
     * @param text desplay text
     */
    public void displayToast(CharSequence text) {
        Toast.makeText(this, text, TOAST_DURATION).show();
    }

    public DaifanApplication getDaifanApplication() {
        return mApplication;
    }
}
