package com.paicheya.hammer.graffitipicture.graffitlib;

import android.graphics.Matrix;
import android.graphics.Path;

/**
 *
 * Created by cly on 16/11/15.
 */
public   class GraffitiPath {
//    GraffitiView.Pen mPen; // 画笔类型
//    GraffitiView.Shape mShape; // 画笔形状
    float mStrokeWidth; // 大小
    GraffitiColor mGraffitiColor; // 颜色
    Path mPath; // 画笔的路径
    float mSx, mSy; // 映射后的起始坐标，（手指点击）
    float mDx, mDy; // 映射后的终止坐标，（手指抬起）
    Matrix mMatrix; //　仿制图片的偏移矩阵

    static GraffitiPath generator(float width, GraffitiColor graffitiColor,
                                float sx, float sy, float dx, float dy, Matrix matrix) {
        GraffitiPath path = new GraffitiPath();
        path.mStrokeWidth = width;
        path.mGraffitiColor = graffitiColor;
        path.mSx = sx;
        path.mSy = sy;
        path.mDx = dx;
        path.mDy = dy;
        path.mMatrix = matrix;
        return path;
    }

    static GraffitiPath generator(float width, GraffitiColor graffitiColor, Path p, Matrix matrix) {
        GraffitiPath path = new GraffitiPath();
        path.mStrokeWidth = width;
        path.mGraffitiColor = graffitiColor;
        path.mPath = p;
        path.mMatrix = matrix;
        return path;
    }
}