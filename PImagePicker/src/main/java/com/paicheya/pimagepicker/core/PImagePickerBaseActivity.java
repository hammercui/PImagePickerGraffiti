package com.paicheya.pimagepicker.core;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.paicheya.pimagepicker.PImagePicker;
import com.yalantis.ucrop.UCrop;

import java.util.ArrayList;

/**
 * 图片选择器，基础类
 * Created by cly on 17/2/17.
 */

public class PImagePickerBaseActivity extends AppCompatActivity {

    protected String configOutPutPath;
    protected int configPressQuality ;
    protected float configAspectRatio;
    protected boolean configCameraRoll;
    //protected String configDirPath;
    protected String configImageName;
    protected boolean configMultiple;
    protected int configMaxFiles;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getProcessIntentConfig(this.getIntent());
    }

    /**
     * 获得传递过来的属性值
     */
    private void getProcessIntentConfig(@NonNull Intent intent){
        configOutPutPath = intent.getStringExtra(PImagePicker.CONFIG_OUTPUT_PATH);
        //configDirPath = intent.getStringExtra(PImagePicker.CONFIG_OUTPUT_DIR);
        configImageName = intent.getStringExtra(PImagePicker.CONFIG_OUTPUT_IMAGE_NAME);
        configPressQuality = intent.getIntExtra(PImagePicker.CONFIG_PRESS_QUALITY,70);
        configAspectRatio = intent.getFloatExtra(PImagePicker.CONFIG_ASPECT_RATIO,4f/3f);
        configCameraRoll = intent.getBooleanExtra(PImagePicker.CONFIG_CAMERA_ROLL,true);
        configMultiple = intent.getBooleanExtra(PImagePicker.CONFIG_MULTIPLE,false);
        configMaxFiles = intent.getIntExtra(PImagePicker.CONFIG_MAX_FILES,5);
    }




    protected void setResultUri(OutputUri uri) {
        //多选
        if(configMultiple){
            ArrayList<OutputUri> list = new ArrayList<>();
            list.add(uri);
            setResultUris(list);
        }
        else{ //单选
            setResult(RESULT_OK, new Intent()
                    .putExtra(PImagePicker.OUT_PUT_URI, uri)
//                    .putExtra(PImagePicker.OUT_PUT_IMAGE_WIDTH, imageWidth)
//                    .putExtra(PImagePicker.OUT_PUT_IMAGE_HEIGHT, imageHeight)
//                    .putExtra(PImagePicker.OUT_PUT_IMAGE_SIZE,size)
            );
            this.finish();
        }
    }

    protected void setResultUris(ArrayList<OutputUri> list){
        setResult(RESULT_OK,new Intent().putParcelableArrayListExtra(PImagePicker.OUT_PUT_URIS,list));
        this.finish();
    }

    protected void setResultError(String  msg) {
        setResult(PImagePicker.REQUEST_PICKER_IMAGE_ERROR,
                new Intent().putExtra(PImagePicker.OUT_PUT_ERROR_MSG, msg));
        this.finish();
    }

}
