package com.paicheya.hammer.graffitipicture.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


import com.paicheya.hammer.graffitipicture.R;
import com.paicheya.hammer.graffitipicture.R;



import java.util.Random;

/**
 * 马赛克测试视图
 * Created by hammer on 2016/11/13.
 */

public class MosaicView extends View{
    public static final String TAG = "测试";
    private Bitmap mBitmap;
    private static final String ERROR_INFO = "bad bitmap to add mosaic";
    private final int BLOCK_SIZE = 30;// 马赛克的大小：BLOCK_SIZE*BLOCK_SIZE  像素点X像素点
    /**
     * 马赛克块数组
     */
    private int[] mSampleColors;
    private Paint mPaint;
    private float mLastX, mLastY;
    private int mBitmapWidth, mBitmapHeight;
    /**
     * // 保留原图的像素数组
     */
    private int[] mSrcBitmapPixs;
    /**
     * // 用于马赛克的临时像素数组
     */
    private int[] mTempBitmapPixs;
    /**
     * 每行马赛克块数
     */
    private int mRowCount;
    /**
     * 每列马赛克块数
     */
    private int mColumnCount;
    private final int VALID_DISTANCE = 4; // 滑动的有效距离

    public MosaicView(Context context, int width, int height) {
        super(context);
        // image 为图片地址
        mBitmap = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.pic_test),
                 720,1000, true);
        Log.d("测试","view的宽高："+this.getWidth()+"      "+this.getHeight());
//        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic_test);
        init(mBitmap);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    }

    /**
     * 初始化bitmap 包括原像素组 缓存像素组 马赛克块组
     *
     * @param bitmap
     */
    public void init(Bitmap bitmap) {
        if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0
                || bitmap.isRecycled()) {
            throw new RuntimeException(ERROR_INFO);
        }
        mBitmapWidth = bitmap.getWidth();
        mBitmapHeight = bitmap.getHeight();
        mRowCount = (int) Math.ceil((float) mBitmapHeight / BLOCK_SIZE); //math.cell向上取整，表示行有几个马赛克块
        mColumnCount = (int) Math.ceil((float) mBitmapWidth / BLOCK_SIZE);
        mSampleColors = new int[mRowCount * mColumnCount]; //表示一共有多少块马赛克，也就有多少块色块

        int maxX = mBitmapWidth - 1;
        int maxY = mBitmapHeight - 1;
        mSrcBitmapPixs = new int[mBitmapWidth * mBitmapHeight];
        mTempBitmapPixs = new int[mBitmapWidth * mBitmapHeight];

        bitmap.getPixels(mSrcBitmapPixs, 0, mBitmapWidth, 0, 0, mBitmapWidth,
                mBitmapHeight);//逐行读取，把像素存入一维数组
        bitmap.getPixels(mTempBitmapPixs, 0, mBitmapWidth, 0, 0, mBitmapWidth,
                mBitmapHeight);

        //马赛克块数组存入色值
        for (int row = 0; row < mRowCount; row++) {
            for (int column = 0; column < mColumnCount; column++) {
                int startX = column * BLOCK_SIZE;
                int startY = row * BLOCK_SIZE;
                mSampleColors[row * mColumnCount + column] = sampleBlock(
                        mSrcBitmapPixs, startX, startY, BLOCK_SIZE, maxX, maxY);
            }
        }

        bitmap.setPixels(mSrcBitmapPixs, 0, mBitmapWidth, 0, 0, mBitmapWidth,
                mBitmapHeight);
    }


    /**
     * 生成一个马赛克块的色值
     * @param pxs
     * @param startX
     * @param startY
     * @param blockSize
     * @param maxX
     * @param maxY
     * @return
     */
    private int sampleBlock(int[] pxs, int startX, int startY, int blockSize,
                            int maxX, int maxY) {
        int stopX = startX + blockSize - 1;
        int stopY = startY + blockSize - 1;
        stopX = Math.min(stopX, maxX);
        stopY = Math.min(stopY, maxY);
        int sampleColor = 0;
        int red = 0;
        int green = 0;
        int blue = 0;
        // 将该块的所有点的颜色求平均值
        for (int y = startY; y <= stopY; y++) {
            int p = y * mBitmapWidth;
            for (int x = startX; x <= stopX; x++) {
                int color = pxs[p + x];
                red += Color.red(color);
                green += Color.green(color);
                blue += Color.blue(color);
            }
        }
        int sampleCount = (stopY - startY + 1) * (stopX - startX + 1);
        red /= sampleCount;
        green /= sampleCount;
        blue /= sampleCount;
        //java.util.Random random=new java.util.Random();// 定义随机类
        //sampleColor = Color.rgb(random.nextInt(255),random.nextInt(255),random.nextInt(255));
        sampleColor = Color.rgb(red, green, blue);
        return sampleColor;
    }

    private void touchStart(float x, float y) {
        Log.d("测试","start x:"+x+"       y"+y);
        mLastX = x;
        mLastY = y;
    }

    /**
     * 点数是屏幕的以本视图左上角为原点，属于屏幕的点数
     * @param x
     * @param y
     */
    private void touchMove(float x, float y) {

        if (Math.abs(x - mLastX) >= VALID_DISTANCE
                || Math.abs(y - mLastY) >= VALID_DISTANCE) {
            Point startPoint = new Point((int)mLastX, (int)mLastY);
            Point endPoint = new Point((int)x, (int)y);
            mosaic(startPoint, endPoint);
        }
        mLastX = x;
        mLastY = y;
    }

    /**
     * 根据移动手势绘制马赛克 思路是
     * 手指移动是按照像素点移动， 移动过的点，判断点在哪个马赛克块，然后确定颜色，把该块内的所有像素赋值
     * 缓存像素绘制给我们的bitmap,然后调用canves
     * @param startPoint
     * @param endPoint
     */
    private void mosaic(Point startPoint, Point endPoint) {

        float startTouchX = startPoint.x;
        float startTouchY = startPoint.y;

        float endTouchX = endPoint.x;
        float endTouchY = endPoint.y;

        float minX = Math.min(startTouchX, endTouchX);
        float maxX = Math.max(startTouchX, endTouchX);

        int startIndexX = (int) minX / BLOCK_SIZE;
        int endIndexX = (int) maxX / BLOCK_SIZE;

        float minY = Math.min(startTouchY, endTouchY);
        float maxY = Math.max(startTouchY, endTouchY);

        int startIndexY = (int) minY / BLOCK_SIZE;
        int endIndexY = (int) maxY / BLOCK_SIZE;// 确定矩形的判断范围
        for (int row = startIndexY; row <= endIndexY; row++) {
            for (int colunm = startIndexX; colunm <= endIndexX; colunm++) {
                Rect rect = new Rect(colunm * BLOCK_SIZE, row * BLOCK_SIZE,
                        (colunm + 1) * BLOCK_SIZE, (row + 1) * BLOCK_SIZE);
                Boolean intersectRect = GeometryHelper.IsLineIntersectRect(startPoint, endPoint, rect);
                if (intersectRect) {// 线段与直线相交
                    int rowMax = Math
                            .min((row + 1) * BLOCK_SIZE, mBitmapHeight);
                    int colunmMax = Math.min((colunm + 1) * BLOCK_SIZE,
                            mBitmapWidth);
                    for (int i = row * BLOCK_SIZE; i < rowMax; i++) {
                        for (int j = colunm * BLOCK_SIZE; j < colunmMax; j++) {
                            mTempBitmapPixs[i * mBitmapWidth + j] = mSampleColors[row
                                    * mColumnCount + colunm];
                        }
                    }
                }
            }
        }
        mBitmap.setPixels(mTempBitmapPixs, 0, mBitmapWidth, 0, 0, mBitmapWidth,
                mBitmapHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(Math.abs(x), Math.abs(y));
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(Math.abs(x), Math.abs(y));
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                invalidate();
                break;
        }
        return true;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("测试","view的宽"+w+"高"+h);
       // mTotalWidth = w;
        //mTotalHeight = h;
    }
}
