package com.paicheya.pimagepicker.base;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.paicheya.pimagepicker.R;

import java.io.File;

/**
 * Created by cly on 17/2/8.
 */

public class PermissionUtil {
   // protected Context context;
    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    /**
     * 检测申请读sd权限
     * @return
     */
    protected boolean checkPermissionReadSDkard(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE,
                    context.getString(R.string.pai_permission_read_storage_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
            return false;
        }
        return  true;
    }

    protected boolean checkPermissionCamera(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(context,Manifest.permission.CAMERA,
                    context.getString(R.string.pai_permission_camer),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
            return false;
        }
        return true;
    }

    /**
     * Requests given permission.
     * If the permission has been denied previously, a Dialog will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    protected void requestPermission( Context context,final String permission, String rationale, final int requestCode) {

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission))
        {
            showAlertDialog(
                    context,
                    context.getString(R.string.pai_permission_title_rationale),
                    rationale,
                    null,
                    context.getString(R.string.pai_label_ok),
                    null,
                    context.getString(R.string.pai_label_cancel),
                    permission,
                    requestCode
            );
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, requestCode);
        }
    }


    /**
     * This method shows dialog with given title & message.
     * Also there is an option to pass onClickListener for positive & negative button.
     *
     * @param title                         - dialog title
     * @param message                       - dialog message
     * @param onPositiveButtonClickListener - listener for positive button
     * @param positiveText                  - positive button text
     * @param onNegativeButtonClickListener - listener for negative button
     * @param negativeText                  - negative button text
     */
    protected void showAlertDialog(final Context context, @Nullable String title, @Nullable String message,
                                   @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                   @NonNull String positiveText,
                                   @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                                   @NonNull String negativeText,
                                   final String permission,
                                   final int requestCode

    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{permission}, requestCode);
            }
        });
        builder.setNegativeButton(negativeText, onNegativeButtonClickListener);
        AlertDialog mAlertDialog = builder.show();
    }

    public void checkFileExist(String path){
        File file = new File(path);
        if (!file.exists()){
            file.mkdir();
        }
    }
}
