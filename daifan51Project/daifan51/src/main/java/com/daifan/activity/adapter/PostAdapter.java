package com.daifan.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

    private Activity activity;
    private ArrayList<Post> posts = new ArrayList<Post>();
    private static LayoutInflater inflater = null;
    private static CommentComp commentComp = null;
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
    public View getView(int i, View vi, ViewGroup viewGroup) {

        if (commentComp == null)
            commentComp = new CommentComp(activity);

        if (vi == null)
            vi = inflater.inflate(R.layout.list_row, null);

        TextView title = (TextView) vi.findViewById(R.id.title); // title
        TextView desc = (TextView) vi.findViewById(R.id.desc); // artist name
        ImageView thumb_image = (ImageView) vi.findViewById(R.id.thumbnail); // thumb image
        TextView createdAtTxt = (TextView) vi.findViewById(R.id.createdAt);


        final Post post = posts.get(i);

        imageLoader.DisplayImage(post.getThumbnailUrl(), thumb_image);

        title.setText(post.getUserName() + ":" + post.getId());
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
        relayoutBooked(post, commentContainers, bookedUNameTxt);

        commentContainers.removeViews(1, commentContainers.getChildCount()-1);

        if (post.getComments().size() > 0) {
            for (Comment cm : post.getComments())
                appendComment(commentContainers, cm);
            commentContainers.setVisibility(View.VISIBLE);
        }

        final Button bookBtn = (Button) vi.findViewById(R.id.btnBooked);
        final User currU = Singleton.getInstance().getCurrUser();
        boolean booked = (currU == null ? false : post.booked(currU.getId()));
        if (booked) {
            bookBtn.setText(R.string.bookBtn_cancel);
        }
        if (post.outofOrder()) {
            //bookBtn.setTextColor(R.colors.grey);
        }


        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (post.outofOrder() && !post.booked(currU.getId())) {
                    Toast.makeText(activity, R.string.out_of_order, Toast.LENGTH_LONG).show();
                    bookBtn.setText(activity.getString(R.string.out_of_order));
                    return;
                }

                if (currU == null) {
                    Toast.makeText(activity, R.string.login_required, Toast.LENGTH_LONG).show();
                    return;
                }

                if (post.booked(currU.getId()))
                    post.undoBook(currU);
                else
                    post.addBooked(currU);


                final boolean nowBooked = post.booked(currU.getId());
                bookBtn.setText(nowBooked ? R.string.bookBtn_cancel : R.string.bookBtn_book);

                relayoutBooked(post, commentContainers, bookedUNameTxt);

                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        PostService postService = Singleton.getInstance().getPostService();
                        return nowBooked ?
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

        final Button commentBtn = (Button) vi.findViewById(R.id.btnComment);

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentComp.showForPost(post, PostAdapter.this);
            }
        });

        return vi;
    }

    private void relayoutBooked(Post post, LinearLayout commentContainers, TextView bookedUNameTxt) {
        if (post.getBookedUids().length > 0) {
            bookedUNameTxt.setText(activity.getString(R.string.booked_names_prefix) + post.getBookedUNames());
            Log.d(Singleton.DAIFAN_TAG, "refresh booked names for post " + post.getId() + ", names:" + post.getBookedUNames());
            commentContainers.setVisibility(View.VISIBLE);
        }
    }

    private void appendComment(LinearLayout commentContainers, Comment cm) {
        TextView tx = new TextView(activity);
        tx.setText(Singleton.getInstance().getUNameById(String.valueOf(cm.getUid())) + ": " + cm.getComment());
        commentContainers.addView(tx);
    }
}
