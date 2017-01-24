package com.paicheya.hammer.graffitipicture.mycamera;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.paicheya.hammer.graffitipicture.util.MyLog;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 自定义相机
 */
public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback, AutoFocusCallback {
	private static final String TAG = "CameraPreview";

	private int viewWidth = 0;
	private int viewHeight = 0;

	/** 监听接口 */
	private OnCameraStatusListener listener;

	private SurfaceHolder holder;
	private Camera camera;
	private FocusView mFocusView;
	private List<Camera.Size> supportPreviewSizeList;
	private List<Camera.Size> supprotPictureSizeList;
	private Camera.Size bestPictureSize;
	private Camera.Size bestPreviewSize;
	private int bestPictureWidth = 2048;
	//private int bestPreviewWidth = 0;

	//创建一个PictureCallback对象，并实现其中的onPictureTaken方法
	private PictureCallback pictureCallback = new PictureCallback() {

		//该方法用于处理拍摄后的照片数据
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// 停止照片拍摄
			try {
				camera.stopPreview();
			} catch (Exception e) {
			}
			// 调用事件结束
			if (null != listener) {
				listener.onCameraStopped(data);
			}
		}
	};


	public CameraPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获得SurfaceHolder对象
		holder = getHolder();
		// 指定用于捕捉拍照事件的SurfaceHolder.Callback对象
		holder.addCallback(this);
		// 设置SurfaceHolder对象的类型
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		setOnTouchListener(onTouchListener);
	}

	// 在surface创建时调用
	public void surfaceCreated(SurfaceHolder holder) {
		MyLog.log(TAG, "MyCamera ==surfaceCreated==");
		if(!Utils.checkCameraHardware(getContext())) {
			Toast.makeText(getContext(), "����ͷ��ʧ�ܣ�", Toast.LENGTH_SHORT).show();
			return;
		}

		camera = getCameraInstance();
		try {
			// 设置用于显示拍照摄像的SurfaceHolder对象
			camera.setPreviewDisplay(holder);
			camera.startPreview();
		} catch (IOException e) {
			e.printStackTrace();
			MyLog.log(TAG,"Error setting camera preview: " + e.getMessage());
			camera.release();
			camera = null;
		}

		//updateCameraParameters();
		findBestPreviewSize(camera.getParameters());
		findBestPictureSize(camera.getParameters());

		setFocus();
	}

	/**
	 * SurfaceHolder销毁的回调
	 * @param holder
     */
	public void surfaceDestroyed(SurfaceHolder holder) {
		MyLog.log(TAG, "MyCamera ==surfaceDestroyed==");
		camera.release();
		camera = null;
	}

	/**
	 * // 在surface的大小发生改变时激发
	 * 如果要设置预览大小，必须在这里设置
	 * @param holder
	 * @param format
	 * @param w
     * @param h
     */
	public void surfaceChanged(final SurfaceHolder holder, int format, int w,
			int h) {

		MyLog.log(TAG, "MyCamera ==surfaceChanged==");
		// stop preview before making changes
		try {
			camera.stopPreview();
		} catch (Exception e){
			// ignore: tried to stop a non-existent preview
		}

		updateCameraParameters();
		// start preview with new settings
		try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();

		} catch (Exception e){
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
		setFocus();
	}


	/**
	 * 点击监听，触发聚焦
	 */
	OnTouchListener onTouchListener = new OnTouchListener() {
		@SuppressWarnings("deprecation")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				int width = mFocusView.getWidth();
				int height = mFocusView.getHeight();
				mFocusView.setX(event.getX() - (width / 2));
				mFocusView.setY(event.getY() - (height / 2));
				mFocusView.beginFocus();
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				focusOnTouch(event);
			}
			return true;
		}
	};

	/**
	 * 获得相机实例，记得销毁
	 * @return
     */
	private Camera getCameraInstance() {
		Camera c = null;
		try {
			int cameraCount = 0;
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			cameraCount = Camera.getNumberOfCameras(); // get cameras number

			for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
				Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
				// 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
					try {
						c = Camera.open(camIdx);   //打开后置摄像头
					} catch (RuntimeException e) {
						Toast.makeText(getContext(), "摄像头打开失败", Toast.LENGTH_SHORT).show();
					}
				}
			}
			if (c == null) {
				c = Camera.open(0); // attempt to get a Camera instance
			}



		} catch (Exception e) {
			Toast.makeText(getContext(), "����ͷ��ʧ�ܣ�", Toast.LENGTH_SHORT).show();
		}

		return c;
	}

	/**
	 * 修改相机属性
	 */
	private void updateCameraParameters() {
		if (camera != null) {
			Camera.Parameters p = camera.getParameters();
			 p = getDefaultParameters(p);
			try {
				camera.setParameters(p);
			} catch (Exception e) {
				MyLog.log(e.toString());
			}
		}
	}

	/**
	 * 获得相机属性
	 * @param p
	 */
	private Camera.Parameters getDefaultParameters(Camera.Parameters p) {
		List<String> focusModes = p.getSupportedFocusModes();
		if (focusModes
				.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
			p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		}

		long time = new Date().getTime();
		p.setGpsTimestamp(time);
		p.setPictureFormat(PixelFormat.JPEG);//设置图片格式
		p.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);//设置对焦模式 自动
		p.setJpegQuality(100);//设置照片品质0~100
		p.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO); //设置白平衡 自动
		if (bestPreviewSize == null){
			findBestPreviewSize(p);
		}

		if (bestPictureSize==null)
		{
			findBestPictureSize(p);
		}

		p.setPreviewSize(bestPreviewSize.width,bestPreviewSize.height);
		p.setPictureSize(bestPictureSize.width,bestPictureSize.height);

		if (getContext().getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
			camera.setDisplayOrientation(90);
			p.setRotation(90);
		}

		MyLog.log("当前相机的预览分辨率："+p.getPreviewSize().width+"  "+p.getPreviewSize().height);
		MyLog.log("当前相机的照片分辨率："+p.getPictureSize().width+"  "+p.getPictureSize().height);

		return p;
	}

	/**
	 * 进行拍照，并将拍摄的照片传入PictureCallback接口的onPictureTaken方法
	 */
	public void takePicture() {
		if (camera != null) {
			try {
				camera.takePicture(null, null, pictureCallback);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setOnCameraStatusListener(OnCameraStatusListener listener) {
		this.listener = listener;
	}

	@Override
	public void onAutoFocus(boolean success, Camera camera) {

	}

	public void start() {
		if (camera != null) {
			camera.startPreview();
		}
	}

	public void stop() {
		if (camera != null) {
			camera.stopPreview();
		}
	}

	/**
	 * 相机拍照监听接口
	 */
	public interface OnCameraStatusListener {
		//拍照结束事件
		void onCameraStopped(byte[] data);
	}

	@Override
	protected void onMeasure(int widthSpec, int heightSpec) {
		viewWidth = MeasureSpec.getSize(widthSpec);
		viewHeight = MeasureSpec.getSize(heightSpec);

		super.onMeasure(
				MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY));
	}

	/**
	 * 将预览大小设置为屏幕大小
	 * @param parameters
	 * @return
	 */
	private Camera.Size findPreviewSizeByScreen(Camera.Parameters parameters) {
		if (viewWidth != 0 && viewHeight != 0) {
			return camera.new Size(Math.max(viewWidth, viewHeight),
					Math.min(viewWidth, viewHeight));
		} else {
			return camera.new Size(Utils.getScreenWH(getContext()).heightPixels,
					Utils.getScreenWH(getContext()).widthPixels);
		}
	}

	/**
	 * 找到最合适的图片分辨率4：3
	 * @param parameters
	 * @return
	 */
	private  void findBestPictureSize(Camera.Parameters parameters) {
		if(bestPictureSize != null)
			return ;
		if (parameters != null)
		{
			if (supprotPictureSizeList == null){
				supprotPictureSizeList = parameters.getSupportedPictureSizes();
			}
			float hw;
			// 系统支持的照片分辨率
			for (Camera.Size size : supprotPictureSizeList){
				hw = (float)Math.min(size.height,size.width)/(float) Math.max(size.height,size.width);

//				MyLog.log("支持照片的分辨率width_"+size.width+"   height_"+size.height
//						+"  宽高比_"+hw);
				//宽高比：4——3 宽度=2048
				if (hw == 0.75f&& size.width == bestPictureWidth){
					bestPictureSize = size;
					MyLog.log("选中width="+size.width);
					return;
				}
			}
			//返回默认
			bestPictureSize = supprotPictureSizeList.get(0);


		}
	}

	/**
	 * 找到合适的相机预览分辨率
	 * @param parameters
     */
	private void findBestPreviewSize(Camera.Parameters parameters){
		if (bestPreviewSize!= null)
			return;

		if (parameters != null){
			if (supportPreviewSizeList == null)
				supportPreviewSizeList = parameters.getSupportedPreviewSizes();
		}
		float hw;
		// 系统支持的所有预览分辨率
		for (Camera.Size size : supportPreviewSizeList){
			hw = (float)Math.min(size.height,size.width)/(float) Math.max(size.height,size.width);
//			MyLog.log("支持相机预览的分辨率width_"+size.width+"   height_"+size.height
//					+"  宽高比_"+hw);
			//宽高比：4——3 宽度=2048
			if (hw == 0.75f){
				if (bestPreviewSize == null)
					bestPreviewSize = size;
				if (bestPreviewSize.width<size.width)
					bestPreviewSize = size;
			}
		}
	}


	/**
	 * 设置焦点和测光区域
	 *
	 * @param event
	 */
	public void focusOnTouch(MotionEvent event) {

		int[] location = new int[2];
		RelativeLayout relativeLayout = (RelativeLayout)getParent();
		relativeLayout.getLocationOnScreen(location);

		Rect focusRect = Utils.calculateTapArea(mFocusView.getWidth(),
				mFocusView.getHeight(), 1f, event.getRawX(), event.getRawY(),
				location[0], location[0] + relativeLayout.getWidth(), location[1],
				location[1] + relativeLayout.getHeight());
		Rect meteringRect = Utils.calculateTapArea(mFocusView.getWidth(),
				mFocusView.getHeight(), 1.5f, event.getRawX(), event.getRawY(),
				location[0], location[0] + relativeLayout.getWidth(), location[1],
				location[1] + relativeLayout.getHeight());

		Camera.Parameters parameters = camera.getParameters();
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

		if (parameters.getMaxNumFocusAreas() > 0) {
			List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
			focusAreas.add(new Camera.Area(focusRect, 1000));

			parameters.setFocusAreas(focusAreas);
		}

		if (parameters.getMaxNumMeteringAreas() > 0) {
			List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
			meteringAreas.add(new Camera.Area(meteringRect, 1000));

			parameters.setMeteringAreas(meteringAreas);
		}

		try {
			camera.setParameters(parameters);
		} catch (Exception e) {
		}
		camera.autoFocus(this);
	}

	/**
	 * 设置聚焦的照片
	 * @param focusView
	 */
	public void setFocusView(FocusView focusView) {
		this.mFocusView = focusView;
	}

	/**
	 * 设置自动聚焦，并且聚焦的圈圈显示在屏幕中间位置
	 */
	public void setFocus() {
		if(!mFocusView.isFocusing()) {
			try {
				camera.autoFocus(this);
				mFocusView.setX((Utils.getWidthInPx(getContext())-mFocusView.getWidth()) / 2);
				mFocusView.setY((Utils.getHeightInPx(getContext())-mFocusView.getHeight()) / 2);
				mFocusView.beginFocus();
			} catch (Exception e) {
			}
		}
	}

}