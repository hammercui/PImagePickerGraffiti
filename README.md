# PImagePicker
从相机选择：
![image](image/111.gif)
从相册选择：
![image](image/222.gif)
选择器：
![image](image/333.gif)

# 目录
* [Module:PImagePicker](#Module:PImagePicker)
 	* [感谢](#感谢)
 	* [relase note](#relase-note)
 	* [1.注意事项](#1注意事项)
 		* [1.三星机型旋转角度问题](#1三星机型旋转角度问题)
 		* [2.glide内存管理](#2glide内存管理)
 		* [3.android多图选择上传前压缩问题](#3.android多图选择上传前压缩问题)
 	* [2. 目标功能](#2目标功能)
 	* [3. 设计实现](#3设计实现)
 		* [PImagePickerConfig类](#pimagepickerconfig类)
 		* [PImagePicker类](#pimagepicker类)
 		* [BitemapUtil工具类](#bitemaputil工具类)
 		* [](#)

 	* [引入的第三方库](#引入的第三方库)
 	* [工程目录](#工程目录)
 



## 感谢：
本module的多图选择模块，大部分使用了[廖子尧](https://github.com/jeasonlzy)的[ImagePicker](https://github.com/jeasonlzy/ImagePicker)组件库，十分感谢！

>图片选择、编辑库，包含自定义相机，照片处理


## relase note
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
+ 2017-02-26：
 - 修改[廖子尧](https://github.com/jeasonlzy)的[ImagePicker](https://github.com/jeasonlzy/ImagePicker)库，并嵌入功能
 - 完成多图选择，并测试内存占用，完美！暂时未发现内存泄漏问题，待更新
 - 考虑到activity的独占性，修改单例模式，使用继承模式，每个activity自己维护数据。

+ 2017-03-02:
 - 替换图片加载库univers image loader为gilde
 - 发现uil的内存泄漏问题，glide的内存管理更优秀
 - gradle增加了library打包功能，可以打包aar并提交到maven仓库，maven仓库可以是本地仓库，也可是网络私有仓库。
 
## 1.注意事项


### 1.三星机型旋转角度问题

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


### 2.glide内存管理

glide的内存管理区域于uil的地方在于，glide可以把图片内存生命周期限定在具体activity或者fragment，然后在onDestory时手动销毁

```
 Glide.get(context).clearMemory();
``` 

### 3.android多图选择上传前压缩问题(未完成）
最近使用中发现，如果批量上传，图片过大的话，rn会超时，所以考虑在选择多个图片并回传给rn的时候，先批量压缩并存储，然后把压缩后的图片uri回传给rn。
设计思路

* 点击完成，弹出压缩进度图
* asyncTask执行压缩存储进程
* 缓存地址暂定为: 包地址/compressCache 
* 可定制参数：压缩比例quality，阀值maxBytes,缓存最大个数60，超出定时清理(暂时按照先进先出FIFO)



## 2.目标功能
* 图片获得
 - 相册，单选（支持按比例裁切，压缩），多选（暂时不支持裁切）
 - 相机，单拍（支持按比例裁切，压缩）
* 图片裁切（可选）
* 图片涂鸦（未完成）
  - 马赛克（有回退，有清屏）
  - 图标，新建一个图标view,可旋转，可取消
* 图片加载库提供两种：
  - glide
  - uiversual image loader


## 3.设计实现 
### PImagePickerConfig类
>配置类，配置图片的各项属性，通过构建者模式实现
如：

* `setPressQuality(int )`图片压缩比例 0~100
* `setAspectRatio(4,3)`图片宽高比，如4：3，16：9
* `setImageName(System.currentTimeMillis()+".jpg")`图片名称
* `setDirPath(String)`图片保存相对路径,比如`"/padDemo"`
* `setFromCamera(Boolean)`相机情况，相机一定要把获得的图片放到其他地址，而不是app包路径

### PImagePicker类
>图片选择功能类，功能入口

* `creat(PImagePickerConfig)`:初始化PImagePickerConfig属性
* `startCameraActivity(Activity)`:从相机获得图片，拍照后可直接存储返回，或者进入编辑页
* `startGalleryActivity(Activity)`:从相册获得图片，目前单选模式


### BitemapUtil工具类
>提供操作Bitemap的工具，比如按比例裁切，旋转，byte转bitmap,uri获得realpath等，


## 引入的第三方库

```
 //google官方弹性布局
    compile 'com.google.android:flexbox:0.2.5'
    //crop
    compile 'com.yalantis:ucrop:2.2.0-native'
    //PhotoView
    compile 'com.github.chrisbanes.photoview:library:1.2.4'
```










