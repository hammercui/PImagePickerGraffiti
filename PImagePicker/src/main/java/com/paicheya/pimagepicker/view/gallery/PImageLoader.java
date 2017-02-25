package com.paicheya.pimagepicker.view.gallery;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.File;

/**
 * ============================
 * Author：  hammercui
 * Version： 1.0
 * Time:     17/2/24
 * Description: 显示图片
 * Fix History:
 * =============================
 */

public class PImageLoader {

    public static void displayImage(Context context, String path, ImageView imageView,int width,int height){
        ImageLoader.getInstance().displayImage(Uri.fromFile(new File(path)).toString(),imageView,new ImageSize(width,height));
    }
}
