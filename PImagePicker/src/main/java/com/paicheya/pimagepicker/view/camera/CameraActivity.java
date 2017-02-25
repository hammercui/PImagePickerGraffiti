package com.paicheya.pimagepicker.view.camera;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.android.flexbox.FlexboxLayout;
import com.paicheya.pimagepicker.ActionsManager;
import com.paicheya.pimagepicker.R;
import com.paicheya.pimagepicker.bean.ImageItem;
import com.paicheya.pimagepicker.listener.CameraDealCallback;
import com.paicheya.pimagepicker.core.BaseActivity;
import com.paicheya.pimagepicker.util.BitmapUtil;
import com.paicheya.pimagepicker.util.MyLog;
import com.paicheya.pimagepicker.util.ScreenRotateUtil;
import com.paicheya.pimagepicker.util.ScreenUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 * 自定义相机
 * Created by cly on 16/11/24.
 */

public class CameraActivity extends BaseActivity implements CameraPreview.OnCameraStatusListener,ScreenRotateUtil.ScreenRotateListener {

    private CameraPreview cameraPreview;//相机视图
    private ImageView imagePreview;//预览视图
    private RelativeLayout cameraLayout;
    private FlexboxLayout resultLayout;
    private RelativeLayout topLayout;
    private ReferenceLine referenceLine;
    private TextView tvFlashLight;
    private TextView tvCancel;
    private ImageView btnCamera;
    private LinearLayout guideLayout;
    private TextView tvTips;


    private ScreenRotateUtil screenRotateUtil;
    private Bitmap bitmap;
    private boolean flashLight = false;

    int cameraWidth ;
    int cameraHeight ;
    int previewWidth;
    int previewHeight;

    private CameraDealTask cameraDealTask;

    //独占操作
    private Boolean doingCamera = false;

    private Direction currDirect = Direction.Vertical;

    public enum Direction{
        Horizontal,//水平方向
        Vertical, //垂直方向
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //强制设置相应屏幕旋转
       // this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        setContentView(R.layout.pip_activity_camera);

        this.initView(this);
    }

    @Override
    public void isPortrait(boolean isPortrait) {
        //竖屏
        if(isPortrait && currDirect== Direction.Horizontal ){
            currDirect = Direction.Vertical;
            referenceLine.setVisibility(View.VISIBLE);
            tvTips.setVisibility(View.VISIBLE);
            guideLayout.setRotation(0);
            btnCamera.setRotation(0);
            tvFlashLight.setRotation(0);
            tvCancel.setRotation(0);
        }
        //横屏
        else if(!isPortrait && currDirect == Direction.Vertical){
            currDirect = Direction.Horizontal;
            referenceLine.setVisibility(View.INVISIBLE);
            tvTips.setVisibility(View.INVISIBLE);
            guideLayout.setRotation(90);
            btnCamera.setRotation(90);
            tvFlashLight.setRotation(90);
            tvCancel.setRotation(90);
        }
    }

    /**
     * 初始化视图
     */
    private void initView(Context context){

        cameraWidth   = ScreenUtils.getScreenWH(context).widthPixels;
        cameraHeight  = (int)(cameraWidth * pImagePickerConfig.getAspectRatio());
        previewWidth  = cameraWidth;
        previewHeight = (int)(previewWidth / pImagePickerConfig.getAspectRatio());
        this.initSensor();
        this.initTopView();
        this.initCameraView();
        this.initBottomView();

        this.showCameraLayout();
    }



    /**
     * 初始化传感器
     */
    private void initSensor(){
        //初始化传感器
       screenRotateUtil = new ScreenRotateUtil(this);
    }

    //初始化顶部菜单
    private void initTopView(){
        topLayout = (RelativeLayout)this.findViewById(R.id.layout_top);
        tvFlashLight = (TextView)this.findViewById(R.id.tv_flashLight);
        tvFlashLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPreview.turnLight();
                MyLog.log(flashLight?"关闭闪光灯":"打开闪光灯");
                flashLight = !flashLight;
                tvFlashLight.setTextColor(flashLight? Color.YELLOW:Color.WHITE);

            }
        });
        tvCancel = (TextView)this.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doNothingFinish();
            }
        });
    }


    private void doNothingFinish(){
        this.finish();
    }

    //初始化中间取景框
    private void initCameraView(){
        FocusView focusView = (FocusView)this.findViewById(R.id.viewFocus);

        cameraPreview = (CameraPreview)this.findViewById(R.id.cameraPreview);
        cameraPreview.setFocusView(focusView);
        cameraPreview.setOnCameraStatusListener(this);
        cameraPreview.setAspectRatio(pImagePickerConfig.getAspectRatio());
        //提示框
        referenceLine = (ReferenceLine)this.findViewById(R.id.referenceLine);
        RelativeLayout.LayoutParams referenceParams = new RelativeLayout.LayoutParams(previewWidth,previewHeight);
        referenceParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        referenceLine.setLayoutParams(referenceParams);
        referenceLine.setAspectRatio(pImagePickerConfig.getAspectRatio());
        //提示文字
        tvTips = (TextView)this.findViewById(R.id.tv_tips);


        //重设取景框宽高//照相区域宽高比4：3
        cameraLayout = (RelativeLayout)this.findViewById(R.id.cameraLayout);
        cameraLayout.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams cameraParams = new RelativeLayout.LayoutParams(cameraWidth,cameraHeight);
        cameraParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        cameraLayout.setLayoutParams(cameraParams);

        //获得预览控件
        imagePreview = (ImageView) this.findViewById(R.id.image_preview);
        imagePreview.setScaleType(ImageView.ScaleType.FIT_XY); //预览图宽高比4：3
        RelativeLayout.LayoutParams previewParams = new RelativeLayout.LayoutParams(previewWidth,previewHeight);
        previewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        imagePreview.setLayoutParams(previewParams);

        //引导图片
        guideLayout = (LinearLayout) this.findViewById(R.id.layout_guide);
    }

    //初始化底部菜单
    private void initBottomView(){
        btnCamera = (ImageView)this.findViewById(R.id.btn_takephoto);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cameraPreview != null && !doingCamera) {
                    doingCamera = true;
                    cameraPreview.takePicture();
                }
            }
        });
        resultLayout = (FlexboxLayout)this.findViewById(R.id.layout_result);
        TextView tvRetry = (TextView)this.findViewById(R.id.tv_retry);
        TextView tvEdit = (TextView)this.findViewById(R.id.tv_edit);
        TextView tvOk = (TextView)this.findViewById(R.id.tv_ok);
        //重拍
        tvRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraLayout();
            }
        });
        //去编辑
        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //saveAndEdit();
            }
        });
        //保存
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndClose();
            }
        });

    }



    /**
     * 保存拍照图片进入编辑
     */
    public void saveAndEdit(){
        if(bitmap == null){
            setResultError("拍照图片失败，未知错误");
            return;
        }
        Uri uri = Uri.fromFile(new File(pImagePickerConfig.getImagePath()));
        //图片保存 并跳转编辑页
        if(BitmapUtil.saveBitmapFile(bitmap,pImagePickerConfig.getImagePath(), pImagePickerConfig.getPressQuality())){
            insertGallery(uri);
            ActionsManager.transEditActivity(this,uri);
        }
        else{
            setResultError("保存图片失败");
        }
    }

    /**
     * 保存拍照并返回
     */
    public void saveAndClose(){
        if(bitmap == null){
            setResultError("拍照图片失败，未知错误");
            return;
        }
        Uri uri = Uri.fromFile(new File(pImagePickerConfig.getImagePath()));
        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();
        //图片保存成功
        if(BitmapUtil.saveBitmapFile(bitmap,pImagePickerConfig.getImagePath(), pImagePickerConfig.getPressQuality())){
            //insertGallery(uri);
            long size   =  new File(pImagePickerConfig.getImagePath()).length();
            MyLog.log("处理后图片大小:"+size);
            setResultUri(new ImageItem(pImagePickerConfig.getImagePath(),width,height,size));
        }
        else{
            setResultError("图片保存失败！");
        }
    }


    //图片插入到系统图库
    private void insertGallery(Uri uri){
        if(!pImagePickerConfig.isCameraRoll())
            return;
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(
                    this.getContentResolver(),
                    pImagePickerConfig.getImagePath(),
                    pImagePickerConfig.getImageName(),
                    null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri ));
    }


    /**
     * 拍照获得图片
     * @param data
     */
    @Override
    public void onCameraStopped(byte[] data) {
        //Uri testUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath()+"/image/test.jpg"));
        if(cameraDealTask != null){
            cameraDealTask.cancel(true);
            cameraDealTask = null;
        }
        cameraDealTask = new CameraDealTask(new CameraDealCallback() {
            @Override
            public void onSuccess(Bitmap params) {
                bitmap = params;
                showPreviewLayout();
            }
        },currDirect,pImagePickerConfig.getAspectRatio());
        cameraDealTask.setOutPutPath(pImagePickerConfig.getImagePath());
        cameraDealTask.execute(data);
    }


    /**
     * 显示相机
     */
    private void showCameraLayout() {
        cameraLayout.setVisibility(View.VISIBLE);
        cameraPreview.start();   //继续启动摄像头
        tvFlashLight.setVisibility(View.VISIBLE);
        resultLayout.setVisibility(View.INVISIBLE);
        btnCamera.setVisibility(View.VISIBLE);
        imagePreview.setVisibility(View.INVISIBLE);

    }

    /**
     * 预览结果
     */
    private void showPreviewLayout() {
        doingCamera = false;
        cameraLayout.setVisibility(View.INVISIBLE);
        tvFlashLight.setVisibility(View.INVISIBLE);
        resultLayout.setVisibility(View.VISIBLE);
        btnCamera.setVisibility(View.INVISIBLE);
        imagePreview.setVisibility(View.VISIBLE);
        imagePreview.setImageBitmap(bitmap);
        isPortrait(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        screenRotateUtil.resume(this);
        doingCamera = false;
    }
    @Override
    protected void onPause() {
        super.onPause();
        screenRotateUtil.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消任务
        if(cameraDealTask!=null){
            cameraDealTask.cancel(true);
        }
        cameraPreview = null;
        Log.i("销毁","测试相机销毁");
    }
}
