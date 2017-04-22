package com.wuxl.design.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.wuxl.design.connect.protocol.DataProtocol;

import java.io.Serializable;

import static com.wuxl.design.utils.DataUtils.toHex;
/**
 * wifi设备
 * Created by wuxingle on 2017/4/11 0011.
 */
public class WifiDevice implements Serializable,Parcelable {

    public static final int ONLINE = 1;

    public static final int BUSY = 2;

    public static final int UNONLINE = 3;

    private byte[] id;

    private String name;

    private int lightLevel = 100;

    private transient int status;

    public static final Parcelable.Creator<WifiDevice> CREATOR=new Parcelable.Creator<WifiDevice>(){
        @Override
        public WifiDevice createFromParcel(Parcel source) {
            byte[] id = new byte[DataProtocol.ORIGIN_LENGTH];
            source.readByteArray(id);
            String name = source.readString();
            int status = source.readInt();
            WifiDevice device = new WifiDevice(id);
            device.setName(name);
            device.setStatus(status);
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
        dest.writeInt(status);
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


    @Override
    public String toString() {
        return "WifiDevice{" +
                "id=" + getHexId() +
                ", name='" + name + '\'' +
                '}';
    }
}
