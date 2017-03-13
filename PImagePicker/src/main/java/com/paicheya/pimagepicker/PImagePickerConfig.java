package com.paicheya.pimagepicker;

import android.content.Context;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;


import com.hammer.anlib.pandroidutils.AppUtil;
import com.hammer.anlib.pandroidutils.MathUtil;
import com.hammer.anlib.pandroidutils.MyLog;

import java.io.File;

/**
 * Created by cly on 17/2/8.
 *  图片存储路径暂时设计为：
 *  默认时 ：sdk路径/appname图片/图片名
 *  cameraRoll时，需要插入相册： 相册路径(Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera")
 *
 */

public class PImagePickerConfig implements Parcelable {
    private int pressQuality;
    private String imageName;
    private String imagePath;
    private float  aspectRatio;
    private boolean cameraRoll;
    private boolean fromCamera;
    private int maxFiles;
    private boolean multiple;

    public int getPressQuality() {
        return pressQuality;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public boolean isCameraRoll() {
        return cameraRoll;
    }

    public int getMaxFiles() {
        return maxFiles;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public PImagePickerConfig(Builder builder){
        this.pressQuality = builder.pressQuality;
        this.imageName = builder.imageName;
        this.cameraRoll = builder.cameraRoll;
        this.fromCamera = builder.fromCamera;
        String folder  ;
        //插入相册
        if(cameraRoll){
            folder = AppUtil.getDCIMDirect();
            //path = AppUtil.getDCIMDirect();
            //path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/Camera";
        }
        else{
            folder = builder.imageFolder;
            //path = builder.packageDirPath+"/"+builder.dirPath;
        }

        File fileName = new File(folder);
        if (!fileName.exists()) {
            fileName.mkdir();
        }
        this.imagePath   = folder +"/"+builder.imageName;
        this.aspectRatio = builder.aspectRatio;
        this.multiple = builder.multiple;
        this.maxFiles = builder.maxFiles;
    }




    public static class Builder{
        /**
         * 压缩比0~100
         */
        private int pressQuality = 70;

//        /**
//         * 默认图片存储路径
//         */
//        private String  dirPath = "pip";
        /**
         * 默认图片名称
         */
        private String imageName = System.currentTimeMillis()+".jpg";

        /**
         * 宽高比 默认 4：3
         */
        private float aspectRatio = MathUtil.getAspectRatioValue(4,3);

        /**
         * 照片是否保存到相册
         */
        private boolean cameraRoll = false;

        private boolean fromCamera = false;

       // private String  packageDirPath ;

        /**
         * -1 表示不限制最大个数
         */
        private int maxFiles = -1;

        private boolean multiple = false;


        /**
         * 图片存储文件夹
         */
        private String imageFolder ;

        /**
         * 设置压缩比,默认70，从0~100
         * @param quality
         * @return
         */
        public Builder setPressQuality(int quality){
            this.pressQuality = quality;
            return  this;
        }

//        public Builder setDirPath(String dirPath){
//            this.dirPath = dirPath;
//            return this;
//        }
        public Builder setImageName(String imageName){
            this.imageName = imageName;
            return this;
        }

        /**
         * 是否插入相册
         * @param cameraRoll
         * @return
         */
        public Builder setCameraRoll(Boolean cameraRoll){
            this.cameraRoll = cameraRoll;
            return this;
        }

        /**
         * 是否是相机模式
         * @param fromCamera
         * @return
         */
        public Builder setFromCamera(Boolean fromCamera){
            this.fromCamera = fromCamera;
            return this;
        }

        public Builder setMultiple(Boolean multiple){
            this.multiple = multiple;
            return this;
        }
        public Builder setMaxFiles(int maxFiles){
            this.maxFiles = maxFiles;
            return this;
        }

        /**
         * 设置图片存储的文件夹，默认在sd卡文件夹
         * @param folder
         * @return
         */
        public Builder setImageFolder(String folder){
            this.imageFolder = folder;
            return this;
        }

        /**
         * 设置高宽比 高/款 3：4
         * @return
         */
        public Builder setAspectRatio(int width,int height){
            if(width < height){
                MyLog.log("Warn!! 宽高比，宽必须大于高！");
                return this;
            }
            this.aspectRatio = MathUtil.getAspectRatioValue(width,height);
            return this;
        }


        public Builder(Context context){
            //this.packageDirPath = context.getApplicationContext().getFilesDir().getAbsolutePath();
            this.imageFolder = AppUtil.getStorageDirect()+"/"+AppUtil.getAppInfo(context).get(0)+"图片";
        }

        /**
         * 构建
         * @return
         */
        public PImagePickerConfig builder(){
            return new PImagePickerConfig(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.pressQuality);
        dest.writeString(this.imageName);
        dest.writeString(this.imagePath);
        dest.writeFloat(this.aspectRatio);
        dest.writeByte(this.cameraRoll ? (byte) 1 : (byte) 0);
        dest.writeByte(this.fromCamera ? (byte) 1 : (byte) 0);
        dest.writeInt(this.maxFiles);
        dest.writeByte(this.multiple ? (byte) 1 : (byte) 0);
    }

    protected PImagePickerConfig(Parcel in) {
        this.pressQuality = in.readInt();
        this.imageName = in.readString();
        this.imagePath = in.readString();
        this.aspectRatio = in.readFloat();
        this.cameraRoll = in.readByte() != 0;
        this.fromCamera = in.readByte() != 0;
        this.maxFiles = in.readInt();
        this.multiple = in.readByte() != 0;
    }

    public static final Creator<PImagePickerConfig> CREATOR = new Creator<PImagePickerConfig>() {
        @Override
        public PImagePickerConfig createFromParcel(Parcel source) {
            return new PImagePickerConfig(source);
        }

        @Override
        public PImagePickerConfig[] newArray(int size) {
            return new PImagePickerConfig[size];
        }
    };
}
