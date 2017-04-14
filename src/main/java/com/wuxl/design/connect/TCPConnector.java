package com.wuxl.design.connect;

/**
 * tcp连接接口
 * Created by wuxingle on 2017/4/12 0012.
 */
public interface TCPConnector {

    /**
     * 连接
     */
    void connect(String ip,int port);

    /**
     * 关闭
     */
    void close();

    /**
     * 设置监听
     */
    void setListener(ConnectorListener listener);

    /**
     * 发送数据
     */
    void sendData(byte[] data);

}
