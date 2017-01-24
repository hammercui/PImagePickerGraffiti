package com.paicheya.hammer.graffitipicture.mycamera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * @Class: ReferenceLine
 * @author: lling(www.cnblogs.com/liuling)
 * @Date: 2015/10/20
 */
public class ReferenceLine extends View {

	private Paint mLinePaint;

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

		int screenHeight = Utils.getScreenWH(getContext()).heightPixels;
		int screenWidth = screenHeight*4/3;

		int width = screenWidth/3;
		int height = screenHeight/3;

		for (int i=1;i<3;i++){
			//竖线画2条
			canvas.drawLine(i*width, 0, i*width, screenHeight, mLinePaint);
			canvas.drawLine(0, i*height, screenWidth, i*height, mLinePaint);
		}
//		for (int i = width, j = 0;i < screenWidth && j<2;i += width, j++) {
//			canvas.drawLine(i, 0, i, screenHeight, mLinePaint);
//		}
//		for (int j = height,i = 0;j < screenHeight && i < 2;j += height,i++) {
//			canvas.drawLine(0, j, screenWidth, j, mLinePaint);
//		}
	}


}
