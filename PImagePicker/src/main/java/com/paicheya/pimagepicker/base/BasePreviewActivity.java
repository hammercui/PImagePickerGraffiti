package com.paicheya.pimagepicker.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.paicheya.pimagepicker.PImagePicker;
import com.paicheya.pimagepicker.bean.ImageItem;

import java.util.ArrayList;

/**
 * ============================
 * Author：  hammercui
 * Version： 1.0
 * Time:     17/2/25
 * Description:
 * Fix History:
 * =============================
 */

public abstract class BasePreviewActivity extends BaseMultipleActivity {
    protected ArrayList<ImageItem> mImageItems;      //跳转进ImagePreviewFragment的图片文件夹
    protected Boolean singlePreview;  //是否开启仅仅预览模式


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singlePreview = getIntent().getBooleanExtra(PImagePicker.EXTRA_PREVIEW_MODE_SINGLE,false);
        if(singlePreview){//仅预览模式
            mImageItems = mSelectedImages;
            mCurrentPosition = 0;
        }
        else{
            mImageItems =  getIntent().getParcelableArrayListExtra(PImagePicker.EXTRA_IMAGE_ITEMS);
        }

    }

    /** 单击时，隐藏头和尾 */
    public abstract void onImageSingleTap();
}
