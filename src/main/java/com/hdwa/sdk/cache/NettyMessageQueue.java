package com.hdwa.sdk.cache;

import com.redxun.core.entity.alarm.netty.NettyMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * netty通信queue 在云端与与边缘端断开连接后，报警消息，报警恢复消息，报警过期消息缓冲在边缘端的NettyMessageQueue队列中，
 * 再次连接后，从队列发送到云端
 **/
public class NettyMessageQueue {
    //队列大小
    static final int QUEUE_MAX_SIZE = 20000;

    static BlockingQueue<NettyMessage> blockingQueue = new LinkedBlockingQueue<NettyMessage>(QUEUE_MAX_SIZE);

    /**
     * 私有的默认构造子，保证外界无法直接实例化
     */
    private NettyMessageQueue() {
    }

    //单例队列
    public static NettyMessageQueue getNettyMessageQueue() {
        return SingletonHolder.queue;
    }

    //生产入队
    public void produce(NettyMessage commandResult) throws InterruptedException {
        blockingQueue.put(commandResult);
    }

    //消费出队
    public NettyMessage consume() throws InterruptedException {
        return blockingQueue.take();
    }

    // 获取队列大小
    public int size() {
        return blockingQueue.size();
    }

    /**
     * 类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例
     * 没有绑定关系，而且只有被调用到才会装载，从而实现了延迟加载
     */
    private static class SingletonHolder {
        /**
         * 静态初始化器，由JVM来保证线程安全
         */
        private static final NettyMessageQueue queue = new NettyMessageQueue();
    }
}
