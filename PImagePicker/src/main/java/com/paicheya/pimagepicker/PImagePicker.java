package com.paicheya.pimagepicker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.paicheya.pimagepicker.core.OutputUri;
import com.paicheya.pimagepicker.view.camera.CameraActivity;
import com.paicheya.pimagepicker.core.PermissionUtil;
import com.paicheya.pimagepicker.view.gallery.GalleryActivity;

import java.util.ArrayList;

/**
 * Created by cly on 17/2/9.
 */

public class PImagePicker extends PermissionUtil {
    public  static final int REQUEST_PICKER_IMAGE_SINGLE = 10086;//单选图片
    public  static final int REQUEST_PICKER_IMAGE_MULTIPLE = 10096; //多选照片
    public  static final int REQUEST_PICKER_IMAGE_ERROR = 10010;

    public  static final String EXTRA_PREFIX = "com.hammer.pimagepicker";
    public  static final String CONFIG_OUTPUT_PATH = EXTRA_PREFIX +"config_output_path";
    public  static final String CONFIG_OUTPUT_DIR = EXTRA_PREFIX +"config_output_dir";
    public  static final String CONFIG_OUTPUT_IMAGE_NAME = EXTRA_PREFIX +"config_output_image_name";
    public  static final String CONFIG_ASPECT_RATIO = EXTRA_PREFIX +"config_aspect_ratio";
    public  static final String CONFIG_PRESS_QUALITY = EXTRA_PREFIX +"config_press_quality";
    public  static final String CONFIG_CAMERA_ROLL = EXTRA_PREFIX +"config_camera_roll";
    public  static final String CONFIG_MULTIPLE = EXTRA_PREFIX +"config_multiple";
    public  static final String CONFIG_MAX_FILES = EXTRA_PREFIX +"config_max_files";

    public  static final String OUT_PUT_URI = EXTRA_PREFIX +"out_put_uri";
    public  static final String OUT_PUT_URIS = EXTRA_PREFIX +"out_put_uris";
//    public  static final String OUT_PUT_IMAGE_WIDTH = EXTRA_PREFIX +"out_put_image_width";
//    public  static final String OUT_PUT_IMAGE_HEIGHT = EXTRA_PREFIX +"out_put_image_height";
//    public  static final String OUT_PUT_IMAGE_SIZE = EXTRA_PREFIX +"out_put_image_size";
    public  static final String OUT_PUT_ERROR_MSG = EXTRA_PREFIX+"out_put_error_msg";

    private PImagePickerConfig config;

    private int requestCode = REQUEST_PICKER_IMAGE_SINGLE;
    /**
     * 获得配置信息
     * @return
     */
    public PImagePickerConfig getConfig() {
        return config;
    }


    public PImagePicker(PImagePickerConfig config){
        this.config = config;
        requestCode = config.isMultiple()? REQUEST_PICKER_IMAGE_MULTIPLE:REQUEST_PICKER_IMAGE_SINGLE;
    }


    /**
     * 初始化配置
     * @param config
     */
    public static PImagePicker  create(PImagePickerConfig config){
        return new PImagePicker(config);
    }


    /**
     * 从相机中获得图片
     */
    public   void startCameraActivity(@NonNull Activity activity){
        if (!checkPermissionReadSDkard(activity))
            return;
        if (!checkPermissionCamera(activity))
            return;

        //方法2：调用自定义相机
        Intent intent = new Intent(activity, CameraActivity.class);
        intent = processConfig(intent);
        activity.startActivityForResult(intent,requestCode);
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
        activity.startActivityForResult(intent,requestCode);
    }

    /**
     * 给intent传递属性
     */
    public Intent processConfig(@NonNull Intent intent){
        intent.putExtra(CONFIG_OUTPUT_PATH,config.getImagePath());
        intent.putExtra(CONFIG_PRESS_QUALITY,config.getPressQuality());
        intent.putExtra(CONFIG_ASPECT_RATIO,config.getAspectRatio());
        intent.putExtra(CONFIG_CAMERA_ROLL,config.isCameraRoll());
        intent.putExtra(CONFIG_OUTPUT_IMAGE_NAME,config.getImageName());
        intent.putExtra(CONFIG_MULTIPLE,config.isMultiple());
        intent.putExtra(CONFIG_MAX_FILES,config.getMaxFiles());
        return intent;
    }

    public static OutputUri getOutput(@NonNull final Intent intent) {
        return intent.getParcelableExtra(OUT_PUT_URI);
    }

    /**
     * 获得数组
     * @param intent
     * @return
     */
    public static ArrayList<OutputUri> getOutputArray(@NonNull final  Intent intent){
        return intent.getParcelableArrayListExtra(OUT_PUT_URIS);
    }

//    /**
//     * Retrieve the width of the cropped image
//     *
//     * @param intent crop result intent
//     */
//    public static int getOutputImageWidth(@NonNull final  Intent intent) {
//        return intent.getIntExtra(OUT_PUT_IMAGE_WIDTH, -1);
//    }
//
//    /**
//     * Retrieve the height of the cropped image
//     *
//     * @param intent crop result intent
//     */
//    public static int getOutputImageHeight(@NonNull final  Intent intent) {
//        return intent.getIntExtra(OUT_PUT_IMAGE_HEIGHT, -1);
//    }
//
//    public static int getOutputSize(@NonNull final Intent intent){
//        return intent.getIntExtra(OUT_PUT_IMAGE_SIZE,-1);
//    }
    /**
     * Retrieve the height of the cropped image
     *
     * @param intent crop result intent
     */
    public static String  getOutputError(@NonNull final  Intent intent) {
        return intent.getStringExtra(OUT_PUT_ERROR_MSG);
    }
}
