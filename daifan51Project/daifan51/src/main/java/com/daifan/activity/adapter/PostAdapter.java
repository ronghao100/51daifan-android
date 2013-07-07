package com.daifan.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daifan.R;
import com.daifan.domain.Post;
import com.daifan.service.ImageLoader;

import java.util.ArrayList;

/**
 * Created by ronghao on 6/23/13.
 */
public class PostAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Post> posts = new ArrayList<Post>();
    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;

    public PostAdapter(Activity activity, ArrayList<Post> posts) {
        this.activity = activity;
        this.posts = posts;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext());
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

        Post post = posts.get(i);

        // Setting all values in listview
        title.setText(post.getUserName());
        desc.setText(post.getName()+" "+post.getDesc());
        imageLoader.DisplayImage(post.getThumbnailUrl(), thumb_image);
        return vi;
    }
}
