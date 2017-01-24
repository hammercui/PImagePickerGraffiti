package com.paicheya.hammer.graffitipicture.graffitlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.concurrent.CopyOnWriteArrayList;

import cn.forward.androids.utils.ThreadUtil;

/**
 * Created by huangziwei on 2016/9/3.
 */
public class GraffitiView extends View {

    public static final int ERROR_INIT = -1;
    public static final int ERROR_SAVE = -2;

    private static final float VALUE = 1f;
    private final int TIME_SPAN = 80;

    /**
     * 回调接口，返回保存的bitmao
     */
    private GraffitiListener mGraffitiListener;

    /**
     * 原图
     */
    private Bitmap mBitmap;
    /**
     * 用绘制涂鸦的图片
     */
    private Bitmap mGraffitiBitmap;
    private Bitmap bmCoverLayer;
//    private Bitmap mTouchLayerBitmap;
//    private Bitmap mMosaicBitmap;
    private Canvas mBitmapCanvas;
    private Canvas mTouchLayerCanvas;


    //private Bitmap bmMosaicLayer;

    /**
     * 图片适应屏幕时的缩放倍数 图片尺寸/控件尺寸
     */
    private float mPrivateScale;
    private int mPrivateHeight, mPrivateWidth;// 图片适应屏幕时的大小
    private float mCentreTranX, mCentreTranY;// 图片居中时的偏移

    private BitmapShader mBitmapShader; // 用于涂鸦的图片上
    private BitmapShader mBitmapShader4C;
    private Path mCurrPath; // 当前手写的路径
    //private Path mCanvasPath; //
    //private CopyLocation mCopyLocation; // 仿制的定位器

    private Paint mPaint;
            //,mosaicPaint;
    private int mTouchMode; // 触摸模式，用于判断单点或多点触摸
    private float mPaintSize;
    private GraffitiColor mGraffitiColor; // 画笔底色
    /**
     * 缩放倍数, 图片真实的缩放倍数为 mPrivateScale*mScale
     */
    private float mScale = 1.0f;
    private float mTransX = 0, mTransY = 0; // 偏移量，图片真实偏移量为　mCentreTranX + mTransX

    //private boolean mIsPainting = false; // 是否正在绘制
    private boolean isJustDrawOriginal; // 是否只绘制原图

    public static  final  String TAG = "涂鸦测试" ;
    // 保存涂鸦操作，便于撤销
    private CopyOnWriteArrayList<GraffitiPath> mPathStack = new CopyOnWriteArrayList<GraffitiPath>();
//    private CopyOnWriteArrayList<GraffitiPath> mPathStackBackup = new CopyOnWriteArrayList<GraffitiPath>();

//    /**
//     * 画笔
//     */
//    public enum Pen {
//        HAND, // 手绘
////        COPY, // 仿制
////        ERASER // 橡皮擦
//    }
//
//    /**
//     * 图形
//     */
//    public enum Shape {
//        HAND_WRITE, //
////        ARROW, // 箭头
////        LINE, // 直线
////        FILL_CIRCLE, // 实心圆
////        HOLLOW_CIRCLE, // 空心圆
////        FILL_RECT, // 实心矩形
////        HOLLOW_RECT, // 空心矩形
//    }

//    private Pen mPen;
//    private Shape mShape;

    private float mTouchDownX, mTouchDownY, mLastTouchX, mLastTouchY, mTouchX, mTouchY;
    private Matrix mShaderMatrix, mShaderMatrix4C;

    public GraffitiView(Context context, Bitmap bitmap, GraffitiListener listener) {
        super(context);
        mBitmap = bitmap;
        mGraffitiListener = listener;
        if (mGraffitiListener == null) {
            throw new RuntimeException("GraffitiListener is null!!!");
        }
        if (mBitmap == null) {
            throw new RuntimeException("Bitmap is null!!!");
        }

        initPaint();
        bmCoverLayer = getCoveryLayerBitmap();
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setBG();
//        mCopyLocation.updateLocation(toX4C(w / 2), toY4C(h / 2));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                mTouchMode = 1;
                mTouchDownX = mTouchX = mLastTouchX = event.getX();
                mTouchDownY = mTouchY = mLastTouchY = event.getY();

                mTouchX += VALUE; // 为了仅点击时也能出现绘图，模拟滑动一个像素点
                mTouchY += VALUE;

//                    if (mShape == Shape.HAND_WRITE) { // 手写
                        mCurrPath = new Path();
                        mCurrPath.moveTo(toX(mTouchDownX), toY(mTouchDownY));
//
//                        mCanvasPath.reset();
//                        mCanvasPath.moveTo(toX4C(mTouchDownX), toY4C(mTouchDownY));
//
//                        // 为了仅点击时也能出现绘图，必须移动path
//                        mCanvasPath.quadTo(
//                                toX4C(mLastTouchX),
//                                toY4C(mLastTouchY),
//                                toX4C((mTouchX + mLastTouchX) / 2),
//                                toY4C((mTouchY + mLastTouchY) / 2));
//                    }
//                    mIsPainting = true;
                //}
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                mTouchMode = 0;
                mLastTouchX = mTouchX;
                mLastTouchY = mTouchY;
                mTouchX = event.getX();
                mTouchY = event.getY();

                GraffitiPath path = null;
                mCurrPath.quadTo(
                                toX(mLastTouchX),
                                toY(mLastTouchY),
                                toX((mTouchX + mLastTouchX) / 2),
                                toY((mTouchY + mLastTouchY) / 2));
                path = GraffitiPath.generator(mPaintSize, mGraffitiColor.copy(), mCurrPath, null);

                // 把操作记录到加入的堆栈中
                mPathStack.add(path);
                drawFunc2(mCurrPath,mGraffitiColor,mPaintSize);

                invalidate();

            case MotionEvent.ACTION_MOVE:
                if (mTouchMode < 2) { // 单点滑动
                    mLastTouchX = mTouchX;
                    mLastTouchY = mTouchY;
                    mTouchX = event.getX();
                    mTouchY = event.getY();


                    mCurrPath.quadTo(
                                    toX(mLastTouchX),
                                    toY(mLastTouchY),
                                    toX((mTouchX + mLastTouchX) / 2),
                                    toY((mTouchY + mLastTouchY) / 2));

                    drawFunc2(mCurrPath,mGraffitiColor,mPaintSize);
                    invalidate();
                }
                return true;

        }
        return super.onTouchEvent(event);
    }


    private void setBG() {// 不用resize preview
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        float nw = w * 1f / getWidth();
        float nh = h * 1f / getHeight();
        if (nw > nh) {
            mPrivateScale = 1 / nw;
            mPrivateWidth = getWidth();
            mPrivateHeight = (int) (h * mPrivateScale);
        } else {
            mPrivateScale = 1 / nh;
            mPrivateWidth = (int) (w * mPrivateScale);
            mPrivateHeight = getHeight();
        }
        // 使图片居中
        mCentreTranX = (getWidth() - mPrivateWidth) / 2f;
        mCentreTranY = (getHeight() - mPrivateHeight) / 2f;

        initCanvas();
        //resetMatrix();
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBitmap.isRecycled()) {
            return;
        }

        canvas.scale(mPrivateScale * mScale, mPrivateScale * mScale);


        if (mGraffitiBitmap != null) {
            canvas.drawBitmap(mGraffitiBitmap, (mCentreTranX + mTransX) / (mPrivateScale * mScale), (mCentreTranY + mTransY) / (mPrivateScale * mScale), null);
        }

    }



    private void initPaint(){

        this.mBitmapShader = new BitmapShader(this.mBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        this.mBitmapShader4C = new BitmapShader(this.mBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        mShaderMatrix = new Matrix();
        mShaderMatrix4C = new Matrix();

        mPaintSize = 30;
        mGraffitiColor = new GraffitiColor(Color.BLUE);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setPathEffect(new CornerPathEffect(10));
        mPaint.setStrokeWidth(mPaintSize);
        mPaint.setColor(mGraffitiColor.mColor);
    }

    /**
     * 初始化缓存用canvas
     */
    private void initCanvas() {
        if (mGraffitiBitmap != null) {
            mGraffitiBitmap.recycle();
        }

        mGraffitiBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
//        mGraffitiBitmap = Bitmap.createBitmap(mImageWidth, mImageHeight,
//                Bitmap.Config.ARGB_8888);

        mBitmapCanvas = new Canvas(mGraffitiBitmap);
        mTouchLayerCanvas = new Canvas();


    }


    /**
     * 实际绘制
     * @param path  Path
     * @param graffitiColor GraffitiColor存储颜色和是否马赛克
     * @param size 画笔宽度
     */
    private void drawFunc2(Path path,GraffitiColor graffitiColor,float size){
        //1 获得手势绘画的bitmap bmTouchLayer
        if (graffitiColor.isMosiac()){
            Bitmap mTouchLayerBitmap =    Bitmap.createBitmap(mImageWidth,mImageHeight, Bitmap.Config.ARGB_8888);
            Bitmap mMosaicBitmap     =    Bitmap.createBitmap(mImageWidth,mImageHeight, Bitmap.Config.ARGB_8888);
            mTouchLayerCanvas.setBitmap(mTouchLayerBitmap);
            mPaint.setXfermode(null);
            mPaint.setStrokeWidth(size);
            mTouchLayerCanvas.drawPath(path,mPaint);


            mTouchLayerCanvas.setBitmap(mMosaicBitmap);
            mTouchLayerCanvas.drawBitmap(bmCoverLayer,0,0,null);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));//DST_IN显示原有图层
            mTouchLayerCanvas.drawBitmap(mTouchLayerBitmap,0,0,mPaint);

            mBitmapCanvas.drawBitmap(mMosaicBitmap,0,0,null);

            mTouchLayerBitmap.recycle();
            mMosaicBitmap.recycle();

         }
        else {
            //Canvas canvas = new Canvas(mGraffitiBitmap);
            mPaint.setXfermode(null);
            mPaint.setColor(graffitiColor.mColor);
            mPaint.setStrokeWidth(size);
            //mPaint.setColor(mColor.mColor);
            mBitmapCanvas.drawPath(path, mPaint);
        }
    }



    /**
     * 回退操作
     * @param canvas
     * @param pathStack
     * @param is4Canvas
     */
    private void drawUndo(Canvas canvas, CopyOnWriteArrayList<GraffitiPath> pathStack, boolean is4Canvas) {
        initCanvas();
        // 还原堆栈中的记录的操作
        for (GraffitiPath path : pathStack) {
            drawFunc2(path.mPath,path.mGraffitiColor,path.mStrokeWidth);
        }
    }

    /**
     * 将屏幕触摸坐标x转换成在图片中的坐标
     */
    public final float toX(float touchX) {
        return (touchX - mCentreTranX - mTransX) / (mPrivateScale * mScale);
    }

    /**
     * 将屏幕触摸坐标y转换成在图片中的坐标
     */
    public final float toY(float touchY) {
        return (touchY - mCentreTranY - mTransY) / (mPrivateScale * mScale);
    }

    /**
     * 坐标换算
     * （公式由toX()中的公式推算出）
     *
     * @param touchX    触摸坐标
     * @param graffitiX 在涂鸦图片中的坐标
     * @return 偏移量
     */
    public final float toTransX(float touchX, float graffitiX) {
        return -graffitiX * (mPrivateScale * mScale) + touchX - mCentreTranX;
    }

    public final float toTransY(float touchY, float graffitiY) {
        return -graffitiY * (mPrivateScale * mScale) + touchY - mCentreTranY;
    }
    /**
     * 将屏幕触摸坐标x转换成在canvas中的坐标
     */
    public final float toX4C(float x) {
        return (x) / (mPrivateScale * mScale);
    }

    /**
     * 将屏幕触摸坐标y转换成在canvas中的坐标
     */
    public final float toY4C(float y) {
        return (y) / (mPrivateScale * mScale);
    }



    private  int mImageWidth;
    private int mImageHeight;

    /**
     * 获得马赛克遮罩
     * @return
     */
    private Bitmap getCoveryLayerBitmap(){
        mImageWidth = mBitmap.getWidth();
        mImageHeight = mBitmap.getHeight();
        int mGridWidth = 30;

        Bitmap bitmap = Bitmap.createBitmap(mImageWidth, mImageHeight,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        int horCount = (int) Math.ceil(mImageWidth / (float) mGridWidth);
        int verCount = (int) Math.ceil(mImageHeight / (float) mGridWidth);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        for (int horIndex = 0; horIndex < horCount; ++horIndex) {
            for (int verIndex = 0; verIndex < verCount; ++verIndex) {
                int l = mGridWidth * horIndex;
                int t = mGridWidth * verIndex;
                int r = l + mGridWidth;
                if (r > mImageWidth) {
                    r = mImageWidth;
                }
                int b = t + mGridWidth;
                if (b > mImageHeight) {
                    b = mImageHeight;
                }
                int color = mBitmap.getPixel(l, t);
                Rect rect = new Rect(l, t, r, b);
                paint.setColor(color);
                canvas.drawRect(rect, paint);
            }
        }
        canvas.save();
        return bitmap;
    }

    private void resetMatrix() {
            this.mShaderMatrix.set(null);
            this.mBitmapShader.setLocalMatrix(this.mShaderMatrix);

            this.mShaderMatrix4C.set(null);
            this.mShaderMatrix4C.postTranslate((mCentreTranX + mTransX) / (mPrivateScale * mScale), (mCentreTranY + mTransY) / (mPrivateScale * mScale));
            this.mBitmapShader4C.setLocalMatrix(this.mShaderMatrix4C);
        //}
    }

    /**
     * 调整图片位置
     */
    private void judgePosition() {
        boolean changed = false;
        if (mScale > 1) { // 当图片放大时，图片偏移的位置不能超过屏幕边缘
            if (mTransX > 0) {
                mTransX = 0;
                changed = true;
            } else if (mTransX + mPrivateWidth * mScale < mPrivateWidth) {
                mTransX = mPrivateWidth - mPrivateWidth * mScale;
                changed = true;
            }
            if (mTransY > 0) {
                mTransY = 0;
                changed = true;
            } else if (mTransY + mPrivateHeight * mScale < mPrivateHeight) {
                mTransY = mPrivateHeight - mPrivateHeight * mScale;
                changed = true;
            }
        } else { // 当图片缩小时，图片只能在屏幕可见范围内移动
            if (mTransX + mBitmap.getWidth() * mPrivateScale * mScale > mPrivateWidth) { // mScale<1是preview.width不用乘scale
                mTransX = mPrivateWidth - mBitmap.getWidth() * mPrivateScale * mScale;
                changed = true;
            } else if (mTransX < 0) {
                mTransX = 0;
                changed = true;
            }
            if (mTransY + mBitmap.getHeight() * mPrivateScale * mScale > mPrivateHeight) {
                mTransY = mPrivateHeight - mBitmap.getHeight() * mPrivateScale * mScale;
                changed = true;
            } else if (mTransY < 0) {
                mTransY = 0;
                changed = true;
            }
        }
        if (changed) {
            resetMatrix();
        }
    }

    /**
     * 仿制的定位器
     */
    private class CopyLocation {

        private float mCopyStartX, mCopyStartY; // 仿制的坐标
        private float mTouchStartX, mTouchStartY; // 开始触摸的坐标
        private float mX, mY; // 当前位置

        private Paint mPaint;

        private boolean isRelocating = true; // 正在定位中
        private boolean isCopying = false; // 正在仿制绘图中

        public CopyLocation(float x, float y) {
            mX = x;
            mY = y;
            mTouchStartX = x;
            mTouchStartY = y;
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setStrokeWidth(mPaintSize);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
        }


        public void updateLocation(float x, float y) {
            mX = x;
            mY = y;
        }

        public void setStartPosition(float x, float y) {
            mCopyStartX = mX;
            mCopyStartY = mY;
            mTouchStartX = x;
            mTouchStartY = y;
        }

        public void drawItSelf(Canvas canvas) {
            mPaint.setStrokeWidth(mPaintSize / 4);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(0xaa666666); // 灰色
            DrawUtil.drawCircle(canvas, mX, mY, mPaintSize / 2 + mPaintSize / 8, mPaint);

            mPaint.setStrokeWidth(mPaintSize / 16);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(0xaaffffff); // 白色
            DrawUtil.drawCircle(canvas, mX, mY, mPaintSize / 2 + mPaintSize / 32, mPaint);

            mPaint.setStyle(Paint.Style.FILL);
            if (!isCopying) {
                mPaint.setColor(0x44ff0000); // 红色
                DrawUtil.drawCircle(canvas, mX, mY, mPaintSize / 2, mPaint);
            } else {
                mPaint.setColor(0x44000088); // 蓝色
                DrawUtil.drawCircle(canvas, mX, mY, mPaintSize / 2, mPaint);
            }
        }

        /**
         * 判断是否点中
         */
        public boolean isInIt(float x, float y) {
            if ((mX - x) * (mX - x) + (mY - y) * (mY - y) <= mPaintSize * mPaintSize) {
                return true;
            }
            return false;
        }

    }




    // ===================== api ==============

    /**
     * 保存
     */
    public void save() {
//            initTempCanvas();
//            draw(mBitmapCanvas, mPathStackBackup, false);
//            draw(mBitmapCanvas, mPathStack, false);
        mGraffitiListener.onSaved(mGraffitiBitmap);
    }

    /**
     * 清屏
     */
    public void clear() {
        mPathStack.clear();
//        mPathStackBackup.clear();
        initCanvas();
        invalidate();
    }

    /**
     * 撤销
     */
    public void undo() {
        if (mPathStack.size() > 0) {
            mPathStack.remove(mPathStack.size() - 1);
            initCanvas();
            drawUndo(mBitmapCanvas, mPathStack, false);
            invalidate();
        }
    }

    /**
     * 是否有修改
     */
    public boolean isModified() {
        return mPathStack.size() != 0;
    }

    /**
     * 居中图片
     */
    public void centrePic() {
        if (mScale > 1) {
            ThreadUtil.getInstance().runOnAsyncThread(new Runnable() {
                boolean isScaling = true;

                public void run() {
                    do {
                        mScale -= 0.2f;
                        if (mScale <= 1) {
                            mScale = 1;
                            isScaling = false;
                        }
                        judgePosition();
                        postInvalidate();
                        try {
                            Thread.sleep(TIME_SPAN / 2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } while (isScaling);

                }
            });
        } else if (mScale < 1) {
            ThreadUtil.getInstance().runOnAsyncThread(new Runnable() {
                boolean isScaling = true;

                public void run() {
                    do {
                        mScale += 0.2f;
                        if (mScale >= 1) {
                            mScale = 1;
                            isScaling = false;
                        }
                        judgePosition();
                        postInvalidate();
                        try {
                            Thread.sleep(TIME_SPAN / 2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } while (isScaling);
                }
            });
        }
    }

    /**
     * 只绘制原图
     *
     * @param justDrawOriginal
     */
    public void setJustDrawOriginal(boolean justDrawOriginal) {
        isJustDrawOriginal = justDrawOriginal;
        invalidate();
    }

    public boolean isJustDrawOriginal() {
        return isJustDrawOriginal;
    }

    /**
     * 设置画笔底色
     *
     * @param color
     */
    public void setColor(int color) {
        mGraffitiColor.setColor(color);
        //isMosaic = false;
        invalidate();
    }

    //private boolean isMosaic = false;
    /**
     * 设置画笔为马赛克
     */
    public void setMosaic(){
        mGraffitiColor.setColor(true);
        invalidate();
    }

    /**
     *设置画笔图片
     * @param bitmap
     */
    public void setColor(Bitmap bitmap) {
        if (mBitmap == null) {
            return;
        }
        mGraffitiColor.setColor(bitmap);
        invalidate();
    }

    /**
     * 设置画笔为马赛克笔
     */
    public void hammerSetMosaicColor(){


    }

    public void setColor(Bitmap bitmap, Shader.TileMode tileX, Shader.TileMode tileY) {
        if (mBitmap == null) {
            return;
        }
        mGraffitiColor.setColor(bitmap, tileX, tileY);
        invalidate();
    }

    public GraffitiColor getGraffitiColor() {
        return mGraffitiColor;
    }

    /**
     * 缩放倍数，图片真实的缩放倍数为 mPrivateScale*mScale
     *
     * @param scale
     */
    public void setScale(float scale) {
        this.mScale = scale;
        judgePosition();
        resetMatrix();
        invalidate();
    }

    public float getScale() {
        return mScale;
    }



    public void setTrans(float transX, float transY) {
        mTransX = transX;
        mTransY = transY;
        judgePosition();
        resetMatrix();
        invalidate();
    }

    /**
     * 设置图片偏移
     *
     * @param transX
     */
    public void setTransX(float transX) {
        this.mTransX = transX;
        judgePosition();
        invalidate();
    }

    public float getTransX() {
        return mTransX;
    }

    public void setTransY(float transY) {
        this.mTransY = transY;
        judgePosition();
        invalidate();
    }

    public float getTransY() {
        return mTransY;
    }


    public void setPaintSize(float paintSize) {
        mPaintSize = paintSize;
        invalidate();
    }

    public float getPaintSize() {
        return mPaintSize;
    }

    public interface GraffitiListener {

        /**
         * 保存图片
         *
         * @param bitmap
         */
        void onSaved(Bitmap bitmap);

        /**
         * 出错
         *
         * @param i
         * @param msg
         */
        void onError(int i, String msg);
    }
}
