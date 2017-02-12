package com.paicheya.pimagepicker;

import android.os.Environment;

import com.paicheya.pimagepicker.interfaces.ImgPickerCallback;
import com.paicheya.pimagepicker.util.MathUtil;
import com.paicheya.pimagepicker.util.MyLog;

import java.io.File;
import java.util.UUID;

/**
 * Created by cly on 17/2/8.
 */

public class PImagePickerConfig {


    private ImgPickerCallback imageCallback;
    private int pressQuality;
    private String dirPath;
    private String imageName;
    private String imagePath;
    private float aspectRatio;
    public ImgPickerCallback getImageCallback() {
        return imageCallback;
    }

    public int getPressQuality() {
        return pressQuality;
    }

    public String getDirPath() {
        return dirPath;
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

    public PImagePickerConfig(Builder builder){
        this.imageCallback = builder.imageCallback;
        this.pressQuality = builder.pressQuality;
        this.imageName = builder.imageName;

        //默认图片路径
        if(builder.dirPath == null){
            builder.dirPath  = Environment.getExternalStorageDirectory().getPath()+"/duiduihce";
            this.dirPath = builder.dirPath;
        }
        this.dirPath = builder.dirPath;
        File fileName = new File(this.dirPath);
        if (!fileName.exists()) {
            fileName.mkdir();
        }
        this.imagePath = builder.dirPath+"/"+builder.imageName;
        this.aspectRatio = builder.aspectRatio;
    }


    public static class Builder{
        /**
         * 图片回调
         */
        private ImgPickerCallback imageCallback;

        /**
         * 压缩比0~100
         */
        private int pressQuality = 70;

        /**
         * 默认图片存储路径
         */
        private String  dirPath ;
        /**
         * 默认图片名称
         */
        private String imageName = System.currentTimeMillis()+".jpg";

        /**
         * 宽高比 默认 4：3
         */
        private float aspectRatio = MathUtil.getAspectRatioValue(4,3);

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

        public Builder setResultCallback(ImgPickerCallback callback){
            this.imageCallback = callback;
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


        public Builder(){
        }

        /**
         * 构建
         * @return
         */
        public PImagePickerConfig builder(){
            return new PImagePickerConfig(this);
        }
    }
}
