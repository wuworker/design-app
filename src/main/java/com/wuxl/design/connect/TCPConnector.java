package com.wuxl.design.connect;

import java.io.IOException;

/**
 * tcp连接接口
 * Created by wuxingle on 2017/4/12 0012.
 */
public interface TCPConnector {

    /**
     * 连接
     */
    void connect(String ip,int port) throws IOException;

    /**
     * 关闭
     * @throws IOException
     */
    void close()throws IOException;

    /**
     * 设置监听
     * @param listener
     */
    void setListener(ConnectorListener listener);

    /**
     * 发送数据
     * @param data
     */
    void sendData(byte[] data);

}
