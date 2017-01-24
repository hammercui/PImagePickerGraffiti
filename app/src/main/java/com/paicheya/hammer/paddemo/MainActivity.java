package com.paicheya.hammer.paddemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.paicheya.hammer.graffitipicture.CropStartActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
    }

    private void initUI(){
        Button btn = (Button)this.findViewById(R.id.demo1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGraffitipicture();
            }
        });

    }

    private void startGraffitipicture(){

        startActivity(new Intent(this, CropStartActivity.class));
    }

}
