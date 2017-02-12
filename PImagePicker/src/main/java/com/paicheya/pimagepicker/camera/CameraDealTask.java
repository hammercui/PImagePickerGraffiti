package com.paicheya.pimagepicker.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.AsyncTask;

import com.paicheya.pimagepicker.PImagePicker;
import com.paicheya.pimagepicker.PImagePickerConfig;
import com.paicheya.pimagepicker.interfaces.CameraDealCallback;
import com.paicheya.pimagepicker.util.BitmapUtil;
import com.paicheya.pimagepicker.util.MyLog;

import java.io.IOException;

/**
 * 相机获得bitmap并处理的异步任务
 * Created by cly on 17/2/10.
 */

public  class CameraDealTask extends AsyncTask<byte[],Integer,Bitmap> {

    private CameraDealCallback cameraDealCallback;
    private CameraActivity.Direction currDirect = CameraActivity.Direction.Vertical;
    public CameraDealTask(CameraDealCallback cameraDealCallback,CameraActivity.Direction currDirect){
        super();
        this.cameraDealCallback = cameraDealCallback;
        this.currDirect = currDirect;
    }

    /**
     * 子线程执行操作
     * @param params
     * @return
     */
    @Override
    protected Bitmap doInBackground(byte[]... params) {

        // 相机获得图像
        MyLog.log("拍照获得图片大小："+(float)params[0].length/1024f+"kb");
        Bitmap bitmap = BitmapFactory.decodeByteArray(params[0], 0, params[0].length);
        MyLog.log("拍照获得图片分辨率：width_"+bitmap.getWidth()+"  height_"+bitmap.getHeight() );
        //首先存储一次，并获得ExifInterface
        PImagePickerConfig config = PImagePicker.getDefault().getConfig();
        int degree = 0;
        
        if(BitmapUtil.saveBitmapFile(bitmap,config.getImagePath(),50)){
            degree = BitmapUtil.getDegree(config.getImagePath());
        }
        MyLog.log("图片的degree:"+degree);


        //竖屏进行裁切处理
        if(currDirect == CameraActivity.Direction.Vertical){
            bitmap = BitmapUtil.cropWithAspect(bitmap, PImagePicker.getDefault().getConfig().getAspectRatio());
        }
        //横屏进行旋转处理
        else if(currDirect == CameraActivity.Direction.Horizontal){
            bitmap = BitmapUtil.cropWithRotation(bitmap,-90);
        }
        MyLog.log("处理后图片分辨率：width_"+bitmap.getWidth()+"  height_"+bitmap.getHeight());

        return bitmap;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        MyLog.log("CameraDealTask被主动取消");
    }



    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        cameraDealCallback.onSuccess(bitmap);
    }
}
