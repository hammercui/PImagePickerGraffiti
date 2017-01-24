package com.paicheya.hammer.graffitipicture.graffitlib;

/**
 * Created by cly on 16/11/15.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;

/**
 * 涂鸦底色
 */
public  class GraffitiColor {
    //public boolean isMosiac = false;
    public enum Type {
        COLOR, // 颜色值
        BITMAP, // 图片
        MOSAIC //马赛克
    }

    public int mColor;
    private Bitmap mBitmap;
    private boolean isMosiac = false;
    private Type mType;
    private Shader.TileMode mTileX = Shader.TileMode.MIRROR;
    private Shader.TileMode mTileY = Shader.TileMode.MIRROR;  // 镜像

    public GraffitiColor(int color) {
        mType = Type.COLOR;
        mColor = color;
        isMosiac = false;
    }

    public GraffitiColor(Bitmap bitmap) {
        mType = Type.BITMAP;
        mBitmap = bitmap;
        isMosiac = false;
    }

    public GraffitiColor(boolean isMosiac) {
        mType = Type.MOSAIC;
        this.isMosiac = isMosiac;
    }


//    void initColor(Paint paint, Matrix matrix) {
//        if (mType == Type.COLOR ) {
//            paint.setColor(mColor);
//        }
//        else if (mType == Type.BITMAP) {
//            BitmapShader shader = new BitmapShader(mBitmap, mTileX, mTileY);
//            shader.setLocalMatrix(matrix);
//            paint.setShader(shader);
//        }
//        else{
//            paint.setColor(Color.BLUE);
//        }
//    }

    public void setColor(int color) {
        mType = Type.COLOR;
        mColor = color;
        isMosiac = false;
    }

    public void setColor(Bitmap bitmap) {
        mType = Type.BITMAP;
        mBitmap = bitmap;
        isMosiac = false;
    }
    public void setColor(boolean isMosiac)
    {
        mType = Type.MOSAIC;
        this.isMosiac = isMosiac;
    }

    public void setColor(Bitmap bitmap, Shader.TileMode tileX, Shader.TileMode tileY) {
        mType = Type.BITMAP;
        mBitmap = bitmap;
        mTileX = tileX;
        mTileY = tileY;
    }

    public int getColor() {
        return mColor;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public boolean isMosiac(){return  isMosiac;}

    public Type getType() {
        return mType;
    }

    public GraffitiColor copy() {
        GraffitiColor color = null;
        if (mType == Type.COLOR) {
            color = new GraffitiColor(mColor);
        } else  if (mType == Type.BITMAP){
            color = new GraffitiColor(mBitmap);
        }else  if (mType == Type.MOSAIC){
            color = new GraffitiColor(true);
        }

        color.mTileX = mTileX;
        color.mTileY = mTileY;
        return color;
    }
}