package com.daifan.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.actionbarsherlock.app.SherlockActivity;
import com.daifan.R;
import com.daifan.Singleton;

public class ImagesActivity extends SherlockActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_images_viewpager);

        ImagePagerAdapter adapter = new ImagePagerAdapter();
        Intent in = getIntent();
        if (in != null) {
            String[] images = in.getStringArrayExtra("images");
            if (images != null)
                adapter.setImages(images);
        }

        viewPager.setAdapter(adapter);
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private String[] images = new String[0];

        @Override
        public int getCount() {
            return this.images.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Context context = ImagesActivity.this;
            ImageView imageView = new ImageView(context);
            int padding = context.getResources().getDimensionPixelSize(R.dimen.padding_medium);
            imageView.setPadding(padding, padding, padding, padding);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Singleton.getInstance().getImageLoader().DisplayImage(images[position], imageView);
            ((ViewPager) container).addView(imageView, 0);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }

        public void setImages(String[] images) {
            this.images = images;
        }

        public String[] getImages() {
            return images;
        }
    }
}