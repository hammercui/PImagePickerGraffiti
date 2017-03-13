package com.hammer.anlib.pandroidutils;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;



import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * ============================
 * Author：  hammercui
 * Version： 1.0
 * Time:     17/3/2
 * Description: bitmap工具类
 * Fix History:
 * =============================
 */
public class PIPBitmapUtil {

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
     * @param pressQuality 压缩比例 0~100
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
     * camera获得的data数据转换为bitmap
     * @param data
     * @return
     */
    public static Bitmap verticalCameraByteToBitmap(byte[] data ){
        Bitmap croppedImage;
        //获得图片大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        //MyLog.log("预处理option的width:"+options.outWidth+",height"+options.outHeight);
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

    /**
     * 获得uri的真实存储地址
     * @param context
     * @param uri
     * @return
     */
    public static String getRealFilePath(final Context context, final Uri uri ) {
       // final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * 通知系统图库更新
     * @param context
     * @param uri
     */
    public static void updateSystemGallery(Context context,Uri uri){
        // 通知系统图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri ));
    }

}
