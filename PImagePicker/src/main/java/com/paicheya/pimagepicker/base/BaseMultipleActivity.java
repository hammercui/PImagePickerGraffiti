package com.paicheya.pimagepicker.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.paicheya.pimagepicker.PImagePicker;
import com.paicheya.pimagepicker.bean.ImageFolder;
import com.paicheya.pimagepicker.bean.ImageItem;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================
 * Author：  hammercui
 * Version： 1.0
 * Time:     17/2/25
 * Description:
 * Fix History:
 * =============================
 */

public abstract class BaseMultipleActivity extends BaseActivity {
    public ArrayList<ImageItem> mSelectedImages ;   //选中的图片集合
    private int mCurrentImageFolderPosition = 0;  //当前选中的文件夹位置 0表示所有图片
    public List<ImageFolder> mImageFolders;      //所有的图片文件夹
    public  int mCurrentPosition = 0;              //跳转进ImagePreviewFragment时的序号，第几个图片
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentPosition = getIntent().getIntExtra(PImagePicker.EXTRA_SELECTED_IMAGE_POSITION,0);
        mSelectedImages  = getIntent().getParcelableArrayListExtra(PImagePicker.EXTRA_SELECT_IMAGES);

        if(mSelectedImages == null)
            mSelectedImages = new ArrayList<>();
    }

    public void setCurrentImageFolderPosition(int mCurrentSelectedImageSetPosition) {
        mCurrentImageFolderPosition = mCurrentSelectedImageSetPosition;

    }

    public ArrayList<ImageItem> getCurrentImageFolderItems() {
        return mImageFolders.get(mCurrentImageFolderPosition).images;
    }

    public int getSelectImageCount() {
        if (mSelectedImages == null) {
            return 0;
        }
        return mSelectedImages.size();
    }

    /**
     * 准备传递属性
     * @param intent
     * @return
     */
    public Intent generateExtraIntent(@NonNull Intent intent){
        return intent.putParcelableArrayListExtra(PImagePicker.EXTRA_SELECT_IMAGES,mSelectedImages)
                .putExtra(PImagePicker.EXTRA_PIMAGE_PICKER_CONFIG,pImagePickerConfig)
                .putExtra(PImagePicker.EXTRA_SELECTED_IMAGE_POSITION,mCurrentPosition)
                .putExtra(PImagePicker.EXTRA_IMAGE_ITEMS,getCurrentImageFolderItems())
                ;
    }

    public boolean isSelect(ImageItem item) {
        return mSelectedImages.contains(item);
    }
    /**
     * 图片选中
     * @param position
     * @param item
     * @param isAdd
     */
    public abstract void onImageSelected(int position, ImageItem item, boolean isAdd);
}
