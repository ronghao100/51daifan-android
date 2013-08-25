package com.daifan.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragment;
import com.daifan.R;
import com.daifan.Singleton;
import com.daifan.activity.adapter.ThumbnailsLoader;

import java.io.File;
import java.io.IOException;

public class ThumbnailsFragment extends SherlockFragment {

    static public interface ThumbnailsConfig {
        public boolean supportNew();
    }

    private ThumbnailsLoader loader;

    public ThumbnailsLoader getLoader(){
        return  this.loader;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        boolean supportNew = false;
        if (this.getSherlockActivity() instanceof ThumbnailsConfig) {
            supportNew = ((ThumbnailsConfig) this.getSherlockActivity()).supportNew();
        }
        View v = inflater.inflate(R.layout.postnew_gridview, container, false);
        ExpandGridView grid = (ExpandGridView) v.findViewById(R.id.mygallery);
        grid.setExpanded(true);
        this.loader = new ThumbnailsLoader(this.getSherlockActivity(), new ThumbnailsLoader.NewImageCallback() {
            @Override
            public void postNewImage(Bitmap previewBm, ImageView imageView) {
                Log.d(Singleton.DAIFAN_TAG, "accepted callback for postNewImage");
                if (imageView != null) {
                    registerForContextMenu(imageView);
                }
            }

            @Override
            public View.OnClickListener newBtnClickListener() {
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //ThumbnailsFragment.this.clickAddPic(v);
                    }
                };
            }

            @Override
            public void postNotANewImage(ImageView imageView) {
                if (imageView != null)
                    unregisterForContextMenu(imageView);
            }
        });
        grid.setAdapter(this.loader);


        if (supportNew)
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(Singleton.DAIFAN_TAG, "onItemClickListener " + position);
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = this.getSherlockActivity().getMenuInflater();

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (!Singleton.isIntentAvailable(this.getSherlockActivity(), takePhotoIntent))
            menu.removeItem(R.id.pic_menu_take_photo);

        inflater.inflate(R.menu.pic, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        return this.onNewImgMenu(item);
    }

    private Uri currentTakingDest = null;
    private void clickAddPic(View clickedV) {
        PopupMenu popup = new PopupMenu(this.getSherlockActivity(), clickedV);
        popup.getMenuInflater().inflate(R.menu.pic, popup.getMenu());

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (!Singleton.isIntentAvailable(this.getSherlockActivity(), takePhotoIntent))
            popup.getMenu().removeItem(R.id.pic_menu_take_photo);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(android.view.MenuItem item) {
                return onNewImgMenu(item);
            }
        });

        popup.show();
    }

    private boolean onNewImgMenu(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.pic_menu_pick_photo:

                intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 2);

                return true;
            case R.id.pic_menu_take_photo:
                File tmpImg;
                try {
                    tmpImg = Singleton.getFileCache().createTmpImg();
                } catch (IOException e) {
                    Log.e(Singleton.DAIFAN_TAG, "cannot creat tmp file in file Cache");
                    Toast.makeText(getSherlockActivity(), "无法创建用于保存图像的临时文件!", Toast.LENGTH_SHORT).show();
                    return false;
                }
                currentTakingDest = Uri.fromFile(tmpImg);
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, currentTakingDest);
                startActivityForResult(intent, 3);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.d(Singleton.DAIFAN_TAG, "the requestCode is " + requestCode + ", resultCode=" + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 3) {
                this.galleryAddPic(this.currentTakingDest);
                addToNewImage(this.currentTakingDest);
                return;
            } else if (requestCode == 2) {
                if (null == intent) {
                    Toast.makeText(this.getSherlockActivity(), "添加图片失败!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Uri uri = intent.getData();
                addToNewImage(uri);
                return;
            }
        }

    }

    private void addToNewImage(Uri uri) {
        try {
            this.loader.addNewImage(uri);
        } catch (IOException e) {
            Log.e(Singleton.DAIFAN_TAG, "add pic failed for exception:" + e.getMessage(), e);
            Toast.makeText(this.getSherlockActivity(), "添加图片失败!", Toast.LENGTH_SHORT).show();
            return;
        }
        return;
    }

    private void galleryAddPic(Uri contentUri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        this.getSherlockActivity().sendBroadcast(mediaScanIntent);
    }
}
