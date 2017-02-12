
> module-mygraffitipicture是android横屏pad平台，图片比例4：3， 已实现：从相机、相册获得图片，并裁切，涂鸦（纯色笔，马赛克笔)并保存

> module-PImagePicker是android手机平台，图片比例支持4：3，16：9等，目标实现：可配置、从相机、相册获得图片，并裁切，涂鸦（马赛克笔，标记)并保存

## Module:PImagePicker

>图片选择、编辑库，包含自定义相机，照片处理

### 注意事项
#### 1.三星机型旋转角度问题

在三星部分机型，发现camera捕捉bitmap后，会对bitmap进行旋转处理，其他机型没有这个问题。

为了解决这个问题，考虑获得拍照后图片的旋转角度，但是Android不像ios，可以直接拍照后获得ExifInterface信息，Android必须在保存后才能获得ExifInterface。因此我们分为散步：

1. 压缩存储图片
2.  `ExifInterface exifInterface = new ExifInterface(path)`获得 `ExifInterfac`类,`    int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);  `获得旋转角度。
3. 修正旋转角度，具体如何旋转，需要在三星的设备上测试
```
exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, "no");
exifInterface.saveAttributes();
} catch (IOException e) {
// TODO Auto-generated catch block
e.printStackTrace();
}  
```



### 目标功能
* 图片获得
 - 相册
 - 相机
* 图片裁切（可选）
* 图片涂鸦
  - 马赛克（有回退，有清屏）
  - 图标，新建一个图标view,可旋转，可取消


### 设计实现 
#### PImagePickerConfig类
>配置类，配置图片的各项属性，通过构建者模式实现
如：

1. 图片压缩比例
2. 图片宽高比，如4：3，16：9
3. 结果回调接口
4. 图片名称
5. 图片保存路径

#### PImagePicker类
>图片选择功能类，目前使用单例模式

* `init()`:初始化PImagePickerConfig属性
* `getDefault()`:获得单例
* `getCOnfig()`:获得配置类
* `pickFromCamera()`:从相机获得图片，拍照后可直接存储返回，或者进入编辑页
* `pickFromCaleary()`:从相册获得图片，目前单选模式
* `transEditImage(Uri uri)`:进入图片编辑页
* `transCropImage(Uri uri)`:进入图片裁切页

#### ImageResultCallback接口
>图片结果回调类，有`onSuccess()`,`onFail()`方法 

#### BitemapUtil工具类
>提供操作Bitemap的工具，比如按比例裁切，旋转等，


## 引入的第三方库

裁切库ucrop(但是已经被我该的它亲妈都不认得了)


##  工程目录

+app 入口

+mygraffitipicture->CropStartActivity.java 开始获得图片Avtivity

+mygraffitipicture->GraffitiActivity.java  开始涂鸦

+curop->UCropActivity.java  开始裁切






