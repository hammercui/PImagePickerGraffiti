package com.paicheya.hammer.paddemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.paicheya.pimagepicker.PImagePicker;
import com.paicheya.pimagepicker.PImagePickerConfig;
import com.paicheya.pimagepicker.core.OutputUri;
import com.paicheya.pimagepicker.util.ScreenUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {

    private ImageView imageView;
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
    }


    private void testCamera(){
        PImagePickerConfig pImagePickerConfig = new PImagePickerConfig.Builder(this)
                .setImageName(System.currentTimeMillis()+".jpg")
                .setPressQuality(50)
                .setFromCamera(true)
                .setAspectRatio(4,3)
                .setDirPath("/padDemo")
                .setCameraRoll(true)
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

    private void showImageView(Uri uri){
        int width = ScreenUtils.getWidthInPx(this);
        int height = (int)(width * 0.75);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,height);
        imageView.setLayoutParams(params);
        Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath());
        //MyLog.log("获得图片的size："+(bitmap.get()/1024) );
        imageView.setImageBitmap(bitmap);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //成功获得图片
                case PImagePicker.REQUEST_PICKER_IMAGE_SINGLE:
                    OutputUri  outputUri = PImagePicker.getOutput(data);
                    if (outputUri != null) {
                        showImageView(outputUri.getUri());
                    } else {
                        Log.i("错误：","发生未知错误，选取图片失败");
                    }
                    break;
                case PImagePicker.REQUEST_PICKER_IMAGE_MULTIPLE:
                    ArrayList<OutputUri> list = PImagePicker.getOutputArray(data);
                    break;
            }
        }
        else if(resultCode == PImagePicker.REQUEST_PICKER_IMAGE_ERROR){
            String error = PImagePicker.getOutputError(data);
            Log.i("回调获得错误：",error);
        }
    }
}
