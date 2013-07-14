package com.daifan.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.daifan.R;
import com.daifan.Singleton;
import com.daifan.activity.ImagesActivity;
import com.daifan.domain.Post;
import com.daifan.service.ImageLoader;

import java.util.ArrayList;

/**
 * Created by ronghao on 6/23/13.
 */
public class PostAdapter extends BaseAdapter {

    public static final int IMG_TAG_IMAGES = 1;
    private Activity activity;
    private ArrayList<Post> posts = new ArrayList<Post>();
    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;

    public PostAdapter(Activity activity, ArrayList<Post> posts) {
        this.activity = activity;
        this.posts = posts;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = Singleton.getInstance().getImageLoader();
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int i) {
        return posts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if (view == null)
            vi = inflater.inflate(R.layout.list_row, null);

        TextView title = (TextView) vi.findViewById(R.id.title); // title
        TextView desc = (TextView) vi.findViewById(R.id.desc); // artist name
        ImageView thumb_image = (ImageView) vi.findViewById(R.id.thumbnail); // thumb image
        TextView createdAtTxt = (TextView) vi.findViewById(R.id.createdAt);


        final Post post = posts.get(i);

        imageLoader.DisplayImage(post.getThumbnailUrl(), thumb_image);

        // Setting all values in listview
        title.setText(post.getUserName());
        desc.setText(post.getName() + " " + post.getDesc());

        long time = post.getCreatedAt().getTime();
        Log.d(Singleton.DAIFAN_TAG, "created at " + post.getCreatedAt());
        createdAtTxt.setText(DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(), 0));


        ImageView imageV = (ImageView) vi.findViewById(R.id.list_row_image);
        if (post.getImages().length == 0)
            imageV.setVisibility(View.GONE);
        else
            this.imageLoader.DisplayImage(Post.thumb(post.getImages()[0]), imageV);

        imageV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(activity.getApplicationContext(), ImagesActivity.class);
                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                login.putExtra("images", post.getImages());
                activity.startActivity(login);
                imageLoader.preload(post.getImages());
            }
        });

        final Button bookBtn = (Button) vi.findViewById(R.id.btnBooked);
        final String currUid = Singleton.getInstance().getCurrUid();
        final boolean booked = post.booked(currUid);
        if (booked) {
            bookBtn.setText(R.string.bookBtn_cancel);
        }
        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookBtn.setText(booked ? R.string.bookBtn_cancel : R.string.bookBtn_book);
                post.addBooked(currUid);
                //TODO: update server data
            }
        });

        final InputMethodManager inputManager =
                (InputMethodManager) activity.
                        getSystemService(Context.INPUT_METHOD_SERVICE);

        Button commentBtn = (Button) vi.findViewById(R.id.btnComment);
        final EditText commentTxt = (EditText) activity.findViewById(R.id.post_comment_txt);
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentTxt.setVisibility(View.VISIBLE);
                commentTxt.requestFocus();

                inputManager.showSoftInput(commentTxt, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        commentTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    //sendMessage();
                    handled = true;
                    commentTxt.setVisibility(View.INVISIBLE);
                    commentTxt.setText("");

                    inputManager.hideSoftInputFromWindow(
                            activity.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                }
                return handled;
            }
        });

        return vi;
    }

    class UpdateBookedTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object... params) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
