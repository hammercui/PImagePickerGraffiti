package com.paicheya.pimagepicker.interfaces;

import android.net.Uri;

/**
 * 图片回调
 * Created by cly on 17/2/8.
 */

public interface ImgPickerCallback {

    /**
     * 回调获得图片uri
     * @param uri
     */
    public   void  onSuccess(Uri uri);

    /**
     * 失败
     * @param msg
     */
    public void onFail(String msg);
}
