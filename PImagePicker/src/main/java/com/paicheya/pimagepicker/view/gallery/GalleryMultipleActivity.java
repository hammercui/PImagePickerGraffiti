package com.paicheya.pimagepicker.view.gallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.paicheya.pimagepicker.PImagePicker;
import com.paicheya.pimagepicker.R;
import com.paicheya.pimagepicker.adapter.ImageFolderAdapter;
import com.paicheya.pimagepicker.adapter.ImageGridAdapter;
import com.paicheya.pimagepicker.bean.ImageFolder;
import com.paicheya.pimagepicker.bean.ImageItem;
import com.paicheya.pimagepicker.core.BaseActivity;
import com.paicheya.pimagepicker.core.BaseMultipleActivity;
import com.paicheya.pimagepicker.listener.OnImageSelectedListener;
import com.paicheya.pimagepicker.util.ImageLoaderUtils;
import com.paicheya.pimagepicker.util.MyLog;

import java.util.List;

/**
 * ============================
 * Author：  cly (崔丽阳) Github:https://github.com/hammercui
 * Version： 1.0
 * Time:     17/2/24
 * Description: 图册多选的入口
 * Fix History:
 * =============================
 */

public class GalleryMultipleActivity extends BaseMultipleActivity implements ImageDataSource.OnImagesLoadedListener,ImageGridAdapter.OnImageItemClickListener,View.OnClickListener {

    public static final int REQUEST_PERMISSION_STORAGE = 0x01;
    public static final int REQUEST_PERMISSION_CAMERA = 0x02;

    private boolean isOrigin = false;  //是否选中原图
    private GridView mGridView;  //图片展示控件
    private View mFooterBar;     //底部栏
    private Button mBtnOk;       //确定按钮
    private Button mBtnDir;      //文件夹切换按钮
    private Button mBtnPre;      //预览按钮
    private ImageFolderAdapter mImageFolderAdapter;    //图片文件夹的适配器
    private FolderPopUpWindow mFolderPopupWindow;  //ImageSet的PopupWindow
   // private List<ImageFolder> mImageFolders;   //所有的图片文件夹
    private ImageGridAdapter mImageGridAdapter;  //图片九宫格展示的适配器
    private ImageDataSource imageDataSource ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pip_activity_gallery_multiple);
        initView();
    }
    private void initView(){
        findViewById(R.id.btn_back).setOnClickListener(this);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);
        mBtnDir = (Button) findViewById(R.id.btn_dir);
        mBtnDir.setOnClickListener(this);
        mBtnPre = (Button) findViewById(R.id.btn_preview);
        mBtnPre.setOnClickListener(this);
        mGridView = (GridView) findViewById(R.id.gridview);
        mFooterBar = findViewById(R.id.footer_bar);
        mImageGridAdapter = new ImageGridAdapter(this, null);
        mImageFolderAdapter = new ImageFolderAdapter(this, null);

        addSelectedImageItem(0, null, false);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                imageDataSource = new ImageDataSource(this, null, this);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new ImageDataSource(this, null, this);
            } else {
                showToast("权限被禁止，无法选择本地图片");
            }
        } else if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //imagePicker.takePicture(this, ImagePicker.REQUEST_CODE_TAKE);
                //加载相机
            } else {
                showToast("权限被禁止，无法打开相机");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清空内存
        ImageLoader.getInstance().clearMemoryCache();
    }


    @Override
    public void onImagesLoaded(List<ImageFolder> imageFolders) {
        this.mImageFolders = imageFolders;
        if (imageFolders.size() == 0)
            mImageGridAdapter.refreshData(null);
        else
            mImageGridAdapter.refreshData(imageFolders.get(0).images);

        mImageGridAdapter.setOnImageItemClickListener(this);
        mGridView.setAdapter(mImageGridAdapter);
        mImageFolderAdapter.refreshData(imageFolders);

        updateUI();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ok) {
            setResultUris(mSelectedImages);
        }
        else if (id == R.id.btn_dir) {
            if (mImageFolders == null) {
                MyLog.D("ImageGridActivity"+ "您的手机没有图片");
                return;
            }
            //点击文件夹按钮
            createPopupFolderList();
            mImageFolderAdapter.refreshData(mImageFolders);  //刷新数据
            if (mFolderPopupWindow.isShowing()) {
                mFolderPopupWindow.dismiss();
            } else {
                mFolderPopupWindow.showAtLocation(mFooterBar, Gravity.NO_GRAVITY, 0, 0);
                //默认选择当前选择的上一个，当目录很多时，直接定位到已选中的条目
                int index = mImageFolderAdapter.getSelectIndex();
                index = index == 0 ? index : index - 1;
                mFolderPopupWindow.setSelection(index);
            }
        }
        else if (id == R.id.btn_preview) {//点击预览页
           transPreviewActivity(true);
        }
        else if (id == R.id.btn_back) {
            //点击返回按钮
            finish();
        }
    }

    /** 创建弹出的ListView */
    private void createPopupFolderList() {
//        if(mFolderPopupWindow == null){
            mFolderPopupWindow = new FolderPopUpWindow(this, mImageFolderAdapter);
            mFolderPopupWindow.setOnItemClickListener(new FolderPopUpWindow.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    mImageFolderAdapter.setSelectIndex(position);
                    setCurrentImageFolderPosition(position);
                    mFolderPopupWindow.dismiss();
                    ImageFolder imageFolder = (ImageFolder) adapterView.getAdapter().getItem(position);
                    if (null != imageFolder) {
                        mImageGridAdapter.refreshData(imageFolder.images);
                        mBtnDir.setText(imageFolder.name);
                    }
                    mGridView.smoothScrollToPosition(0);//滑动到顶部
                }
            });
//        }
        mFolderPopupWindow.setMargin(mFooterBar.getHeight());
        //mFolderPopupWindow.setMargin(0);
    }


    private void transPreviewActivity(boolean singlePreview){
        //仅仅预览
        if(singlePreview){
            if(mSelectedImages == null || mSelectedImages.size() == 0 )
                return;
            Intent intent = new Intent(GalleryMultipleActivity.this, GalleryPreviewActivity.class);
            intent.putExtra(PImagePicker.EXTRA_PREVIEW_MODE_SINGLE,singlePreview);
            startActivityForResult(generateExtraIntent(intent), PImagePicker.REQUEST_CODE_PREVIEW);
        }
        else{//预览加选择
            Intent intent = new Intent(GalleryMultipleActivity.this, GalleryPreviewActivity.class);
            startActivityForResult(generateExtraIntent(intent), PImagePicker.REQUEST_CODE_PREVIEW);
        }
    }



    @Override
    public void onImageItemClick(View view, ImageItem imageItem, int position) {
        this.mCurrentPosition  = position;
        transPreviewActivity(false);
        //根据是否有相机按钮确定位置
        //position = imagePicker.isShowCamera() ? position - 1 : position;
//        if (imagePicker.isMultiMode()) {
//            Intent intent = new Intent(ImageGridActivity.this, ImagePreviewActivity.class);
//            intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
//            intent.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, imagePicker.getCurrentImageFolderItems());
//            intent.putExtra(ImagePreviewActivity.ISORIGIN, isOrigin);
//            startActivityForResult(intent, ImagePicker.REQUEST_CODE_PREVIEW);  //如果是多选，点击图片进入预览界面
//        } else {
//            imagePicker.clearSelectedImages();
//            imagePicker.addSelectedImageItem(position, imagePicker.getCurrentImageFolderItems().get(position), true);
//            if (imagePicker.isCrop()) {
//                Intent intent = new Intent(ImageGridActivity.this, ImageCropActivity.class);
//                startActivityForResult(intent, ImagePicker.REQUEST_CODE_CROP);  //单选需要裁剪，进入裁剪界面
//            } else {
//                Intent intent = new Intent();
//                intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
//                setResult(ImagePicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
//                finish();
//            }
//        }
    }


    @Override
    public void onImageSelected(int position, ImageItem item, boolean isAdd) {
        this.mCurrentPosition = position;
        updateUI();
        mImageGridAdapter.notifyDataSetChanged();
    }

    /**
     * 刷新ui
     */
    private void updateUI(){
        int selectCount = this.getSelectImageCount();
        if (selectCount > 0) {
            mBtnOk.setText(getString(R.string.select_complete, selectCount, pImagePickerConfig.getMaxFiles()));
            mBtnOk.setEnabled(true);
            mBtnPre.setEnabled(true);
        } else {
            mBtnOk.setText(getString(R.string.complete));
            mBtnOk.setEnabled(false);
            mBtnPre.setEnabled(false);
        }
        mBtnPre.setText(getResources().getString(R.string.preview_count, selectCount));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == PImagePicker.RESULT_CODE_ITEMS) {
                mSelectedImages = data.getParcelableArrayListExtra(PImagePicker.EXTRA_SELECT_IMAGES);
                setResultUris(mSelectedImages);
            }
            else if(resultCode == PImagePicker.RESULT_CODE_BACK){
                mSelectedImages = data.getParcelableArrayListExtra(PImagePicker.EXTRA_SELECT_IMAGES);
                mCurrentPosition = data.getIntExtra(PImagePicker.EXTRA_SELECTED_IMAGE_POSITION,0);
                updateUI();
                mImageGridAdapter.refreshSelectedData(mSelectedImages);
            }
        }
        else {
            MyLog.D("GalleryMultipleActivity 返回 data 为null");
        }
    }
}
