package com.daifan.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.daifan.R;
import com.daifan.Singleton;
import com.daifan.domain.User;
import com.daifan.service.PostService;
import com.daifan.service.UserService;
import com.daifan.service.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Activity which post new recipes.
 */
public class PostNewActivity extends BaseActivity {
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

        findViewById(R.id.postnew_post_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postNew();
            }
        });

        findViewById(R.id.postnew_add_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(PostNewActivity.this, v);
                popup.getMenuInflater().inflate(R.menu.pic, popup.getMenu());

                final Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (!Singleton.isIntentAvailable(PostNewActivity.this, takePhotoIntent))
                    popup.getMenu().removeItem(R.id.pic_menu_take_photo);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(android.view.MenuItem item) {

                        Intent intent = new Intent();
                        switch (item.getItemId()) {
                            case R.id.pic_menu_pick_photo:

                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent, 2);

                                return true;
                            case R.id.pic_menu_take_photo:
                                startActivityForResult(takePhotoIntent, 3);
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popup.show();
            }
        });
    }

    View insertPhoto(String path){
        Bitmap bm = decodeSampledBitmapFromUri(path, 220, 220);
        return insertImage(bm);
    }

    private View insertImage(Bitmap bm) {

        LinearLayout layout = (LinearLayout) findViewById(R.id.mygallery);

        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setLayoutParams(new LinearLayout.LayoutParams(220, 220));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(bm);

        layout.addView(imageView);
        return layout;
    }

    public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {
        Bitmap bm = null;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, options);

        return bm;
    }

    public int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }

        return inSampleSize;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(Singleton.DAIFAN_TAG, "the requestCode is " + requestCode + ", resultCode=" + resultCode);
        if (requestCode == 3) {
            Bundle extras = intent.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            this.insertImage(bitmap);
            return;
        } else if (requestCode == 2) {
            if (null == intent) {
                Toast.makeText(PostNewActivity.this, "添加图片失败!", Toast.LENGTH_SHORT).show();
                return;
            }
            Uri uri = intent.getData();
            String mPicPath = getRealPathFromURI(uri);
            Log.d(Singleton.DAIFAN_TAG, "pic url == " + mPicPath);
            this.insertPhoto(mPicPath);
            return;
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    // And to convert the image URI to the direct file system path of the image file
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void postNew() {
        if (runningPost != null) {
            return;
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
            runningPost = new PostNew();
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

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class PostNew extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return postService.postNew(countStr, eatDateStr, nameStr, descStr, currUid);
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
