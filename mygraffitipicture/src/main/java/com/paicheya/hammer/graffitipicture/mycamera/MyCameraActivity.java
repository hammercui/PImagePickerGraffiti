package com.paicheya.hammer.graffitipicture.mycamera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.paicheya.hammer.graffitipicture.R;
import com.paicheya.hammer.graffitipicture.util.MyFileUtil;
import com.paicheya.hammer.graffitipicture.util.MyLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.forward.androids.utils.ImageUtils;

/**
 *
 * 自定义相机
 * Created by cly on 16/11/24.
 */

public class MyCameraActivity extends AppCompatActivity implements CameraPreview.OnCameraStatusListener{

    private CameraPreview cameraPreview;
    private RelativeLayout takePhotoLayout;
    private RelativeLayout resultLayout;
    private SensorManager sensorManager;
    private ImageView resultImageView;
    private Bitmap bitmap;
    private Uri outPutUri;
    /**
     * 自定义相机完成后回调行为
     */
    public static final String MY_CAMERA_ACTION ="my_camera_action";

    /**
     * 相机回调行为：仅保存
     */
    public static final int MY_CAMERA_ACTION_SAVE = 0;

    /**
     * 相机回调行为，保存并进入裁切
     */
    public static final int MY_CAMERA_ACTION_CROP = 1;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        outPutUri = getIntent().getExtras().getParcelable(MediaStore.EXTRA_OUTPUT);
        // 设置横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.graffiti_activity_camera);

        initView(this);
    }

    /**
     * 初始化视图
     */
    private void initView(Context context){

        int height = Utils.getScreenWH(context).heightPixels;
        int width = height*4/3;
        int buttonLayoutWd = Utils.getScreenWH(context).widthPixels - width;

        cameraPreview = (CameraPreview)this.findViewById(R.id.cameraPreview);
        takePhotoLayout = (RelativeLayout)this.findViewById(R.id.takePhotoLayout);
        FocusView focusView = (FocusView)this.findViewById(R.id.viewFocus);
        RelativeLayout photoLayout = (RelativeLayout)this.findViewById(R.id.photoLayout);
        photoLayout.setLayoutParams(new RelativeLayout.LayoutParams(width,height));//照相区域宽高比4：3


        RelativeLayout.LayoutParams buttonLayoutParams1 = new RelativeLayout.LayoutParams(buttonLayoutWd,height);
        buttonLayoutParams1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        RelativeLayout buttonLayout1 = (RelativeLayout) this.findViewById(R.id.buttonLayout1);
        buttonLayout1.setLayoutParams(buttonLayoutParams1);


        RelativeLayout.LayoutParams buttonLayoutParams2 = new RelativeLayout.LayoutParams(buttonLayoutWd,height);
        buttonLayoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        RelativeLayout buttonLayout2 = (RelativeLayout) this.findViewById(R.id.buttonLayout2);
        buttonLayout2.setLayoutParams(buttonLayoutParams2);

        resultLayout = (RelativeLayout)this.findViewById(R.id.resultLayout);
        resultImageView = (ImageView) resultLayout.findViewById(R.id.resultImage);
        resultImageView.setScaleType(ImageView.ScaleType.FIT_XY); //预览图宽高比4：3
        resultImageView.setLayoutParams(new RelativeLayout.LayoutParams(width,height));



        cameraPreview.setFocusView(focusView);
        cameraPreview.setOnCameraStatusListener(this);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        showTakePhotoLayout();
    }



    /**
     * 点击拍照
     * @param view
     */
    public void takePhoto(View view) {
        if(cameraPreview != null) {
            cameraPreview.takePicture();
        }
    }

    /**
     * 关闭当前activity
     * @param view
     */
    public void close(View view) {
        finish();
    }

    /**
     * 返回拍照
     * @param view
     */
    public void backTakePhoto(View view) {
        showTakePhotoLayout();
    }

    /**
     * 保存拍照图片进入裁切
     * @param view
     */
    public void saveAndCrop(View view){
        //　保存的路径
        File file = new File(outPutUri.getPath());
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            Intent intent = new Intent();
            intent.putExtra(MY_CAMERA_ACTION,MY_CAMERA_ACTION_CROP);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } catch (Exception e) {

            e.printStackTrace();
            MyLog.log("Error","e:"+e.toString());
            finish();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 保存拍照并返回
     * @param view
     */
    public void saveAndClose(View view){
//　保存的路径
        File file = new File(outPutUri.getPath());
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            Intent intent = new Intent();
            intent.putExtra(MY_CAMERA_ACTION,MY_CAMERA_ACTION_SAVE);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } catch (Exception e) {

            e.printStackTrace();
            MyLog.log("Error","e:"+e.toString());
            finish();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }



    @Override
    public void onCameraStopped(byte[] data) {
        MyLog.log("拍照获得图片大小："+(float)data.length/1024f+"kb");
        // 创建图像
        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        MyLog.log("拍照获得图片分辨率：widht_"+bitmap.getWidth()+"  height_"+bitmap.getHeight());
        showResultLayout();
    }

    private void showTakePhotoLayout() {
        takePhotoLayout.setVisibility(View.VISIBLE);
        resultLayout.setVisibility(View.GONE);
        cameraPreview.start();   //继续启动摄像头
    }

    private void showResultLayout() {
        takePhotoLayout.setVisibility(View.GONE);
        resultLayout.setVisibility(View.VISIBLE);
        resultImageView.setImageBitmap(bitmap);
    }
}
