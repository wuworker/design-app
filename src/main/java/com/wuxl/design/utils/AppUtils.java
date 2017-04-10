package com.wuxl.design.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * 关于手机的一些设置
 * Created by wuxingle on 2017/4/10 0010.
 */
public class AppUtils {

    /**
     * 设置状态栏颜色
     */
    public static void setStatusBarColor(Activity activity,int color){
        //activity.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //手机版本要大于等于要求的最低版本
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        } else {
            Log.w("Application","该手机版本不支持状态栏功能");
        }
    }

    /**
     * 设置手机状态栏背景色透明
     * */
    public static void setStatusBarTransparent(Activity activity){
        //activity.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //手机版本要大于等于要求的最低版本
        setStatusBarColor(activity,Color.TRANSPARENT);
    }


}
