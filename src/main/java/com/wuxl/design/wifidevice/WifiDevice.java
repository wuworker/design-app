package com.wuxl.design.wifidevice;

import android.os.Parcel;
import android.os.Parcelable;

import com.wuxl.design.connect.protocol.DataProtocol;

import java.io.Serializable;

import static com.wuxl.design.common.utils.DataUtils.toHex;
/**
 * wifi设备
 * Created by wuxingle on 2017/4/11 0011.
 */
public class WifiDevice implements Serializable,Parcelable {

    public static final long serialVersionUID = 1L;

    //unOnline为0,这样初始化出来默认断开状态
    public static final int UNONLINE = 0;

    public static final int ONLINE = 1;

    public static final int BUSY = 2;

    private byte[] id;

    private String name;

    private int lightLevel = 50;

    //定时功能是否开启
    private boolean timeEnable;
    //定时时间
    private long time;
    //定时占空比
    private int timePwm;
    //定时开/关
    private boolean timeOn;

    private transient int status;

    public static final Parcelable.Creator<WifiDevice> CREATOR=new Parcelable.Creator<WifiDevice>(){
        @Override
        public WifiDevice createFromParcel(Parcel source) {
            byte[] id = new byte[DataProtocol.ORIGIN_LENGTH];
            source.readByteArray(id);
            String name = source.readString();
            int lightLevel = source.readInt();
            int status = source.readInt();
            long time = source.readLong();
            int timePwm = source.readInt();
            boolean[] onOroff = new boolean[2];
            source.readBooleanArray(onOroff);
            WifiDevice device = new WifiDevice(id);
            device.setName(name);
            device.setLightLevel(lightLevel);
            device.setStatus(status);
            device.setTime(time);
            device.setTimePwm(timePwm);
            device.setTimeEnable(onOroff[0]);
            device.setTimeOn(onOroff[1]);
            return device;
        }

        @Override
        public WifiDevice[] newArray(int size) {
            return new WifiDevice[size];
        }
    };


    public WifiDevice() {

    }

    public WifiDevice(byte[] id) {
        this.id = id;
    }

    public WifiDevice(byte[] id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(id);
        dest.writeString(name);
        dest.writeInt(lightLevel);
        dest.writeInt(status);
        dest.writeLong(time);
        dest.writeInt(timePwm);
        dest.writeBooleanArray(new boolean[]{timeEnable,timeOn});
    }

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public String getHexId(){
        return toHex(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLightLevel() {
        return lightLevel;
    }

    public void setLightLevel(int lightLevel) {
        this.lightLevel = lightLevel;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getTimePwm() {
        return timePwm;
    }

    public void setTimePwm(int timePwm) {
        this.timePwm = timePwm;
    }

    public boolean isTimeOn() {
        return timeOn;
    }

    public void setTimeOn(boolean timeOn) {
        this.timeOn = timeOn;
    }

    public boolean isTimeEnable() {
        return timeEnable;
    }

    public void setTimeEnable(boolean timeEnable) {
        this.timeEnable = timeEnable;
    }


    @Override
    public String toString() {
        return "WifiDevice{" +
                "id=" + getHexId() +
                ", name='" + name + '\'' +
                '}';
    }
}
