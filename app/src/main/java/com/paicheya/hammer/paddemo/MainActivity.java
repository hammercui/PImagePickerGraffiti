package com.paicheya.hammer.paddemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.paicheya.pimagepicker.PImagePicker;
import com.paicheya.pimagepicker.PImagePickerConfig;
import com.paicheya.pimagepicker.bean.ImageItem;
import com.paicheya.pimagepicker.bean.OutputUri;
import com.paicheya.pimagepicker.util.ScreenUtils;
import com.paicheya.pimagepicker.view.gallery.PImageLoader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    private ImageView imageView;
    private GridView gridView;
    private ArrayList<ImageItem> list = new ArrayList<>();
    private MyAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
    }

    private void initUI(){
       findViewById(R.id.btn_test_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testCamera();
            }
        });

        findViewById(R.id.btn_test_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testGallery();
            }
        });

        imageView = (ImageView)this.findViewById(R.id.image_test);
        gridView = (ExpandGridView)this.findViewById(R.id.gridview_demo);

        adapter = new MyAdapter(list);
        gridView.setAdapter(adapter);
    }


    private void testCamera(){
        PImagePickerConfig pImagePickerConfig = new PImagePickerConfig.Builder(this)
                .setImageName(System.currentTimeMillis()+".jpg")
                .setPressQuality(50)
                .setFromCamera(true)
                .setAspectRatio(4,3)
                .setDirPath("/padDemo")
                .setCameraRoll(true)
                .setMultiple(true)
                .builder();
        PImagePicker.create(pImagePickerConfig).startCameraActivity(this);
    }

    private void testGallery(){
        PImagePickerConfig pImagePickerConfig = new PImagePickerConfig.Builder(this)
                .setDirPath("/img")
                .setImageName(System.currentTimeMillis()+".jpg")
                .setPressQuality(50)
                .setMultiple(false)
                .setMaxFiles(10)
                .builder();
        PImagePicker.create(pImagePickerConfig).startGalleryActivity(this);
    }

    private void showImageView(ArrayList<ImageItem> list){
       adapter.setData(list);
//
//        int width = ScreenUtils.getWidthInPx(this);
//        int height = (int)(width * 0.75);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,height);
//        imageView.setLayoutParams(params);
//        Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath());
//        //MyLog.log("获得图片的size："+(bitmap.get()/1024) );
//        imageView.setImageBitmap(bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //成功获得图片
                case PImagePicker.REQUEST_PICKER_IMAGE_SINGLE:
                    ImageItem  imageItem = PImagePicker.getOutput(data);
                    if (imageItem != null) {
                        list.clear();
                        list.add(imageItem);
                        showImageView(list);
                    } else {
                        Log.i("错误：","发生未知错误，选取图片失败");
                    }
                    break;
                case PImagePicker.REQUEST_PICKER_IMAGE_MULTIPLE:
                    list.clear();
                    list = PImagePicker.getOutputArray(data);
                    showImageView(list);
                    break;
            }
        }
        else if(resultCode == PImagePicker.REQUEST_PICKER_IMAGE_ERROR){
            String error = PImagePicker.getOutputError(data);
            Log.i("回调获得错误：",error);
        }
    }

    private class MyAdapter extends BaseAdapter {

        private List<ImageItem> items;

        public MyAdapter(List<ImageItem> items) {
            this.items = items;
        }

        public void setData(List<ImageItem> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public ImageItem getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            int size = gridView.getWidth() / 3;
            if (convertView == null) {
                imageView = new ImageView(MainActivity.this);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, size);
                imageView.setLayoutParams(params);
                imageView.setBackgroundColor(Color.parseColor("#88888888"));
            } else {
                imageView = (ImageView) convertView;
            }
            PImageLoader.displayImage(MainActivity.this, getItem(position).path, imageView, size, size);
            return imageView;
        }
    }
}
