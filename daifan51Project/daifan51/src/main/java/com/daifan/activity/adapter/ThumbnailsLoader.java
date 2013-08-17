package com.daifan.activity.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.daifan.R;
import com.daifan.Singleton;
import com.daifan.activity.BaseActivity;
import com.daifan.service.Utils;

import java.util.ArrayList;
import java.util.List;

public class ThumbnailsLoader extends BaseAdapter {

    public static final int THUMBNAIL_W_DP = 100;
    public static final int THUMBNAIL_H_DP = 100;
    private final int thumbnailW;
    private final int thumbnailH;

    private boolean isSupportNew() {
        return newImgCallback != null;
    }

    static public interface NewImageCallback {
        public void postNewImage(Bitmap previewBm);
        public View.OnClickListener newBtnClickListener();
    }

    private Context context;
    private NewImageCallback newImgCallback;

    private ArrayList<Bitmap> bitMaps = new ArrayList<Bitmap>();
    private ArrayList<String> imageUrls = new ArrayList<String>();

    public ThumbnailsLoader(Context context, NewImageCallback newImageCallback) {
        this.context = context;
        this.newImgCallback = newImageCallback;
        this.thumbnailH = dpToPx(THUMBNAIL_H_DP, context);
        this.thumbnailW = dpToPx(THUMBNAIL_W_DP, context);
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = null;
        if (convertView != null) {
            imageView = (ImageView) convertView;
        }

        return setImage(imageView, position);
    }

    public void add(Bitmap bm) {
        this.bitMaps.add(bm);
        this.notifyDataSetChanged();
    }

    public void addImageUrls(List<String> urls) {
        this.imageUrls.addAll(urls);
        this.notifyDataSetChanged();
    }

    public void addImage(Uri uri) {
        String mPicPath = getRealPathFromURI(uri);
        Log.d(Singleton.DAIFAN_TAG, "pic url = " + mPicPath);
        this.insertPhoto(mPicPath);
    }

    void insertPhoto(String path) {
        if (path == null) {
            Log.d(Singleton.DAIFAN_TAG, "insert photo failed for the path is null");
            return;
        }
        Bitmap bm = decodeSampledBitmapFromUri(path, this.thumbnailW, this.thumbnailH);
        this.add(bm);
        this.newImgCallback.postNewImage(bm);
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

        //make sure image size larger than Preview bounds
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            } else {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            }
        }

        Log.d(Singleton.DAIFAN_TAG, "calculateInSampleSize: orgW=" + width + ", orgH=" + height
                + ", newW=" + reqWidth + ", newH=" + reqHeight + ", inSampleSize=" + inSampleSize);

        return inSampleSize;
    }

    // And to convert the image URI to the direct file system path of the image file
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public int getCount() {
        return this.bitMaps.size() + this.imageUrls.size() + (isSupportNew() ? 1 : 0);
    }

    public ImageView setImage(ImageView imageView, int position) {
        if (isSupportNew() && (position == this.getCount() - 1)) {

            if (imageView == null) {
                imageView = new ImageView(context);
                decorImageView(imageView, context);
            }

            imageView.setImageResource(R.drawable.pic_plus);
            final ImageView iv = imageView;
            imageView.setOnClickListener(newImgCallback.newBtnClickListener());

            return imageView;
        } else if (position < this.bitMaps.size()) {
             if (imageView == null) {
                 imageView = new ImageView(context);
                 decorImageView(imageView, context);
             }

             imageView.setImageBitmap(bitMaps.get(position));
            return imageView;
        } else {

            NetworkImageView niv;

            if (imageView == null || !(imageView instanceof NetworkImageView)) {
                if (imageView != null) {
                    Log.d(Singleton.DAIFAN_TAG, "image-view to network image isn't NetworkImageView:" + position);
                    imageView = null;
                }
                niv = new NetworkImageView(context);
                decorImageView(niv, context);
            } else {
                niv = (NetworkImageView) imageView;
            }

            ImageLoader imgLoader = ((BaseActivity) context).getDaifanApplication().getImageLoader();
            niv.setImageUrl(this.imageUrls.get(position - this.bitMaps.size()), imgLoader);
            return niv;
        }
    }

    private void decorImageView(ImageView imageView, Context mContext) {
        int padding = mContext.getResources().getDimensionPixelSize(R.dimen.padding_medium);
        imageView.setPadding(0, padding, padding, padding);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        //TODO: it should be better to assign by callers
        imageView.setMaxHeight(thumbnailH);
        imageView.setMaxWidth(thumbnailW);

        imageView.setMinimumHeight(thumbnailH);
        imageView.setMinimumWidth(thumbnailW);

        Log.d(Singleton.DAIFAN_TAG, "set thunbnail w:"+ thumbnailW + ", h:" + thumbnailH);
    }

    public static int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
