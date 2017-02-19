package com.paicheya.pimagepicker.view.edit;

import android.animation.ObjectAnimator;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.paicheya.pimagepicker.R;

/**
 * 图片编辑页 可涂鸦 可裁切
 * Created by cly on 17/2/13.
 */

public class EditActivity extends AppCompatActivity{

    public static final String  SOURCE_PATH = "source_path";
    private Uri  sourceUri;

    private FlexboxLayout maskBottomLayout;
    private FlexboxLayout tagBottomLayout;
    private TextView tvTitle;

    //底部菜单层级
    private int bottomTiger = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sourceUri = this.getIntent().getParcelableExtra(SOURCE_PATH);

        this.setContentView(R.layout.pip_activity_edit);

        initView();
    }

    /**
     * 初始化view
     */
    private void initView(){
        tvTitle = (TextView)this.findViewById(R.id.tv_title);
        //获得涂鸦view

        //初始化底部菜单view
        initBottomLayout();

    }

    private void initBottomLayout(){
        maskBottomLayout = (FlexboxLayout) this.findViewById(R.id.layout_bottomMask);
        maskBottomLayout.setTranslationY(500);
        tagBottomLayout = (FlexboxLayout)this.findViewById(R.id.layout_bottomTag);
        tagBottomLayout.setTranslationY(500);

        //edit的功能键
       findViewById(R.id.tv_editCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doNothingFinish();
            }
        });
       findViewById(R.id.btn_editOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImgFile();
                doNothingFinish();
            }
        });
        findViewById(R.id.tv_editDoTag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomEnterAnimator(tagBottomLayout);
                tvTitle.setText("标记中");
            }
        });
        findViewById(R.id.tv_editDoMask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomEnterAnimator(maskBottomLayout);
                tvTitle.setText("涂抹中");
            }
        });

        //tag的功能键
        tagBottomLayout.findViewById(R.id.tv_tagCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomExitAnimator(tagBottomLayout);
            }
        });
        tagBottomLayout.findViewById(R.id.tv_tagDo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        tagBottomLayout.findViewById(R.id.btn_tagOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomExitAnimator(tagBottomLayout);

                //保存修改过的bitmap
            }
        });

        //mask的功能键
        maskBottomLayout.findViewById(R.id.tv_maskCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomExitAnimator(maskBottomLayout);
            }
        });
        TextView maskBack = (TextView)maskBottomLayout.findViewById(R.id.tv_maskBack);
        maskBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        TextView maskForward = (TextView) maskBottomLayout.findViewById(R.id.tv_maskForward);
        maskForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        maskBottomLayout.findViewById(R.id.btn_maskOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomExitAnimator(maskBottomLayout);
                //保存修改过的bitmap
            }
        });

    }

    private void doNothingFinish(){
        //PImagePicker.getDefault().getConfig().getImageCallback().onSuccess(sourceUri);
        finish();
    }

    //保存图片文件
    private void saveImgFile(){

    }




    /**
     * 底部进入动画
     * @param layout
     */
    private void  bottomEnterAnimator(FlexboxLayout layout){
        ObjectAnimator animator = ObjectAnimator.ofFloat(layout,"translationY",500,0);
        animator.setDuration(800);
        animator.start();
        bottomTiger = 1;
    }

    /**
     * 底部进入退出动画
     * @param layout
     */
    private void bottomExitAnimator(FlexboxLayout layout){
        ObjectAnimator animator = ObjectAnimator.ofFloat(layout,"translationY",0,500);
        animator.setDuration(800);
        animator.start();
        bottomTiger = 0;
        tvTitle.setText("编辑");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
