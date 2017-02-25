package com.paicheya.pimagepicker;

import android.content.Context;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;

import com.paicheya.pimagepicker.util.MathUtil;
import com.paicheya.pimagepicker.util.MyLog;

import java.io.File;

/**
 * Created by cly on 17/2/8.
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
        String path  ;
        //生成图片路径
        if(fromCamera && cameraRoll){
            //path = Environment.getExternalStorageDirectory().getPath()+"/"+builder.dirPath;
            path = Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera";
        }
        else{
            path = builder.packageDirPath+"/"+builder.dirPath;
        }
        File fileName = new File(path);
        if (!fileName.exists()) {
            fileName.mkdir();
        }
        this.imagePath = path +"/"+builder.imageName;
        this.aspectRatio = builder.aspectRatio;
        this.multiple = builder.multiple;
        this.maxFiles = builder.maxFiles;
    }




    public static class Builder{
        /**
         * 压缩比0~100
         */
        private int pressQuality = 70;

        /**
         * 默认图片存储路径
         */
        private String  dirPath = "pip";
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
        private boolean cameraRoll = true;

        private boolean fromCamera = false;

        private String  packageDirPath ;

        private int maxFiles = 5;
        private boolean multiple = false;

        /**
         * 设置压缩比,默认70，从0~100
         * @param quality
         * @return
         */
        public Builder setPressQuality(int quality){
            this.pressQuality = quality;
            return  this;
        }

        public Builder setDirPath(String dirPath){
            this.dirPath = dirPath;
            return this;
        }
        public Builder setImageName(String imageName){
            this.imageName = imageName;
            return this;
        }

        public Builder setCameraRoll(Boolean cameraRoll){
            this.cameraRoll = cameraRoll;
            return this;
        }

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
            this.packageDirPath = context.getApplicationContext().getFilesDir().getAbsolutePath();
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
