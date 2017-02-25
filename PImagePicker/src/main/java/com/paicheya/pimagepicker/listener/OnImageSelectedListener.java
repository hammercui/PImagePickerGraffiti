package com.paicheya.pimagepicker.listener;

import com.paicheya.pimagepicker.bean.ImageItem;

/**
 * ============================
 * Author：  hammercui
 * Version： 1.0
 * Time:     17/2/25
 * Description:  image被选中的监听
 * Fix History:
 * =============================
 */

public interface OnImageSelectedListener {
    void onImageSelected(int position, ImageItem item, boolean isAdd);
}
