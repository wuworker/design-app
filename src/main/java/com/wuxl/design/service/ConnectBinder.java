package com.wuxl.design.service;

import android.os.Binder;

import com.wuxl.design.connect.ConnectorListener;
import com.wuxl.design.connect.DataExecutor;
import com.wuxl.design.connect.TCPConnector;

/**
 * Created by wuxingle on 2017/4/13 0013.
 * binder
 */
public class ConnectBinder extends Binder{

    private TCPConnector tcpConnector;

    private DataExecutor dataExecutor;

    public ConnectBinder(TCPConnector tcpConnector){
        this.tcpConnector = tcpConnector;
        dataExecutor = DataExecutor.getDefaultDataExecutor(tcpConnector);
    }

    public void setConnectorListener(ConnectorListener listener){
        tcpConnector.setListener(listener);
    }


    public void connect(String ip,int port){
        tcpConnector.connect(ip,port);
    }

    public DataExecutor getDataExecutor() {
        return dataExecutor;
    }
}
