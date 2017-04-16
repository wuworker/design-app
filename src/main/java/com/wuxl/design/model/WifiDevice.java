package com.wuxl.design.model;

import java.io.Serializable;

import static com.wuxl.design.utils.DataUtils.toHex;
/**
 * wifi设备
 * Created by wuxingle on 2017/4/11 0011.
 */
public class WifiDevice implements Serializable{

    public static final int ONLINE = 1;

    public static final int BUSY = 2;

    public static final int UNONLINE = 3;

    private byte[] id;

    private String name;

    private int lightLevel = 100;

    private transient int status;

    public WifiDevice() {
    }

    public WifiDevice(byte[] id) {
        this.id = id;
    }

    public WifiDevice(byte[] id, String name) {
        this.id = id;
        this.name = name;
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
