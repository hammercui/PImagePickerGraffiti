package com.paicheya.hammer.demo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * ============================
 * Author：  hammercui
 * Version： 1.0
 * Time:     17/2/25
 * Description:
 * Fix History:
 * =============================
 */

public class ExpandGridView extends GridView {
    public ExpandGridView(Context context) {
        super(context);
    }

    public ExpandGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
