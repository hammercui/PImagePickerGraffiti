package com.paicheya.pimagepicker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.paicheya.pimagepicker.activity.edit.EditActivity;

/**
 * 路由管理类
 * Created by cly on 17/2/8.
 */

public class ActionsManager {


    /**
     * 跳转图片编辑页
     */
    public static void transEditActivity(Context context,Uri sourceUri){
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra(EditActivity.SOURCE_PATH,sourceUri);
        context.startActivity(intent);
    }
}
