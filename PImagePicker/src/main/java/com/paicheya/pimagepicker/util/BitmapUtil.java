package com.paicheya.pimagepicker.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import com.paicheya.pimagepicker.PImagePicker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 像素工具
 * Created by cly on 17/2/9.
 */

public class BitmapUtil {

    /**
     * 按宽高比裁切图片
     * @param bitmap
     * @param aspectRatio
     * @return
     */
    public static Bitmap cropWithAspect(Bitmap bitmap,float aspectRatio)
    {
        if (bitmap == null)
        {
            return null;
        }

        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();


        int nw, nh, retX, retY;
        Bitmap bmp;
        if (w > h)
        {

            return bitmap;
//            nw = h;
//            nh = (int)(nw /aspectRatio) ;
//            retX = (w - nw)/2;
//            retY = 0;
//            bmp = Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null,
//                    false);
        }
        else {
            nw = w;
            nh = (int)(w / aspectRatio);
            retX = 0;
            retY = (h - nh) / 2;
            bmp = Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null,
                    false);
        }

        // 清理
        if (bitmap != null && !bitmap.equals(bmp) && !bitmap.isRecycled())
        {
            bitmap.recycle();
            bitmap = null;
        }
        return bmp;// Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null,
        // false);
    }

    /**
     * 根据旋转角度裁切
     * @param bitmap
     * @param rotation
     * @return
     */
    public static Bitmap cropWithRotation(Bitmap bitmap,int rotation){

        if(bitmap == null)
            return bitmap;
        Matrix matrix = new Matrix();
        matrix.reset();

        matrix.postRotate(rotation);
        Bitmap bmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        if (bitmap != null && !bitmap.equals(bmp) && !bitmap.isRecycled())
        {
            bitmap.recycle();
            bitmap = null;
        }
        return bmp;
    }

    /**
     * Bitmap保存为图片文件
     * @param bitmap
     * @param imgPath 图片路径 默认jpg文件
     * @param pressQuality 压缩比例
     * @return
     */
    public static Boolean saveBitmapFile(Bitmap bitmap,String imgPath,int pressQuality){
        //保存图片的路径
        File file = new File(imgPath);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,
                    pressQuality, outputStream);
            outputStream.flush();
            outputStream.close();
            return  true;

        } catch (Exception e) {
            e.printStackTrace();
            MyLog.log("Error","e:"+e.toString());
            return false;
        }

    }

    /**
     * 获得图片的ExifInterface提供的旋转角度
     * @param imgPath
     * @return
     */
    public static int getDegree(String imgPath){
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(imgPath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    degree = 0;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            MyLog.log("获得ExifInterfaces失败，e:"+e.toString());
        }
        return degree;
    }
}
