package com.paicheya.pimagepicker.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.paicheya.pimagepicker.PImagePicker;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.RecursiveTask;

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
     * Bitmap保存为图片文件,并删除bitmap
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
            //销毁bitmap
            if(!bitmap.isRecycled())
                bitmap.recycle();
            return  true;

        } catch (Exception e) {
            if(!bitmap.isRecycled())
                bitmap.recycle();
            e.printStackTrace();
            MyLog.log("Error","e:"+e.toString());
            return false;
        }
    }

    /**
     * data数据转换为bitmap
     * @param data
     * @return
     */
    public static Bitmap byteToBitmap(byte[] data ){
        Bitmap croppedImage;
        //获得图片大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        MyLog.log("预处理option的width:"+options.outWidth+",height"+options.outHeight);
        int width = options.outWidth;
        int height = options.outHeight;
        options.inJustDecodeBounds = false;
        Rect r = new Rect(0, 0, width, height);

        try {
            croppedImage = decodeRegionCrop(data, r,width,height);
        } catch (Exception e) {
            return null;
        }
        return croppedImage;
    }

    private static Bitmap decodeRegionCrop(byte[] data, Rect rect,int width,int height) {
        InputStream is = null;
        System.gc();
        Bitmap croppedImage = null;
        try {
            is = new ByteArrayInputStream(data);
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);

            try {
                croppedImage = decoder.decodeRegion(rect, new BitmapFactory.Options());
            } catch (IllegalArgumentException e) {
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeStream(is);
        }
        Matrix m = new Matrix();
        m.setRotate(90, width / 2, height / 2);
        Bitmap rotatedImage = Bitmap.createBitmap(croppedImage, 0, 0, width, height, m, true);
        if (rotatedImage != croppedImage)
            croppedImage.recycle();
        return rotatedImage;
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
            MyLog.log("获得ExifInterfaces的orientation:"+orientation);
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
        MyLog.log("获得ExifInterfaces的degree:"+degree);
        return degree;
    }

//
//    public static boolean samsungPhone = false;
//    public static void setSamsungPhone(boolean sure){
//        samsungPhone = sure;
//    }
//
//    private static  int isSamsungFromVertical = -1;
//    public static void checkSamsungFromVertical(int width,int height){
//        if(isSamsungFromVertical != -1)
//            return;
//        if(width>height){
//            isSamsungFromVertical = 1;
//        }
//        else{
//            isSamsungFromVertical = 0;
//        }
//    }
//
//    private static String[] brandArray= {"samsung","Meizu"};
//    /**
//     * 检测是否是Samsung类机型
//     * @return
//     */
//    public static boolean isSamsungPhone(){
//        if(samsungPhone){
//            return true;
//        }
//        //已使用竖屏判断出结果
//        if(isSamsungFromVertical == 1){
//            samsungPhone = true;
//            return true;
//        }
//        else if (isSamsungFromVertical == 0){
//            samsungPhone = false;
//            return false;
//        }
//
//        Log.i("设备信息","Build.BRAND:"+Build.BRAND);
//        Log.i("设备信息","Build.MODEL:"+Build.MODEL);
//        Log.i("设备信息","Build.PRODUCT:"+Build.PRODUCT);
//        for(int i=0;i<brandArray.length;i++){
//            if(brandArray[i].equals(Build.BOARD)){
//                samsungPhone = true;
//                return true;
//            }
//        }
//
//        return false;
//    }
}
