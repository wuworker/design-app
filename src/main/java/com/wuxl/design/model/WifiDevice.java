package com.wuxl.design.model;

import static com.wuxl.design.utils.DataUtils.toHex;
/**
 * wifi设备
 * Created by wuxingle on 2017/4/11 0011.
 */
public class WifiDevice {

    private byte[] id;

    private boolean online;

    private String name;

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

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "WifiDevice{" +
                "id=" + getHexId() +
                ", name='" + name + '\'' +
                '}';
    }
}
