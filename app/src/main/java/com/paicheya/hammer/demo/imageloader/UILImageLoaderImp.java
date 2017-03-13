package com.paicheya.hammer.demo.imageloader;


import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.paicheya.pimagepicker.manager.ImageLoaderInterface;

import java.io.File;

public class UILImageLoaderImp implements ImageLoaderInterface {

    @Override
    public void displayImage(Context activity, String path, ImageView imageView, int width, int height) {
        ImageSize size = new ImageSize(width, height);
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(Uri.fromFile(new File(path)).toString(), imageView, size);
    }

    @Override
    public void clearMemoryCache(Context context) {

    }

    @Override
    public void stop() {
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().stop();
    }

}
