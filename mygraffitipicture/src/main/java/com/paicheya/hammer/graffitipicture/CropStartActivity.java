package com.paicheya.hammer.graffitipicture;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.paicheya.hammer.graffitipicture.mycamera.MyCameraActivity;
import com.paicheya.hammer.graffitipicture.util.MyFileUtil;
import com.paicheya.hammer.graffitipicture.util.MyLog;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.view.UCropView;

import java.io.File;
import java.util.logging.Logger;

/**
 * Created by cly on 16/11/11.
 */

public class CropStartActivity extends BaseActivity {

    //选择图片请求
    private static final int REQUEST_SELECT_PICTURE = 0x01;
    //涂鸦图片请求
    private static final int REQUEST_GRAFFITI_PICTURE = 0X02;

    private static final int REQUEST_OPEN_CAMERA = 0x03;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";

    private static final String TAG = "CropStartActivity";


    private TextView txPath;
    private UCropView ucropview_test;

//    private  String  cropFilePath ;
//    private String   graFilePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graffiti_activity_crop_start);
        initView();
    }

    public void initView(){
        Button btn = (Button)findViewById(R.id.btn_select_picture);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });
        findViewById(R.id.btn_open_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromCamera();
            }
        });

        txPath = (TextView)findViewById(R.id.text_path);
        ucropview_test = (UCropView) findViewById(R.id.ucropview_test);

        //申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.permission_read_storage_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        }

//        cropFilePath = appFileDirPath+"/crops";
//        graFilePath = appFileDirPath+"/graffs";
//        checkFileExist(cropFilePath);
//        checkFileExist(graFilePath);
//        MyLog.log("cropFilePath == "+cropFilePath);
//        MyLog.log("graFilePath == "+graFilePath);
    }



    /*
      从画廊中选择图片
      */
    private void pickFromGallery() {
        if (!checkPermissionReadSDkard())
            return;
        imageName = System.currentTimeMillis()+".jpg";
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_picture)), REQUEST_SELECT_PICTURE);
    }

    private Uri camerauri;
    /**
     * 从相机中获得图片
     */
    private void pickFromCamera(){
        if (!checkPermissionReadSDkard())
            return;
        if (!checkPermissionCamera())
            return;

        imageName = System.currentTimeMillis()+".jpg";
        //拍照后存储的地址
        //　保存的路径
        File file = new File(MyFileUtil.getFolderInExternalStorage("paddemo_camera"),imageName);
        camerauri=Uri.fromFile(file);

        //方法1：调用系统相机
//        Intent intent =new Intent();
//        intent.setAction(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, camerauri);
//        startActivityForResult(intent, REQUEST_OPEN_CAMERA);
        //方法2：调用自定义相机
        Intent intent1 = new Intent(this, MyCameraActivity.class);
        intent1.putExtra(MediaStore.EXTRA_OUTPUT,camerauri);
        startActivityForResult(intent1,REQUEST_OPEN_CAMERA);
    }


    /**
     * 检测申请读sd权限
     * @return
     */
    private boolean checkPermissionReadSDkard(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.permission_read_storage_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
            return false;
        }
        return  true;
    }

    private boolean checkPermissionCamera(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.CAMERA,
                    getString(R.string.permission_camer),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
            return false;
        }
        return true;
    }

    /**
     * 取得权限之后的回调
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        MyLog.log(TAG,"onRequestPermissionsResult 返回requestCode："+requestCode);
        switch (requestCode) {
            case REQUEST_STORAGE_READ_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLog.log(TAG,"sd权限获得成功");
                    //pickFromGallery();
                }
                break;
            case REQUEST_OPEN_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLog.log(TAG,"cammera权限获得成功，开始打开画廊");
                    pickFromCamera();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private String imageName ;
    /**
     * 取得图片的uri，开始裁剪
     * @param uri
     */
    private void startCropActivity(@NonNull Uri uri) {

        Uri sourceUri = uri;
        Uri destinationUri = Uri.fromFile(new File(MyFileUtil.getFolderInExternalStorage("paddemo_crop"),imageName));
        MyLog.log(TAG,"相册或相机获得的uri: "+sourceUri);

        UCrop uCrop = UCrop.of(sourceUri,destinationUri);
        uCrop = advancedConfig(uCrop);
        uCrop.start(CropStartActivity.this);
    }

    /**
     * 取得裁剪uri,开始涂鸦
     * @param uri
     */
    private void startGraffityActivity(@NonNull Uri uri){
        MyLog.log(TAG,"裁切后Uri uri:"+uri);
        Intent intent = new Intent(this, GraffitiActivity.class);
        intent.putExtra(GraffitiActivity.KEY_IMAGE_PATH, uri.getPath());
        intent.putExtra(GraffitiActivity.KEY_IMAGE_NAME, imageName);
        startActivityForResult(intent, REQUEST_GRAFFITI_PICTURE);
    }

    /**
     * activity获得返回信息
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                //选择图片成功，开始裁剪
                case REQUEST_SELECT_PICTURE:
                    final Uri selectedUri = data.getData();
                    if (selectedUri != null) {
                        startCropActivity(selectedUri);
                    } else {
                        Toast.makeText(CropStartActivity.this, R.string.toast_cannot_retrieve_selected_image, Toast.LENGTH_SHORT).show();
                    }
                    break;
                //裁剪图片成功，开始涂鸦
                case UCrop.REQUEST_CROP:
                    handleCropResult(data);
                    break;
                //涂鸦成功，开始显示
                case REQUEST_GRAFFITI_PICTURE:
                    Uri resultUri = data.getParcelableExtra(GraffitiActivity.KEY_IMAGE_PATH);
                    if (resultUri == null)
                        return;

                    showResultImage(resultUri);
                    break;
                //打开相机成功
                case REQUEST_OPEN_CAMERA:
                    if (camerauri !=null){
                        int action = data.getIntExtra(MyCameraActivity.MY_CAMERA_ACTION,MyCameraActivity.MY_CAMERA_ACTION_SAVE);
                        switch (action){
                            case MyCameraActivity.MY_CAMERA_ACTION_CROP:
                                startCropActivity(camerauri);
                                break;
                            case MyCameraActivity.MY_CAMERA_ACTION_SAVE:
                                showResultImage(camerauri);
                                break;
                        }
                    }
                    else{
                        Toast.makeText(CropStartActivity.this, "从相机获得图片失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError(data);
        }
    }


    /**
     * 显示结果图片
     * @param resultUri
     */
    public void showResultImage(Uri resultUri){
        try {
            ucropview_test.getCropImageView().setImageUri(resultUri, null);
            ucropview_test.getOverlayView().setShowCropFrame(false);
            ucropview_test.getOverlayView().setShowCropGrid(false);
            ucropview_test.getOverlayView().setDimmedColor(Color.TRANSPARENT);
            MyLog.log("涂鸦后保存文件路径："+resultUri.getPath());
            txPath.setText(resultUri.getPath());
        }
        catch (Exception e){
            MyLog.log(TAG,"e:"+e.toString()) ;
        }
    }


    /**
     * 配置裁剪的基础属性
     *
     * Sometimes you want to adjust more options, it's done via {@link com.yalantis.ucrop.UCrop.Options} class.
     *
     * @param uCrop - ucrop builder instance
     * @return - ucrop builder instance
     */
    private UCrop advancedConfig(@NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();

        //压缩模式
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        //options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        //设置压缩比例
        options.setCompressionQuality(50);
        //设置是否隐藏底部容器
        options.setHideBottomControls(false);
        //设置是否能调整裁剪框
        options.setFreeStyleCropEnabled(false);

        //设置裁剪图片可选择的手势
        // options.setAllowedGestures(UCropActivity.SCALE,UCropActivity.ROTATE,UCropActivity.ALL);

        return uCrop.withOptions(options);

    }

        /**
         * 裁剪结束，打开涂鸦Activity
         * @param result
         */
    private void handleCropResult(@NonNull Intent result) {
        MyLog.log(TAG,"裁剪后图片宽—高："+UCrop.getOutputImageWidth(result)+"/"+UCrop.getOutputImageHeight(result));
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            //GraffitiStartActivity.startWithUri(CropStartActivity.this, resultUri);
            //GraffitiActivity.startActivityForResult(CropStartActivity.this,resultUri.getPath(),REQUEST_GRAFFITI_PICTURE);
            startGraffityActivity(resultUri);
        } else {
            Toast.makeText(CropStartActivity.this, R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 裁剪失败
     * @param result
     */

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e(TAG, "handleCropError: ", cropError);
            Toast.makeText(CropStartActivity.this, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(CropStartActivity.this, R.string.toast_unexpected_error, Toast.LENGTH_SHORT).show();
        }
    }

}
