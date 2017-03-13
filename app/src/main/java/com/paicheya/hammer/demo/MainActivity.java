package com.paicheya.hammer.demo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.hammer.anlib.pandroidutils.ScreenUtil;
import com.paicheya.hammer.demo.imageloader.GlideImageLoaderImp;
import com.paicheya.hammer.demo.imageloader.UILImageLoaderImp;
import com.paicheya.pimagepicker.PImagePicker;
import com.paicheya.pimagepicker.PImagePickerConfig;
import com.paicheya.pimagepicker.bean.ImageItem;
import com.paicheya.pimagepicker.manager.PImageLoader;
import com.paicheya.pimagepicker.widget.BottomSelectDialog;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //private GridView gridView;
    private ArrayList<ImageItem> list = new ArrayList<>();
    private MyAdapter adapter;
    private RadioButton rb_uil;
    private RadioButton rb_glide;
    private RecyclerView recyclerView;
    private BottomSelectDialog bottomSelectDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
    }

    private void initUI(){
        PImageLoader.init(new GlideImageLoaderImp());

        findViewById(R.id.btn_test_camera).setOnClickListener(this);
        findViewById(R.id.btn_test_gallery).setOnClickListener(this);
        findViewById(R.id.btn_test_contact).setOnClickListener(this);


        adapter = new MyAdapter(list,this);
        recyclerView = (RecyclerView)findViewById(R.id.recycleview_demo);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView.setAdapter(adapter);


        rb_uil = (RadioButton) findViewById(R.id.rb_uil);
        rb_glide = (RadioButton) findViewById(R.id.rb_glide);

        bottomSelectDialog = new BottomSelectDialog();
    }



    private void showImageView(ArrayList<ImageItem> list){
        adapter.setData(list);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //成功获得图片
                case PImagePicker.REQUEST_PICKER_IMAGE_SINGLE:
                    ImageItem  imageItem = PImagePicker.getOutput(data);
                    if (imageItem != null) {
                        list.clear();
                        list.add(imageItem);
                        showImageView(list);
                    }
                    break;
                case PImagePicker.REQUEST_PICKER_IMAGE_MULTIPLE:
                    list.clear();
                    list = PImagePicker.getOutputArray(data);
                    showImageView(list);
                    break;
            }
        }
        else if(resultCode == PImagePicker.REQUEST_PICKER_IMAGE_ERROR){
            String error = PImagePicker.getOutputError(data);
            Log.i("回调获得错误：",error);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_test_camera:
                PImagePickerConfig pImagePickerConfig = new PImagePickerConfig.Builder(this)
                        .setImageName(System.currentTimeMillis()+".jpg")
                        .setPressQuality(50)
                        .setFromCamera(true)
                        .setAspectRatio(4,3)
                        .setCameraRoll(true)
                        .setMultiple(true)
                        .builder();
                PImagePicker.create(pImagePickerConfig).startCameraActivity(this);
                break;
            case R.id.btn_test_gallery:
                if(rb_uil.isChecked()){
                    PImageLoader.init(new UILImageLoaderImp());
                }
                else if(rb_glide.isChecked()){
                    PImageLoader.init(new GlideImageLoaderImp());
                }
                PImagePickerConfig pImagePickerConfig2 = new PImagePickerConfig.Builder(this)
                        .setImageName(System.currentTimeMillis()+".jpg")
                        .setPressQuality(50)
                        .setMultiple(true)
                        .setMaxFiles(10)
                        .builder();
                PImagePicker.create(pImagePickerConfig2).startGalleryActivity(this);
                break;
            case R.id.btn_test_contact:
                PImagePicker.showSelectDialog(this);
                break;
        }
    }



    private static  class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<ImageItem> items;
        private WeakReference<Context> contextWeakReference;
        private int size = 30;
        public MyAdapter(List<ImageItem> items,Context context) {
            this.items = items;
            this.contextWeakReference = new WeakReference<Context>(context);
            this.size = size;
        }

        public void setData(List<ImageItem> items) {
            this.items = items;
            notifyDataSetChanged();
        }


        @Override
        public MainActivity.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(contextWeakReference.get()).inflate(R.layout.adapter_image_item,parent,false);
            return new MainActivity.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MainActivity.MyViewHolder holder, int position) {
            ImageView imageView = holder.imageView;
            PImageLoader.displayImage(this.contextWeakReference.get(),
                    items.get(position).path, imageView, 1000, 1000);

            //绑定数据的同时，修改每个ItemView的高度
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            lp.width  = ScreenUtil.getWidthInPx(contextWeakReference.get()) /3 ;
            lp.height = lp.width;
            holder.itemView.setLayoutParams(lp);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private View rootView;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.rootView = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.image_item);
        }
    }

}
