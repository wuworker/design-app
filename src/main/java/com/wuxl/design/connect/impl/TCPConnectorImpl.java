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
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Queue;

/**
 * tcp连接的实现
 * Created by wuxingle on 2017/4/12 0012.
 */
public class TCPConnectorImpl implements TCPConnector{

    private static final String TAG = "TCPConnectorImpl";

    private Selector selector;

    private SocketChannel socketChannel;

    private ByteBuffer readBuffer = ByteBuffer.allocate(32);

    //最多缓存100条发送消息
    private Queue<ByteBuffer> messageQueue = new ArrayDeque<>(100);

    //系统是否运行的标志
    private boolean running;

    //select是否运行的标志
    private boolean isclose = true;

    private boolean connectable;

    private ConnectorListener listener;

    public TCPConnectorImpl(){}

    /**
     * 进行连接
     * @param port 端口号
     */
    @Override
    public void connect(String ip,int port){
        if(running){
            Log.i(TAG,"正在连接中...");
            return;
        }
        try {
            Log.i(TAG,"开始连接");
            running = true;
            isclose = false;
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(new InetSocketAddress(ip,port));
            listen();
        }catch (IOException e){
            Log.e(TAG,"启动连接失败",e);
            if(listener!=null){
                listener.connectResult(false);
            }
        }finally {
            running = false;
            Log.i(TAG,"连接关闭");
        }
    }

    /**
     * 发送数据
     */
    @Override
    public void sendData(byte[] data) {
        if(isclose){
            Log.w(TAG,"连接不可用，不能发送");
            return;
        }
        messageQueue.offer(ByteBuffer.wrap(data));

        if(listener!=null){
            listener.sendComplete(data);
        }
    }

    /**
     * 关闭连接
     */
    @Override
    public void close(){
        if(!running || isclose){
            Log.i(TAG,"连接已关闭");
            return;
        }
        connectable = false;
        isclose = true;
        Log.i(TAG,"连接正在断开");
        try {
            socketChannel.close();
        }catch (IOException e){
            Log.e(TAG,"socket channel关闭异常",e);
        }
        selector.wakeup();
    }

    /**
     * 设置监听
     */
    @Override
    public void setListener(ConnectorListener listener){
        this.listener = listener;
    }

    /**
     * @return 是否正在连接
     */
    @Override
    public boolean isConnecting() {
        return running;
    }

    /**
     * 连接是否可用
     */
    @Override
    public boolean isConnectable() {
        return connectable;
    }

    /**
     * selector开始监听
     */
    private void listen()throws IOException{
        int count;
        try {
            while(!isclose){
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
            selector.close();
            isclose = true;
            Log.i(TAG,"释放连接资源");
        }
    }

    /**
     * 处理事件
     * 这里已关闭了通道
     * 要继续关闭selector
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
        try {
            if(socketChannel.isConnectionPending()){
                if(socketChannel.finishConnect()){
                    key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    Log.i(TAG,"连接成功");
                    connectable = true;
                }else {
                    key.cancel();
                    Log.i(TAG,"连接失败");
                }
            }
        }catch (IOException e){
            Log.e(TAG,"连接事件异常",e);
            key.cancel();
            close();
        }finally {
            if(listener!=null){
                listener.connectResult(connectable);
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
                key.cancel();
                close();
            }
        }catch (IOException e){
            Log.e(TAG,"读取事件异常",e);
            notifyInterested(e.getMessage());
            key.cancel();
            close();
        }
    }

    /**
     * 处理写事件
     * @param key key
     */
    private void handlerWriter(SelectionKey key)throws IOException{
        try{
            if(!messageQueue.isEmpty()){
                Log.i(TAG,"发送了一条数据");
                //移除并返回
                socketChannel.write(messageQueue.poll());
            }
        }catch (IOException e){
            Log.e(TAG,"发送数据异常");
            notifyInterested(e.getMessage());
            key.cancel();
            close();
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

















