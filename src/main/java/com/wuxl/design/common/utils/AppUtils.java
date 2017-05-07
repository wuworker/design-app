package com.wuxl.design.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

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
     * 获取手机mac地址wlan0
     * */
    public static String getMacWlan0() throws SocketException {
        return getMacAddress("wlan0");
    }

    /**
     * 获取手机虚拟mac地址（dummy0）
     * */
    public static String getMacDummy0() throws SocketException {
        return getMacAddress("dummy0");
    }

    /**
     * 获取mac地址
     * */
    public static String getMacAdress(Context context)throws SocketException{
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            Log.d("获取mac","此手机版本为:"+Build.VERSION.SDK_INT+",小于"+Build.VERSION_CODES.M);
            WifiInfo info = getWifiInfo(context);
            return info.getMacAddress();
        } else {
            Log.d("获取mac","此手机版本为:"+Build.VERSION.SDK_INT+",不小于"+Build.VERSION_CODES.M);
            return getMacDummy0();
        }
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


    /**
     * 根据不同的参数得到手机的mac地址
     * @param name 应该为:
     *             wlan0
     *             p2p0
     *             dummy0
     * */
    private static String getMacAddress(String name)throws SocketException {
        String address = "";
        // 把当前机器上的访问网络接口的存入 Enumeration集合中
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {
            NetworkInterface netWork = interfaces.nextElement();
            // 如果存在硬件地址并可以使用给定的当前权限访问，则返回该硬件地址（通常是 MAC）。
            byte[] by = netWork.getHardwareAddress();
            if (by == null || by.length == 0) {
                continue;
            }
            StringBuilder builder = new StringBuilder();
            for (byte b : by) {
                builder.append(String.format("%02X", b));
            }
            if (builder.length() > 0) {
                builder.deleteCharAt(builder.length() - 1);
            }
            String mac = builder.toString();
            Log.d("mac address", "interfaceName="+netWork.getName()+", mac="+mac);
            // 从路由器上在线设备的MAC地址列表，可以印证设备Wifi的 name 是 wlan0
            if (netWork.getName().equals(name)) {
                address = mac;
            }
        }
        return address;
    }

    /**
     *获取wifi管理
     * */
    private static WifiInfo getWifiInfo(Context context) {
        WifiManager mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        return mWifiManager.getConnectionInfo();
    }
}
