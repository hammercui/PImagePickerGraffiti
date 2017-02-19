package com.paicheya.pimagepicker.view.gallery;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;


import com.paicheya.pimagepicker.R;
import com.paicheya.pimagepicker.util.ScreenUtils;
import com.paicheya.pimagepicker.core.PImagePickerBaseActivity;
import com.yalantis.ucrop.callback.BitmapCropCallback;
import com.yalantis.ucrop.view.GestureCropImageView;
import com.yalantis.ucrop.view.OverlayView;
import com.yalantis.ucrop.view.TransformImageView;
import com.yalantis.ucrop.view.UCropView;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * 垂直方向裁切
 */

@SuppressWarnings("ConstantConditions")
public class GalleryActivity extends PImagePickerBaseActivity {

    public static final int DEFAULT_COMPRESS_QUALITY = 90;
    public static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;

    public static final int NONE = 0;
    public static final int SCALE = 1;
    public static final int ROTATE = 2;
    public static final int ALL = 3;

    public static final int aspect_id_43 = 0;
    public static final int aspect_id_169 = 1;
    public int curr_aspect_state = aspect_id_169;

    @IntDef({NONE, SCALE, ROTATE, ALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GestureTypes {

    }

    private static final String TAG = "UCropActivity";

    private static final int TABS_COUNT = 3;

    private boolean mShowBottomControls = true;

    private boolean mShowLoader = true;



    /**
     * 裁切视图
     */
    private UCropView mUCropView;
    /**
     * 裁切视图-手势区域 完整图片
     */
    private GestureCropImageView mGestureCropImageView;
    /**
     * 裁切视图-裁切区域 遮罩
     */
    private OverlayView mOverlayView;
    private List<ViewGroup> mCropAspectRatioViews = new ArrayList<>();

    private ProgressDialog lockProgressDialog;
    private View mBlockingView;

    private Bitmap.CompressFormat mCompressFormat = DEFAULT_COMPRESS_FORMAT;
    private int mCompressQuality = DEFAULT_COMPRESS_QUALITY;
    private int[] mAllowedGestures = new int[]{SCALE, ROTATE, ALL};

    private Uri galleryUri; //输入的uri
    private Uri outputUri ; //输出uri
    private boolean initSuccess = false;

    //选择图片请求
    private static final int REQUEST_SELECT_PICTURE = 0x01;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        outputUri = Uri.fromFile(new File(configOutPutPath));
        openGallery();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        setContentView(R.layout.pip_activity_gallery);

        initCropViews();
        initBottomView();
        addBlockingView();
        setImageData();

        updateAspectRadio();

        initSuccess = true;
    }

    /**
     * 初始化菜单视图
     */
    private void initBottomView(){
        //重试按钮
        findViewById(R.id.tv_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //重选
                openGallery();
            }
        });
        //确定按钮
        findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //裁切并保存返回
                cropAndSaveImage();
            }
        });
    }

    /**
     *初始化crop视图
     */
    private void initCropViews() {
        mUCropView = (UCropView) findViewById(R.id.ucrop);
        mGestureCropImageView = mUCropView.getCropImageView();
        mOverlayView = mUCropView.getOverlayView();
        mGestureCropImageView.setTransformImageListener(mImageListener);

        processOptions();
    }

    /**
     * 加载裁切属性
     */
    private void  processOptions(){
        //设置输出最大尺寸
        int maxSizeX = ScreenUtils.getWidthInPx(this);
        int maxSizeY =  maxSizeX;
        mGestureCropImageView.setMaxResultImageSizeX(maxSizeX);
        mGestureCropImageView.setMaxResultImageSizeY(maxSizeY);
        //配置遮罩助兴
        //显示矩形裁切框
        mOverlayView.setShowCropFrame(true);
        //设置裁切框的宽度
        mOverlayView.setCropGridStrokeWidth(5);
        mOverlayView.setCropGridColor(Color.WHITE);//横竖线的颜色
        mOverlayView.setCropGridColumnCount(2);//竖线数量
        mOverlayView.setCropGridRowCount(2);//竖线数量

    }

    private void openGallery(){
        // 方法1 直接跳转图库选择页
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivityForResult(Intent.createChooser(intent, getString(R.string.pai_label_select_picture)),
//                REQUEST_SELECT_PICTURE);

        //方法2
        Intent libraryIntent;
        libraryIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (libraryIntent.resolveActivity(this.getPackageManager()) == null) {
            setResultError("Cannot launch photo library");
            return;
        }
        try {
            this.startActivityForResult(libraryIntent, REQUEST_SELECT_PICTURE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            setResultError("Cannot launch photo library");
        }
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
            switch (requestCode) {
                //从图库选择图片成功
                case REQUEST_SELECT_PICTURE:
                    final Uri selectedUri = data.getData();
                    if (selectedUri != null) {
                        galleryUri = selectedUri;
                        //已初始化成功
                        if(initSuccess){
                            setImageData();
                        }else{
                            initView();
                        }
                    } else {
                        setResultError("发生未知错误，选取图片失败");
                    }
                    break;
            }
        }
        else {
            setResultError("发生未知错误，选取图片失败");
        }
    }





    @Override
    protected void onStop() {
        super.onStop();
        if (mGestureCropImageView != null) {
            mGestureCropImageView.cancelAllAnimations();
        }
    }

    /**
     * 设置图片显示内容
     * This method extracts all data from the incoming intent and setups views properly.
     */
    private void setImageData() {
        try {
            mGestureCropImageView.setImageUri(galleryUri, outputUri);
        } catch (Exception e) {
           setResultError(e.toString());
        }
    }


    private TransformImageView.TransformImageListener mImageListener = new TransformImageView.TransformImageListener() {
        @Override
        public void onRotate(float currentAngle) {
        }

        @Override
        public void onScale(float currentScale) {
        }

        @Override
        public void onLoadComplete() {
            mUCropView.animate().alpha(1).setDuration(300).setInterpolator(new AccelerateInterpolator());
            //mBlockingView.setClickable(false);
            mShowLoader = false;
            supportInvalidateOptionsMenu();
        }

        @Override
        public void onLoadFailure(@NonNull Exception e) {
            finish();
        }

    };




    /**
     * Sets status-bar color for L devices.
     *
     * @param color - status-bar color
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);
            }
        }
    }



    /**
     * 更新宽高比
     */
    public void updateAspectRadio(){
        mGestureCropImageView.setTargetAspectRatio(configAspectRatio);
        mGestureCropImageView.setImageToWrapCropBounds();

    }


    /**
     * 锁定视图，裁切时，锁定所有触摸
     * 替换为progress
     * Adds view that covers everything below the Toolbar.
     * When it's clickable - user won't be able to click/touch anything below the Toolbar.
     * Need to block user input while loading and cropping an image.
     */
    private void addBlockingView() {
        if(lockProgressDialog == null){
            lockProgressDialog = new ProgressDialog(GalleryActivity.this);
            lockProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            lockProgressDialog.setMessage("裁剪中...请稍等");
        }
    }

    /**
     * 2 存储图片
     */
    protected void cropAndSaveImage() {
        //mBlockingView.setClickable(true);
        lockProgressDialog.show();
        mShowLoader = true;
        supportInvalidateOptionsMenu();
        mGestureCropImageView.cropAndSaveImage(mCompressFormat, mCompressQuality, new BitmapCropCallback() {
            @Override
            public void onBitmapCropped(@NonNull Uri resultUri, int imageWidth, int imageHeight) {
                lockProgressDialog.dismiss();
                Log.i("裁切完成","裁切后uri："+resultUri);
                Log.i("裁切完成","裁切后大小imageWidth："+imageWidth+",imageHeight:"+imageHeight);
                setResultUri(resultUri,imageWidth,imageWidth);
            }

            @Override
            public void onCropFailure(@NonNull Throwable t) {
                lockProgressDialog.dismiss();
                Log.i("裁切失败",t.toString());
                setResultError(t.toString());
            }
        });

    }
}
