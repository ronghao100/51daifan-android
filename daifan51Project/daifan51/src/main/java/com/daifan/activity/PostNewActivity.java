package com.daifan.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.daifan.service.ImageUploader;
import com.daifan.R;
import com.daifan.Singleton;
import com.daifan.domain.User;
import com.daifan.service.PostService;
import com.daifan.service.UserService;
import com.daifan.service.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Activity which post new recipes.
 */
public class PostNewActivity extends BaseActivity implements ThumbnailsFragment.ThumbnailsConfig {
    public static final int ITEM_ID_POST = 1;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private PostNew runningPost = null;

    // UI references.
    private View postForm;
    private View postStatusView;
    private TextView postMsgView;

    private EditText countTxt;
    private EditText nameTxt;
    private EditText eatDateTxt;
    private EditText descTxt;

    private PostService postService;
    private UserService userService;
    private String eatDateStr;
    private String countStr;
    private String nameStr;
    private String descStr;
    private String currUid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Sherlock_Light);
        super.onCreate(savedInstanceState);

        if (null == postService)
            postService = new PostService();
        if (null == userService)
            userService = new UserService(this.getApplicationContext());

        setContentView(R.layout.activity_postnew);

        // Set up the login form.
        countTxt = (EditText) findViewById(R.id.postnew_count);
        eatDateTxt = (EditText) findViewById(R.id.postnew_eatdate);
        descTxt = (EditText) findViewById(R.id.postnew_desc);
        nameTxt = (EditText) findViewById(R.id.postnew_name);

        eatDateTxt.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()+24 * 3600*1000)));

        descTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.ime_action_postnew || id == EditorInfo.IME_NULL) {
                    postNew();
                    return true;
                }
                return false;
            }
        });

        postForm = findViewById(R.id.postnew_form);
        postStatusView = findViewById(R.id.postnew_status);
        postMsgView = (TextView) findViewById(R.id.postnew_status_message);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(0, ITEM_ID_POST, 0, R.string.action_postnew_post);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == ITEM_ID_POST) {
            postNew();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void postNew() {

        ThumbnailsFragment f = (ThumbnailsFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.thumbnails_grid);

        if (runningPost != null) {
            return;
        }

        String[] imgs = {};
        if (f.getLoader() != null) {
            imgs = f.getLoader().getNewImages();
        }

        // Reset errors.
        this.eatDateTxt.setError(null);
        this.nameTxt.setError(null);
        this.countTxt.setError(null);
        this.descTxt.setError(null);

        eatDateStr = this.eatDateTxt.getText().toString();
        countStr = this.countTxt.getText().toString();
        nameStr = this.nameTxt.getText().toString();
        descStr = this.descTxt.getText().toString();

        boolean hasError = false;
        View focusView = null;

        if (TextUtils.isEmpty(eatDateStr)) {
            this.eatDateTxt.setError(getString(R.string.error_field_required));
            focusView = this.eatDateTxt;
            hasError = true;
        }

        if (TextUtils.isEmpty(countStr)) {
            this.countTxt.setError(getString(R.string.error_field_required));
            if (null == focusView) focusView = this.countTxt;
            hasError = true;
        }

        if (TextUtils.isEmpty(nameStr)) {
            this.nameTxt.setError(getString(R.string.error_field_required));
            if(focusView == null) focusView = this.nameTxt;
            hasError = true;
        }

        if (TextUtils.getTrimmedLength(descStr)< 5) {
            this.descTxt.setError(getString(R.string.error_postnew_desc_minlen));
            if(focusView == null) focusView = this.descTxt;
            hasError = true;
        }

        User currU = this.userService.getCurrUser();
        if (currU == null) {
            //TODO: redirect to login
        } else
            this.currUid = currU.getId();


        if (hasError) {
            focusView.requestFocus();
        } else {
            postMsgView.setText(R.string.postnew_progress_text);
            Utils.swithLoadingView(true, postStatusView, postForm, switchAnimTime());
            runningPost = new PostNew(imgs);
            runningPost.execute((Void) null);
        }
    }


    private int switchAnimTime() {
        return getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }

    @Override
    public boolean supportNew() {
        return true;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class PostNew extends AsyncTask<Void, Void, Boolean> {
        private String[] imgs;
        public PostNew(String[] imgs) {
            this.imgs = imgs;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                List<String> thumbnails = new ArrayList<String>(this.imgs.length);
                for (String p : this.imgs) {
                    Log.d(Singleton.DAIFAN_TAG, "start uploading new image:" + p);
                    String img = new ImageUploader().upload(p);
                    if (img != null)
                        thumbnails.add(img);
                    else {
                        Log.e(Singleton.DAIFAN_TAG, "thumbnails failed to upload " + p);
                    }
                    //TODO: give user a tips for uploading failed pictures.
                }
                return postService.postNew(countStr, eatDateStr, nameStr, descStr, currUid, thumbnails);
            }catch (Exception e){
                Log.e(Singleton.DAIFAN_TAG, "Exception when postNew " + e.getMessage(), e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            runningPost = null;

            if (success) {
                Intent postList=new Intent(getApplicationContext(),PostListActivity.class);
                postList.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(postList);
                finish();
            } else {
                postMsgView.setText(getString(R.string.error_postnew_post_failed));
                postMsgView.requestFocus();

                postMsgView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    Utils.swithLoadingView(false, postStatusView, postForm, switchAnimTime());
                    }
                }, 2000);
            }
        }

        @Override
        protected void onCancelled() {
            runningPost = null;
            Utils.swithLoadingView(false, postStatusView, postForm, switchAnimTime());
        }
    }
}
