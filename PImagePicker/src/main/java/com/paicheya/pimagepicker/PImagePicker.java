package com.paicheya.pimagepicker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.paicheya.pimagepicker.view.camera.CameraActivity;
import com.paicheya.pimagepicker.core.PermissionUtil;
import com.paicheya.pimagepicker.view.gallery.GalleryActivity;

/**
 * Created by cly on 17/2/9.
 */

public class PImagePicker extends PermissionUtil {
    public  static final int REQUEST_PICKER_IMAGE = 10086;
    public  static final int REQUEST_PICKER_IMAGE_ERROR = 10010;
    public  static final String EXTRA_PREFIX = "com.hammer.pimagepicker";
    public  static final String CONFIG_OUTPUT_PATH = EXTRA_PREFIX +"config_output_path";
    public  static final String CONFIG_OUTPUT_DIR = EXTRA_PREFIX +"config_output_dir";
    public  static final String CONFIG_OUTPUT_IMAGE_NAME = EXTRA_PREFIX +"config_output_image_name";
    public  static final String CONFIG_ASPECT_RATIO = EXTRA_PREFIX +"config_aspect_ratio";
    public  static final String CONFIG_PRESS_QUALITY = EXTRA_PREFIX +"config_press_quality";
    public  static final String CONFIG_CAMERA_ROLL = EXTRA_PREFIX +"config_camera_roll";
    public  static final String OUT_PUT_URI = EXTRA_PREFIX +"out_put_uri";
    public  static final String OUT_PUT_IMAGE_WIDTH = EXTRA_PREFIX +"out_put_image_width";
    public  static final String OUT_PUT_IMAGE_HEIGHT = EXTRA_PREFIX +"out_put_image_height";
    public  static final String OUT_PUT_ERROR_MSG = EXTRA_PREFIX+"out_put_error_msg";
    private PImagePickerConfig config;

    /**
     * 获得配置信息
     * @return
     */
    public PImagePickerConfig getConfig() {
        return config;
    }
//    static PImagePicker instance;
//    public static PImagePicker getDefault(){
//        if(instance == null){
//           synchronized (PImagePicker.class){
//               if(instance == null){
//                   instance = new PImagePicker();
//               }
//           }
//        }
//        return instance;
//    }


    public PImagePicker(PImagePickerConfig config){
        this.config = config;
    }


    /**
     * 初始化配置
     * @param config
     */
    public static PImagePicker  create(PImagePickerConfig config){
        return new PImagePicker(config);
        //PImagePicker.getDefault().config = config;
    }


    /**
     * 从相机中获得图片
     */
    public  void startCameraActivity(@NonNull Activity activity ){
        if (!checkPermissionReadSDkard(activity))
            return;
        if (!checkPermissionCamera(activity))
            return;

        //方法2：调用自定义相机
        Intent intent = new Intent(activity, CameraActivity.class);
        intent = processConfig(intent);
        activity.startActivityForResult(intent,REQUEST_PICKER_IMAGE);
    }

    /**
     * 从相册获得图片
     */
    public void startGalleryActivity(@NonNull Activity activity){
        if (!checkPermissionReadSDkard(activity))
            return;
        //方法2：打开相册
        Intent intent = new Intent(activity, GalleryActivity.class);
        intent = processConfig(intent);
        activity.startActivityForResult(intent,REQUEST_PICKER_IMAGE);
    }

    /**
     * 给intent传递属性
     */
    public Intent processConfig(@NonNull Intent intent){
        intent.putExtra(CONFIG_OUTPUT_PATH,config.getImagePath());
        intent.putExtra(CONFIG_PRESS_QUALITY,config.getPressQuality());
        intent.putExtra(CONFIG_ASPECT_RATIO,config.getAspectRatio());
        intent.putExtra(CONFIG_CAMERA_ROLL,config.isCameraRoll());
        //intent.putExtra(CONFIG_OUTPUT_DIR,config.getDirPath());
        intent.putExtra(CONFIG_OUTPUT_IMAGE_NAME,config.getImageName());
        return intent;
    }

    public static Uri getOutput(@NonNull Intent intent) {
        return intent.getParcelableExtra(OUT_PUT_URI);
    }

    /**
     * Retrieve the width of the cropped image
     *
     * @param intent crop result intent
     */
    public static int getOutputImageWidth(@NonNull Intent intent) {
        return intent.getIntExtra(OUT_PUT_IMAGE_WIDTH, -1);
    }

    /**
     * Retrieve the height of the cropped image
     *
     * @param intent crop result intent
     */
    public static int getOutputImageHeight(@NonNull Intent intent) {
        return intent.getIntExtra(OUT_PUT_IMAGE_HEIGHT, -1);
    }

    /**
     * Retrieve the height of the cropped image
     *
     * @param intent crop result intent
     */
    public static String  getOutputError(@NonNull Intent intent) {
        return intent.getStringExtra(OUT_PUT_ERROR_MSG);
    }
}
