package com.wuxl.design.service;

import android.os.Binder;

import com.wuxl.design.connect.ConnectorListener;
import com.wuxl.design.connect.DataExecutor;
import com.wuxl.design.connect.TCPConnector;
import com.wuxl.design.connect.protocol.DataPackage;

import java.io.IOException;

/**
 * Created by wuxingle on 2017/4/13 0013.
 *
 */
public class ConnectBinder extends Binder{

    private TCPConnector tcpConnector;

    private DataExecutor dataExecutor;

    private ConnectorListener connectorListener;

    public ConnectBinder(TCPConnector tcpConnector){
        this.tcpConnector = tcpConnector;
        dataExecutor = DataExecutor.getDefaultDataExecutor(tcpConnector);
    }

    public void setConnectorListener(ConnectorListener listener){
        tcpConnector.setListener(listener);
    }

    public DataPackage parse(byte[] bytes){
        return dataExecutor.toDataPackage(bytes);
    }

    public void send(DataPackage dataPackage){
        dataExecutor.sendData(dataPackage);
    }

    public void connect(String ip,int port)throws IOException{
        tcpConnector.connect(ip,port);
    }

}
