package com.paicheya.hammer.paddemo;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.paicheya.pimagepicker.PImagePicker;
import com.paicheya.pimagepicker.PImagePickerConfig;
import com.paicheya.pimagepicker.interfaces.ImgPickerCallback;
import com.paicheya.pimagepicker.util.MyLog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
    }

    private void initUI(){
        Button btn = (Button)this.findViewById(R.id.btn_test_camera);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testCamera();
            }
        });

        ImgPickerCallback resultCallback = new ImgPickerCallback() {
            @Override
            public void onSuccess(Uri uri) {
                MyLog.log("成功获得uri："+uri.getPath());
            }

            @Override
            public void onFail(String msg) {

            }
        };
        PImagePickerConfig pImagePickerConfig = new PImagePickerConfig.Builder()
                .setPressQuality(50)
                .setResultCallback(resultCallback)
                .builder();
        PImagePicker.init(pImagePickerConfig);
    }

    private void testCamera(){
        PImagePicker.getDefault().pickFromCamera(this);
    }

}
