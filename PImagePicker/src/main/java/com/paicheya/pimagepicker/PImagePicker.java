package com.paicheya.pimagepicker;

import android.content.Context;
import android.content.Intent;

import com.paicheya.pimagepicker.camera.CameraActivity;
import com.paicheya.pimagepicker.core.PermissionUtil;

/**
 * Created by cly on 17/2/9.
 */

public class PImagePicker extends PermissionUtil {

    private PImagePickerConfig config;

    /**
     * 获得配置信息
     * @return
     */
    public PImagePickerConfig getConfig() {
        return config;
    }


    static PImagePicker instance;
    public static PImagePicker getDefault(){
        if(instance == null){
           synchronized (PImagePicker.class){
               if(instance == null){
                   instance = new PImagePicker();
               }
           }
        }
        return instance;
    }

    /**
     * 初始化配置
     * @param config
     */
    public static void  init(PImagePickerConfig config){
        PImagePicker.getDefault().config = config;
    }

    /**
     * 从相机中获得图片
     */
    public   void pickFromCamera(Context context){
        if (!checkPermissionReadSDkard(context))
            return;
        if (!checkPermissionCamera(context))
            return;

        //拍照后存储的地址
        String  imagePath = config.getImagePath();
        //方法2：调用自定义相机
        Intent intent = new Intent(context, CameraActivity.class);
        //intent.putExtra(MediaStore.EXTRA_OUTPUT,imagePath);
        context.startActivity(intent);
    }

}
