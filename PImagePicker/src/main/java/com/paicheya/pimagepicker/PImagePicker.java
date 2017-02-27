package com.paicheya.pimagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.paicheya.pimagepicker.bean.ImageItem;
import com.paicheya.pimagepicker.view.camera.CameraActivity;
import com.paicheya.pimagepicker.core.PermissionUtil;
import com.paicheya.pimagepicker.view.gallery.GallerySingleActivity;
import com.paicheya.pimagepicker.view.gallery.GalleryMultipleActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by cly on 17/2/9.
 */

public class PImagePicker extends PermissionUtil {
    public  static final int REQUEST_PICKER_IMAGE_SINGLE = 10086;//单选图片
    public  static final int REQUEST_PICKER_IMAGE_MULTIPLE = 10096; //多选照片
    public  static final int REQUEST_PICKER_IMAGE_ERROR = 10010;
    public static  final int REQUEST_CODE_PREVIEW = 10097;//申请预览

    public static final int RESULT_CODE_BACK = 200001;
    public static final int RESULT_CODE_ITEMS   = 200002;

    private   static final String EXTRA_PREFIX = "com.hammer.pimagepicker";
    public  static final  String EXTRA_PIMAGE_PICKER_CONFIG = EXTRA_PREFIX +"config"; //配置属性
    public  static final  String EXTRA_SELECT_IMAGES = EXTRA_PREFIX +"select_images"; //传递已选的item
    public static final   String EXTRA_SELECTED_IMAGE_POSITION = "selected_image_position";
    public static final   String EXTRA_IMAGE_ITEMS = "extra_image_items";
    public static final   String EXTRA_PREVIEW_MODE_SINGLE = "extra_preview_mode_single";//仅预览模式
//    public  static final String CONFIG_OUTPUT_PATH = EXTRA_PREFIX +"config_output_path";
//    public  static final String CONFIG_OUTPUT_DIR = EXTRA_PREFIX +"config_output_dir";
//    public  static final String CONFIG_OUTPUT_IMAGE_NAME = EXTRA_PREFIX +"config_output_image_name";
//    public  static final String CONFIG_ASPECT_RATIO = EXTRA_PREFIX +"config_aspect_ratio";
//    public  static final String CONFIG_PRESS_QUALITY = EXTRA_PREFIX +"config_press_quality";
//    public  static final String CONFIG_CAMERA_ROLL = EXTRA_PREFIX +"config_camera_roll";
//    public  static final String CONFIG_MULTIPLE = EXTRA_PREFIX +"config_multiple";
//    public  static final String CONFIG_MAX_FILES = EXTRA_PREFIX +"config_max_files";

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
        if(config.isMultiple()){ //多选
            Intent intent = new Intent(activity, GalleryMultipleActivity.class);
            intent = processConfig(intent);
            activity.startActivityForResult(intent,requestCode);
        }
        else{ //多选
            Intent intent = new Intent(activity, GallerySingleActivity.class);
            intent = processConfig(intent);
            activity.startActivityForResult(intent,requestCode);
        }

    }

    /**
     * 给intent传递属性
     */
    public Intent processConfig(@NonNull Intent intent){
        intent.putExtra(EXTRA_PIMAGE_PICKER_CONFIG,config);
//        intent.putExtra(CONFIG_OUTPUT_PATH,config.getImagePath());
//        intent.putExtra(CONFIG_PRESS_QUALITY,config.getPressQuality());
//        intent.putExtra(CONFIG_ASPECT_RATIO,config.getAspectRatio());
//        intent.putExtra(CONFIG_CAMERA_ROLL,config.isCameraRoll());
//        intent.putExtra(CONFIG_OUTPUT_IMAGE_NAME,config.getImageName());
//        intent.putExtra(CONFIG_MULTIPLE,config.isMultiple());
//        intent.putExtra(CONFIG_MAX_FILES,config.getMaxFiles());
        return intent;
    }

    public static ImageItem getOutput(@NonNull final Intent intent) {
        return intent.getParcelableExtra(OUT_PUT_URI);
    }

    /**
     * 获得数组
     * @param intent
     * @return
     */
    public static ArrayList<ImageItem> getOutputArray(@NonNull final  Intent intent){
        return intent.getParcelableArrayListExtra(OUT_PUT_URIS);
    }

    /**
     * 获得单张图
     * @param intent
     * @return
     */
    public static String  getOutputError(@NonNull final  Intent intent) {
        return intent.getStringExtra(OUT_PUT_ERROR_MSG);
    }


    /** 扫描图片 插入相册 */
    public static void galleryAddPic(Context context, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
}
