package com.paicheya.pimagepicker.manager;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

/**
 * ============================
 * Author：  hammercui
 * Version： 1.0
 * Time:     17/2/28
 * Description: ImageLoader抽象类，便于切换图片加载库
 * Fix History:
 * =============================
 */

public interface ImageLoaderInterface {
    void displayImage(Context context, String path, ImageView imageView, int width, int height);

    void clearMemoryCache(Context context);

    void stop();
}
