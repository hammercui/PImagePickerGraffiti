package com.paicheya.pimagepicker.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;


import com.hammer.anlib.pandroidutils.ScreenUtil;
import com.paicheya.pimagepicker.bean.ImageItem;
import com.paicheya.pimagepicker.manager.PImageLoader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * 浏览视图的adapter
 */
public class ImagePageAdapter extends PagerAdapter {

    private int screenWidth;
    private int screenHeight;
    private ArrayList<ImageItem> images = new ArrayList<>();
    private WeakReference<Activity> mActivity;
    public  PhotoViewClickListener listener;
    //private List<PhotoView> caches = new ArrayList<>();

    public ImagePageAdapter(Activity activity, ArrayList<ImageItem> images) {
        this.mActivity = new WeakReference<Activity>(activity) ;
        this.images = images;

        DisplayMetrics dm = ScreenUtil.getScreenWH(activity);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        //imagePicker = ImagePicker.getInstance();
    }

    public void setData(ArrayList<ImageItem> images) {
        this.images = images;
    }

    public void setPhotoViewClickListener(PhotoViewClickListener listener) {
        this.listener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(mActivity.get());
        ImageItem imageItem = images.get(position);
        PImageLoader.displayImage(mActivity.get(), imageItem.path, photoView, screenWidth, screenHeight);
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                if (listener != null) listener.OnPhotoTapListener(view, x, y);
            }
        });

        container.addView(photoView);
        return photoView;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        PhotoView photoview = (PhotoView)object;
        if(photoview.getVisibleRectangleBitmap() != null){
            photoview.getVisibleRectangleBitmap().recycle();
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public interface PhotoViewClickListener {
        void OnPhotoTapListener(View view, float v, float v1);
    }
}
