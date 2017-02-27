package com.paicheya.pimagepicker.view.gallery;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.format.Formatter;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.paicheya.pimagepicker.PImagePicker;
import com.paicheya.pimagepicker.R;
import com.paicheya.pimagepicker.adapter.ImagePageAdapter;
import com.paicheya.pimagepicker.bean.ImageItem;
import com.paicheya.pimagepicker.core.BaseActivity;
import com.paicheya.pimagepicker.core.BaseMultipleActivity;
import com.paicheya.pimagepicker.core.BasePreviewActivity;

/**
 * ============================
 * Author：  hammercui
 * Version： 1.0
 * Time:     17/2/25
 * Description: 图片预览 并可以选择
 * Fix History:
 * =============================
 */

public class GalleryPreviewActivity extends BasePreviewActivity implements View.OnClickListener{
    protected TextView mTitleCount;                  //显示当前图片的位置  例如  5/31
    protected View topBar;
    private View bottomBar;
    protected ViewPagerFixed mViewPager;
    protected ImagePageAdapter mAdapter;
    private   CheckBox mCbCheck;                //是否选中当前图片的CheckBox
    private Button mBtnOk;                         //确认图片的选择

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pip_activity_gallery_preview);
        initView();
    }

    @Override
    public void onImageSingleTap() {
        if (topBar.getVisibility() == View.VISIBLE) {
            topBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_out));
            bottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
            topBar.setVisibility(View.GONE);
            bottomBar.setVisibility(View.GONE);
//            tintManager.setStatusBarTintResource(R.color.transparent);//通知栏所需颜色
//            //给最外层布局加上这个属性表示，Activity全屏显示，且状态栏被隐藏覆盖掉。
//            if (Build.VERSION.SDK_INT >= 16) content.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            topBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_in));
            bottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            topBar.setVisibility(View.VISIBLE);
            bottomBar.setVisibility(View.VISIBLE);
//            tintManager.setStatusBarTintResource(R.color.status_bar);//通知栏所需颜色
//            //Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住
//            if (Build.VERSION.SDK_INT >= 16) content.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    private void initView(){
        topBar  = findViewById(R.id.top_bar);
        topBar.setVisibility(View.VISIBLE);
        bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setVisibility(View.VISIBLE);
        //标题
        mTitleCount = (TextView) findViewById(R.id.tv_desc);
        mTitleCount.setText(getString(R.string.preview_image_count, mCurrentPosition + 1, mImageItems.size()));
        //viewpage
        mViewPager = (ViewPagerFixed) findViewById(R.id.viewpager);
        mAdapter = new ImagePageAdapter(this, mImageItems);
        mAdapter.setPhotoViewClickListener(new ImagePageAdapter.PhotoViewClickListener() {
            @Override
            public void OnPhotoTapListener(View view, float v, float v1) {
                onImageSingleTap();
            }
        });
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentPosition, false);

        //完成
        mBtnOk = (Button)findViewById(R.id.btn_ok);
        mBtnOk.setVisibility(View.VISIBLE);
        mBtnOk.setOnClickListener(this);

        //返回
        findViewById(R.id.btn_back).setOnClickListener(this);
        //选择
        mCbCheck = (CheckBox) findViewById(R.id.cb_check);
        mCbCheck.setVisibility(singlePreview?View.GONE:View.VISIBLE);
        //初始化
        ImageItem item = mImageItems.get(mCurrentPosition);
        boolean isSelected = this.isSelect(item);
        onImageSelected(mCurrentPosition,null,isSelected);
        mTitleCount.setText(getString(R.string.preview_image_count, mCurrentPosition + 1, mImageItems.size()));
        mCbCheck.setChecked(isSelected);
        //滑动ViewPager的时候，根据外界的数据改变当前的选中状态和当前的图片的位置描述文本
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                ImageItem item = mImageItems.get(mCurrentPosition);
                boolean isSelected = isSelect(item);
                mCbCheck.setChecked(isSelected);
                mTitleCount.setText(getString(R.string.preview_image_count, mCurrentPosition + 1, mImageItems.size()));
            }
        });
        //当点击当前选中按钮的时候，需要根据当前的选中状态添加和移除图片
        mCbCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageItem imageItem = mImageItems.get(mCurrentPosition);
                int selectLimit = pImagePickerConfig.getMaxFiles();
                if (mCbCheck.isChecked() && mSelectedImages.size() >= selectLimit) {
                    Toast.makeText(GalleryPreviewActivity.this, GalleryPreviewActivity.this.getString(R.string.select_limit, selectLimit), Toast.LENGTH_SHORT).show();
                    mCbCheck.setChecked(false);
                } else {
                    addSelectedImageItem(mCurrentPosition, imageItem, mCbCheck.isChecked());
                }
            }
        });

    }


    public void onImageSelected(int position, ImageItem item, boolean isAdd) {
        if (getSelectImageCount() > 0) {
            mBtnOk.setText(getString(R.string.select_complete, getSelectImageCount(), pImagePickerConfig.getMaxFiles()));
            mBtnOk.setEnabled(true);
        } else {
            mBtnOk.setText(getString(R.string.complete));
            mBtnOk.setEnabled(false);
        }
//
//        if (mCbOrigin.isChecked()) {
//            long size = 0;
//            for (ImageItem imageItem : selectedImages)
//                size += imageItem.size;
//            String fileSize = Formatter.formatFileSize(this, size);
//            mCbOrigin.setText(getString(R.string.origin_size, fileSize));
//        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ok) {
            popOk();
        } else if (id == R.id.btn_back) {
            popBack();
        }
    }


    @Override
    public void onBackPressed() {
        popBack();
        super.onBackPressed();
    }

    /**
     * 返回
     */
    private void  popBack(){
        if(singlePreview){
            finish();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(PImagePicker.EXTRA_SELECT_IMAGES, mSelectedImages);
        intent.putExtra(PImagePicker.EXTRA_SELECTED_IMAGE_POSITION, mCurrentPosition);
        setResult(PImagePicker.RESULT_CODE_BACK, intent);
        finish();
    }

    /**
     * 成功返回
     */
    private void popOk(){
        if(singlePreview){
            finish();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(PImagePicker.EXTRA_SELECT_IMAGES, mSelectedImages);
        //intent.putExtra(PImagePicker.EXTRA_SELECTED_IMAGE_POSITION, mCurrentPosition);
        setResult(PImagePicker.RESULT_CODE_ITEMS, intent);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
