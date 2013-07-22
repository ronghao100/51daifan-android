package com.daifan.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.RelativeLayout.LayoutParams;

import com.android.volley.toolbox.NetworkImageView;
import com.daifan.R;
import com.daifan.Singleton;
import com.daifan.activity.ImagesActivity;
import com.daifan.domain.Comment;
import com.daifan.domain.Post;
import com.daifan.domain.User;
import com.daifan.service.ImageLoader;
import com.daifan.service.PostService;

import java.util.ArrayList;

/**
 * Created by ronghao on 6/23/13.
 */
public class PostAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Post> posts = new ArrayList<Post>();
    private static LayoutInflater inflater = null;
    private static CommentComp commentComp = null;
    public ImageLoader imageLoader;

    private final com.android.volley.toolbox.ImageLoader mImageLoader;

    public PostAdapter(Activity activity, ArrayList<Post> posts,com.android.volley.toolbox.ImageLoader imageLoader2) {
        this.activity = activity;
        this.posts = posts;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = Singleton.getInstance().getImageLoader();
        mImageLoader=imageLoader2;
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
            commentComp = new CommentComp();

        this.commentComp.onActive(this.activity);

        if (vi == null)
            vi = inflater.inflate(R.layout.list_row, null);

        TextView title = (TextView) vi.findViewById(R.id.title); // title
        TextView desc = (TextView) vi.findViewById(R.id.desc); // artist name
        NetworkImageView thumb_image=(NetworkImageView) vi.findViewById(R.id.ivItemAvatar);
        TextView createdAtTxt = (TextView) vi.findViewById(R.id.createdAt);

        final Post post = posts.get(i);

        thumb_image.setImageUrl(post.getThumbnailUrl(), mImageLoader);

        title.setText(post.getUserName() );
        desc.setText(post.getName() + " " + post.getDesc());

        long time = post.getCreatedAt().getTime();
        Log.d(Singleton.DAIFAN_TAG, "created at " + post.getCreatedAt());
        createdAtTxt.setText(DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(), 0));

        ((TextView)vi.findViewById(R.id.post_total_num)).setText(String.valueOf(post.getCount()));
        ((TextView)vi.findViewById(R.id.post_left_num)).setText(String.valueOf(post.getLeft()));
        ((TextView)vi.findViewById(R.id.post_address)).setText(post.getAddress());

        NetworkImageView imageV = (NetworkImageView) vi.findViewById(R.id.list_row_image);
        if (post.hasImage()){
            imageV.setImageUrl(post.getImages().get(0), mImageLoader);
            imageV.setVisibility(View.VISIBLE);
        }  else {
            imageV.setVisibility(View.GONE);
        }

        imageV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(activity.getApplicationContext(), ImagesActivity.class);
                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ArrayList<String> fullImages = post.fullImages();
                login.putExtra("images", fullImages.toArray(new String[0]));
                activity.startActivity(login);
                imageLoader.preLoad(fullImages);
            }
        });


        final LinearLayout orderLayout = (LinearLayout)vi.findViewById(R.id.subOrderLayout);
        final TextView bookedUNameTxt = (TextView) vi.findViewById(R.id.booked_uname_txts);
        final ImageView bookedNamePic = (ImageView) vi.findViewById(R.id.book_pic);
        reLayoutBooked(post, bookedUNameTxt,orderLayout);

        final RelativeLayout commentContainers = (RelativeLayout) vi.findViewById(R.id.list_row_comments_container);
        commentContainers.removeViews(0, commentContainers.getChildCount());

        if (post.getComments().size() > 0) {
            View pre = bookedNamePic;
            for (Comment cm : post.getComments()) {
                pre = appendComment(commentContainers, cm, pre);
            }
            commentContainers.setVisibility(View.VISIBLE);
        }

        final ImageButton bookBtn = (ImageButton) vi.findViewById(R.id.btnBooked);
        final User currU = Singleton.getInstance().getCurrUser();
        boolean booked = (currU == null ? false : post.booked(currU.getId()));
        if (booked) {
           // bookBtn.setImageDrawable(R.d);
        }
        if (post.outofOrder()) {
            bookBtn.setImageDrawable(activity.getResources().getDrawable(R.drawable.book_outoforder));
        }

        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (post.isInactive()) {
                    Toast.makeText(activity, R.string.not_active_any_more, Toast.LENGTH_LONG).show();
                    return;
                }

                if (post.outofOrder() && !post.booked(currU.getId())) {
                    Toast.makeText(activity, R.string.out_of_order, Toast.LENGTH_LONG).show();
                    bookBtn.setImageDrawable(activity.getResources().getDrawable(R.drawable.book_outoforder));
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
//                bookBtn.setHint(nowBooked ? R.string.bookBtn_cancel : R.string.bookBtn_book);
                if (post.outofOrder())
                    bookBtn.setImageDrawable(activity.getResources().getDrawable(R.drawable.book_outoforder));

                reLayoutBooked(post, bookedUNameTxt,orderLayout);

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

        final ImageButton commentBtn = (ImageButton) vi.findViewById(R.id.btnComment);

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(Singleton.DAIFAN_TAG, "accepted commentBtn onclick event");
                commentComp.showForPost(post, PostAdapter.this);
            }
        });

        return vi;
    }

    private void reLayoutBooked(Post post, TextView bookedUNameTxt, LinearLayout orderLayout) {

        if (bookedUNameTxt == null) {
            Log.e(Singleton.DAIFAN_TAG, "booked name text view is null");
            return;
        }

        if (post.getBookedUids().length > 0) {
            bookedUNameTxt.setText(post.getBookedUNames());
            orderLayout.setVisibility(View.VISIBLE);
            Log.d(Singleton.DAIFAN_TAG, "refresh booked names for post " + post.getId() + ", names:" + post.getBookedUNames());
        }
    }

    private TextView appendComment(RelativeLayout commentContainers, Comment cm, View pre) {
        TextView textLabel = (TextView) new TextView(activity);
        TextView textView = new TextView(activity);
        textLabel.setId(pre != null? pre.getId()+2 : 1);
        textView.setId(textLabel.getId()+1);

        LayoutParams p1 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p1.addRule(RelativeLayout.BELOW, pre != null ? pre.getId() : R.id.book_pic);
        p1.addRule(RelativeLayout.ALIGN_BOTTOM, textView.getId());
        p1.addRule(RelativeLayout.ALIGN_TOP, textView.getId());

        textLabel.setLayoutParams(p1);
        textLabel.setTextColor(activity.getResources().getColor(R.color.post_anota_num_color));
        textLabel.setPadding(5,2,5,2);
        textLabel.setGravity(Gravity.CENTER_VERTICAL);

        LayoutParams p2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,  ViewGroup.LayoutParams.WRAP_CONTENT);
        p2.addRule(RelativeLayout.BELOW, pre != null ? pre.getId() : R.id.book_pic);
        p2.addRule(RelativeLayout.RIGHT_OF, textLabel.getId());
        textView.setLayoutParams(p2);
        textView.setPadding(0,2,0,2);

        textLabel.setText(Singleton.getInstance().getUNameById(String.valueOf(cm.getUid())) + ": ");
        textView.setText(cm.getComment());
        commentContainers.addView(textLabel);
        commentContainers.addView(textView);
        return textLabel;
    }
}
