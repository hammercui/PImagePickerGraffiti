package com.paicheya.hammer.graffitipicture.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.paicheya.hammer.graffitipicture.R;



/**
 * Created by hammer on 2016/11/12.
 */
public class DesignButton extends FrameLayout {
   // private ImageView background;
    private ImageView src;
    private int checkId;


    public int getCheckId() {
        return checkId;
    }

    public void setCheckId(int checkId) {
        this.checkId = checkId;
    }



    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
        this.setBackgroundResource(selected? R.drawable.react_round_read:R.drawable.react_round_alpha);
    }

    private boolean selected = false;
    @Override
    public boolean isSelected() {
        return selected;
    }


    public DesignButton(Context context) {
        super(context);
        initView(context,null);

    }

    public DesignButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context,attrs);
    }

    public void initView(Context context,AttributeSet attrs){
        //View view = LayoutInflater.from(context).inflate(R.layout.btn_design, this, true);
        src = new ImageView(context);
        LayoutParams  params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(10,10,10,10);
        src.setLayoutParams(params);
        this.addView(src);
        setSelected(selected);


//        background =  (ImageView)view.findViewById(R.id.designbt_background);
//        src = (ImageView)view.findViewById(R.id.designbt_src);
        if (attrs == null)
            return;

        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.DesignButton);
        int dsrc = a.getResourceId(R.styleable.DesignButton_designbt_src,0);
        src.setBackgroundResource(dsrc);

        boolean selected = a.getBoolean(R.styleable.DesignButton_designbt_selected,false);
        setSelected(selected);
        a.recycle();
    }

    /**
     * 按钮的图片
     * @param id
     */
    public void setResDrawableId(@DrawableRes int id){
        src.setImageResource(id);
    }


}
