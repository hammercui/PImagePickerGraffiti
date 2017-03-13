package com.paicheya.hammer.demo.imageloader;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.paicheya.pimagepicker.manager.ImageLoaderInterface;

import java.io.File;


public class GlideImageLoaderImp implements ImageLoaderInterface {

    @Override
    public void displayImage(Context context, String path, ImageView imageView, int width, int height) {
        Glide.with(context)                             //配置上下文
                .load(Uri.fromFile(new File(path)))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                .error(com.paicheya.pimagepicker.R.drawable.pip_default_image)           //设置错误图片
                .placeholder(com.paicheya.pimagepicker.R.drawable.pip_default_image)     //设置占位图片
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                .into(imageView);
    }

    @Override
    public void clearMemoryCache(Context context) {
        Glide.get(context).clearMemory();
    }

    @Override
    public void stop() {

    }
}
