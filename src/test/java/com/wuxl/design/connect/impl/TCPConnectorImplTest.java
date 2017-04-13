package com.wuxl.design.connect.impl;

import com.wuxl.design.connect.ConnectorListener;
import com.wuxl.design.connect.DataExecutor;
import com.wuxl.design.connect.TCPConnector;
import com.wuxl.design.connect.protocol.DataPackage;

import java.io.IOException;
import java.util.Arrays;

/**
 * 连接测试
 * Created by wuxingle on 2017/4/12 0012.
 */
public class TCPConnectorImplTest {
    private static final byte[] ID1 = new byte[6];
    private static final byte[] ID2 = new byte[6];
    private static final byte[] ID3 = new byte[6];
    private static final byte[] EMPTY = new byte[6];

    static{
        Arrays.fill(ID1,(byte)0x1f);
        Arrays.fill(ID2,(byte)0x23);
        Arrays.fill(ID3,(byte)0x56);
    }

    private TCPConnector connector;

    private ConnectorListener listener;

    private DataExecutor dataExecutor;

    private boolean success;

    public TCPConnectorImplTest(){
        connector = new TCPConnectorImpl();
        listener = new ConnectorListenerTest();
        dataExecutor = DataExecutor.getDefaultDataExecutor(connector);
        connector.setListener(listener);
    }

    public void start()throws IOException{
        connector.connect("",9999);
    }

    public void sendData(DataPackage dataPackage){
        dataExecutor.sendData(dataPackage);
    }

    private class ConnectorListenerTest implements ConnectorListener{
        private DataPackage dataPackage = new DataPackage();
        public ConnectorListenerTest(){
            dataPackage.setOrigin(ID2);
            dataPackage.setTarget(EMPTY);
            dataPackage.setCmd(new byte[]{0x11});
            dataPackage.setData(new byte[]{0x12,0x13,0x14});
        }
        @Override
        public void connectResult(boolean success) {
            System.out.printf("连接成功");
            success = true;

            sendData(dataPackage);
        }

        @Override
        public void arrivedMessage(byte[] bytes) {
            DataPackage dataPackage = dataExecutor.toDataPackage(bytes);
            System.out.println("接收到数据来源:"+dataPackage.getHexOrigin());
            System.out.println("数据为:"+ Arrays.toString(dataPackage.getCmd()));
            System.out.println("数据为:"+ Arrays.toString(dataPackage.getData()));
        }

        @Override
        public void sendComplete(byte[] bytes) {
            System.out.println("发送成功");
        }

        @Override
        public void connectLost(String msg) {
            System.out.println("连接失败"+msg);
        }
    }

    public static void main(String[] args) {
        //ExecutorService service = Executors.newFixedThreadPool(3);
        System.out.println("开始");
        TCPConnectorImplTest tcpConnectorImplTest = new TCPConnectorImplTest();
        try {
            tcpConnectorImplTest.start();
            System.in.read();
        }catch (IOException e){
            e.printStackTrace();
        }
        //service.execute();
        System.out.println("结束");
    }


}