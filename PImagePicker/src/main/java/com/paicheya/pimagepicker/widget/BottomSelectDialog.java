package com.paicheya.pimagepicker.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.hammer.anlib.pandroidutils.MyLog;
import com.paicheya.pimagepicker.R;

/**
 * ============================
 * Author：  hammercui
 * Version： 1.0
 * Time:     17/3/10
 * Description:
 * Fix History:
 * =============================
 */

public class BottomSelectDialog extends DialogFragment implements View.OnClickListener {
    private  View masker;
    private  LinearLayout layoutBottom;
    private SelectFromListener selectFromListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //去掉dialog的标题，需要在setContentView()之前
        this.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = this.getDialog().getWindow();
        //去掉dialog默认的padding
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //设置dialog的位置在底部
        lp.gravity = Gravity.BOTTOM;
        //设置dialog的动画
        lp.windowAnimations = R.style.BottomDialogAnimation;
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable());

        final View view = inflater.inflate(R.layout.pip_dialog_select, null);
        view.findViewById(R.id.btn_from_camera).setOnClickListener(this);
        view.findViewById(R.id.btn_from_gallery).setOnClickListener(this);

        return view;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        selectFromListener = null;
    }

    @Override
    public void onClick(View v) {
        if(selectFromListener == null){
            MyLog.E("selectFromListener is null");
            return;
        }
        final int id = v.getId();
        if(id == R.id.btn_from_camera){
            dismiss();
            selectFromListener.fromCamera();
        }
        else if(id == R.id.btn_from_gallery){
            dismiss();
            selectFromListener.fromGallery();
        }
    }



    public void setSelectFromListener(SelectFromListener selectFromListener1){
        this.selectFromListener = selectFromListener1;
    }

    public interface SelectFromListener{
        void fromCamera();
        void fromGallery();
    }
}
