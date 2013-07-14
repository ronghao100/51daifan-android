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
import com.daifan.activity.PostListActivity;
import com.daifan.domain.Comment;
import com.daifan.domain.Post;
import com.daifan.domain.User;
import com.daifan.service.ImageLoader;
import com.daifan.service.PostService;

import java.util.ArrayList;
import java.util.List;

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


        final LinearLayout commentContainers = (LinearLayout) vi.findViewById(R.id.list_row_comments_container);
        final TextView bookedUNameTxt = (TextView) vi.findViewById(R.id.booked_uname_txt);
        if (post.getBookedUids().length > 0) {
            bookedUNameTxt.setText(activity.getString(R.string.booked_names_prefix) + post.getBookedUNames());
            commentContainers.setVisibility(View.VISIBLE);
        }

        commentContainers.removeViews(1, commentContainers.getChildCount()-1);

        if (post.getComments().size() > 0) {
            for (Comment cm : post.getComments()) {
                TextView tx = new TextView(activity);
                tx.setText(Singleton.getInstance().getUNameById(String.valueOf(cm.getUid())) + ": " + cm.getComment());
                commentContainers.addView(tx);
            }
            commentContainers.setVisibility(View.VISIBLE);
        }

        final Button bookBtn = (Button) vi.findViewById(R.id.btnBooked);
        final User currU = Singleton.getInstance().getCurrUser();
        final boolean booked = (currU == null ? false : post.booked(currU.getId()));
        if (booked) {
            bookBtn.setText(R.string.bookBtn_cancel);
        }
        if (post.outofOrder()) {
            //bookBtn.setTextColor(R.colors.grey);
        }


        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (post.outofOrder() && !booked) {
                    Toast.makeText(activity, R.string.out_of_order, Toast.LENGTH_LONG).show();
                    bookBtn.setText(activity.getString(R.string.out_of_order));
                    return;
                }

                if (currU == null) {
                    Toast.makeText(activity, R.string.login_required, Toast.LENGTH_LONG).show();
                    return;
                }

                final boolean booked = post.booked(currU.getId());
                bookBtn.setText(booked ? R.string.bookBtn_cancel : R.string.bookBtn_book);

                if (booked)
                    post.addBooked(currU);
                else
                    post.undoBook(currU);

                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        PostService postService = Singleton.getInstance().getPostService();
                        return booked ?
                                postService.undoBook(post)
                                : postService.book(post);
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        Log.i(Singleton.DAIFAN_TAG, "onPostExecute of book:" + result);
                    }
                }.execute();
            }
        });

        final InputMethodManager imm =
                (InputMethodManager) activity.
                        getSystemService(Context.INPUT_METHOD_SERVICE);

        Button commentBtn = (Button) vi.findViewById(R.id.btnComment);
        final EditText commentTxt = (EditText) activity.findViewById(R.id.post_comment_txt);
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentTxt.setVisibility(View.VISIBLE);
                commentTxt.requestFocus();

                imm.showSoftInput(commentTxt, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        commentTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {

                    handled = true;
                    commentTxt.setVisibility(View.INVISIBLE);
                    commentTxt.setText("");

                    imm.hideSoftInputFromWindow(
                            activity.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                }
                return handled;
            }
        });
        return vi;
    }
}
