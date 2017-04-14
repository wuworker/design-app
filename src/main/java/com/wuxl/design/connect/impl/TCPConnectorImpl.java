package com.wuxl.design.connect.impl;

import android.util.Log;

import com.wuxl.design.connect.ConnectorListener;
import com.wuxl.design.connect.TCPConnector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * tcp连接的实现
 * Created by wuxingle on 2017/4/12 0012.
 */
public class TCPConnectorImpl implements TCPConnector{

    private static final String TAG = "TCPConnectorImpl";

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Selector selector;

    private SocketChannel socketChannel;

    private ByteBuffer readBuffer = ByteBuffer.allocate(32);

    private ByteBuffer writeBuffer = ByteBuffer.allocate(32);

    private boolean running;

    private ConnectorListener listener;

    private boolean canSend;

    public TCPConnectorImpl(){}

    /**
     * 进行连接
     * @param port 端口号
     */
    @Override
    public void connect(final String ip,final int port){
        if(running || selector!=null){
            Log.i(TAG,"已在连接...");
            return;
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    start(ip,port);
                }catch (IOException e){
                    Log.e(TAG,"连接失败",e);
                }finally {
                    selector = null;
                    Log.i(TAG,"连接已关闭");
                }
            }
        });
    }

    /**
     * 发送数据
     */
    @Override
    public void sendData(byte[] data) {
        if(!running){
            Log.w(TAG,"连接不可用，不能发送");
            return;
        }
        Log.i(TAG,"发送就绪"+Arrays.toString(data));
        //put不行
        writeBuffer = ByteBuffer.wrap(data);
        canSend = true;
        if(listener!=null){
            listener.sendComplete(data);
        }
    }

    /**
     * 关闭连接
     */
    @Override
    public void close(){
        if(!running){
            Log.i(TAG,"连接已关闭");
            return;
        }
        running = false;
        try {
            socketChannel.close();
        }catch (IOException e){
            Log.e(TAG,"close异常",e);
        }

        executorService.shutdown();
        Log.i(TAG,"连接正在断开");
    }

    /**
     * 设置监听
     */
    @Override
    public void setListener(ConnectorListener listener){
        this.listener = listener;
    }


    /**
     * 开始运行
     */
    private void start(String ip,int port)throws IOException{
        try{
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(ip,port));
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            Log.d(TAG,"开始连接...");
            running = true;
            listen();
        }catch (IOException e){
            running = false;
            Log.e(TAG,"start方法异常,连接异常终止",e);
        }finally {
            if(selector!=null)
                selector.close();
            Log.i(TAG,"连接资源已释放");
        }
    }

    /**
     * selector开始监听
     * @throws IOException
     */
    private void listen()throws IOException{
        int count;
        try {
            while(running){
                count = selector.select();
                if(count == 0){
                    continue;
                }
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while(it.hasNext()){
                    SelectionKey key = it.next();
                    it.remove();
                    handlerKey(key);
                }
            }
        }catch (IOException e){
            Log.e(TAG,"select调度异常,准备停止连接");
            notifyInterested(e.getMessage());
        }finally {
            running = false;
            socketChannel.close();
        }
    }

    /**
     * 处理事件
     * @param key key
     */
    private void handlerKey(SelectionKey key)throws IOException{
        if(key.isConnectable()){
            handlerConnection(key);
        } else if(key.isReadable()){
            handlerReader(key);
        } else if(key.isWritable()){
            handlerWriter(key);
        }
    }

    /**
     * 处理连接事件
     * @param key key
     * @throws IOException
     */
    private void handlerConnection(SelectionKey key)throws IOException{
        boolean success = false;
        try {
            if(socketChannel.isConnectionPending()){
                if(socketChannel.finishConnect()){
                    key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    Log.i(TAG,"连接成功");
                    success = true;
                }else {
                    key.cancel();
                    Log.i(TAG,"连接失败");
                }
            }
        }catch (IOException e){
            Log.e(TAG,"连接事件异常",e);
            running = false;
            key.cancel();
            socketChannel.close();
        }finally {
            if(listener!=null){
                listener.connectResult(success);
            }
        }
    }

    /**
     * 处理读取事件
     * @param key key
     * @throws IOException
     */
    private void handlerReader(SelectionKey key)throws IOException{
        try {
            int count = socketChannel.read(readBuffer);
            if(count!=-1){
                readBuffer.flip();
                byte[] bytes = readBuffer.array();
                byte[] data = Arrays.copyOf(bytes,count);
                Log.i(TAG,"收到数据"+Arrays.toString(data));
                notifyInterested(data);
                readBuffer.clear();
            } else {
                Log.i(TAG,"读取到-1连接断开");
                notifyInterested("与服务器断开连接");
                running = false;
                key.cancel();
                socketChannel.close();
            }
        }catch (IOException e){
            Log.e(TAG,"读取事件异常",e);
            notifyInterested(e.getMessage());
            running =false;
            key.cancel();
            socketChannel.close();
        }
    }

    /**
     * 处理写事件
     * @param key key
     */
    private void handlerWriter(SelectionKey key)throws IOException{
        try{
            if(canSend){
                Log.i(TAG,"发送了一条数据");
                socketChannel.write(writeBuffer);
                canSend = false;
                writeBuffer.clear();
            }
        }catch (IOException e){
            Log.e(TAG,"发送数据异常");
            notifyInterested(e.getMessage());
            running = false;
            key.cancel();
            socketChannel.close();
        }
    }

    /**
     * 通知对数据感兴趣的人
     */
    private void notifyInterested(byte[] bytes){
        if(listener!=null){
            listener.arrivedMessage(bytes);
        }
    }

    private void notifyInterested(String msg){
        if(listener!=null){
            listener.connectLost(msg);
        }
    }

}

















