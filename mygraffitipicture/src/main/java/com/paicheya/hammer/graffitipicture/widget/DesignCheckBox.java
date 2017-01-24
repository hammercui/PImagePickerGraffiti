package com.paicheya.hammer.graffitipicture.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import com.paicheya.hammer.graffitipicture.R;

/**
 * 自定义的选择器
 * Created by hammer on 2016/11/12.
 */
public class DesignCheckBox extends LinearLayout{
    private List<DesignButton> buttonList;
    private Context context;
    private DesignCheckListener checkListener;
    private int currCheckId;


    public DesignCheckBox(Context context) {
        super(context);
        this.context = context;
    }

    public DesignCheckBox(Context context, AttributeSet attrs){
        super(context,attrs);
        this.context = context;
    }

    public void initCheckListener(DesignCheckListener checkListener,int defaultId){
        this.checkListener = checkListener;

         buttonList = new ArrayList<>();
//        float density = ((BaseActivity)context).getDensity();
//        int marginLR = (int)density*20;
//        int marginTP = (int)density*10;

//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
//        params.setMargins(marginLR,marginTP,marginTP,marginLR);
//        DesignButton button = new DesignButton(context);
//        button.setLayoutParams(params);
//        button.setResDrawableId(R.drawable.ucrop_ic_cross);
//        this.addView(button);
        int  count= this.getChildCount();
        Log.d("测试","子项总数"+this.getChildCount());

        for (int i=0;i<count;i++){
            DesignButton button = (DesignButton)this.getChildAt(i);
            button.setOnClickListener(new MyClickListener());
            button.setCheckId(i);
            button.setSelected(i==defaultId?true:false);
            buttonList.add(button);
        }
    }

    private class MyClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            DesignButton button = (DesignButton)v;
            for (DesignButton designButton:buttonList){
                designButton.setSelected(button.getCheckId()==designButton.getCheckId()?true:false);
            }
            if (checkListener ==null)
                return;
            checkListener.onChecked(button.getCheckId());
        }
    }


}
