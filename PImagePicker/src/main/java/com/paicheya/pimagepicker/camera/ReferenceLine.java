package com.paicheya.pimagepicker.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.paicheya.pimagepicker.PImagePicker;

/**
 * @Class: ReferenceLine
 * @author: lling(www.cnblogs.com/liuling)
 * @Date: 2015/10/20
 */
public class ReferenceLine extends View {

	private Paint mLinePaint;
	private int left;
	private int top;


	public ReferenceLine(Context context) {
		super(context);
		init();
	}

	public ReferenceLine(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ReferenceLine(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		mLinePaint = new Paint();
		mLinePaint.setAntiAlias(true);
		mLinePaint.setColor(Color.parseColor("#ffffff"));
		mLinePaint.setStrokeWidth(5);
	}



	@Override
	protected void onDraw(Canvas canvas) {
		if(isInEditMode())
			return;

		int screenWidth  = ScreenUtils.getScreenWH(getContext()).widthPixels;
		int screenHeight = (int )((float)screenWidth / PImagePicker.getDefault().getConfig().getAspectRatio());

		int width = screenWidth/2;
		int height = screenHeight/2;
//
//		for (int i=1;i<4;i++){
//			canvas.drawLine(i*width, 0, i*width, screenHeight, mLinePaint);
//			canvas.drawLine(0, i*height, screenWidth, i*height, mLinePaint);
//		}
		//竖线画
		for (int i = width, j = 0;i < screenWidth && j<2;i += width, j++) {
			canvas.drawLine(i, 0, i, screenHeight, mLinePaint);
		}
		//划横线
		for (int j = 0,i = 0;j <= screenHeight && i < 3;j += height,i++) {
			canvas.drawLine(0, j, screenWidth, j, mLinePaint);
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
//		this.top = top;
//		invalidate();
	}
}
