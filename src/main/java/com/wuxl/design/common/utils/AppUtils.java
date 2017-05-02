package com.wuxl.design.common.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 关于手机的一些设置
 * Created by wuxingle on 2017/4/10 0010.
 */
public class AppUtils {

    public static final int CAMERA_OK = 1;

    /**
     * 设置状态栏颜色
     */
    public static void setStatusBarColor(Activity activity, int color) {
        //activity.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //手机版本要大于等于要求的最低版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        } else {
            Log.w("Application", "该手机版本不支持状态栏功能");
        }
    }

    /**
     * 设置手机状态栏背景色透明
     */
    public static void setStatusBarTransparent(Activity activity) {
        //activity.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //手机版本要大于等于要求的最低版本
        setStatusBarColor(activity, Color.TRANSPARENT);
    }

    /**
     * 调用摄像头的权限申请
     */
    public static void applyCameraPerssion(Activity activity) {
        //android6.0要申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请权限
                ActivityCompat.requestPermissions(activity,
                        new String[]{android.Manifest.permission.CAMERA}, CAMERA_OK);
            }
        }
    }

    /**
     * 是否有摄像头权限
     */
    public static boolean haveCamerPerssion(Activity activity) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 序列化输入
     */
    public static Object readSerialize(String path, FileInputStream inputStream) {
        File file = new File(path);
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(inputStream)) {
                return in.readObject();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 序列化输出
     * */
    public static boolean writeSerialize(FileOutputStream outputStream, Serializable obj){
        try(ObjectOutputStream out = new ObjectOutputStream(outputStream)){
            out.writeObject(obj);
            out.flush();
            return true;
        } catch (IOException e){
            return false;
        }
    }

}
