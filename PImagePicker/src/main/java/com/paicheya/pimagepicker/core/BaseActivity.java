package com.paicheya.pimagepicker.core;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.paicheya.pimagepicker.PImagePicker;
import com.paicheya.pimagepicker.PImagePickerConfig;
import com.paicheya.pimagepicker.adapter.ImageGridAdapter;
import com.paicheya.pimagepicker.bean.ImageFolder;
import com.paicheya.pimagepicker.bean.ImageItem;
import com.paicheya.pimagepicker.bean.OutputUri;
import com.paicheya.pimagepicker.listener.OnImageSelectedListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片选择器，基础类
 * Created by cly on 17/2/17.
 */

public class BaseActivity extends AppCompatActivity {
    public PImagePickerConfig pImagePickerConfig;//配置属性


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiveProcessIntent(this.getIntent());
    }

    /**
     * 获得传递过来的属性值
     */
    private void receiveProcessIntent(@NonNull Intent intent){
        pImagePickerConfig = intent.getParcelableExtra(PImagePicker.EXTRA_PIMAGE_PICKER_CONFIG);
    }


    protected void setResultUri(ImageItem imageItem) {
        //多选
        if(pImagePickerConfig.isMultiple()){
            ArrayList<ImageItem> list = new ArrayList<>();
            list.add(imageItem);
            setResultUris(list);
        }
        else{ //单选
            setResult(RESULT_OK, new Intent().putExtra(PImagePicker.OUT_PUT_URI, imageItem));
            this.finish();
        }
    }




    protected void setResultUris(ArrayList<ImageItem> list){
        setResult(RESULT_OK,new Intent().putParcelableArrayListExtra(PImagePicker.OUT_PUT_URIS,list));
        this.finish();
    }

    protected void setResultError(String  msg) {
        setResult(PImagePicker.REQUEST_PICKER_IMAGE_ERROR,
                new Intent().putExtra(PImagePicker.OUT_PUT_ERROR_MSG, msg));
        this.finish();
    }



    public boolean checkPermission(@NonNull String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * toast提示
     * @param toastText
     */
    public void showToast(String toastText) {
        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
    }
}
