package com.paicheya.hammer.graffitipicture;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.paicheya.hammer.graffitipicture.graffitlib.GraffitiView;
import com.paicheya.hammer.graffitipicture.util.MyFileUtil;
import com.paicheya.hammer.graffitipicture.util.MyLog;
import com.paicheya.hammer.graffitipicture.widget.DesignCheckBox;
import com.paicheya.hammer.graffitipicture.widget.DesignCheckListener;


import cn.forward.androids.utils.ImageUtils;
import cn.forward.androids.utils.ThreadUtil;

/**
 * 涂鸦界面，根据GraffitiView的接口，提供页面交互
 * （这边代码和ui比较粗糙，主要目的是告诉大家GraffitiView的接口具体能实现什么功能，实际需求中的ui和交互需另提别论）
 * Created by huangziwei on 2016/9/3.
 */
public class GraffitiActivity extends Activity {

    public static final String KEY_IMAGE_PATH = "key_image_path";
    public static final String KEY_IMAGE_NAME = "key_image_name";
    private String mImagePath;
    private Bitmap mBitmap;

    private FrameLayout mFrameLayout;
    private GraffitiView mGraffitiView;

    private View.OnClickListener buttonOnClickListener;

    //private SeekBar mPaintSizeBar;
    //private TextView mPaintSizeView;

    //private View mBtnColor;
    private Runnable mUpdateScale;

    private int mTouchMode;
    private boolean mIsMovingPic = false;

    // 手势操作相关
    private float mOldScale, mOldDist, mNewDist, mToucheCentreXOnGraffiti,
            mToucheCentreYOnGraffiti, mTouchCentreX, mTouchCentreY;// 双指距离

    private float mTouchLastX, mTouchLastY;

    private boolean mIsScaling = false;
    private float mScale = 1;
    private final float mMaxScale = 3.5f; // 最大缩放倍数
    private final float mMinScale = 0.25f; // 最小缩放倍数
    private final int TIME_SPAN = 40;
    private String mImageName;
    //private View mBtnMovePic;

    private int mTouchSlop;

    // 当前屏幕中心点对应在GraffitiView中的点的坐标
    float mCenterXOnGraffiti;
    float mCenterYOnGraffiti;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImagePath = getIntent().getExtras().getString(KEY_IMAGE_PATH);
        mImageName = getIntent().getExtras().getString(KEY_IMAGE_NAME);
        MyLog.log("开始涂鸦image path:"+mImagePath);
        if (mImagePath == null) {
            this.finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        mBitmap = ImageUtils.createBitmapFromPath(mImagePath, this);
        if (mBitmap == null) {
            this.finish();
            return;
        }

        setContentView(R.layout.graffiti_activity_gra_start);

        //添加涂抹区域
        mFrameLayout = (FrameLayout) findViewById(R.id.graffiti_container);
        mGraffitiView = new GraffitiView(this, mBitmap, new GraffitiView.GraffitiListener() {
            //存储图片
            @Override
            public void onSaved(Bitmap bitmap) { // 保存图片
                saveFileFisish(bitmap);
            }
            @Override
            public void onError(int i, String msg) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
        //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(matc)
        mFrameLayout.addView(mGraffitiView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        buttonOnClickListener = new GraffitiOnClickListener();
        mTouchSlop = ViewConfiguration.get(getApplicationContext()).getScaledTouchSlop();
        //add按钮
        initView();
    }


    private ProgressDialog savingProgressDialog;
    /**
     * save and finish
     * @param bitmap
     */
    private void saveFileFisish(Bitmap bitmap){
        if(savingProgressDialog == null){
            savingProgressDialog = new ProgressDialog(GraffitiActivity.this);
            savingProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            savingProgressDialog.setMessage("保存中...请稍等");
        }
//        savingProgressDialog.show();
        //　保存的路径
        File file = new File(MyFileUtil.getFolderInExternalStorage("pademo_graf"), mImageName);

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            Uri resultUri = Uri.fromFile(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            ImageUtils.addImage(getContentResolver(), file.getAbsolutePath());
            Intent intent = new Intent();
            intent.putExtra(KEY_IMAGE_PATH, resultUri);
            setResult(Activity.RESULT_OK, intent);
            //savingProgressDialog.dismiss();
            finish();
        } catch (Exception e) {
            //savingProgressDialog.dismiss();
            e.printStackTrace();
            MyLog.log("Error","e:"+e.toString());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private int[] paintColors = {Color.parseColor("#568700"),Color.parseColor("#ffa500"),Color.parseColor("#ffffff")};
    private float[] paintSizes = {8f*2.5f,20f*2.5f,40f*2.5f,60f*2.5f};
    private void initView() {

        //清屏按钮
        findViewById(R.id.btn_clear).setOnClickListener(buttonOnClickListener);
        //撤销按钮
        findViewById(R.id.btn_undo).setOnClickListener(buttonOnClickListener);

        //返回
        findViewById(R.id.btn_grafi_back).setOnClickListener(buttonOnClickListener);
        //findViewById(R.id.title_bar_btn01).setOnClickListener(mOnClickListener); //隐藏底部
        //保存
        findViewById(R.id.btn_save).setOnClickListener(buttonOnClickListener);


        //初始化属性 画笔 手绘 居中 颜色argb
        mGraffitiView.centrePic();
        mGraffitiView.setColor(paintColors[0]);
        mGraffitiView.setPaintSize(paintSizes[0]);

        //画笔大小选择器
        DesignCheckBox designCheckBox1 = (DesignCheckBox)findViewById(R.id.designcheck_size);
        designCheckBox1.initCheckListener(new DesignCheckListener() {
            @Override
            public void onChecked(int id) {
                mGraffitiView.setPaintSize(paintSizes[id]);
            }
        },0);

        DesignCheckBox designCheckBox2 = (DesignCheckBox)findViewById(R.id.designcheck_color);
        designCheckBox2.initCheckListener(new DesignCheckListener() {
            @Override
            public void onChecked(int id) {
                if (id ==2){
                    mGraffitiView.setMosaic();
                    return;
                }
                mGraffitiView.setColor(paintColors[id]);
            }
        },0);


        // 添加涂鸦的触摸监听器，移动图片位置
        mGraffitiView.setOnTouchListener(new View.OnTouchListener() {
            boolean mIsBusy = false; // 避免双指滑动，手指抬起时处理单指事件。
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!mIsMovingPic) {
                    return false;
                }
                mScale = mGraffitiView.getScale();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchMode = 1;
                        mTouchLastX = event.getX();
                        mTouchLastY = event.getY();
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mTouchMode = 0;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        if (mTouchMode < 2) { // 单点滑动
                            if (mIsBusy) {
                                mIsBusy = false;
                                mTouchLastX = event.getX();
                                mTouchLastY = event.getY();
                                return true;
                            }
                            float tranX = event.getX() - mTouchLastX;
                            float tranY = event.getY() - mTouchLastY;
                            mGraffitiView.setTrans(mGraffitiView.getTransX() + tranX, mGraffitiView.getTransY() + tranY);
                            mTouchLastX = event.getX();
                            mTouchLastY = event.getY();
                        } else { // 多点
                            mNewDist = spacing(event);// 两点滑动时的距离
                            if (Math.abs(mNewDist - mOldDist) >= mTouchSlop) {
                                float scale = mNewDist / mOldDist;
                                mScale = mOldScale * scale;

                                if (mScale > mMaxScale) {
                                    mScale = mMaxScale;
                                }
                                if (mScale < mMinScale) { // 最小倍数
                                    mScale = mMinScale;
                                }
                                // 围绕坐标(0,0)缩放图片
                                mGraffitiView.setScale(mScale);
                                // 缩放后，偏移图片，以产生围绕某个点缩放的效果
                                float transX = mGraffitiView.toTransX(mTouchCentreX, mToucheCentreXOnGraffiti);
                                float transY = mGraffitiView.toTransY(mTouchCentreY, mToucheCentreYOnGraffiti);
                                mGraffitiView.setTrans(transX, transY);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_POINTER_UP:
                        mTouchMode -= 1;
                        return true;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mTouchMode += 1;
                        mOldScale = mGraffitiView.getScale();
                        mOldDist = spacing(event);// 两点按下时的距离
                        mTouchCentreX = (event.getX(0) + event.getX(1)) / 2;// 不用减trans
                        mTouchCentreY = (event.getY(0) + event.getY(1)) / 2;
                        mToucheCentreXOnGraffiti = mGraffitiView.toX(mTouchCentreX);
                        mToucheCentreYOnGraffiti = mGraffitiView.toY(mTouchCentreY);
                        mIsBusy = true;
                        return true;
                }
                return false;
            }
        });
    }

    /**
     * 计算两指间的距离
     *
     * @param event
     * @return
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private class GraffitiOnClickListener implements View.OnClickListener {
        //private View mLastPenView, mLastShapeView;
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_clear) {  //清屏
                new AlertDialog.Builder(GraffitiActivity.this)
                        .setTitle(R.string.graffiti_clear_screen)
                        .setMessage(R.string.graffiti_cant_undo_after_clearing)
                        .setPositiveButton(R.string.graffiti_enter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mGraffitiView.clear();
                            }
                        })
                        .setNegativeButton(R.string.graffiti_cancel, null)
                        .show();

            } else if (v.getId() == R.id.btn_undo) { //撤销
                mGraffitiView.undo();

            } else if (v.getId() == R.id.btn_save) { //保存
                mGraffitiView.save();

            } else if (v.getId() == R.id.btn_grafi_back) {  //返回
                if (!mGraffitiView.isModified()) {
                    finish();
                    return;
                }
                new AlertDialog.Builder(GraffitiActivity.this).setTitle(R.string.graffiti_saving_picture)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton(R.string.graffiti_enter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                mGraffitiView.save();
                            }
                        })
                        .setNegativeButton(R.string.graffiti_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                finish();
                            }
                        }).show();

            }
        }
    }

    @Override
    public void onBackPressed() {
            findViewById(R.id.btn_grafi_back).performClick();
    }

    /**
     * 放大缩小
     */
    private class ScaleOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    scalePic(v);
                    v.setSelected(true);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mIsScaling = false;
                    v.setSelected(false);
                    break;
            }
            return true;
        }
    }

    /**
     * 缩放
     *
     * @param v
     */
    public void scalePic(View v) {
        if (mIsScaling)
            return;
        mIsScaling = true;
        mScale = mGraffitiView.getScale();

        // 确定当前屏幕中心点对应在GraffitiView中的点的坐标，之后将围绕这个点缩放
        mCenterXOnGraffiti = mGraffitiView.toX(mGraffitiView.getWidth() / 2);
        mCenterYOnGraffiti = mGraffitiView.toY(mGraffitiView.getHeight() / 2);

//        if (v.getid() == r.id.btn_amplifier) { // 放大
//            threadutil.getinstance().runonasyncthread(new runnable() {
//                public void run() {
//                    do {
//                        mscale += 0.05f;
//                        if (mscale > mmaxscale) {
//                            mscale = mmaxscale;
//                            misscaling = false;
//                        }
//                        updatescale();
//                        try {
//                            thread.sleep(time_span);
//                        } catch (interruptedexception e) {
//                            e.printstacktrace();
//                        }
//                    } while (misscaling);
//
//                }
//            });
//        } else if (v.getid() == r.id.btn_reduce) { // 缩小
//            threadutil.getinstance().runonasyncthread(new runnable() {
//                public void run() {
//                    do {
//                        mscale -= 0.05f;
//                        if (mscale < mminscale) {
//                            mscale = mminscale;
//                            misscaling = false;
//                        }
//                        updatescale();
//                        try {
//                            thread.sleep(time_span);
//                        } catch (interruptedexception e) {
//                            e.printstacktrace();
//                        }
//                    } while (misscaling);
//                }
//            });
//        }
    }

    private void updateScale() {
        if (mUpdateScale == null) {

            mUpdateScale = new Runnable() {
                public void run() {
                    // 围绕坐标(0,0)缩放图片
                    mGraffitiView.setScale(mScale);
                    // 缩放后，偏移图片，以产生围绕某个点缩放的效果
                    float transX = mGraffitiView.toTransX(mGraffitiView.getWidth() / 2, mCenterXOnGraffiti);
                    float transY = mGraffitiView.toTransY(mGraffitiView.getHeight() / 2, mCenterYOnGraffiti);
                    mGraffitiView.setTrans(transX, transY);
                }
            };
        }
        ThreadUtil.getInstance().runOnMainThread(mUpdateScale);
    }
}
