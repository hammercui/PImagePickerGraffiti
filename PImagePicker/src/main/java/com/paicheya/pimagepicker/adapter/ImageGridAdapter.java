package com.paicheya.pimagepicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;


import com.hammer.anlib.pandroidutils.ScreenUtil;
import com.paicheya.pimagepicker.PImagePickerConfig;
import com.paicheya.pimagepicker.R;
import com.paicheya.pimagepicker.bean.ImageItem;
import com.paicheya.pimagepicker.listener.OnImageSelectedListener;
import com.paicheya.pimagepicker.manager.PImageLoader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * 网格的adapter
 */
public class ImageGridAdapter extends BaseAdapter {

    private static final int ITEM_TYPE_CAMERA = 0;  //第一个条目是相机
    private static final int ITEM_TYPE_NORMAL = 1;  //第一个条目不是相机

    //private ImagePicker imagePicker;
    private WeakReference<Context> context;
    private ArrayList<ImageItem> images;       //当前需要显示的所有的图片数据
    private ArrayList<ImageItem> mSelectedImages; //全局保存的已经选中的图片数据
    private boolean isShowCamera = false;         //是否显示拍照按钮
    private int mImageSize;               //每个条目的大小
    private OnImageItemClickListener imageItemClickListener;   //图片被点击的监听
    private OnImageSelectedListener imageSelectedListener; //图片呗选中的监听
    private LayoutInflater layoutInflater;
    private PImagePickerConfig pImagePickerConfig;
    public ImageGridAdapter(Context activity, ArrayList<ImageItem> images,ArrayList<ImageItem> selectImages,PImagePickerConfig config) {
        this.context = new WeakReference<Context>(activity);
        if (images == null || images.size() == 0) this.images = new ArrayList<>();
        else this.images = images;

        if (selectImages == null || selectImages.size() == 0) this.mSelectedImages = new ArrayList<>();
        else this.mSelectedImages = selectImages;

        this.pImagePickerConfig = config;
        this.layoutInflater = LayoutInflater.from(context.get());
        this.mImageSize = ScreenUtil.getImageItemWidth(context.get());
    }

    public void refreshData(ArrayList<ImageItem> images) {
        if (images == null || images.size() == 0) this.images = new ArrayList<>();
        else this.images = images;
        notifyDataSetChanged();
    }

    public void refreshSelectedData(ArrayList<ImageItem> selectImage){
        if(selectImage !=null ){
            this.mSelectedImages = selectImage;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowCamera) return position == 0 ? ITEM_TYPE_CAMERA : ITEM_TYPE_NORMAL;
        return ITEM_TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        return isShowCamera ? images.size() + 1 : images.size();
    }

    @Override
    public ImageItem getItem(int position) {
        if (isShowCamera) {
            if (position == 0) return null;
            return images.get(position - 1);
        } else {
            return images.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //int itemViewType = getItemViewType(position);
        final ViewHolder holder;
        if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.pip_adapter_image_list_item, parent, false);
                convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize)); //让图片是个正方形
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
        } else {
                holder = (ViewHolder) convertView.getTag();
        }
        final ImageItem imageItem = getItem(position);

        holder.ivThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imageItemClickListener != null) imageItemClickListener.onImageItemClick(holder.rootView, imageItem, position);
                }
            });
        holder.cbCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int selectLimit = pImagePickerConfig.getMaxFiles();
                    if (selectLimit!=-1 && holder.cbCheck.isChecked() && mSelectedImages.size() >= selectLimit) {
                        Toast.makeText(context.get().getApplicationContext(), context.get().getString(R.string.select_limit, selectLimit), Toast.LENGTH_SHORT).show();
                        holder.cbCheck.setChecked(false);
                        holder.mask.setVisibility(View.GONE);
                    } else {
                        imageSelectedListener.onImageSelected(position, imageItem, holder.cbCheck.isChecked());
                        holder.mask.setVisibility(View.VISIBLE);
                    }
                }
            });
        //根据是否多选，显示或隐藏checkbox
//        if (pImagePickerConfig.isMultiple()) {
                holder.cbCheck.setVisibility(View.VISIBLE);
                boolean checked = mSelectedImages.contains(imageItem);
                if (checked) {
                    holder.mask.setVisibility(View.VISIBLE);
                    holder.cbCheck.setChecked(true);
                } else {
                    holder.mask.setVisibility(View.GONE);
                    holder.cbCheck.setChecked(false);
                }
//        } else {
//                holder.cbCheck.setVisibility(View.GONE);
//        }


        PImageLoader.displayImage(context.get(), imageItem.path, holder.ivThumb, mImageSize, mImageSize); //显示图片

        return convertView;
    }


    private static class ViewHolder {
        public View rootView;
        public ImageView ivThumb;
        public View mask;
        public CheckBox cbCheck;
        public ViewHolder(View view) {
            rootView = view;
            ivThumb = (ImageView) view.findViewById(R.id.iv_thumb);
            mask = view.findViewById(R.id.mask);
            cbCheck = (CheckBox) view.findViewById(R.id.cb_check);
        }
    }


    public void setOnImageItemClickListener(OnImageItemClickListener listener) {
        this.imageItemClickListener = listener;
    }

    public void setOnImageSelectedListener(OnImageSelectedListener listener){
        this.imageSelectedListener = listener;
    }

    public interface OnImageItemClickListener {
        void onImageItemClick(View view, ImageItem imageItem, int position);
    }
}