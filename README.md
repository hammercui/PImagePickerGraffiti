
> module-mygraffitipicture是android横屏pad平台，图片比例4：3， 已实现：从相机、相册获得图片，并裁切，涂鸦（纯色笔，马赛克笔)并保存

> module-PImagePicker是android手机平台，图片比例支持4：3，16：9等，目标实现：可配置、从相机、相册获得图片，并裁切，涂鸦（马赛克笔，标记)并保存

## Module:PImagePicker

>图片选择、编辑库，包含自定义相机，照片处理

### relase note:
+ 2017-02-19:
 - 完美解决里的自定义camera在竖屏应用，三星等部分手机拍照之后bitmap自动旋转的问题。
 - 发现内存溢出问题，是持有了外部的匿名内部类，也会持有匿名内部类的外部类。解决方式：不再使用单例模式+callback的回调方式，使用onResultActivity方式。
+ 2017-02-23:
 - 从图库选择，新增多选功能，使用系统自带的多选模式。
 
 ```
   Intent galleryIntent = new Intent();
   galleryIntent.setType("image/*");
   galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
   galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
   galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
   galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
    final Intent chooserIntent = Intent.createChooser(galleryIntent, "Pick an image");
   this.startActivityForResult(chooserIntent, REQUEST_SELECT_PICTURE)
 ```

### 1. 注意事项
#### 1.三星机型旋转角度问题


在三星部分机型，发现camera捕捉bitmap后，会对bitmap进行旋转处理，其他机型没有这个问题。

以下解决方式验证不成功：

~~为了解决这个问题，考虑获得拍照后图片的旋转角度，但是Android不像ios，可以直接拍照后获得ExifInterface信息，Android必须在保存后才能获得ExifInterface。因此我们分为三步:~~


~~1. 压缩存储图片~~
~~2.  `ExifInterface exifInterface = new ExifInterface(path)`获得 `ExifInterfac`类,`    int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);  `获得旋转角度。~~
~~3. 修正旋转角度，具体如何旋转，需要在三星的设备上测试`
exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, "no");
exifInterface.saveAttributes();
} catch (IOException e) {
// TODO Auto-generated catch block
e.printStackTrace();
}  
`~~


**新的解决方式，已经完美解决了，如下**

首先在竖屏应用设置camera旋转90度

```
if (Build.VERSION.SDK_INT >= 8) {
	Method downPolymorphic;
		try {
			downPolymorphic = camera.getClass().getMethod("setDisplayOrientation",
					new Class[]{int.class});
			if (downPolymorphic != null) {
				downPolymorphic.invoke(camera, new Object[]{90});
			}
		} catch (Exception e) {
			Log.e("Came_e", "图像出错");
		}
} else {
		parameters.setRotation(90);
}
```

camer获得data转换成bitmap时，进行bitmap的旋转

```
 public static Bitmap byteToBitmap(byte[] data ){
        Bitmap croppedImage;
        //获得图片大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        MyLog.log("预处理option的width:"+options.outWidth+",height"+options.outHeight);
        int width = options.outWidth;
        int height = options.outHeight;
        options.inJustDecodeBounds = false;
        Rect r = new Rect(0, 0, width, height);
        try {
            croppedImage = decodeRegionCrop(data, r,width,height);
        } catch (Exception e) {
            return null;
        }
        return croppedImage;
    }

    private static Bitmap decodeRegionCrop(byte[] data, Rect rect,int width,int height) {
        InputStream is = null;
        System.gc();
        Bitmap croppedImage = null;
        try {
            is = new ByteArrayInputStream(data);
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);
            try {
                croppedImage = decoder.decodeRegion(rect, new BitmapFactory.Options());
            } catch (IllegalArgumentException e) {
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeStream(is);
        }
        Matrix m = new Matrix();
        m.setRotate(90, width / 2, height / 2);
        Bitmap rotatedImage = Bitmap.createBitmap(croppedImage, 0, 0, width, height, m, true);
        if (rotatedImage != croppedImage)
            croppedImage.recycle();
        return rotatedImage;
    }

```





### 2. 目标功能
* 图片获得
 - 相册
 - 相机
* 图片裁切（可选）
* 图片涂鸦
  - 马赛克（有回退，有清屏）
  - 图标，新建一个图标view,可旋转，可取消


### 3. 设计实现 
#### PImagePickerConfig类
>配置类，配置图片的各项属性，通过构建者模式实现
如：

1. `setPressQuality(int )`图片压缩比例 0~100
2. `setAspectRatio(4,3)`图片宽高比，如4：3，16：9
4. `setImageName(System.currentTimeMillis()+".jpg")`图片名称
5. `setDirPath(String)`图片保存相对路径
6. `setFromCamera(Boolean)`相机情况，相机一定要把获得的图片放到其他地址，而不是app包路径

#### PImagePicker类
>图片选择功能类，功能入口

* `creat(PImagePickerConfig)`:初始化PImagePickerConfig属性
* `startCameraActivity(Activity)`:从相机获得图片，拍照后可直接存储返回，或者进入编辑页
* `startGalleryActivity(Activity)`:从相册获得图片，目前单选模式


#### BitemapUtil工具类
>提供操作Bitemap的工具，比如按比例裁切，旋转，byte转bitmap,uri获得realpath等，


## 引入的第三方库

裁切库ucrop(以源码的形式引入，便于定制化修改),目前使用版本
`compile 'com.yalantis:ucrop:2.2.0' `


##  工程目录

+app 入口

+mygraffitipicture->CropStartActivity.java 开始获得图片Avtivity

+mygraffitipicture->GraffitiActivity.java  开始涂鸦

+curop->UCropActivity.java  开始裁切






