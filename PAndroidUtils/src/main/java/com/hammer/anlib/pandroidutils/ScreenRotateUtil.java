package com.hammer.anlib.pandroidutils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;

/**
 * ============================
 * Author：  hammercui
 * Version： 1.0
 * Time:     17/3/2
 * Description: 屏幕旋转工具类
 * Fix History:
 * =============================
 */


public class ScreenRotateUtil {

    private static final String TAG = ScreenRotateUtil.class.getSimpleName();
    private ScreenRotateListener screenRotateListener;

    // 是否是竖屏
    //private boolean isPortrait = true;

    private SensorManager sm;
    private OrientationSensorListener listener;
    private Sensor sensor;

    private SensorManager sm1;
    private Sensor sensor1;
    private OrientationSensorListener1 listener1;


    /**
     * 相机的旋转方向，用来做图片旋转用
     */
    public enum ScreenDirection{
        Vertical_0,     //垂直方向0度 默认方向
        Horizontal_90,  //水平方向90度
        Vertical_180,   //垂直方向0度
        Horizontal_270, //水平方向270度
    }

    //默认垂直
    private ScreenDirection currDirection = ScreenDirection.Vertical_0;

    private  Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 888:
                    int orientation = msg.arg1;
                    if (orientation > 45 && orientation < 135) {
                        if(currDirection != ScreenDirection.Horizontal_270){
                            MyLog.log("切换成270度横屏 45到135");
                            screenRotateListener.getRotateDirection(ScreenDirection.Horizontal_270);
                            currDirection = ScreenDirection.Horizontal_270;
                        }
                    } else if (orientation > 135 && orientation < 225) {
                        if(currDirection != ScreenDirection.Vertical_180){
                            MyLog.log("切换成180度竖屏 135到225");
                            screenRotateListener.getRotateDirection(ScreenDirection.Vertical_180);
                            currDirection = ScreenDirection.Vertical_180;
                        }
                    } else if (orientation > 225 && orientation < 315) {
                        if (currDirection != ScreenDirection.Horizontal_90) {
                            MyLog.log("切换成90度横屏 215到315");
                            screenRotateListener.getRotateDirection(ScreenDirection.Horizontal_90);
                            currDirection = ScreenDirection.Horizontal_90;
                        }
                    } else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {
                        if (currDirection != ScreenDirection.Vertical_0) {
                            MyLog.log("切换成0度竖屏 0到45 315到360");
                            screenRotateListener.getRotateDirection(ScreenDirection.Vertical_0);
                            currDirection = ScreenDirection.Vertical_0;
                        }
                    }
                    break;
                default:
                    break;
            }

        };
    };


    public ScreenRotateUtil(Context context) {
        //MyLog.log(TAG, "init orientation listener.");
        // 注册重力感应器,监听屏幕旋转
        sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listener = new OrientationSensorListener(mHandler);

        // 根据 旋转之后/点击全屏之后 两者方向一致,激活sm.
        sm1 = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor1 = sm1.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listener1 = new OrientationSensorListener1();
    }

    /** 开始监听 */
    public void resume(ScreenRotateListener screenRotateListener) {
        //.log(TAG, "start orientation listener.");
        this.screenRotateListener = screenRotateListener;
        sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    /** 停止监听 */
    public void pause() {
       // MyLog.log(TAG, "stop orientation listener.");
        sm.unregisterListener(listener);
        sm1.unregisterListener(listener1);
    }

    /**
     * 屏幕旋转监听
     */
    public interface  ScreenRotateListener{
        //public void isPortrait(boolean value);

        /**
         * 获得旋转方向
         * @param direction
         */
        public void getRotateDirection(ScreenDirection direction);
    }

    /**
     * 手动横竖屏切换方向
     */
    public void toggleScreen() {
//        sm.unregisterListener(listener);
//        sm1.registerListener(listener1, sensor1,SensorManager.SENSOR_DELAY_UI);
//        if (isPortrait) {
//            isPortrait = false;
//            // 切换成横屏
//            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        } else {
//            isPortrait = true;
//            // 切换成竖屏
//            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
    }

    public ScreenDirection getRotateDirect(){
        return this.currDirection;
    }

    /**
     * 重力感应监听者
     */
    public static class OrientationSensorListener implements SensorEventListener {
        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;

        public static final int ORIENTATION_UNKNOWN = -1;

        private Handler rotateHandler;

        public OrientationSensorListener(Handler handler) {
            rotateHandler = handler;
        }

        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            int orientation = ORIENTATION_UNKNOWN;
            float X = -values[_DATA_X];
            float Y = -values[_DATA_Y];
            float Z = -values[_DATA_Z];
            float magnitude = X * X + Y * Y;
            // Don't trust the angle if the magnitude is small compared to the y
            // value
            if (magnitude * 4 >= Z * Z) {
                // 屏幕旋转时
                float OneEightyOverPi = 57.29577957855f;
                float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                orientation = 90 - (int) Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }
            if (rotateHandler != null) {
                rotateHandler.obtainMessage(888, orientation, 0).sendToTarget();
            }
        }
    }

    public class OrientationSensorListener1 implements SensorEventListener {
        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;

        public static final int ORIENTATION_UNKNOWN = -1;

        public OrientationSensorListener1() {
        }

        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            int orientation = ORIENTATION_UNKNOWN;
            float X = -values[_DATA_X];
            float Y = -values[_DATA_Y];
            float Z = -values[_DATA_Z];
            float magnitude = X * X + Y * Y;
            // Don't trust the angle if the magnitude is small compared to the y
            // value
            if (magnitude * 4 >= Z * Z) {
                // 屏幕旋转时
                float OneEightyOverPi = 57.29577957855f;
                float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                orientation = 90 - (int) Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }
            if (orientation > 225 && orientation < 315) {// 检测到当前实际是横屏
                if (currDirection == ScreenDirection.Horizontal_90 || currDirection == ScreenDirection.Horizontal_270) {
                    sm.registerListener(listener, sensor,SensorManager.SENSOR_DELAY_UI);
                    sm1.unregisterListener(listener1);
                }
            } else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {// 检测到当前实际是竖屏
                if (currDirection == ScreenDirection.Vertical_0 || currDirection == ScreenDirection.Vertical_180) {
                    sm.registerListener(listener, sensor,SensorManager.SENSOR_DELAY_UI);
                    sm1.unregisterListener(listener1);
                }
            }
        }
    }
}
