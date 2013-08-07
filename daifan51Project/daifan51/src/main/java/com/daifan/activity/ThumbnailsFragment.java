package com.daifan.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.app.SherlockFragment;
import com.daifan.R;
import com.daifan.Singleton;

import java.util.ArrayList;

public class ThumbnailsFragment extends SherlockFragment {

    static public interface  ThumbnailsConfig {
        public boolean supportNew();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.postnew_gridview, container, false);

        boolean supportNew = false;
        if (this.getSherlockActivity() instanceof ThumbnailsConfig) {
            supportNew = ((ThumbnailsConfig)this.getSherlockActivity()).supportNew();
        }


        ExpandGridView grid = (ExpandGridView) v.findViewById(R.id.mygallery);
        grid.setExpanded(true);
        this.loader = new ImagesLoader(this.getSherlockActivity(), supportNew);

        ImageAdapter adapter = new ImageAdapter(getSherlockActivity());
        grid.setAdapter(adapter);

        this.loader.setAdapter(adapter);

        if (supportNew)
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(Singleton.DAIFAN_TAG, "onItemClickListener " + position );
                }
            });

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    private void clickAddPic(View clickedV) {
        PopupMenu popup = new PopupMenu(this.getSherlockActivity(), clickedV);
        popup.getMenuInflater().inflate(R.menu.pic, popup.getMenu());

        final Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (!Singleton.isIntentAvailable(this.getSherlockActivity(), takePhotoIntent))
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(Singleton.DAIFAN_TAG, "the requestCode is " + requestCode + ", resultCode=" + resultCode);
        if (requestCode == 3) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Bitmap bitmap = (Bitmap) extras.get("data");
                this.loader.add(bitmap);
            }
            return;
        } else if (requestCode == 2) {
            if (null == intent) {
                Toast.makeText(this.getSherlockActivity(), "添加图片失败!", Toast.LENGTH_SHORT).show();
                return;
            }
            Uri uri = intent.getData();
            this.loader.addImage(uri);
            return;
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    //R.drawable.pic_plus

     class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

         public int getCount() {
             return loader.getCount();
         }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }



        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            loader.setImage(imageView, position);
            return imageView;
        }
    }

    private ImagesLoader loader;

    class ImagesLoader {

        private Context context;
        private boolean supportNew;

        ImagesLoader(Context context, boolean supportNew) {
            this.context = context;
            this.supportNew = supportNew;
        }

        private BaseAdapter adapter;

        void setAdapter(BaseAdapter adapter) {
            this.adapter = adapter;
        }

        private ArrayList<Bitmap> bitMaps = new ArrayList<Bitmap>();
        public void add(Bitmap bm) {
            this.bitMaps.add(bm);
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }

        public void addImage(Uri uri) {
            String mPicPath = getRealPathFromURI(uri);
            Log.d(Singleton.DAIFAN_TAG, "pic url == " + mPicPath);
            this.insertPhoto(mPicPath);
        }


        void insertPhoto(String path){
            if (path == null) {
                Log.d(Singleton.DAIFAN_TAG, "insert photo failed for the path is null");
                return;
            }
            Bitmap bm = decodeSampledBitmapFromUri(path, 220, 220);
            loader.add(bm);
        }

//        private View insertImage(Bitmap bm) {
//
//            GridView scrollView = (GridView) findViewById(R.id.mygallery);
//
//            ImageView imageView = new ImageView(getApplicationContext());
//            imageView.setLayoutParams(new LinearLayout.LayoutParams(220, 220));
//            imageView.setPadding(5, 5, 5, 5);
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setImageBitmap(bm);
//            scrollView.addView(imageView, 0);
//            return scrollView;
//        }

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

        // And to convert the image URI to the direct file system path of the image file
        private String getRealPathFromURI(Uri contentUri) {
            String[] proj = { MediaStore.Images.Media.DATA };
            CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
            Cursor cursor = loader.loadInBackground();
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
            else
                return null;
        }

        public int getCount() {
            return this.bitMaps.size() + (supportNew?1:0);
        }

        public void setImage(final ImageView imageView, int position) {
            if (supportNew && (position == this.getCount()-1)) {
                imageView.setImageResource(R.drawable.pic_plus);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ThumbnailsFragment.this.clickAddPic(imageView);
                    }
                });
            } else {
                imageView.setImageBitmap(bitMaps.get(position));
            }
        }
    }
}
