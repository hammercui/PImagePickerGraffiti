package com.hammer.anlib.pandroidutils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * ============================
 * Author：  hammercui
 * Version： 1.0
 * Time:     17/3/2
 * Description: 通讯录工具类
 * Fix History:
 * =============================
 */

public class ContactUtil {

    /**
     * Android通讯 把联系人和具体信息分成两个表存储
     * raw_contacts表：存储联系人的记录ID：字段名称是_id  和data表是一对多的管理，
     *                  另外包含version版本信息，可以判断该条记录有没有更新。
     * data表：存放联系新热信息，_id是主键，raw_contacts_id对应raw_contacts表的_id
     *          mimetype_id字段对应的是mimetypes表的_id
     * mimetypes表：存放data表的每条记录的属性：_id是主键，
     *     为1的时候是email类型,     vnd.android.cursor.item/email_v2
     *     为5的时候是电话号码类型,   vnd.android.cursor.item/phone_v2
     */




    /**
     * 获得头像缩略图的bitmap
     * @param photoData
     * @return
     */
    public static Bitmap loadContactPhotoThumbnail(Context context,String photoData) {
        // Creates an asset file descriptor for the thumbnail file.
        AssetFileDescriptor afd = null;
        try {
            // Creates a holder for the URI.
            Uri thumbUri = Uri.parse(photoData);

        /*
         * Retrieves an AssetFileDescriptor object for the thumbnail
         * URI
         * using ContentResolver.openAssetFileDescriptor
         */
            afd = context.getContentResolver().openAssetFileDescriptor(thumbUri, "r");
        /*
         * Gets a file descriptor from the asset file descriptor.
         * This object can be used across processes.
         */
            assert afd != null;
            FileDescriptor fileDescriptor = afd.getFileDescriptor();
            // Decode the photo file and return the result as a Bitmap
            // If the file descriptor is valid
            if (fileDescriptor != null) {
                // Decodes the bitmap
                return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, null);
            }
            // If the file isn't found
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (afd != null) {
                    afd.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }

}
